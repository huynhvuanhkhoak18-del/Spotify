package com.spotify.model;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Playlist domain model

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int id;
    private String name;
    private User owner;
    private List<Track> tracks = new ArrayList<>();
    private LocalDateTime createdAt;

    public Playlist() {}
    public Playlist(int id, String name, User owner) {
        this.id = id; this.name = name; this.owner = owner;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getName()                     { return name; }
    public void setName(String n)               { this.name = n; }
    public User getOwner()                      { return owner; }
    public void setOwner(User u)                { this.owner = u; }
    public List<Track> getTracks()              { return tracks; }
    public void setTracks(List<Track> t)        { this.tracks = t; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime t)   { this.createdAt = t; }

    @Override public String toString()          { return name; }
}