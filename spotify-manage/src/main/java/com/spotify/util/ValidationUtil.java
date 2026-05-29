package com.spotify.util;

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
import java.util.ArrayList;
import java.util.List;


public final class ValidationUtil {

    private ValidationUtil() {}

    public record ValidationResult(boolean valid, List<String> errors) {
        public static ValidationResult ok() { return new ValidationResult(true, List.of()); }
        public static ValidationResult fail(List<String> errors) {
            return new ValidationResult(false, errors);
        }
        public String errorMessage() { return String.join("\n", errors); }
    }

    /** Validate track form input */
    public static ValidationResult validateTrack(String name, String artistName,
                                                  String popularity, String durationMs,
                                                  String tempo) {
        var errors = new ArrayList<String>();

        if (name == null || name.isBlank())        errors.add("Track name cannot be empty.");
        if (artistName == null || artistName.isBlank()) errors.add("Artist name cannot be empty.");

        // Java enhanced switch expression
        errors.addAll(validateIntRange(popularity, "Popularity", 0, 100));
        errors.addAll(validateIntRange(durationMs, "Duration (ms)", 1000, 36_000_000));

        if (tempo != null && !tempo.isBlank()) {
            try {
                double t = Double.parseDouble(tempo);
                if (t < 0 || t > 300) errors.add("Tempo must be 0–300 BPM.");
            } catch (NumberFormatException e) {
                errors.add("Tempo must be a number.");
            }
        }

        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    private static List<String> validateIntRange(String val, String field, int min, int max) {
        var errs = new ArrayList<String>();
        if (val == null || val.isBlank()) { errs.add(field + " cannot be empty."); return errs; }
        try {
            int v = Integer.parseInt(val.trim());
            if (v < min || v > max) errs.add(field + " must be " + min + "–" + max + ".");
        } catch (NumberFormatException e) {
            errs.add(field + " must be an integer.");
        }
        return errs;
    }

    /** Validate user form input */
    public static ValidationResult validateUser(String username, String fullName, String email) {
        var errors = new ArrayList<String>();
        if (username == null || username.isBlank()) errors.add("Username cannot be empty.");
        if (fullName == null || fullName.isBlank())  errors.add("Full name cannot be empty.");
        if (email != null && !email.isBlank() && !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$"))
            errors.add("Email format is invalid.");
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }
}