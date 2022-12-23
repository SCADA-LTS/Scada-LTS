package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.rt.maint.work.WorkItems;

public class WorkItemInfo {
    private final WorkItems.Execute workItem;

    public WorkItemInfo(WorkItems.Execute workItem) {
        this.workItem = workItem;
    }

    public WorkItems.Execute getWorkItemExecute() {
        return workItem;
    }
}
