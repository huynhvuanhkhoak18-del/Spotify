package com.spotify.model;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Immutable Track summary DTO for table display

/** Lightweight track summary for table rendering – Java record */
public record TrackDTO(int id, String name, String artist, String genre,
                       int popularity, String duration, boolean explicit) {}