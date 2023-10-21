package com.serotonin.mango.rt.maint.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.SystemSettingsUtils;
import org.scada_lts.utils.ThreadUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBeforeAfterWorkItem implements WorkItem, BeforeWork,
        AfterWork, AfterWork.WorkSuccessFail, AfterWork.WorkFinally {

    private static final Log LOG = LogFactory.getLog(AbstractBeforeAfterWorkItem.class);
    private static final WorkItems ALL_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getWorkItemsLimit());
    private static final WorkItems HISTORY_EXECUTED_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getHistoryWorkItemsLimit());
    private static final WorkItems HISTORY_IDLE_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getHistoryWorkItemsLimit());
    private static final WorkItems HISTORY_FAILED_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getFailedWorkItemsLimit());
    private static final WorkItems HISTORY_PROCESS_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getHistoryProcessWorkItemsLimit());
    private static final WorkItems HISTORY_HIGH_PRIORITY_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getHistoryHighPriorityWorkItemsLimit());
    private static final WorkItems HISTORY_MEDIUM_PRIORITY_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getHistoryMediumPriorityWorkItemsLimit());
    private static final WorkItems HISTORY_LOW_PRIORITY_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getHistoryLowPriorityWorkItemsLimit());
    private static final WorkItems HISTORY_EXECUTED_LONGER_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getHistoryExecutedLongerWorkItemsLimit());
    private static final WorkItems.RepeatAdd RUNNING_WORK_ITEMS = new WorkItems.RepeatAdd(SystemSettingsUtils.getRunningWorkItemsLimit(), SystemSettingsUtils.getRepeatRunningWorkItems());
    private static final int EXECUTED_LONGER_WORK_ITEMS_THAN = SystemSettingsUtils.getHistoryExecutedLongerWorkItemsThan();
    private volatile boolean success = false;
    private volatile boolean workFailed = false;
    private volatile boolean running = false;
    private volatile String threadName = "";
    private volatile String failedMessage = "";
    private final LocalDateTime createdDate = LocalDateTime.now();
    private volatile LocalDateTime startedDate = null;
    private volatile LocalDateTime executedDate = null;

    public static WorkItems.RepeatAdd getRunningWorkItems() {
        return RUNNING_WORK_ITEMS;
    }
    public static WorkItems getHistoryFailedWorkItems() {
        return HISTORY_FAILED_WORK_ITEMS;
    }
    public static WorkItems getHistoryExecutedLongerWorkItems() {
        return HISTORY_EXECUTED_LONGER_WORK_ITEMS;
    }
    public static WorkItems getHistoryProcessWorkItems() {
        return HISTORY_PROCESS_WORK_ITEMS;
    }
    public static WorkItems getHistoryHighPriorityWorkItems() {
        return HISTORY_HIGH_PRIORITY_WORK_ITEMS;
    }
    public static WorkItems getHistoryMediumPriorityWorkItems() {
        return HISTORY_MEDIUM_PRIORITY_WORK_ITEMS;
    }
    public static WorkItems getHistoryLowPriorityWorkItems() {
        return HISTORY_LOW_PRIORITY_WORK_ITEMS;
    }
    public static WorkItems getAllWorkItems() {
        return ALL_WORK_ITEMS;
    }
    public static WorkItems getHistoryExecutedWorkItems() {
        return HISTORY_EXECUTED_WORK_ITEMS;
    }

    public static WorkItems getHistoryIdleWorkItems() {
        return HISTORY_IDLE_WORK_ITEMS;
    }

    private static void addWorkItemAfterExecuted(WorkItem workItem, boolean failed, long executedMs) {
        if(!SystemSettingsUtils.isWorkItemsReportingEnabled())
            return;
        if(failed)
            HISTORY_FAILED_WORK_ITEMS.add(workItem);
        if(executedMs > EXECUTED_LONGER_WORK_ITEMS_THAN)
            HISTORY_EXECUTED_LONGER_WORK_ITEMS.add(workItem);
        if(workItem instanceof ProcessWorkItem || workItem instanceof ProcessWorkItem.InputReader
                || workItem instanceof ProcessWorkItem.ProcessTimeout)
            HISTORY_PROCESS_WORK_ITEMS.add(workItem);
        switch (WorkItemPriority.priorityOf(workItem.getPriority())) {
            case HIGH:
                HISTORY_HIGH_PRIORITY_WORK_ITEMS.add(workItem);
                break;
            case MEDIUM:
                HISTORY_MEDIUM_PRIORITY_WORK_ITEMS.add(workItem);
                break;
            case LOW:
                HISTORY_LOW_PRIORITY_WORK_ITEMS.add(workItem);
                break;
            case IDLE:
                HISTORY_IDLE_WORK_ITEMS.add(workItem);
                break;
        }
        HISTORY_EXECUTED_WORK_ITEMS.add(workItem);
    }
    private static void addWorkItemIfNotRunning(WorkItem workItem, boolean running) {
        if(!SystemSettingsUtils.isWorkItemsReportingEnabled())
            return;
        if(running) {
            LOG.warn("Work items is running! : " + workItem);
        } else {
            RUNNING_WORK_ITEMS.add(workItem, WorkItemMetrics::isRunning);
        }
    }

    protected AbstractBeforeAfterWorkItem() {
        if(SystemSettingsUtils.isWorkItemsReportingEnabled())
            ALL_WORK_ITEMS.add(this);
    }

    @Override
    public final void execute() {
        boolean runningNow = this.running;
        this.running = true;
        this.threadName = Thread.currentThread().getName();
        String suffix = suffixThreadName();
        if(!StringUtils.isEmpty(suffix))
            Thread.currentThread().setName(this.threadName + suffix);
        this.startedDate = LocalDateTime.now();
        addWorkItemIfNotRunning(this, runningNow);
        this.workFailed = false;
        this.executedDate = null;
        this.success = false;
        this.failedMessage = "";
        boolean failed = false;
        String msg = "";
        Map<String, Throwable> exceptions = new HashMap<>();
        try {
            try {
                beforeWork();
            } catch (Throwable beforeWorkException) {
                this.workFailed = true;
                msg = msg + "beforeWorkException: " +  beforeWorkException.getMessage() + "; ";
                failed = true;
                exceptions.put("beforeWork", beforeWorkException);
                try {
                    beforeWorkFail(beforeWorkException);
                } catch (Exception beforeWorkFailException) {
                    LOG.error(beforeWorkFailException.getMessage(), beforeWorkFailException);
                    exceptions.put("beforeWorkFail", beforeWorkFailException);
                }
                return;
            }
            try {
                work();
            } catch (Throwable workException) {
                this.workFailed = true;
                msg = msg + "workException: " +  workException.getMessage() + "; ";
                failed = true;
                exceptions.put("work", workException);
                try {
                    workFail(workException);
                } catch (Throwable workFailException) {
                    LOG.error(workFailException.getMessage(), workFailException);
                    exceptions.put("workFail", workFailException);
                }
                return;
            }
            try {
                workSuccess();
            } catch (Throwable workSuccessException) {
                msg = msg + "workSuccessException: " +  workSuccessException.getMessage() + "; ";
                failed = true;
                exceptions.put("workSuccess", workSuccessException);
                try {
                    workSuccessFail(workSuccessException);
                } catch (Throwable workSuccessFailException) {
                    LOG.error(workSuccessFailException.getMessage(), workSuccessFailException);
                    exceptions.put("workSuccessFail", workSuccessFailException);
                }
                return;
            }
        } finally {
            try {
                workFinally(exceptions);
            } catch (Throwable workFinallyException) {
                msg = msg + "workFinallyException: " +  workFinallyException.getMessage() + "; ";
                failed = true;
                exceptions.put("workFinally", workFinallyException);
                try {
                    workFinallyFail(workFinallyException, exceptions);
                } catch (Throwable workFinallyFailException) {
                    LOG.error(workFinallyFailException.getMessage(), workFinallyFailException);
                    exceptions.put("workFinallyFail", workFinallyFailException);
                }
            }
            this.failedMessage = msg;
            this.success = !failed;
            this.executedDate = LocalDateTime.now();
            addWorkItemAfterExecuted(this, failed, getExecutedMs());
            if(!StringUtils.isEmpty(suffix))
                Thread.currentThread().setName(this.threadName);
            this.running = false;
        }
    }

    @Override
    public void beforeWork() {}

    @Override
    public void beforeWorkFail(Throwable exception) {
        LOG.error(exception.getMessage() + " - " + this, exception);
    }

    public abstract void work();

    @Override
    public void workFail(Throwable exception) {
        LOG.error(exception.getMessage() + " - " + this, exception);
    }

    @Override
    public void workSuccessFail(Throwable exception) {
        LOG.error(exception.getMessage() + " - " + this, exception);
    }

    @Override
    public void workFinally(Map<String, Throwable> exceptions) {
        if(!exceptions.isEmpty())
            LOG.error(exceptionsToString(exceptions) + " - " + this, exceptions.entrySet().iterator().next().getValue());
    }

    @Override
    public void workFinallyFail(Throwable finallyException, Map<String, Throwable> exceptions) {
        LOG.error(exceptionsToString(exceptions) + " - " + this, finallyException);
    }

    @Override
    public boolean isExecuted() {
        return executedDate != null;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public boolean isWorkFailed() {
        return workFailed;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long getTimeInitMs() {
        LocalDateTime started = this.startedDate;
        if(started == null)
            return -1;
        try {
            return ChronoUnit.MILLIS.between(this.createdDate, started);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage() + " - " + this, ex);
            return -1;
        }
    }

    @Override
    public long getTimeInitNanos() {
        LocalDateTime started = this.startedDate;
        if(started == null)
            return -1;
        try {
            return ChronoUnit.NANOS.between(this.createdDate, started);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage() + " - " + this, ex);
            return -1;
        }
    }

    @Override
    public long getExecutedMs() {
        LocalDateTime started = this.startedDate;
        LocalDateTime executed = this.executedDate;
        if(started == null || executed == null)
            return -1;
        try {
            return ChronoUnit.MILLIS.between(started, executed);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage() + " - " + this, ex);
            return -1;
        }
    }

    @Override
    public long getExecutedNanos() {
        LocalDateTime started = this.startedDate;
        LocalDateTime executed = this.executedDate;
        if(started == null || executed == null)
            return -1;
        try {
            return ChronoUnit.NANOS.between(started, executed);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage() + " - " + this, ex);
            return -1;
        }
    }

    @Override
    public String getFailedMessage() {
        return failedMessage;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public LocalDateTime getExecutedDate() {
        return executedDate;
    }

    @Override
    public LocalDateTime getStartedDate() {
        return startedDate;
    }

    private String suffixThreadName() {
        return ThreadUtils.reduceName(" - " + WorkItemPriority.priorityOf(getPriority()) + " - " + getDetails());
    }

    private static String exceptionsToString(Map<String, Throwable> exceptions) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Throwable> entry: exceptions.entrySet()) {
            sb.append(entry.getValue().getClass()).append(": ").append(entry.getValue().getMessage()).append("\n\r");
        }
        return sb.toString();
    }
}
