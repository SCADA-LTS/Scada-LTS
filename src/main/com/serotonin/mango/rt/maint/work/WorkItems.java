package com.serotonin.mango.rt.maint.work;

import org.scada_lts.quartz.ItemsPerSecond;
import org.scada_lts.quartz.ReadItemsPerSecond;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class WorkItems implements ReadWorkItems {

    private final Map<Integer, WorkItemExecute> items;
    private final int limit;
    private final AtomicInteger counter;
    private final AtomicLong serial;
    private final ItemsPerSecond itemsPerSecond;


    public WorkItems(int limit) {
        this.items = new ConcurrentHashMap<>(limit);
        this.limit = limit;
        this.counter = new AtomicInteger();
        this.serial = new AtomicLong();
        this.itemsPerSecond = ItemsPerSecond.fromFifteenMinutes();
    }

    public WorkItems(int limit, ItemsPerSecond itemsPerSecond) {
        this.items = new ConcurrentHashMap<>(limit);
        this.limit = limit;
        this.counter = new AtomicInteger();
        this.serial = new AtomicLong();
        this.itemsPerSecond = itemsPerSecond;
    }

    public void add(WorkItem item) {
        if (limit == 0) {
            return;
        }
        itemsPerSecond.increment();
        WorkItemExecute execute = new WorkItemExecute(item, serial.incrementAndGet());
        int index = counter.incrementAndGet();
        if (index >= limit) {
            counter.set(0);
            items.put(0, execute);
        } else {
            items.put(index, execute);
        }
    }

    @Override
    public List<WorkItemExecute> get() {
        return new ArrayList<>(items.values());
    }

    @Override
    public ReadItemsPerSecond getItemsPerSecond() {
        return itemsPerSecond;
    }

    @Override
    public String toString() {
        return "ExecuteItems{" +
                "items=" + items +
                '}';
    }

    static class RepeatAdd implements ReadWorkItems {

        private final Map<Integer, WorkItemExecute> items;
        private final int limit;
        private final int safe;
        private final AtomicInteger counter;
        private final AtomicLong serial;
        private final ItemsPerSecond itemsPerSecond;

        public RepeatAdd(int limit, int safe) {
            this.items = new ConcurrentHashMap<>(limit);
            this.limit = limit;
            this.counter = new AtomicInteger();
            this.serial = new AtomicLong();
            this.safe = safe;
            this.itemsPerSecond = ItemsPerSecond.fromFifteenMinutes();
        }

        public void add(WorkItem item, Predicate<WorkItem> repeatAddIf) {
            if(limit == 0) {
                return;
            }
            add(item, repeatAddIf, safe, -1);
        }

        private void add(WorkItem item, Predicate<WorkItem> repeatAddIf, int safe, long identifier) {
            if(safe < 0 || !repeatAddIf.test(item)) {
                return;
            }
            itemsPerSecond.increment();
            WorkItemExecute execute = new WorkItemExecute(item, identifier == -1 ? serial.incrementAndGet() : identifier);
            int index = counter.incrementAndGet();
            if(index >= limit) {
                counter.set(0);
                WorkItemExecute executeOld = items.put(0, execute);
                repeatAddIf(executeOld, repeatAddIf, safe);
            } else {
                WorkItemExecute executeOld = items.put(index, execute);
                repeatAddIf(executeOld, repeatAddIf, safe);
            }
        }

        private void repeatAddIf(WorkItemExecute execute, Predicate<WorkItem> repeatAddIf, int safe) {
            if (execute != null) {
                WorkItem workItem = execute.getWorkItem();
                if (workItem != null && repeatAddIf.test(workItem)) {
                    add(workItem, repeatAddIf, --safe, execute.getSerial());
                }
            }
        }

        @Override
        public List<WorkItemExecute> get() {
            return new ArrayList<>(items.values());
        }

        @Override
        public ReadItemsPerSecond getItemsPerSecond() {
            return itemsPerSecond;
        }

        @Override
        public String toString() {
            return "ExecuteItems{" +
                    "items=" + items +
                    '}';
        }
    }
}
