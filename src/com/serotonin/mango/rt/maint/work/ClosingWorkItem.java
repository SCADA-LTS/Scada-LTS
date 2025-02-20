package com.serotonin.mango.rt.maint.work;

public class ClosingWorkItem extends AbstractBeforeAfterWorkItem {

    private final AutoCloseable toClose;
    private final String details;

    public ClosingWorkItem(AutoCloseable toClose, String details) {
        this.details = details;
        this.toClose = toClose;
    }

    @Override
    public void work() {
        try {
            toClose.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WorkItemPriority getPriorityType() {
        return WorkItemPriority.LOW;
    }

    @Override
    public String getDetails() {
        return details;
    }
}
