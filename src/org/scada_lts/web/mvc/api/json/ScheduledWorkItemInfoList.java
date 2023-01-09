package org.scada_lts.web.mvc.api.json;

import java.util.List;

public class ScheduledWorkItemInfoList {
    private final int size;
    private final List<ScheduledWorkItem> workItemScheduled;

    public ScheduledWorkItemInfoList(List<ScheduledWorkItem> workItemScheduled) {
        this.workItemScheduled = workItemScheduled;
        this.size = workItemScheduled.size();
    }

    public List<ScheduledWorkItem> getWorkItemScheduled() {
        return workItemScheduled;
    }

    public int getSize() {
        return size;
    }
}
