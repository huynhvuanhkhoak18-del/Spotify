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


public class Track {
    private int id;
    private String trackId;        // Spotify track_id
    private String trackName;
    private Artist artist;
    private Album album;
    private int popularity;        // 0-100
    private int durationMs;
    private boolean explicit;
    private double danceability;
    private double energy;
    private int trackKey;
    private double loudness;
    private int mode;              // 0=minor, 1=major
    private double speechiness;
    private double acousticness;
    private double instrumentalness;
    private double liveness;
    private double valence;
    private double tempo;
    private int timeSignature;
    private String trackGenre;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Track() {}

    // ─── Utility helpers ────────────────────────────────────────────
    /** Duration formatted as mm:ss */
    public String getDurationFormatted() {
        int totalSeconds = durationMs / 1000;
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    public String getModeLabel() { return mode == 1 ? "Major" : "Minor"; }

    // ─── Getters / Setters ──────────────────────────────────────────
    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }
    public String getTrackId()                      { return trackId; }
    public void setTrackId(String s)                { this.trackId = s; }
    public String getTrackName()                    { return trackName; }
    public void setTrackName(String s)              { this.trackName = s; }
    public Artist getArtist()                       { return artist; }
    public void setArtist(Artist a)                 { this.artist = a; }
    public Album getAlbum()                         { return album; }
    public void setAlbum(Album a)                   { this.album = a; }
    public int getPopularity()                      { return popularity; }
    public void setPopularity(int v)                { this.popularity = v; }
    public int getDurationMs()                      { return durationMs; }
    public void setDurationMs(int v)                { this.durationMs = v; }
    public boolean isExplicit()                     { return explicit; }
    public void setExplicit(boolean v)              { this.explicit = v; }
    public double getDanceability()                 { return danceability; }
    public void setDanceability(double v)           { this.danceability = v; }
    public double getEnergy()                       { return energy; }
    public void setEnergy(double v)                 { this.energy = v; }
    public int getTrackKey()                        { return trackKey; }
    public void setTrackKey(int v)                  { this.trackKey = v; }
    public double getLoudness()                     { return loudness; }
    public void setLoudness(double v)               { this.loudness = v; }
    public int getMode()                            { return mode; }
    public void setMode(int v)                      { this.mode = v; }
    public double getSpeechiness()                  { return speechiness; }
    public void setSpeechiness(double v)            { this.speechiness = v; }
    public double getAcousticness()                 { return acousticness; }
    public void setAcousticness(double v)           { this.acousticness = v; }
    public double getInstrumentalness()             { return instrumentalness; }
    public void setInstrumentalness(double v)       { this.instrumentalness = v; }
    public double getLiveness()                     { return liveness; }
    public void setLiveness(double v)               { this.liveness = v; }
    public double getValence()                      { return valence; }
    public void setValence(double v)                { this.valence = v; }
    public double getTempo()                        { return tempo; }
    public void setTempo(double v)                  { this.tempo = v; }
    public int getTimeSignature()                   { return timeSignature; }
    public void setTimeSignature(int v)             { this.timeSignature = v; }
    public String getTrackGenre()                   { return trackGenre; }
    public void setTrackGenre(String v)             { this.trackGenre = v; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime t)       { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)       { this.updatedAt = t; }

    @Override
    public String toString() {
        return trackName + " – " + (artist != null ? artist.getName() : "Unknown");
    }
}