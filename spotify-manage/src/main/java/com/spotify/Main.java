package com.spotify;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Application entry point – initialises L&F and launches LoginFrame

import com.formdev.flatlaf.FlatDarkLaf;
import com.spotify.ui.LoginFrame;
import com.spotify.pattern.DatabaseConnection;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Install modern FlatLaf dark theme before any Swing component is created
        FlatDarkLaf.setup();
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 6);

        SwingUtilities.invokeLater(() -> {
            try {
                // Eagerly test DB connectivity so user sees a clear error at startup
                DatabaseConnection.getInstance().getConnection();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Cannot connect to database.\n" + e.getMessage(),
                        "DB Error", JOptionPane.ERROR_MESSAGE);
            }
            new LoginFrame().setVisible(true);
        });
    }
}