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

import com.spotify.model.UserDTO;
import com.spotify.service.SessionManager;
import com.spotify.ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application window.
 * MVC: this is the View root, containing multiple panel Views.
 * Role-based access: admin sees all tabs; viewer sees read-only tabs only.
 */
public class MainFrame extends JFrame {

    private final UserDTO currentUser;
    private final JTabbedPane tabs = new JTabbedPane();

    public MainFrame(UserDTO user) {
        super("Spotify Tracks Manager  –  " + user.fullName() + "  [" + user.role() + "]");
        this.currentUser = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 780));
        buildUI();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        // ── Top bar ───────────────────────────────────────────────
        var topBar  = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(18, 18, 18));
        topBar.setBorder(new EmptyBorder(8, 16, 8, 16));

        var logo = new JLabel("🎵  Spotify Tracks Manager");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(new Color(30, 215, 96));

        var userInfo = new JLabel(currentUser.fullName() + "  |  " + currentUser.role());
        userInfo.setForeground(Color.LIGHT_GRAY);
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        var btnLogout = new JButton("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            SessionManager.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        var rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(userInfo);
        rightPanel.add(btnLogout);

        topBar.add(logo, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        // ── Tabs ──────────────────────────────────────────────────
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabs.addTab("📊  Dashboard", new DashboardPanel(currentUser));
        tabs.addTab("🎵  Tracks",    new TrackPanel(currentUser));
        tabs.addTab("🎨  Charts",    new ChartPanel(currentUser));

        if (currentUser.isAdmin()) {
            tabs.addTab("👤  Users",     new UserManagementPanel(currentUser));
            tabs.addTab("📥  Import CSV", new ImportPanel(currentUser));
        }

        tabs.addTab("⚙️  My Profile", new ProfilePanel(currentUser));

        // ── Layout ────────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }
}