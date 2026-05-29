package com.spotify.ui.panels;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: UserManagementPanel – admin CRUD for users (role-based access)

import com.spotify.dao.DAOFactory;
import com.spotify.dao.UserDAO;
import com.spotify.model.Role;
import com.spotify.model.User;
import com.spotify.model.UserDTO;
import com.spotify.util.PasswordUtil;
import com.spotify.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin-only user management panel.
 * Shows all users in a table; allows Add, Edit, Deactivate.
 */
public class UserManagementPanel extends JPanel {

    private final UserDAO userDAO = DAOFactory.getUserDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Username", "Full Name", "Email", "Role", "Active", "Last Login"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public UserManagementPanel(UserDTO currentUser) {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI(currentUser);
        loadUsers();
    }

    private void buildUI(UserDTO currentUser) {
        var title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 215, 96));
        add(title, BorderLayout.NORTH);

        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        var btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        var btnAdd      = new JButton("➕ Add User");
        var btnEdit     = new JButton("✏️ Edit");
        var btnDeactivate = new JButton("🚫 Deactivate");

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDeactivate.addActionListener(e -> deactivateSelected());

        btnBar.add(btnAdd); btnBar.add(btnEdit); btnBar.add(btnDeactivate);
        add(btnBar, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        model.setRowCount(0);
        userDAO.findAll().forEach(u -> model.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getFullName(), u.getEmail(),
                u.getRole() != null ? u.getRole().getName() : "",
                u.isActive() ? "Yes" : "No",
                u.getLastLogin() != null ? u.getLastLogin().toString().substring(0, 16) : "Never"
        }));
    }

    private void showAddDialog() {
        var dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add User",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);

        var panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        var gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(6,4,6,4);

        var txtUser  = new JTextField(18);
        var txtFull  = new JTextField(18);
        var txtEmail = new JTextField(18);
        var txtPass  = new JPasswordField(18);
        var cmbRole  = new JComboBox<>(new String[]{"VIEWER", "ADMIN"});

        addFormRow(panel, gbc, 0, "Username:", txtUser);
        addFormRow(panel, gbc, 1, "Full Name:", txtFull);
        addFormRow(panel, gbc, 2, "Email:", txtEmail);
        addFormRow(panel, gbc, 3, "Password:", txtPass);
        addFormRow(panel, gbc, 4, "Role:", cmbRole);

        var btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            var v = ValidationUtil.validateUser(txtUser.getText(), txtFull.getText(), txtEmail.getText());
            if (!v.valid()) { JOptionPane.showMessageDialog(dialog, v.errorMessage()); return; }
            String pass = new String(txtPass.getPassword());
            if (!PasswordUtil.isStrong(pass)) {
                JOptionPane.showMessageDialog(dialog, "Password must be ≥8 chars with 1 uppercase and 1 digit.");
                return;
            }
            var user = new User();
            user.setUsername(txtUser.getText().trim());
            user.setFullName(txtFull.getText().trim());
            user.setEmail(txtEmail.getText().trim());
            user.setPasswordHash(PasswordUtil.hash(pass));
            user.setActive(true);
            var role = new Role(); role.setId("ADMIN".equals(cmbRole.getSelectedItem()) ? 1 : 2);
            role.setName((String) cmbRole.getSelectedItem());
            user.setRole(role);
            userDAO.save(user);
            dialog.dispose();
            loadUsers();
        });

        gbc.gridx = 1; gbc.gridy = 5; panel.add(btnSave, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int id = (int) model.getValueAt(row, 0);
        userDAO.findById(id).ifPresent(user -> {
            // Simple edit: toggle active status or change role
            String[] options = {"Toggle Active", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this, "User: " + user.getUsername(),
                    "Edit User", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                user.setActive(!user.isActive());
                userDAO.update(user);
                loadUsers();
            }
        });
    }

    private void deactivateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        userDAO.findById(id).ifPresent(u -> {
            u.setActive(false);
            userDAO.update(u);
            loadUsers();
        });
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; p.add(field, gbc);
    }
}