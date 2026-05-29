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

public class Album {
    private int id;
    private String name;
    private Artist artist;
    private Integer releaseYear;
    private LocalDateTime createdAt;

    public Album() {}
    public Album(int id, String name, Artist artist) {
        this.id = id; this.name = name; this.artist = artist;
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public String getName()                     { return name; }
    public void setName(String n)               { this.name = n; }
    public Artist getArtist()                   { return artist; }
    public void setArtist(Artist a)             { this.artist = a; }
    public Integer getReleaseYear()             { return releaseYear; }
    public void setReleaseYear(Integer y)       { this.releaseYear = y; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime t)   { this.createdAt = t; }

    @Override public String toString()          { return name; }
}