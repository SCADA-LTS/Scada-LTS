package org.scada_lts.web.mvc.api.json;

import java.util.List;

public class WorkItemInfoList {

    private final int size;
    private final List<WorkItemInfo> workItemExecutes;

    public WorkItemInfoList(List<WorkItemInfo> workItemExecutes) {
        this.workItemExecutes = workItemExecutes;
        this.size = workItemExecutes.size();
    }

    public List<WorkItemInfo> getWorkItemExecutes() {
        return workItemExecutes;
    }

    public int getSize() {
        return size;
    }
}
