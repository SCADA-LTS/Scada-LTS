package com.serotonin.mango.rt.maint.work;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorkItems {

    private final List<WorkItem> items = new CopyOnWriteArrayList<>();
    private volatile int limit;

    public WorkItems(int limit) {
        this.limit = limit;
    }

    public void add(WorkItem workItem) {
        if(items.size() < limit)
            items.add(workItem);
    }

    public void remove(WorkItem workItem) {
        items.remove(workItem);
    }

    public List<WorkItem> get() {
        return items;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        this.items.clear();
    }
}
