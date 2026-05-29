package com.spotify.ui.panels;

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

import com.spotify.dao.DAOFactory;
import com.spotify.model.Track;
import com.spotify.model.UserDTO;
import com.spotify.pattern.DataChangePublisher;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dashboard panel showing:
 *  - KPI strip: Total Tracks, Avg Popularity, Top Genre, Avg Tempo, Explicit Count
 *  - Top 10 tracks by popularity table
 * Registers as Observer to auto-refresh when data changes.
 */
public class DashboardPanel extends JPanel implements DataChangePublisher.DataChangeListener {

    private final UserDTO user;
    private final JPanel kpiStrip = new JPanel(new GridLayout(1, 5, 12, 0));
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"#", "Track", "Artist", "Genre", "Popularity", "Duration"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public DashboardPanel(UserDTO user) {
        this.user = user;
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        DataChangePublisher.addListener(this);
        loadData();
    }

    private void buildUI() {
        // ── Title ─────────────────────────────────────────────────
        var title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 215, 96));
        add(title, BorderLayout.NORTH);

        // ── KPI Strip ─────────────────────────────────────────────
        kpiStrip.setOpaque(false);
        kpiStrip.setBorder(new EmptyBorder(8, 0, 8, 0));
        add(kpiStrip, BorderLayout.CENTER);

        // ── Top Tracks Table ──────────────────────────────────────
        var table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(4).setMaxWidth(90);

        var scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                "  🏆  Top 20 Tracks by Popularity"));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void loadData() {
        new SwingWorker<Void, Void>() {
            Map<String, Object> kpis;
            List<Track> topTracks;

            @Override protected Void doInBackground() {
                var dao = DAOFactory.getTrackDAO();
                kpis = dao.kpiStats();
                topTracks = dao.topByPopularity(20);
                return null;
            }

            @Override protected void done() {
                try { get(); } catch (Exception e) { return; }
                refreshKPI(kpis);
                refreshTable(topTracks);
            }
        }.execute();
    }

    private void refreshKPI(Map<String, Object> kpis) {
        kpiStrip.removeAll();
        var colors = new Color[]{
                new Color(30, 215, 96), new Color(29, 185, 84),
                new Color(255, 167, 38), new Color(66, 165, 245),
                new Color(239, 83, 80)};
        int i = 0;
        for (var entry : kpis.entrySet()) {
            kpiStrip.add(makeKPICard(entry.getKey(), String.valueOf(entry.getValue()),
                    colors[i % colors.length]));
            i++;
        }
        kpiStrip.revalidate();
        kpiStrip.repaint();
    }

    private JPanel makeKPICard(String label, String value, Color accent) {
        var card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(new CompoundBorder(
                new LineBorder(accent, 2, true),
                new EmptyBorder(16, 20, 16, 20)));
        var valLabel = new JLabel(value, SwingConstants.CENTER);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLabel.setForeground(accent);
        var lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Color.GRAY);
        card.add(valLabel);
        card.add(lbl);
        return card;
    }

    private void refreshTable(List<Track> tracks) {
        tableModel.setRowCount(0);
        int[] rank = {1};
        // Stream API with lambda – Modern Java feature
        tracks.stream()
              .sorted((a, b) -> Integer.compare(b.getPopularity(), a.getPopularity()))
              .forEach(t -> tableModel.addRow(new Object[]{
                  rank[0]++,
                  t.getTrackName(),
                  t.getArtist() != null ? t.getArtist().getName() : "",
                  t.getTrackGenre(),
                  t.getPopularity(),
                  t.getDurationFormatted()
              }));
    }

    @Override
    public void onDataChanged() { loadData(); }
}