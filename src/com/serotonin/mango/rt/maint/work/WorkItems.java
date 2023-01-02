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

    public WorkItems(int limit) {
        this.items = new ConcurrentHashMap<>();
        this.limit = limit;
        this.counter = new AtomicInteger();
    }

    public void add(WorkItem item) {
        Execute execute = new Execute(item);
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

    public List<Execute> getByExecuted() {
        return get().stream()
                .filter(a -> a.getWorkItem().isExecuted())
                .collect(Collectors.toList());
    }

    public List<Execute> getByNotExecuted() {
        return get().stream()
                .filter(a -> !a.getWorkItem().isExecuted())
                .collect(Collectors.toList());
    }

    public List<Execute> getBySuccess() {
        return get().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().isSuccess())
                .collect(Collectors.toList());
    }

    public List<Execute> getByFail() {
        return get().stream()
                .filter(a -> a.getWorkItem().isExecuted() && !a.getWorkItem().isSuccess())
                .collect(Collectors.toList());
    }

    public List<Execute> getByExecutedLongerThan(long executedMs) {
        return get().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().getExecutedMs() > executedMs)
                .collect(Collectors.toList());
    }

    public List<Execute> getByExecutedLessThan(long executedMs) {
        return get().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().getExecutedMs() < executedMs)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "ExecuteItems{" +
                "items=" + items +
                '}';
    }

    public static class Execute implements Comparable<Execute> {

        private static final AtomicLong counter = new AtomicLong();
        private final String className;
        private final long serial;
        private final WorkItem workItem;

        public Execute(WorkItem workItem) {
            this.className = workItem.getClass().getName();
            this.workItem = workItem;
            this.serial = counter.incrementAndGet();
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
