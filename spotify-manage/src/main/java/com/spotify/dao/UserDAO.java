package com.spotify.dao;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: UserDAO – CRUD operations for users table (FIXED)

import com.spotify.model.Role;
import com.spotify.model.User;
import com.spotify.pattern.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO extends BaseDAO implements GenericDAO<User, Integer> {

    private static final String SELECT_BASE =
            "SELECT u.id, u.username, u.password_hash, u.full_name, u.email, " +
            "u.role_id, r.name AS role_name, u.last_login, u.created_at, u.is_active " +
            "FROM users u JOIN roles r ON u.role_id = r.id ";

    // ─── Mapper ────────────────────────────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {
        var user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setActive(rs.getBoolean("is_active"));

        Timestamp ll = rs.getTimestamp("last_login");
        if (ll != null) user.setLastLogin(ll.toLocalDateTime());
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) user.setCreatedAt(ca.toLocalDateTime());

        var role = new Role();
        role.setId(rs.getInt("role_id"));
        role.setName(rs.getString("role_name"));
        user.setRole(role);
        return user;
    }

    // ─── findById ──────────────────────────────────────────────────
    @Override
    public Optional<User> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE u.id = ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Optional<User> result = rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.findById failed: " + e.getMessage(), e);
        }
    }

    // ─── findByUsername ────────────────────────────────────────────
    public Optional<User> findByUsername(String username) {
        String sql = SELECT_BASE + "WHERE u.username = ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            Optional<User> result = rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.findByUsername failed: " + e.getMessage(), e);
        }
    }

    // ─── findAll (paginated) ───────────────────────────────────────
    @Override
    public List<User> findAll(int page, int pageSize) {
        var list = new ArrayList<User>();
        String sql = SELECT_BASE + "ORDER BY u.id LIMIT ? OFFSET ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.findAll (paged) failed: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<User> findAll() {
        var list = new ArrayList<User>();
        String sql = SELECT_BASE + "ORDER BY u.username";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.findAll failed: " + e.getMessage(), e);
        }
        return list;
    }

    // ─── save (INSERT) ─────────────────────────────────────────────
    @Override
    public int save(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role_id, is_active) " +
                     "VALUES (?,?,?,?,?,?)";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setInt(5, user.getRole().getId());
            ps.setBoolean(6, user.isActive());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int newId = keys.next() ? keys.getInt(1) : -1;
            keys.close();
            ps.close();
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.save failed: " + e.getMessage(), e);
        }
    }

    // ─── update ────────────────────────────────────────────────────
    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, role_id=?, is_active=? WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getRole().getId());
            ps.setBoolean(4, user.isActive());
            ps.setInt(5, user.getId());
            boolean ok = ps.executeUpdate() > 0;
            ps.close();
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.update failed: " + e.getMessage(), e);
        }
    }

    // ─── updatePassword ────────────────────────────────────────────
    public boolean updatePassword(int userId, String newHash) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET password_hash=? WHERE id=?");
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            boolean ok = ps.executeUpdate() > 0;
            ps.close();
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.updatePassword failed: " + e.getMessage(), e);
        }
    }

    // ─── updateLastLogin ───────────────────────────────────────────
    public void updateLastLogin(int userId) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET last_login=? WHERE id=?");
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, userId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.updateLastLogin failed: " + e.getMessage(), e);
        }
    }

    // ─── delete ────────────────────────────────────────────────────
    @Override
    public boolean delete(Integer id) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?");
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            ps.close();
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.delete failed: " + e.getMessage(), e);
        }
    }

    // ─── count ─────────────────────────────────────────────────────
    @Override
    public long count() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users");
            long c = rs.next() ? rs.getLong(1) : 0;
            rs.close();
            st.close();
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.count failed: " + e.getMessage(), e);
        }
    }
}