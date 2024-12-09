package com.serotonin.mango.rt.maint.work;

import java.io.Closeable;
import java.util.concurrent.ExecutionException;

public class ClosingWorkItem extends AbstractBeforeAfterWorkItem {

    private final AutoCloseable untilClosing;
    private final String details;

    public ClosingWorkItem(AutoCloseable untilClosing, String details) {
        this.details = details;
        this.untilClosing = untilClosing;
    }

    @Override
    public void work() {
        try {
            untilClosing.close();
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
