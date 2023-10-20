package com.serotonin.mango.rt.maint.work;

import org.scada_lts.quartz.ItemsPerSecond;

import java.util.Objects;

public class WorkItemExecute implements Comparable<WorkItemExecute> {

    private final String className;
    private final long serial;
    private final WorkItem workItem;
    private final WorkItemPriority priority;
    private final ItemsPerSecond itemsPerSecond;

    public WorkItemExecute(WorkItem workItem, long serial, ItemsPerSecond itemsPerSecond) {
        this.itemsPerSecond = itemsPerSecond;
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

    public long getItemsPerSecond() {
        return itemsPerSecond.itemsPerSecond();
    }

    public long getItemsPerSecondOneMinute() {
        return itemsPerSecond.itemsPerSecondFromOneMinute();
    }

    public long getItemsPerSecondFiveMinutes() {
        return itemsPerSecond.itemsPerSecondFromFiveMinutes();
    }

    public long getItemsPerSecondFifteenMinutes() {
        return itemsPerSecond.itemsPerSecondFromFifteenMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkItemExecute)) return false;
        WorkItemExecute execute = (WorkItemExecute) o;
        return getSerial() == execute.getSerial();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSerial());
    }

    @Override
    public int compareTo(WorkItemExecute o) {
        return Long.compare(this.serial, o.serial);
    }

    @Override
    public String toString() {
        return "Execute{" +
                "value=" + workItem +
                ", serial=" + serial +
                '}';
    }
}
