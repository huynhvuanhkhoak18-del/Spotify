package com.spotify.ui;

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

import com.spotify.service.AuthService;
import com.spotify.model.UserDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

/**
 * Login screen.
 * On success: opens MainFrame with the authenticated UserDTO.
 */
public class LoginFrame extends JFrame {

    private final JTextField txtUsername   = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JButton btnLogin          = new JButton("Sign In");
    private final JLabel lblError           = new JLabel(" ");
    private final AuthService authService   = new AuthService();

    public LoginFrame() {
        super("Spotify Tracks Manager – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        var root = new JPanel(new BorderLayout(0, 0));
        root.setBorder(new EmptyBorder(40, 60, 40, 60));
        root.setBackground(new Color(18, 18, 18));

        // ── Logo / title ─────────────────────────────────────────────
        var logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        var title = new JLabel("🎵 Spotify Manager", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 215, 96));
        var subtitle = new JLabel("ITE23005 – OOP Final Exam 2526", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setBorder(new EmptyBorder(4, 0, 20, 0));
        logoPanel.add(title, BorderLayout.CENTER);
        logoPanel.add(subtitle, BorderLayout.SOUTH);

        // ── Form ─────────────────────────────────────────────────────
        var form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        gbc.gridx = 0; gbc.gridy = 0;
        var userLabel = new JLabel("Username");
        userLabel.setForeground(Color.LIGHT_GRAY);
        form.add(userLabel, gbc);

        gbc.gridy = 1;
        styleTextField(txtUsername);
        form.add(txtUsername, gbc);

        gbc.gridy = 2;
        var passLabel = new JLabel("Password");
        passLabel.setForeground(Color.LIGHT_GRAY);
        form.add(passLabel, gbc);

        gbc.gridy = 3;
        styleTextField(txtPassword);
        form.add(txtPassword, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(16, 0, 4, 0);
        btnLogin.setBackground(new Color(30, 215, 96));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(200, 38));
        form.add(btnLogin, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(4, 0, 0, 0);
        lblError.setForeground(new Color(255, 80, 80));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(lblError, gbc);

        // ── Hint label ───────────────────────────────────────────────
        var hint = new JLabel("<html><center><font color='gray'>Default: admin / Admin@123<br>" +
                "Viewer: viewer / Viewer@123</font></center></html>", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        root.add(logoPanel, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(hint, BorderLayout.SOUTH);
        add(root);

        // ── Listeners ────────────────────────────────────────────────
        btnLogin.addActionListener(e -> attemptLogin());
        txtPassword.addActionListener(e -> attemptLogin());
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(200, 34));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void attemptLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        btnLogin.setEnabled(false);
        lblError.setText("Signing in…");

        // Run in background to avoid EDT freeze
        new SwingWorker<Optional<UserDTO>, Void>() {
            @Override protected Optional<UserDTO> doInBackground() {
                return authService.login(user, pass);
            }
            @Override protected void done() {
                try {
                    var result = get();
                    if (result.isPresent()) {
                        dispose();
                        new MainFrame(result.get()).setVisible(true);
                    } else {
                        lblError.setText("Invalid username or password.");
                        txtPassword.setText("");
                    }
                } catch (Exception ex) {
                    lblError.setText("Error: " + ex.getMessage());
                }
                btnLogin.setEnabled(true);
            }
        }.execute();
    }
}