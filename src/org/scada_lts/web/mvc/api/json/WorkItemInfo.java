package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.rt.maint.work.WorkItem;

public class WorkItemInfo {

    private int priority;
    private String className;

    public WorkItemInfo(WorkItem workItem) {
        this.priority = workItem.getPriority();
        this.className = workItem.getClass().getName();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
