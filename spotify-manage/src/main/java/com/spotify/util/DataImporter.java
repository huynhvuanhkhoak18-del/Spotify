package com.spotify.util;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: DataImporter – reads Spotify CSV and batch-inserts via JDBC PreparedStatement

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.spotify.dao.ArtistDAO;
import com.spotify.model.Artist;
import com.spotify.pattern.DatabaseConnection;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * CSV bulk-importer for Spotify Tracks dataset (Kaggle).
 * Uses OpenCSV for parsing and JDBC batch execution for performance.
 * Runs on background thread (SwingWorker calls this).
 *
 * Expected CSV columns (Kaggle spotify-tracks-dataset):
 * track_id, artists, album_name, track_name, popularity, duration_ms, explicit,
 * danceability, energy, key, loudness, mode, speechiness, acousticness,
 * instrumentalness, liveness, valence, tempo, time_signature, track_genre
 */
public class DataImporter {

    private static final int BATCH_SIZE = 500;

    private final ArtistDAO artistDAO = new ArtistDAO();
    /** Artist name → DB id cache to avoid repeated SELECTs */
    private final Map<String, Integer> artistCache = new HashMap<>();

    /**
     * Import CSV file into MySQL.
     * @param csvFile  Path to the Kaggle spotify_tracks.csv
     * @param progress Callback receiving progress 0–100
     * @return number of rows imported
     */
    public int importCSV(File csvFile, Consumer<Integer> progress) throws Exception {
        int totalLines = countLines(csvFile) - 1; // exclude header
        int imported = 0;
        int batchCount = 0;

        Connection conn = DatabaseConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        String sql = "INSERT IGNORE INTO tracks " +
                "(track_id, track_name, artist_id, popularity, duration_ms, explicit, " +
                "danceability, energy, track_key, loudness, mode, speechiness, acousticness, " +
                "instrumentalness, liveness, valence, tempo, time_signature, track_genre) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (var reader = new CSVReader(new FileReader(csvFile));
             var ps = conn.prepareStatement(sql)) {

            String[] header = reader.readNext(); // skip header
            String[] row;

            while ((row = reader.readNext()) != null) {
                if (row.length < 20) continue;
                try {
                    int artistId = resolveArtist(row[1].trim());
                    ps.setString(1,  safeStr(row[0]));  // track_id
                    ps.setString(2,  safeStr(row[3]));  // track_name
                    ps.setInt(3,     artistId);
                    ps.setInt(4,     safeInt(row[4], 0));   // popularity
                    ps.setInt(5,     safeInt(row[5], 0));   // duration_ms
                    ps.setBoolean(6, "1".equals(row[6].trim()) || "True".equalsIgnoreCase(row[6].trim()));
                    ps.setDouble(7,  safeDbl(row[7], 0));   // danceability
                    ps.setDouble(8,  safeDbl(row[8], 0));   // energy
                    ps.setInt(9,     safeInt(row[9], 0));   // key
                    ps.setDouble(10, safeDbl(row[10], 0));  // loudness
                    ps.setInt(11,    safeInt(row[11], 0));  // mode
                    ps.setDouble(12, safeDbl(row[12], 0));  // speechiness
                    ps.setDouble(13, safeDbl(row[13], 0));  // acousticness
                    ps.setDouble(14, safeDbl(row[14], 0));  // instrumentalness
                    ps.setDouble(15, safeDbl(row[15], 0));  // liveness
                    ps.setDouble(16, safeDbl(row[16], 0));  // valence
                    ps.setDouble(17, safeDbl(row[17], 0));  // tempo
                    ps.setInt(18,    safeInt(row[18], 4));  // time_signature
                    ps.setString(19, safeStr(row[19]));     // track_genre
                    ps.addBatch();
                    batchCount++;
                    imported++;

                    if (batchCount == BATCH_SIZE) {
                        ps.executeBatch();
                        conn.commit();
                        batchCount = 0;
                        if (progress != null && totalLines > 0)
                            progress.accept(Math.min(99, imported * 100 / totalLines));
                    }
                } catch (Exception ignored) {} // skip malformed rows
            }
            if (batchCount > 0) { ps.executeBatch(); conn.commit(); }
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        if (progress != null) progress.accept(100);
        return imported;
    }

    private int resolveArtist(String name) {
        return artistCache.computeIfAbsent(name, n -> artistDAO.findOrCreate(n).getId());
    }

    private static int countLines(File f) throws IOException {
        int count = 0;
        try (var br = new BufferedReader(new FileReader(f))) {
            while (br.readLine() != null) count++;
        }
        return count;
    }

    private static String safeStr(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t.length() > 490 ? t.substring(0, 490) : t;
    }

    private static int safeInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    private static double safeDbl(String s, double def) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return def; }
    }
}