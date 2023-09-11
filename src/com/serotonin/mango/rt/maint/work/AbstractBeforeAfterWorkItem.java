package com.serotonin.mango.rt.maint.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.SystemSettingsUtils;
import org.scada_lts.utils.ThreadUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBeforeAfterWorkItem implements WorkItem, BeforeWork,
        AfterWork, AfterWork.WorkSuccessFail, AfterWork.WorkFinally {

    private static final Log LOG = LogFactory.getLog(AbstractBeforeAfterWorkItem.class);
    private static final WorkItems FAILED_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getFailedWorkItemsLimit());
    private static final WorkItems RUNNING_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getRunningWorkItemsLimit(), SystemSettingsUtils.getRepeatRunningWorkItems());
    private static final WorkItems EXECUTED_LONGER_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getHistoryExecutedLongerWorkItemsLimit());
    private static final int EXECUTED_LONGER_WORK_ITEMS_THAN = SystemSettingsUtils.getHistoryExecutedLongerWorkItemsThan();
    private volatile boolean executed = false;
    private volatile boolean success = false;
    private volatile boolean workFailed = false;
    private volatile boolean running = false;
    private volatile int executedMs = -1;
    private volatile String threadName = "";
    private volatile String failedMessage = "";
    private volatile String workFailedMessage = "";
    private final String suffixThreadName = ThreadUtils.reduceName(" - " + WorkItemPriority.priorityOf(getPriority()) + " - " + this.getDetails());

    public static WorkItems failedWorkItems() {
        return FAILED_WORK_ITEMS;
    }
    public static WorkItems runningWorkItems() {
        return RUNNING_WORK_ITEMS;
    }
    public static WorkItems executedLongerWorkItems() {
        return EXECUTED_LONGER_WORK_ITEMS;
    }

    private static void addWorkItemAfterExecuted(WorkItem workItem, boolean failed, int executedMs) {
        if(failed)
            FAILED_WORK_ITEMS.add(workItem);
        if(executedMs > EXECUTED_LONGER_WORK_ITEMS_THAN)
            EXECUTED_LONGER_WORK_ITEMS.add(workItem);
    }
    private static void addWorkItemIfNotRunning(WorkItem workItem, boolean running) {
        if(running) {
            LOG.warn("Work items is running! : " + workItem);
        } else {
            RUNNING_WORK_ITEMS.add(workItem, WorkItemMetrics::isRunning);
        }
    }

    @Override
    public final void execute() {
        long startMs = System.currentTimeMillis();
        this.threadName = Thread.currentThread().getName();
        if(!StringUtils.isEmpty(this.suffixThreadName))
            Thread.currentThread().setName(this.threadName + this.suffixThreadName);
        boolean runningNow = this.running;
        this.running = true;
        this.executedMs = -1;
        this.workFailed = false;
        this.executed = false;
        this.success = false;
        this.failedMessage = "";
        this.workFailedMessage = "";
        addWorkItemIfNotRunning(this, runningNow);
        boolean failed = false;
        boolean workFailedTemp = false;
        String msg = "";
        Map<String, Exception> exceptions = new HashMap<>();
        try {
            try {
                beforeWork();
            } catch (Exception beforeWorkException) {
                workFailedTemp = true;
                failed = true;
                msg = beforeWorkException.getMessage();
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
            } catch (Exception workException) {
                workFailedTemp = true;
                failed = true;
                msg = workException.getMessage();
                exceptions.put("work", workException);
                try {
                    workFail(workException);
                } catch (Exception workFailException) {
                    LOG.error(workFailException.getMessage(), workFailException);
                    exceptions.put("workFail", workFailException);
                }
                return;
            }
            try {
                workSuccess();
            } catch (Exception workSuccessException) {
                failed = true;
                msg = workSuccessException.getMessage();
                exceptions.put("workSuccess", workSuccessException);
                try {
                    workSuccessFail(workSuccessException);
                } catch (Exception workSuccessFailException) {
                    LOG.error(workSuccessFailException.getMessage(), workSuccessFailException);
                    exceptions.put("workSuccessFail", workSuccessFailException);
                }
                return;
            }
        } finally {
            try {
                workFinally(exceptions);
            } catch (Exception workFinallyException) {
                failed = true;
                this.failedMessage = workFinallyException.getMessage();
                exceptions.put("workFinally", workFinallyException);
                try {
                    workFinallyFail(workFinallyException, exceptions);
                } catch (Exception workFinallyFailException) {
                    LOG.error(workFinallyFailException.getMessage(), workFinallyFailException);
                    exceptions.put("workFinallyFail", workFinallyFailException);
                }
            }
            this.success = !failed;
            this.workFailed = workFailedTemp;
            this.executedMs = (int)(System.currentTimeMillis() - startMs);
            this.executed = true;
            this.workFailedMessage = msg;
            addWorkItemAfterExecuted(this, failed, executedMs);
            this.running = false;
            if(!StringUtils.isEmpty(this.suffixThreadName))
                Thread.currentThread().setName(this.threadName);
        }
    }

    @Override
    public void beforeWork() {}

    @Override
    public void beforeWorkFail(Exception exception) {
        LOG.error(this + " - " + exception.getMessage(), exception);
    }

    public abstract void work();

    @Override
    public void workFail(Exception exception) {
        LOG.error(this + " - " + exception.getMessage(), exception);
    }

    @Override
    public void workSuccessFail(Exception exception) {
        LOG.error(this + " - " + exception.getMessage(), exception);
    }

    @Override
    public void workFinally(Map<String, Exception> exceptions) {}

    @Override
    public void workFinallyFail(Exception finallyException, Map<String, Exception> exceptions) {
        LOG.error(this + " - " + finallyException.getMessage(), finallyException);
    }

    @Override
    public boolean isExecuted() {
        return executed;
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
    public int getExecutedMs() {
        return executedMs;
    }

    @Override
    public String getFailedMessage() {
        return failedMessage;
    }

    @Override
    public String getWorkFailedMessage() {
        return workFailedMessage;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }
}
