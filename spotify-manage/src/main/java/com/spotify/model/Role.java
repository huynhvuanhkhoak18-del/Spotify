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