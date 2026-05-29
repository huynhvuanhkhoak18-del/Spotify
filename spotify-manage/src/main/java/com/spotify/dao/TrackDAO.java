package com.spotify.dao;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: TrackDAO – CRUD + search/filter/stats for tracks table

import com.spotify.model.Album;
import com.spotify.model.Artist;
import com.spotify.model.Track;

import java.sql.*;
import java.util.*;

/**
 * DAO for Track entity.
 * Features: full CRUD, full-text search, genre filter, pagination, stats aggregations.
 */
public class TrackDAO extends BaseDAO implements GenericDAO<Track, Integer> {

    private static final String SELECT_BASE =
            "SELECT t.id, t.track_id, t.track_name, t.popularity, t.duration_ms, t.explicit, " +
            "t.danceability, t.energy, t.track_key, t.loudness, t.mode, t.speechiness, " +
            "t.acousticness, t.instrumentalness, t.liveness, t.valence, t.tempo, " +
            "t.time_signature, t.track_genre, t.created_at, t.updated_at, " +
            "a.id AS artist_id, a.name AS artist_name, " +
            "al.id AS album_id, al.name AS album_name " +
            "FROM tracks t " +
            "JOIN artists a  ON t.artist_id = a.id " +
            "LEFT JOIN albums al ON t.album_id = al.id ";

    private Track mapRow(ResultSet rs) throws SQLException {
        var t = new Track();
        t.setId(rs.getInt("id"));
        t.setTrackId(rs.getString("track_id"));
        t.setTrackName(rs.getString("track_name"));
        t.setPopularity(rs.getInt("popularity"));
        t.setDurationMs(rs.getInt("duration_ms"));
        t.setExplicit(rs.getBoolean("explicit"));
        t.setDanceability(rs.getDouble("danceability"));
        t.setEnergy(rs.getDouble("energy"));
        t.setTrackKey(rs.getInt("track_key"));
        t.setLoudness(rs.getDouble("loudness"));
        t.setMode(rs.getInt("mode"));
        t.setSpeechiness(rs.getDouble("speechiness"));
        t.setAcousticness(rs.getDouble("acousticness"));
        t.setInstrumentalness(rs.getDouble("instrumentalness"));
        t.setLiveness(rs.getDouble("liveness"));
        t.setValence(rs.getDouble("valence"));
        t.setTempo(rs.getDouble("tempo"));
        t.setTimeSignature(rs.getInt("time_signature"));
        t.setTrackGenre(rs.getString("track_genre"));

        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) t.setCreatedAt(ca.toLocalDateTime());

        var artist = new Artist(rs.getInt("artist_id"), rs.getString("artist_name"));
        t.setArtist(artist);

