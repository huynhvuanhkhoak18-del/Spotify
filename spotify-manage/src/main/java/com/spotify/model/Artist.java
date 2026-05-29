package com.spotify.model;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Artist domain model

import java.time.LocalDateTime;

public class Artist {
    private int id;
    private String name;
    private LocalDateTime createdAt;

    public Artist() {}
    public Artist(int id, String name) { this.id = id; this.name = name; }

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }
    public String getName()                 { return name; }
    public void setName(String n)           { this.name = n; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public void setCreatedAt(LocalDateTime t){ this.createdAt = t; }

    @Override public String toString()      { return name; }
}