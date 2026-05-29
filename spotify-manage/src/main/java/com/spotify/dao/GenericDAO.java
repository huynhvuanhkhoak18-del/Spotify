package com.spotify.dao;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Generic DAO interface – abstraction for all data access objects

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface.
 * OOP: Interface defining contract for all DAO implementations.
 * Generics: T = entity type, ID = primary key type.
 * GoF: Repository pattern – separates business logic from persistence SQL.
 */
public interface GenericDAO<T, ID> {

    /** Find by primary key; returns Optional to handle missing records safely */
    Optional<T> findById(ID id);

    /** Find all records with pagination to avoid loading millions of rows */
    List<T> findAll(int page, int pageSize);

    /** Find all records without pagination (small tables) */
    List<T> findAll();

    /** Insert new record; returns generated ID */
    int save(T entity);

    /** Update existing record */
    boolean update(T entity);

    /** Delete by primary key */
    boolean delete(ID id);

    /** Count total records (used for pagination) */
    long count();
}