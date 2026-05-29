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