package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.mango.rt.maint.work.WorkItems;

public class WorkItemInfo {
    private final WorkItems.Execute workItem;

    public WorkItemInfo(WorkItems.Execute workItem) {
        this.workItem = workItem;
    }

    public WorkItem getWorkItem() {
        return workItem.getWorkItem();
    }

    public long getSerial() {
        return workItem.getSerial();
    }

    public String getClassName() {
        return workItem.getClassName();
    }

    public WorkItemPriority getPriority() {
        return workItem.getPriority();
    }
}
