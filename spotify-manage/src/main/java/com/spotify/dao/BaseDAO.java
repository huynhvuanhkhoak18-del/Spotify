package com.spotify.dao;

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