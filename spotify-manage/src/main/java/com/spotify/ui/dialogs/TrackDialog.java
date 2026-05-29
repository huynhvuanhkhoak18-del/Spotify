package com.spotify.ui.dialogs;

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

import com.spotify.dao.ArtistDAO;
import com.spotify.dao.DAOFactory;
import com.spotify.dao.TrackDAO;
import com.spotify.model.Artist;
import com.spotify.model.Track;
import com.spotify.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for Add / Edit track.
 * Shows all editable track fields with validation before saving.
 */
public class TrackDialog extends JDialog {

    private boolean saved = false;

    private final JTextField txtName        = new JTextField(24);
    private final JTextField txtArtist      = new JTextField(24);
    private final JTextField txtGenre       = new JTextField(24);
    private final JTextField txtPopularity  = new JTextField(8);
    private final JTextField txtDurationMs  = new JTextField(10);
    private final JTextField txtTempo       = new JTextField(8);
    private final JTextField txtDanceability = new JTextField(8);
    private final JTextField txtEnergy      = new JTextField(8);
    private final JTextField txtValence     = new JTextField(8);
    private final JCheckBox  chkExplicit    = new JCheckBox("Explicit");

    private final Track existingTrack;   // null = Add mode

    public TrackDialog(Window owner, Track track) {
        super(owner, track == null ? "Add New Track" : "Edit Track",
                ModalityType.APPLICATION_MODAL);
        this.existingTrack = track;
        buildUI();
        if (track != null) populateFields(track);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        var root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(new EmptyBorder(20, 24, 16, 24));

        // ── Form grid ─────────────────────────────────────────────
        var form = new JPanel(new GridBagLayout());
        var gbc  = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(form, gbc, row++, "Track Name *:",   txtName);
        addRow(form, gbc, row++, "Artist *:",        txtArtist);
        addRow(form, gbc, row++, "Genre:",           txtGenre);
        addRow(form, gbc, row++, "Popularity (0–100) *:", txtPopularity);
        addRow(form, gbc, row++, "Duration (ms) *:", txtDurationMs);
        addRow(form, gbc, row++, "Tempo (BPM):",     txtTempo);
        addRow(form, gbc, row++, "Danceability (0–1):", txtDanceability);
        addRow(form, gbc, row++, "Energy (0–1):",    txtEnergy);
        addRow(form, gbc, row++, "Valence (0–1):",   txtValence);

        // Explicit checkbox
        gbc.gridx = 1; gbc.gridy = row++;
        form.add(chkExplicit, gbc);

        root.add(form, BorderLayout.CENTER);

        // ── Buttons ───────────────────────────────────────────────
        var btnSave   = new JButton("💾 Save");
        var btnCancel = new JButton("Cancel");
        btnSave.setBackground(new Color(30, 215, 96));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        btnCancel.setFocusPainted(false);

        var btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.add(btnCancel);
        btnBar.add(btnSave);
        root.add(btnBar, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> trySave());
        btnCancel.addActionListener(e -> dispose());

        setContentPane(root);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        var lbl = new JLabel(label);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        p.add(field, gbc);
    }

    private void populateFields(Track t) {
        txtName.setText(t.getTrackName());
        txtArtist.setText(t.getArtist() != null ? t.getArtist().getName() : "");
        txtGenre.setText(t.getTrackGenre() != null ? t.getTrackGenre() : "");
        txtPopularity.setText(String.valueOf(t.getPopularity()));
        txtDurationMs.setText(String.valueOf(t.getDurationMs()));
        txtTempo.setText(String.valueOf(t.getTempo()));
        txtDanceability.setText(String.valueOf(t.getDanceability()));
        txtEnergy.setText(String.valueOf(t.getEnergy()));
        txtValence.setText(String.valueOf(t.getValence()));
        chkExplicit.setSelected(t.isExplicit());
    }

    private void trySave() {
        // Validate
        var v = ValidationUtil.validateTrack(
                txtName.getText(), txtArtist.getText(),
                txtPopularity.getText(), txtDurationMs.getText(), txtTempo.getText());
        if (!v.valid()) {
            JOptionPane.showMessageDialog(this, v.errorMessage(), "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build / update Track object
        TrackDAO trackDAO   = DAOFactory.getTrackDAO();
        ArtistDAO artistDAO = DAOFactory.getArtistDAO();

        Artist artist = artistDAO.findOrCreate(txtArtist.getText().trim());

        Track t = existingTrack != null ? existingTrack : new Track();
        t.setTrackName(txtName.getText().trim());
        t.setArtist(artist);
        t.setTrackGenre(txtGenre.getText().trim().isEmpty() ? null : txtGenre.getText().trim());
        t.setPopularity(Integer.parseInt(txtPopularity.getText().trim()));
        t.setDurationMs(Integer.parseInt(txtDurationMs.getText().trim()));
        t.setExplicit(chkExplicit.isSelected());

        if (!txtTempo.getText().isBlank())
            t.setTempo(Double.parseDouble(txtTempo.getText().trim()));
        if (!txtDanceability.getText().isBlank())
            t.setDanceability(parseDoubleSafe(txtDanceability.getText()));
        if (!txtEnergy.getText().isBlank())
            t.setEnergy(parseDoubleSafe(txtEnergy.getText()));
        if (!txtValence.getText().isBlank())
            t.setValence(parseDoubleSafe(txtValence.getText()));

        boolean ok;
        if (existingTrack == null) {
            int newId = trackDAO.save(t);
            ok = newId > 0;
        } else {
            ok = trackDAO.update(t);
        }

        if (ok) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    /** Returns true if the user saved successfully */
    public boolean isSaved() { return saved; }
}