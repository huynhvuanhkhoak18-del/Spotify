package com.spotify.dao;

/*
 * Student Name: Huỳnh Vũ Anh Khoa
 * Student ID: 97482503608
 * Course: Object Oriented Programming
 * Project: Spotify
 */

/*
 * Student Name: Nguyễn Thiên Kỳ
 * Student ID: 77482503643
 * Course: Object Oriented Programming
 * Project: Spotify
 */

import com.spotify.model.Artist;

import java.sql.*;
import java.util.*;

public class ArtistDAO extends BaseDAO implements GenericDAO<Artist, Integer> {

    private Artist mapRow(ResultSet rs) throws SQLException {
        var a = new Artist();
        a.setId(rs.getInt("id"));
        a.setName(rs.getString("name"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) a.setCreatedAt(ca.toLocalDateTime());
        return a;
    }

    @Override
    public Optional<Artist> findById(Integer id) {
        try (var ps = getConnection().prepareStatement("SELECT * FROM artists WHERE id=?")) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Artist> findByName(String name) {
        try (var ps = getConnection().prepareStatement("SELECT * FROM artists WHERE name=?")) {
            ps.setString(1, name);
            var rs = ps.executeQuery();
            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Artist> findAll(int page, int pageSize) {
        var list = new ArrayList<Artist>();
        try (var ps = getConnection().prepareStatement(
                "SELECT * FROM artists ORDER BY name LIMIT ? OFFSET ?")) {
            ps.setInt(1, pageSize); ps.setInt(2, (page-1)*pageSize);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<Artist> findAll() {
        var list = new ArrayList<Artist>();
        try (var st = getConnection().createStatement();
             var rs = st.executeQuery("SELECT * FROM artists ORDER BY name")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    /** Insert or retrieve existing artist by name */
    public Artist findOrCreate(String name) {
        return findByName(name).orElseGet(() -> {
            var a = new Artist(); a.setName(name);
            int id = save(a); a.setId(id);
            return a;
        });
    }

    @Override
    public int save(Artist artist) {
        try (var ps = getConnection().prepareStatement(
                "INSERT INTO artists (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, artist.getName());
            ps.executeUpdate();
            var keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean update(Artist artist) {
        try (var ps = getConnection().prepareStatement("UPDATE artists SET name=? WHERE id=?")) {
            ps.setString(1, artist.getName()); ps.setInt(2, artist.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean delete(Integer id) {
        try (var ps = getConnection().prepareStatement("DELETE FROM artists WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        try (var st = getConnection().createStatement();
             var rs = st.executeQuery("SELECT COUNT(*) FROM artists")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}