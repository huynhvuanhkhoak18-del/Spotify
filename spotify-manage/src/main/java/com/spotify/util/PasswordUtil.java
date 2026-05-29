package com.spotify.util;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Password hashing utility using SHA-256 (no external dependency)

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
public final class PasswordUtil {

    private PasswordUtil() {}

    /** Hash password bằng SHA-256, trả về hex string */
    public static String hash(String plaintext) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plaintext.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /** So sánh plaintext với stored hash */
    public static boolean verify(String plaintext, String storedHash) {
        if (plaintext == null || storedHash == null) return false;
        return hash(plaintext).equals(storedHash);
    }

    /** Kiểm tra độ mạnh: tối thiểu 6 ký tự */
    public static boolean isStrong(String password) {
        return password != null && password.length() >= 6;
    }
}