package com.spotify.ui.panels;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: ProfilePanel – user profile display and password change

import com.spotify.dao.DAOFactory;
import com.spotify.model.UserDTO;
import com.spotify.service.AuthService;
import com.spotify.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * User profile panel.
 * Shows: display name, role, last login.
 * Allows password change with validation.
 */
public class ProfilePanel extends JPanel {

    private final UserDTO user;
    private final AuthService authService = new AuthService();

    public ProfilePanel(UserDTO user) {
        this.user = user;
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        var title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 215, 96));
        add(title, BorderLayout.NORTH);

        // ── Profile info card ─────────────────────────────────────
        var infoCard = new JPanel(new GridLayout(4, 2, 12, 12));
        infoCard.setBorder(new CompoundBorder(
                new TitledBorder("  Profile Information"),
                new EmptyBorder(12, 12, 12, 12)));

        var dao = DAOFactory.getUserDAO();
        dao.findById(user.id()).ifPresent(u -> {
            addInfoRow(infoCard, "Username:",   u.getUsername());
            addInfoRow(infoCard, "Full Name:",  u.getFullName() != null ? u.getFullName() : "—");
            addInfoRow(infoCard, "Email:",      u.getEmail() != null ? u.getEmail() : "—");
            addInfoRow(infoCard, "Role:",       u.getRole() != null ? u.getRole().getName() : "—");
        });

        // ── Password change card ──────────────────────────────────
        var pwCard = new JPanel(new GridBagLayout());
        pwCard.setBorder(new CompoundBorder(
                new TitledBorder("  Change Password"),
                new EmptyBorder(12, 12, 12, 12)));
        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(6, 4, 6, 4);

        var txtOld    = new JPasswordField(20);
        var txtNew    = new JPasswordField(20);
        var txtConfirm = new JPasswordField(20);
        var btnChange = new JButton("Change Password");
        var lblMsg    = new JLabel(" ");
        lblMsg.setForeground(new Color(30, 215, 96));

        addPwRow(pwCard, gbc, 0, "Current Password:", txtOld);
        addPwRow(pwCard, gbc, 1, "New Password:",      txtNew);
        addPwRow(pwCard, gbc, 2, "Confirm New:",       txtConfirm);
        gbc.gridx = 1; gbc.gridy = 3; pwCard.add(btnChange, gbc);
        gbc.gridy = 4; pwCard.add(lblMsg, gbc);

        btnChange.addActionListener(e -> {
            String oldPw  = new String(txtOld.getPassword());
            String newPw  = new String(txtNew.getPassword());
            String confPw = new String(txtConfirm.getPassword());

            if (!newPw.equals(confPw)) {
                lblMsg.setForeground(Color.RED);
                lblMsg.setText("New passwords do not match.");
                return;
            }
            if (!PasswordUtil.isStrong(newPw)) {
                lblMsg.setForeground(Color.RED);
                lblMsg.setText("Password must be ≥8 chars with 1 uppercase and 1 digit.");
                return;
            }
            boolean ok = authService.changePassword(user.id(), oldPw, newPw);
            if (ok) {
                lblMsg.setForeground(new Color(30, 215, 96));
                lblMsg.setText("✅ Password changed successfully.");
                txtOld.setText(""); txtNew.setText(""); txtConfirm.setText("");
            } else {
                lblMsg.setForeground(Color.RED);
                lblMsg.setText("❌ Current password is incorrect.");
            }
        });

        var center = new JPanel(new GridLayout(2, 1, 0, 16));
        center.add(infoCard);
        center.add(pwCard);
        add(center, BorderLayout.CENTER);
    }

    private void addInfoRow(JPanel p, String label, String value) {
        var lbl = new JLabel(label); lbl.setForeground(Color.GRAY);
        var val = new JLabel(value); val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(lbl); p.add(val);
    }

    private void addPwRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; p.add(field, gbc);
    }
}