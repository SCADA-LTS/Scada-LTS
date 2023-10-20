package com.serotonin.mango.rt.maint.work;

import java.util.stream.Stream;

public enum WorkItemPriority {

    HIGH(WorkItem.PRIORITY_HIGH),
    MEDIUM(WorkItem.PRIORITY_MEDIUM),
    LOW(WorkItem.PRIORITY_LOW),
    IDLE(4);

    private final int priority;

    WorkItemPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public static WorkItemPriority priorityOf(int priority) {
        return Stream.of(WorkItemPriority.values())
                .filter(a -> a.getPriority() == priority)
                .findAny()
                .orElse(WorkItemPriority.LOW);
    }
}
