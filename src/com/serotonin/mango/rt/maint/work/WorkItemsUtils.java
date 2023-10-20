package com.serotonin.mango.rt.maint.work;

import com.serotonin.mango.Common;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class WorkItemsUtils {

    private WorkItemsUtils() {}

    public static List<WorkItemExecute> getCurrentAll() {
        idle();
        return AbstractBeforeAfterWorkItem.getAllWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getCurrentNotExecuted() {
        return getCurrentAll().stream()
                .filter(a -> !a.getWorkItem().isExecuted())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getCurrentExecuted() {
        return getCurrentAll().stream()
                .filter(a -> a.getWorkItem().isExecuted())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getCurrentExecutedByPriority(WorkItemPriority priority) {
        return getCurrentExecuted().stream()
                .filter(a -> a.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getCurrentExecutedLessThan(int executedMs) {
        return getCurrentExecuted().stream()
                .filter(a -> a.getWorkItem().getExecutedMs() < executedMs)
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getCurrentExecutedSuccess() {
        return getCurrentExecuted().stream()
                .filter(a -> a.getWorkItem().isSuccess())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getCurrentExecutedLongerThan(int executedMs) {
        return getCurrentExecuted().stream()
                .filter(a -> a.getWorkItem().getExecutedMs() > executedMs)
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryExecuted() {
        idle();
        return AbstractBeforeAfterWorkItem.getHistoryExecutedWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryIdle() {;
        return AbstractBeforeAfterWorkItem.getHistoryIdleWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }


    public static List<WorkItemExecute> getRunning() {
        idle();
        return AbstractBeforeAfterWorkItem.getRunningWorkItems().get().stream()
                .filter(a -> a.getWorkItem().isRunning())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryFailed() {
        return AbstractBeforeAfterWorkItem.getHistoryFailedWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryExecutedLonger() {
        return AbstractBeforeAfterWorkItem.getHistoryExecutedLongerWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryByExecutedLongerThan(int executedMs) {
        return getHistoryExecutedLonger().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().getExecutedMs() > executedMs)
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryProcess() {
        return AbstractBeforeAfterWorkItem.getHistoryProcessWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryHighPriority() {
        return AbstractBeforeAfterWorkItem.getHistoryHighPriorityWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryMediumPriority() {
        return AbstractBeforeAfterWorkItem.getHistoryMediumPriorityWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItemExecute> getHistoryLowPriority() {
        return AbstractBeforeAfterWorkItem.getHistoryLowPriorityWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static void idle() {
        Common.ctx.getBackgroundProcessing().addWorkItem(new IdleWorkItem());
    }
}