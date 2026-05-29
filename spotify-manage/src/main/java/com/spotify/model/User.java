package com.spotify.model;

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

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private boolean active;

    public User() {}

    // ─── Getters / Setters ───────────────────────────────────────
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getUsername()                 { return username; }
    public void setUsername(String u)           { this.username = u; }
    public String getPasswordHash()             { return passwordHash; }
    public void setPasswordHash(String h)       { this.passwordHash = h; }
    public String getFullName()                 { return fullName; }
    public void setFullName(String n)           { this.fullName = n; }
    public String getEmail()                    { return email; }
    public void setEmail(String e)              { this.email = e; }
    public Role getRole()                       { return role; }
    public void setRole(Role r)                 { this.role = r; }
    public LocalDateTime getLastLogin()         { return lastLogin; }
    public void setLastLogin(LocalDateTime t)   { this.lastLogin = t; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime t)   { this.createdAt = t; }
    public boolean isActive()                   { return active; }
    public void setActive(boolean a)            { this.active = a; }

    public boolean isAdmin() {
        return role != null && "ADMIN".equalsIgnoreCase(role.getName());
    }

    @Override public String toString() { return username + " [" + (role != null ? role.getName() : "?") + "]"; }
}