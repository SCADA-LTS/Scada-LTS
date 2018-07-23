package com.serotonin.mango.vo.hierarchy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PointHierarchyEventDispatcher {
    private final static List<PointHierarchyListener> LISTENERS = new CopyOnWriteArrayList<PointHierarchyListener>();

    public static void addListener(PointHierarchyListener l) {
        LISTENERS.add(l);
    }

    public static void removeListener(PointHierarchyListener l) {
        LISTENERS.remove(l);
    }

    public static void firePointHierarchySaved(PointFolder root) {
        for (PointHierarchyListener l : LISTENERS)
            l.pointHierarchySaved(root);
    }
}
