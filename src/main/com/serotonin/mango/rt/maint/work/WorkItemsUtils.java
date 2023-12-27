package com.serotonin.mango.rt.maint.work;

public final class WorkItemsUtils {

    private WorkItemsUtils() {}

    public static ReadWorkItems getCurrentAll() {
        return AbstractBeforeAfterWorkItem.getAllPriorityWorkItems();
    }

    public static ReadWorkItems getCurrentHighPriority() {
        return AbstractBeforeAfterWorkItem.getHighWorkItems();
    }

    public static ReadWorkItems getCurrentMediumPriority() {
        return AbstractBeforeAfterWorkItem.getMediumWorkItems();
    }

    public static ReadWorkItems getCurrentLowPriority() {
        return AbstractBeforeAfterWorkItem.getLowWorkItems();
    }

    public static ReadWorkItems getHistoryExecuted() {
        return AbstractBeforeAfterWorkItem.getHistoryExecutedWorkItems();
    }

    public static ReadWorkItems getRunning() {
        return AbstractBeforeAfterWorkItem.getRunningWorkItems();
    }

    public static ReadWorkItems getHistoryFailed() {
        return AbstractBeforeAfterWorkItem.getHistoryFailedWorkItems();
    }

    public static ReadWorkItems getHistoryExecutedLonger() {
        return AbstractBeforeAfterWorkItem.getHistoryExecutedLongerWorkItems();
    }

    public static ReadWorkItems getHistoryProcess() {
        return AbstractBeforeAfterWorkItem.getHistoryProcessWorkItems();
    }

    public static ReadWorkItems getHistoryHighPriority() {
        return AbstractBeforeAfterWorkItem.getHistoryHighPriorityWorkItems();
    }

    public static ReadWorkItems getHistoryMediumPriority() {
        return AbstractBeforeAfterWorkItem.getHistoryMediumPriorityWorkItems();
    }

    public static ReadWorkItems getHistoryLowPriority() {
        return AbstractBeforeAfterWorkItem.getHistoryLowPriorityWorkItems();
    }

}