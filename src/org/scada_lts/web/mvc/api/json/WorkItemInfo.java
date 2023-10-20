package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.rt.maint.work.WorkItemExecute;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;

public class WorkItemInfo {

    private final WorkItemExecute workItem;
    public WorkItemInfo(WorkItemExecute workItem) {
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
    public long getItemsPerSecondOneSecond() {
        return workItem.getItemsPerSecond();
    }
    public long getItemsPerSecondOneMinute() {
        return workItem.getItemsPerSecondOneMinute();
    }
    public long getItemsPerSecondFiveMinutes() {
        return workItem.getItemsPerSecondFiveMinutes();
    }
    public long getItemsPerSecondFifteenMinutes() {
        return workItem.getItemsPerSecondFifteenMinutes();
    }

}
