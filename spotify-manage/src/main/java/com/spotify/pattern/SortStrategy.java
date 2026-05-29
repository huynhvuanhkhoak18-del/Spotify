package com.spotify.pattern;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Strategy pattern – pluggable sort algorithms for Track lists

import com.spotify.model.Track;

import java.util.Comparator;
import java.util.List;

/**
 * Strategy Pattern (GoF).
 * SortStrategy interface defines the contract; concrete strategies implement it.
 * Client code calls sort() without knowing the algorithm used.
 */
public interface SortStrategy {
    void sort(List<Track> tracks);

    // ─── Concrete strategies ──────────────────────────────────────────────

    /** Sort by popularity descending */
    class ByPopularityDesc implements SortStrategy {
        @Override public void sort(List<Track> tracks) {
            tracks.sort(Comparator.comparingInt(Track::getPopularity).reversed());
        }
    }

    /** Sort by popularity ascending */
    class ByPopularityAsc implements SortStrategy {
        @Override public void sort(List<Track> tracks) {
            tracks.sort(Comparator.comparingInt(Track::getPopularity));
        }
    }

    /** Sort by track name A→Z */
    class ByNameAsc implements SortStrategy {
        @Override public void sort(List<Track> tracks) {
            tracks.sort(Comparator.comparing(Track::getTrackName, String.CASE_INSENSITIVE_ORDER));
        }
    }

    /** Sort by artist name A→Z */
    class ByArtistAsc implements SortStrategy {
        @Override public void sort(List<Track> tracks) {
            tracks.sort(Comparator.comparing(
                    t -> t.getArtist() != null ? t.getArtist().getName() : "",
                    String.CASE_INSENSITIVE_ORDER));
        }
    }

    /** Sort by tempo ascending */
    class ByTempoAsc implements SortStrategy {
        @Override public void sort(List<Track> tracks) {
            tracks.sort(Comparator.comparingDouble(Track::getTempo));
        }
    }

    /** Sort by energy descending */
    class ByEnergyDesc implements SortStrategy {
        @Override public void sort(List<Track> tracks) {
            tracks.sort(Comparator.comparingDouble(Track::getEnergy).reversed());
        }
    }

    /** Factory method: return strategy by column name using enhanced switch */
    static SortStrategy of(String column, boolean ascending) {
        return switch (column.toLowerCase()) {
            case "popularity" -> ascending ? new ByPopularityAsc() : new ByPopularityDesc();
            case "name"       -> new ByNameAsc();
            case "artist"     -> new ByArtistAsc();
            case "tempo"      -> new ByTempoAsc();
            case "energy"     -> new ByEnergyDesc();
            default           -> new ByPopularityDesc();
        };
    }
}