        int albumId = rs.getInt("album_id");
        if (!rs.wasNull()) {
            var album = new Album(albumId, rs.getString("album_name"), artist);
            t.setAlbum(album);
        }
        return t;
    }

    // ─── findById ──────────────────────────────────────────────────
    @Override
    public Optional<Track> findById(Integer id) {
        try (var ps = getConnection().prepareStatement(SELECT_BASE + "WHERE t.id = ?")) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.findById failed", e);
        }
    }

    // ─── findAll (paginated) ───────────────────────────────────────
    @Override
    public List<Track> findAll(int page, int pageSize) {
        var list = new ArrayList<Track>();
        String sql = SELECT_BASE + "ORDER BY t.popularity DESC LIMIT ? OFFSET ?";
        try (var ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.findAll(paged) failed", e);
        }
        return list;
    }

    @Override
    public List<Track> findAll() {
        return findAll(1, 500);
    }

    // ─── search (full-text on track_name / artist) ─────────────────
    public List<Track> search(String keyword, String genre, int minPop, int maxPop,
                               int page, int pageSize) {
        var list = new ArrayList<Track>();
        var sb = new StringBuilder(SELECT_BASE + "WHERE 1=1 ");
        var params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sb.append("AND (t.track_name LIKE ? OR a.name LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            params.add(like); params.add(like);
        }
        if (genre != null && !genre.isBlank() && !genre.equals("All")) {
            sb.append("AND t.track_genre = ? ");
            params.add(genre);
        }
        sb.append("AND t.popularity BETWEEN ? AND ? ");
        params.add(minPop); params.add(maxPop);
        sb.append("ORDER BY t.popularity DESC LIMIT ? OFFSET ?");
        params.add(pageSize); params.add((page - 1) * pageSize);

        try (var ps = getConnection().prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.search failed", e);
        }
        return list;
    }

    public long countSearch(String keyword, String genre, int minPop, int maxPop) {
        var sb = new StringBuilder(
                "SELECT COUNT(*) FROM tracks t JOIN artists a ON t.artist_id=a.id WHERE 1=1 ");
        var params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sb.append("AND (t.track_name LIKE ? OR a.name LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            params.add(like); params.add(like);
        }
        if (genre != null && !genre.isBlank() && !genre.equals("All")) {
            sb.append("AND t.track_genre = ? ");
            params.add(genre);
        }
        sb.append("AND t.popularity BETWEEN ? AND ?");
        params.add(minPop); params.add(maxPop);

        try (var ps = getConnection().prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            var rs = ps.executeQuery();
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.countSearch failed", e);
        }
    }

    // ─── save (INSERT) ─────────────────────────────────────────────
    @Override
    public int save(Track track) {
        String sql = "INSERT INTO tracks (track_id, track_name, artist_id, album_id, popularity, " +
                "duration_ms, explicit, danceability, energy, track_key, loudness, mode, " +
                "speechiness, acousticness, instrumentalness, liveness, valence, tempo, " +
                "time_signature, track_genre) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, track.getTrackId());
            ps.setString(2, track.getTrackName());
            ps.setInt(3, track.getArtist().getId());
            if (track.getAlbum() != null) ps.setInt(4, track.getAlbum().getId());
            else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, track.getPopularity());
            ps.setInt(6, track.getDurationMs());
            ps.setBoolean(7, track.isExplicit());
            ps.setDouble(8, track.getDanceability());
            ps.setDouble(9, track.getEnergy());
            ps.setInt(10, track.getTrackKey());
            ps.setDouble(11, track.getLoudness());
            ps.setInt(12, track.getMode());
            ps.setDouble(13, track.getSpeechiness());
            ps.setDouble(14, track.getAcousticness());
            ps.setDouble(15, track.getInstrumentalness());
            ps.setDouble(16, track.getLiveness());
            ps.setDouble(17, track.getValence());
            ps.setDouble(18, track.getTempo());
            ps.setInt(19, track.getTimeSignature());
            ps.setString(20, track.getTrackGenre());
            ps.executeUpdate();
            var keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.save failed", e);
        }
    }

    // ─── update ────────────────────────────────────────────────────
    @Override
    public boolean update(Track track) {
        String sql = "UPDATE tracks SET track_name=?, artist_id=?, popularity=?, duration_ms=?, " +
                "explicit=?, danceability=?, energy=?, loudness=?, valence=?, tempo=?, " +
                "track_genre=? WHERE id=?";
        try (var ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, track.getTrackName());
            ps.setInt(2, track.getArtist().getId());
            ps.setInt(3, track.getPopularity());
            ps.setInt(4, track.getDurationMs());
            ps.setBoolean(5, track.isExplicit());
            ps.setDouble(6, track.getDanceability());
            ps.setDouble(7, track.getEnergy());
            ps.setDouble(8, track.getLoudness());
            ps.setDouble(9, track.getValence());
            ps.setDouble(10, track.getTempo());
            ps.setString(11, track.getTrackGenre());
            ps.setInt(12, track.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.update failed", e);
        }
    }

    // ─── delete ────────────────────────────────────────────────────
    @Override
    public boolean delete(Integer id) {
        try (var ps = getConnection().prepareStatement("DELETE FROM tracks WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.delete failed", e);
        }
    }

    @Override
    public long count() {
        try (var st = getConnection().createStatement();
             var rs = st.executeQuery("SELECT COUNT(*) FROM tracks")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.count failed", e);
        }
    }

    // ─── Analytics queries ─────────────────────────────────────────

    /** Average popularity per genre, sorted descending */
    public Map<String, Double> avgPopularityByGenre() {
        var map = new LinkedHashMap<String, Double>();
        String sql = "SELECT track_genre, AVG(popularity) AS avg_pop " +
                     "FROM tracks WHERE track_genre IS NOT NULL " +
                     "GROUP BY track_genre ORDER BY avg_pop DESC LIMIT 20";
        try (var st = getConnection().createStatement(); var rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("track_genre"), rs.getDouble("avg_pop"));
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.avgPopularityByGenre failed", e);
        }
        return map;
    }

    /** Count of tracks per genre */
    public Map<String, Integer> trackCountByGenre() {
        var map = new LinkedHashMap<String, Integer>();
        String sql = "SELECT track_genre, COUNT(*) AS cnt FROM tracks " +
                     "WHERE track_genre IS NOT NULL GROUP BY track_genre ORDER BY cnt DESC LIMIT 15";
        try (var st = getConnection().createStatement(); var rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("track_genre"), rs.getInt("cnt"));
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.trackCountByGenre failed", e);
        }
        return map;
    }

    /** Average audio features across all tracks */
    public Map<String, Double> avgAudioFeatures() {
        var map = new LinkedHashMap<String, Double>();
        String sql = "SELECT AVG(danceability) AS danceability, AVG(energy) AS energy, " +
                     "AVG(valence) AS valence, AVG(acousticness) AS acousticness, " +
                     "AVG(speechiness) AS speechiness, AVG(liveness) AS liveness " +
                     "FROM tracks";
        try (var st = getConnection().createStatement(); var rs = st.executeQuery(sql)) {
            if (rs.next()) {
                map.put("Danceability", rs.getDouble("danceability"));
                map.put("Energy",       rs.getDouble("energy"));
                map.put("Valence",      rs.getDouble("valence"));
                map.put("Acousticness", rs.getDouble("acousticness"));
                map.put("Speechiness",  rs.getDouble("speechiness"));
                map.put("Liveness",     rs.getDouble("liveness"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.avgAudioFeatures failed", e);
        }
        return map;
    }

    /** Top N tracks by popularity */
    public List<Track> topByPopularity(int limit) {
        var list = new ArrayList<Track>();
        String sql = SELECT_BASE + "ORDER BY t.popularity DESC LIMIT ?";
        try (var ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.topByPopularity failed", e);
        }
        return list;
    }

    /** Distinct genre list for filter combo-box */
    public List<String> distinctGenres() {
        var list = new ArrayList<String>();
        try (var st = getConnection().createStatement();
             var rs = st.executeQuery(
                     "SELECT DISTINCT track_genre FROM tracks WHERE track_genre IS NOT NULL ORDER BY track_genre")) {
            while (rs.next()) list.add(rs.getString("track_genre"));
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.distinctGenres failed", e);
        }
        return list;
    }

    /** KPI: total tracks, avg popularity, most popular genre */
    public Map<String, Object> kpiStats() {
        var map = new LinkedHashMap<String, Object>();
        String sql = "SELECT COUNT(*) AS total, AVG(popularity) AS avg_pop, " +
                     "AVG(tempo) AS avg_tempo, SUM(explicit) AS explicit_cnt FROM tracks";
        try (var st = getConnection().createStatement(); var rs = st.executeQuery(sql)) {
            if (rs.next()) {
                map.put("Total Tracks",   rs.getLong("total"));
                map.put("Avg Popularity", String.format("%.1f", rs.getDouble("avg_pop")));
                map.put("Avg Tempo",      String.format("%.1f BPM", rs.getDouble("avg_tempo")));
                map.put("Explicit Tracks",rs.getLong("explicit_cnt"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.kpiStats failed", e);
        }
        // Most popular genre
        String sqlGenre = "SELECT track_genre FROM tracks WHERE track_genre IS NOT NULL " +
                          "GROUP BY track_genre ORDER BY AVG(popularity) DESC LIMIT 1";
        try (var st = getConnection().createStatement(); var rs = st.executeQuery(sqlGenre)) {
            if (rs.next()) map.put("Top Genre", rs.getString("track_genre"));
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.kpiStats (genre) failed", e);
        }
        return map;
    }

    /** Energy vs Popularity scatter data */
    public List<double[]> energyVsPopularity(String genre) {
        var list = new ArrayList<double[]>();
        String sql = genre != null && !genre.equals("All")
                ? "SELECT energy, popularity FROM tracks WHERE track_genre=? LIMIT 500"
                : "SELECT energy, popularity FROM tracks LIMIT 500";
        try (var ps = getConnection().prepareStatement(sql)) {
            if (genre != null && !genre.equals("All")) ps.setString(1, genre);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(new double[]{rs.getDouble("energy"), rs.getDouble("popularity")});
        } catch (SQLException e) {
            throw new RuntimeException("TrackDAO.energyVsPopularity failed", e);
        }
        return list;
    }
}