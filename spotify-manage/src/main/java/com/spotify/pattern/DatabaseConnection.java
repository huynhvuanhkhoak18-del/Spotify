package com.spotify.pattern;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Singleton pattern – single JDBC connection pool entry point

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton DatabaseConnection.
 * GoF Pattern: Singleton – ensures only one connection instance exists
 * throughout the application lifecycle.
 */
public class DatabaseConnection {

    private static volatile DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    // Private constructor – load config from db.properties or fallback defaults
    private DatabaseConnection() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException ignored) {}

        this.url      = props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/spotify_manager?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        this.user     = props.getProperty("db.user", "root");
        this.password = props.getProperty("db.password", "");
    }

    /** Thread-safe double-checked locking singleton accessor */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /** Returns an open Connection, re-connecting if stale */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {}
    }
}