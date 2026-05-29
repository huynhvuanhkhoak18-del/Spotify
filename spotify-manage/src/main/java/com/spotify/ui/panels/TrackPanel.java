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
import com.spotify.dao.TrackDAO;
import com.spotify.model.Track;
import com.spotify.model.TrackDTO;
import com.spotify.model.UserDTO;
import com.spotify.pattern.DataChangePublisher;
import com.spotify.pattern.SortStrategy;
import com.spotify.ui.dialogs.TrackDialog;
import com.spotify.util.ExcelExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Track management panel.
 * Features: search (full-text), filter by genre + popularity range,
 * paginated JTable, column-header sorting (Strategy pattern),
 * Add/Edit/Delete (admin only), Export to Excel.
 */
public class TrackPanel extends JPanel {

    private final UserDTO user;
    private final TrackDAO trackDAO = DAOFactory.getTrackDAO();

    // ── Search/filter widgets ─────────────────────────────────────
    private final JTextField txtSearch     = new JTextField(20);
    private final JComboBox<String> cmbGenre = new JComboBox<>();
    private final JSpinner spnMinPop = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner spnMaxPop = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
    private final JButton btnSearch   = new JButton("🔍 Search");
    private final JButton btnReset    = new JButton("↺ Reset");

    // ── Table ─────────────────────────────────────────────────────
    private final String[] COLS = {"ID", "Track", "Artist", "Genre", "Pop.", "Duration", "Explicit"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) {
            return switch (c) { case 0, 4 -> Integer.class; case 6 -> Boolean.class; default -> String.class; };
        }
    };
    private final JTable table = new JTable(tableModel);

    // ── Pagination ────────────────────────────────────────────────
    private int currentPage = 1;
    private static final int PAGE_SIZE = 50;
    private long totalRows = 0;
    private final JLabel lblPaging   = new JLabel();
    private final JButton btnPrev    = new JButton("◀");
    private final JButton btnNext    = new JButton("▶");

    // ── CRUD buttons ──────────────────────────────────────────────
    private final JButton btnAdd    = new JButton("➕ Add");
    private final JButton btnEdit   = new JButton("✏️ Edit");
    private final JButton btnDelete = new JButton("🗑 Delete");
    private final JButton btnExport = new JButton("📥 Export Excel");

    // ── Sort state ────────────────────────────────────────────────
    private String sortColumn = "popularity";
    private boolean sortAsc   = false;

    // ── Current filter state ──────────────────────────────────────
    private List<Track> currentTracks = new ArrayList<>();

    public TrackPanel(UserDTO user) {
        this.user = user;
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
        loadGenres();
        loadData();
    }

    private void buildUI() {
        // ── Filter bar ────────────────────────────────────────────
        var filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterBar.add(new JLabel("Search:"));
        filterBar.add(txtSearch);
        filterBar.add(new JLabel("Genre:"));
        filterBar.add(cmbGenre);
        filterBar.add(new JLabel("Popularity:"));
        filterBar.add(spnMinPop); filterBar.add(new JLabel("–")); filterBar.add(spnMaxPop);
        filterBar.add(btnSearch);
        filterBar.add(btnReset);
        add(filterBar, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        // Column widths
        int[] widths = {50, 280, 180, 120, 55, 70, 70};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Column header sort
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                String colName = switch (col) {
                    case 1 -> "name"; case 2 -> "artist"; case 4 -> "popularity";
                    case 5 -> "tempo"; case 6 -> "energy"; default -> "popularity";
                };
                if (colName.equals(sortColumn)) sortAsc = !sortAsc;
                else { sortColumn = colName; sortAsc = true; }
                applySort();
            }
        });

        var scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ── Bottom bar ────────────────────────────────────────────
        var bottom = new JPanel(new BorderLayout());

        // Pagination
        var pagingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; loadData(); } });
        btnNext.addActionListener(e -> {
            if ((long) currentPage * PAGE_SIZE < totalRows) { currentPage++; loadData(); }
        });
        pagingPanel.add(btnPrev); pagingPanel.add(lblPaging); pagingPanel.add(btnNext);

        // CRUD + Export buttons
        var crudPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnAdd.setBackground(new Color(30, 215, 96)); btnAdd.setForeground(Color.BLACK);
        btnAdd.setFocusPainted(false);
        btnEdit.setFocusPainted(false);
        btnDelete.setFocusPainted(false);
        btnExport.setFocusPainted(false);

        // Role-based: only admin sees Add/Edit/Delete
        if (user.isAdmin()) {
            crudPanel.add(btnAdd);
            crudPanel.add(btnEdit);
            crudPanel.add(btnDelete);
        }
        crudPanel.add(btnExport);

        bottom.add(pagingPanel, BorderLayout.WEST);
        bottom.add(crudPanel, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        // ── Listeners ─────────────────────────────────────────────
        btnSearch.addActionListener(e -> { currentPage = 1; loadData(); });
        txtSearch.addActionListener(e -> { currentPage = 1; loadData(); });
        btnReset.addActionListener(e -> {
            txtSearch.setText(""); cmbGenre.setSelectedIndex(0);
            spnMinPop.setValue(0); spnMaxPop.setValue(100);
            currentPage = 1; loadData();
        });
        btnAdd.addActionListener(e -> showTrackDialog(null));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a track first."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            trackDAO.findById(id).ifPresent(t -> showTrackDialog(t));
        });
        btnDelete.addActionListener(e -> deleteSelected());
        btnExport.addActionListener(e -> exportExcel());
    }

    private void loadGenres() {
        cmbGenre.removeAllItems();
        cmbGenre.addItem("All");
        trackDAO.distinctGenres().forEach(cmbGenre::addItem);
    }

    private void loadData() {
        String keyword = txtSearch.getText().trim();
        String genre   = (String) cmbGenre.getSelectedItem();
        int minPop = (int) spnMinPop.getValue();
        int maxPop = (int) spnMaxPop.getValue();

        new SwingWorker<Void, Void>() {
            List<Track> tracks;
            long total;

            @Override protected Void doInBackground() {
                tracks = trackDAO.search(keyword, genre, minPop, maxPop, currentPage, PAGE_SIZE);
                total  = trackDAO.countSearch(keyword, genre, minPop, maxPop);
                return null;
            }

            @Override protected void done() {
                try { get(); } catch (Exception e) { return; }
                currentTracks = tracks;
                totalRows = total;
                applySort();
                updatePagingLabel();
            }
        }.execute();
    }

    private void applySort() {
        // Strategy pattern: pick sort algorithm dynamically
        var strategy = SortStrategy.of(sortColumn, sortAsc);
        strategy.sort(currentTracks);
        refreshTable(currentTracks);
    }

    private void refreshTable(List<Track> tracks) {
        tableModel.setRowCount(0);
        // Stream API with lambda
        tracks.stream()
              .map(t -> new Object[]{
                  t.getId(), t.getTrackName(),
                  t.getArtist() != null ? t.getArtist().getName() : "",
                  t.getTrackGenre(), t.getPopularity(),
                  t.getDurationFormatted(), t.isExplicit()})
              .forEach(tableModel::addRow);
    }

    private void updatePagingLabel() {
        long pages = Math.max(1, (totalRows + PAGE_SIZE - 1) / PAGE_SIZE);
        lblPaging.setText("  Page " + currentPage + " of " + pages + "  (" + totalRows + " tracks)  ");
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled((long) currentPage * PAGE_SIZE < totalRows);
    }

    private void showTrackDialog(Track track) {
        var dialog = new TrackDialog(SwingUtilities.getWindowAncestor(this), track);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadData();
            DataChangePublisher.notifyListeners();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a track first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete track: " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (trackDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Track deleted.");
                loadData();
                DataChangePublisher.notifyListeners();
            }
        }
    }

    private void exportExcel() {
        var fc = new JFileChooser();
        fc.setSelectedFile(new File("spotify_export.xlsx"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        var file = fc.getSelectedFile();
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                ExcelExporter.exportTracks(currentTracks, file);
                return null;
            }
            @Override protected void done() {
                try { get(); JOptionPane.showMessageDialog(TrackPanel.this,
                        "Exported " + currentTracks.size() + " tracks to:\n" + file.getAbsolutePath());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TrackPanel.this, "Export failed: " + e.getMessage());
                }
            }
        }.execute();
    }
}