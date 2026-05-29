package com.spotify.service;
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
import com.spotify.model.User;
import com.spotify.model.UserDTO;
import com.spotify.util.PasswordUtil;

import java.util.Optional;

/**
 * Authentication service.
 * Validates credentials against SHA-256 hashed passwords in DB.
 */
public class AuthService {

    public Optional<UserDTO> login(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null) {
            return Optional.empty();
        }

        var userDAO = DAOFactory.getUserDAO();

        Optional<User> opt;
        try {
            opt = userDAO.findByUsername(username.trim());
        } catch (Exception e) {
            throw new RuntimeException("Login error: " + e.getMessage(), e);
        }

        if (opt.isEmpty()) return Optional.empty();

        User user = opt.get();
        if (!user.isActive()) return Optional.empty();

        // Verify SHA-256 hash
        if (!PasswordUtil.verify(rawPassword, user.getPasswordHash())) {
            return Optional.empty();
        }

        // Update last login
        try {
            userDAO.updateLastLogin(user.getId());
        } catch (Exception ignored) {}

        var dto = new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getFullName() != null ? user.getFullName() : user.getUsername(),
            user.getRole() != null ? user.getRole().getName() : "VIEWER"
        );
        SessionManager.createSession(dto);
        return Optional.of(dto);
    }

    public void logout() {
        SessionManager.logout();
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        var userDAO = DAOFactory.getUserDAO();
        var opt = userDAO.findById(userId);
        if (opt.isEmpty()) return false;
        var user = opt.get();
        if (!PasswordUtil.verify(oldPassword, user.getPasswordHash())) return false;
        return userDAO.updatePassword(userId, PasswordUtil.hash(newPassword));
    }
}