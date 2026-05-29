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