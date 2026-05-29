package com.spotify.model;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Data Transfer Objects using Java 16+ records (immutable)

/**
 * Java Records – Modern Java Feature (Java 16+).
 * Records are immutable DTOs with auto-generated constructor, getters, equals, hashCode, toString.
 */

/** Lightweight user DTO for session cache */
public record UserDTO(int id, String username, String fullName, String role) {
    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
}