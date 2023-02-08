package com.serotonin.mango.rt.maint.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.SystemSettingsUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBeforeAfterWorkItem implements WorkItem, BeforeWork,
        AfterWork, AfterWork.WorkSuccessFail, AfterWork.WorkFinally {

    private static final Log LOG = LogFactory.getLog(AbstractBeforeAfterWorkItem.class);
    private static final WorkItems FAILED_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getFailedWorkItemsLimit());
    private static final WorkItems RUNNING_WORK_ITEMS = new WorkItems(SystemSettingsUtils.getRunningWorkItemsLimit());
    private volatile boolean executed = false;
    private volatile boolean success = false;
    private volatile boolean workFailed = false;
    private volatile boolean running = false;
    private volatile int executedMs = -1;
    public static WorkItems failedWorkItems() {
        return FAILED_WORK_ITEMS;
    }
    public static WorkItems runningWorkItems() {
        return RUNNING_WORK_ITEMS;
    }
    @Override
    public final void execute() {
        if(running) {
            LOG.warn("Work items is running! : " + this);
        } else {
            RUNNING_WORK_ITEMS.add(this);
        }
        this.running = true;
        long startMs = System.currentTimeMillis();
        this.executedMs = -1;
        this.workFailed = false;
        this.executed = false;
        boolean failed = false;
        Map<String, Exception> exceptions = new HashMap<>();
        try {
            try {
                beforeWork();
            } catch (Exception beforeWorkException) {
                this.workFailed = true;
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
            } catch (Exception workException) {
                this.workFailed = true;
                failed = true;
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
                exceptions.put("workFinally", workFinallyException);
                try {
                    workFinallyFail(workFinallyException, exceptions);
                } catch (Exception workFinallyFailException) {
                    LOG.error(workFinallyFailException.getMessage(), workFinallyFailException);
                    exceptions.put("workFinallyFail", workFinallyFailException);
                }
            }
            this.success = !failed;
            this.executedMs = (int)(System.currentTimeMillis() - startMs);
            this.executed = true;
            if(failed)
                FAILED_WORK_ITEMS.add(this);
            this.running = false;
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

}
