package com.spotify.dao;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: DAOFactory – Factory pattern for creating DAO instances

/**
 * Factory pattern: centralises DAO creation.
 * GoF Pattern: Factory – callers request DAOs through the factory,
 * decoupling instantiation from usage.
 */
public class DAOFactory {

    public enum DBType { MYSQL, H2 }

    private static DBType currentType = DBType.MYSQL;

    public static void setDBType(DBType type) { currentType = type; }

    public static UserDAO getUserDAO() {
        return new UserDAO();      // future: switch on currentType
    }

    public static TrackDAO getTrackDAO() {
        return new TrackDAO();
    }

    public static ArtistDAO getArtistDAO() {
        return new ArtistDAO();
    }
}