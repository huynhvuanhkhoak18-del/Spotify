package com.spotify.pattern;

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

/**
 * Observer Pattern (GoF).
 * DataChangePublisher notifies registered DataChangeListener instances
 * whenever the track dataset is modified (insert/update/delete/import).
 * Charts register as listeners and refresh automatically.
 */
public class DataChangePublisher {

    /** Observer interface */
    public interface DataChangeListener {
        void onDataChanged();
    }

    private static final List<DataChangeListener> listeners = new ArrayList<>();

    public static void addListener(DataChangeListener l)    { listeners.add(l); }
    public static void removeListener(DataChangeListener l) { listeners.remove(l); }

    /** Notify all registered listeners (charts, KPI strips, etc.) */
    public static void notifyListeners() {
        listeners.forEach(DataChangeListener::onDataChanged);
    }
}