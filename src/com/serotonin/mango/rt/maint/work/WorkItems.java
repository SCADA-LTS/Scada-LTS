package com.serotonin.mango.rt.maint.work;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorkItems {

    private final List<WorkItem> items = new CopyOnWriteArrayList<>();

    public void add(WorkItem workItem) {
        if(items.size() < 1000)
            items.add(workItem);
    }

    public void remove(WorkItem workItem) {
        items.remove(workItem);
    }

    public List<WorkItem> get() {
        return items;
    }
}
