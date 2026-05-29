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

import com.spotify.model.UserDTO;
import com.spotify.pattern.DataChangePublisher;
import com.spotify.util.DataImporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * CSV Import panel.
 * Uses SwingWorker for background processing so the EDT is never frozen.
 * Shows progress bar during import.
 * Spec §3.5c: "Long-running operations must run on a background thread."
 */
public class ImportPanel extends JPanel {

    private final JTextField txtFilePath = new JTextField(40);
    private final JButton btnBrowse   = new JButton("Browse…");
    private final JButton btnImport   = new JButton("▶ Start Import");
    private final JProgressBar progress = new JProgressBar(0, 100);
    private final JTextArea   log       = new JTextArea(12, 60);
    private File selectedFile;

    public ImportPanel(UserDTO user) {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        var title = new JLabel("Import Spotify CSV Dataset");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 215, 96));

        var instructions = new JLabel("<html><body style='width:600px'>" +
                "<b>How to get the dataset:</b><br>" +
                "1. Go to <a href=''>kaggle.com/datasets/maharshipandya/spotify-tracks-dataset</a><br>" +
                "2. Download the CSV file (dataset.csv)<br>" +
                "3. Select it below and click Start Import.<br>" +
                "<i>Large files (114k rows) take ~15 seconds – the app stays responsive.</i>" +
                "</body></html>");
        instructions.setForeground(Color.LIGHT_GRAY);
        instructions.setBorder(new EmptyBorder(8, 0, 12, 0));

        // File picker row
        var fileRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fileRow.add(new JLabel("CSV File:"));
        fileRow.add(txtFilePath);
        fileRow.add(btnBrowse);
        fileRow.add(btnImport);

        // Progress bar
        progress.setStringPainted(true);
        progress.setString("Ready");

        // Log area
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 12));
        var logScroll = new JScrollPane(log);
        logScroll.setBorder(BorderFactory.createTitledBorder("Import Log"));

        var center = new JPanel(new BorderLayout(0, 8));
        center.add(fileRow, BorderLayout.NORTH);
        center.add(progress, BorderLayout.CENTER);
        center.add(logScroll, BorderLayout.SOUTH);

        add(title, BorderLayout.NORTH);
        add(instructions, BorderLayout.CENTER);
        add(center, BorderLayout.SOUTH);

        // Listeners
        btnBrowse.addActionListener(e -> browseFile());
        btnImport.addActionListener(e -> startImport());
    }

    private void browseFile() {
        var fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            txtFilePath.setText(selectedFile.getAbsolutePath());
        }
    }

    private void startImport() {
        if (selectedFile == null || !selectedFile.exists()) {
            JOptionPane.showMessageDialog(this, "Please select a valid CSV file first.");
            return;
        }
        btnImport.setEnabled(false);
        progress.setValue(0);
        progress.setString("Importing…");
        log.setText("");
        logLine("Starting import of: " + selectedFile.getName());

        // SwingWorker: background thread so EDT stays responsive
        new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                var importer = new DataImporter();
                return importer.importCSV(selectedFile, pct -> publish(pct));
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progress.setValue(latest);
                progress.setString(latest + "%");
            }

            @Override
            protected void done() {
                try {
                    int count = get();
                    progress.setValue(100);
                    progress.setString("Done! " + count + " rows imported.");
                    logLine("✅ Import complete: " + count + " tracks inserted.");
                    DataChangePublisher.notifyListeners();
                } catch (Exception e) {
                    progress.setString("Error");
                    logLine("❌ Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(ImportPanel.this,
                            "Import failed: " + e.getMessage());
                }
                btnImport.setEnabled(true);
            }
        }.execute();
    }

    private void logLine(String msg) {
        log.append("[" + java.time.LocalTime.now().toString().substring(0, 8) + "] " + msg + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }
}