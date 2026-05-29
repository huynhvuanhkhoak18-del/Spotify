package com.spotify.dao;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Abstract DAO base class – shared DB connection logic

import com.spotify.pattern.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract base DAO.
 * OOP: Abstract class providing shared connection logic to all DAO subclasses.
 * Subclasses must implement GenericDAO<T, ID>.
 */
public abstract class BaseDAO {

    /** Retrieve active JDBC connection from singleton */
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}