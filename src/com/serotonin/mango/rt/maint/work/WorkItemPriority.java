package com.serotonin.mango.rt.maint.work;

import java.util.stream.Stream;

public enum WorkItemPriority {

    HIGH(WorkItem.PRIORITY_HIGH, "high-priority."),
    MEDIUM(WorkItem.PRIORITY_MEDIUM, "medium-priority."),
    LOW(WorkItem.PRIORITY_LOW, "low-priority.");

    private final int priority;
    private final String name;

    WorkItemPriority(int priority, String name) {
        this.priority = priority;
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public static WorkItemPriority priorityOf(int priority) {
        return Stream.of(WorkItemPriority.values())
                .filter(a -> a.getPriority() == priority)
                .findAny()
                .orElse(WorkItemPriority.LOW);
    }
}
