package com.serotonin.mango.rt.maint.work;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class WorkItems {

    private final Map<Integer, Execute> items;
    private final int limit;
    private final AtomicInteger counter;
    private final AtomicLong serial;

    public WorkItems(int limit) {
        this.items = new ConcurrentHashMap<>();
        this.limit = limit;
        this.counter = new AtomicInteger();
        this.serial = new AtomicLong();
    }

    public void add(WorkItem item) {
        if(limit == 0) {
            return;
        }
        Execute execute = new Execute(item, serial.incrementAndGet());
        int index = counter.incrementAndGet();
        if(index >= limit) {
            counter.set(0);
            items.put(0, execute);
        } else {
            items.put(index, execute);
        }
    }

    public List<Execute> get() {
        return items.values().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "ExecuteItems{" +
                "items=" + items +
                '}';
    }

    public static class Execute implements Comparable<Execute> {

        private final String className;
        private final long serial;
        private final WorkItem workItem;
        private final WorkItemPriority priority;

        public Execute(WorkItem workItem, long serial) {
            this.className = workItem.getClass().getName();
            this.workItem = workItem;
            this.serial = serial;
            this.priority = WorkItemPriority.priorityOf(workItem.getPriority());
        }

        public WorkItem getWorkItem() {
            return workItem;
        }

        public long getSerial() {
            return serial;
        }

        public String getClassName() {
            return className;
        }

        public WorkItemPriority getPriority() {
            return priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Execute)) return false;
            Execute execute = (Execute) o;
            return getSerial() == execute.getSerial();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getSerial());
        }

        @Override
        public int compareTo(Execute o) {
            long diff = this.getSerial() - o.getSerial();
            if (diff == 0) {
                return 0;
            }
            return diff < 0 ? -1 : 1;
        }

        @Override
        public String toString() {
            return "Execute{" +
                    "value=" + workItem +
                    ", serial=" + serial +
                    '}';
        }
    }
}
