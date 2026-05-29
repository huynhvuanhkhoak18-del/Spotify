package com.spotify.pattern;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: Observer pattern – chart auto-refresh when data changes

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