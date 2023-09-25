package org.scada_lts.web.mvc.api.json;

import java.util.List;

public class ScheduledWorkItemInfoList<T> {
    private final int size;
    private final List<T> workItemScheduled;

    public ScheduledWorkItemInfoList(List<T> workItemScheduled) {
        this.workItemScheduled = workItemScheduled;
        this.size = workItemScheduled.size();
    }

    public List<T> getWorkItemScheduled() {
        return workItemScheduled;
    }

    public int getSize() {
        return size;
    }
}
