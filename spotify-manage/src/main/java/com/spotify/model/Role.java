package com.spotify.model;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Domain model – Role entity

/**
 * Role entity representing user permission levels.
 * Demonstrates encapsulation with private fields + getters/setters.
 */
public class Role {
    private int id;
    private String name;
    private String description;

    public Role() {}
    public Role(int id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }

    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }
    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }
    public String getDescription()      { return description; }
    public void setDescription(String d){ this.description = d; }

    @Override public String toString()  { return name; }
}