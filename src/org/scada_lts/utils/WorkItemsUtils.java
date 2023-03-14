package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.AbstractBeforeAfterWorkItem;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.mango.rt.maint.work.WorkItems;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class WorkItemsUtils {

    private WorkItemsUtils() {}

    public static List<WorkItems.Execute> getAll() {
        return Common.ctx.getBackgroundProcessing().getWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getFailed() {
        return AbstractBeforeAfterWorkItem.failedWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getExecutedLonger() {
        return AbstractBeforeAfterWorkItem.executedLongerWorkItems().get().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getRunning() {
        return AbstractBeforeAfterWorkItem.runningWorkItems().get().stream()
                .filter(a -> a.getWorkItem().isRunning())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
    public static List<WorkItems.Execute> getByExecuted() {
        return getAll().stream()
                .filter(a -> a.getWorkItem().isExecuted())
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getByNotExecuted() {
        return getAll().stream()
                .filter(a -> !a.getWorkItem().isExecuted())
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getBySuccess() {
        return getAll().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().isSuccess())
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getByFailed() {
        return getFailed().stream()
                .filter(a -> a.getWorkItem().isExecuted() && !a.getWorkItem().isSuccess())
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getByFailedWork() {
        return getFailed().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().isWorkFailed())
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getByExecutedLongerThan(int executedMs) {
        return getAll().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().getExecutedMs() > executedMs)
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getByExecutedLongerThanHistory(int executedMs) {
        return getExecutedLonger().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().getExecutedMs() > executedMs)
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getByExecutedLessThan(int executedMs) {
        return getAll().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getWorkItem().getExecutedMs() < executedMs)
                .collect(Collectors.toList());
    }

    public static List<WorkItems.Execute> getByPriority(WorkItemPriority priority) {
        return getAll().stream()
                .filter(a -> a.getWorkItem().isExecuted() && a.getPriority() == priority)
                .collect(Collectors.toList());
    }
}
