package com.spotify.service;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: SessionManager – in-memory session cache using HashMap

import com.spotify.model.UserDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Session Manager using HashMap<String, UserDTO> as required by spec §3.1.
 * Generates a UUID session token on login; invalidates on logout.
 */
public class SessionManager {

    /** Session cache: token → UserDTO */
    private static final Map<String, UserDTO> sessions = new HashMap<>();

    /** Currently active session token (single-user desktop app) */
    private static String currentToken;

    /** Create a new session and return token */
    public static String createSession(UserDTO user) {
        var token = UUID.randomUUID().toString();
        sessions.put(token, user);
        currentToken = token;
        return token;
    }

    /** Get current logged-in user (may be null) */
    public static UserDTO getCurrentUser() {
        if (currentToken == null) return null;
        return sessions.get(currentToken);
    }

    /** Invalidate current session */
    public static void logout() {
        if (currentToken != null) {
            sessions.remove(currentToken);
            currentToken = null;
        }
    }

    public static boolean isLoggedIn() {
        return currentToken != null && sessions.containsKey(currentToken);
    }

    public static boolean isAdmin() {
        var u = getCurrentUser();
        return u != null && u.isAdmin();
    }
}