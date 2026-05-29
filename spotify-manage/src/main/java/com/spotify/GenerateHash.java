package com.spotify;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Tạo SHA-256 hash cho password, in ra SQL để chạy trong MySQL

import java.security.MessageDigest;

public class GenerateHash {

    public static void main(String[] args) throws Exception {

        // ── Đặt password muốn dùng ở đây ──────────────────────
        String adminPassword  = "admin123";
        String viewerPassword = "viewer123";
        // ───────────────────────────────────────────────────────

        String adminHash  = sha256(adminPassword);
        String viewerHash = sha256(viewerPassword);

        System.out.println("=================================================");
        System.out.println("  COPY SQL dưới vào MySQL Workbench rồi chạy");
        System.out.println("=================================================");
        System.out.println();
        System.out.println("USE spotify_manager;");
        System.out.println("SET SQL_SAFE_UPDATES = 0;");
        System.out.println("DELETE FROM users;");
        System.out.println();
        System.out.println("INSERT INTO users (username, password_hash, full_name, email, role_id, is_active)");
        System.out.println("VALUES");
        System.out.println("(");
        System.out.println("    'admin',");
        System.out.println("    '" + adminHash + "',");
        System.out.println("    'System Administrator',");
        System.out.println("    'admin@spotify.local',");
        System.out.println("    1, 1");
        System.out.println("),");
        System.out.println("(");
        System.out.println("    'viewer',");
        System.out.println("    '" + viewerHash + "',");
        System.out.println("    'Demo Viewer',");
        System.out.println("    'viewer@spotify.local',");
        System.out.println("    2, 1");
        System.out.println(");");
        System.out.println();
        System.out.println("SET SQL_SAFE_UPDATES = 1;");
        System.out.println();
        System.out.println("=================================================");
        System.out.println("  Tài khoản login:");
        System.out.println("  admin  / " + adminPassword);
        System.out.println("  viewer / " + viewerPassword);
        System.out.println("=================================================");
    }

    private static String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}