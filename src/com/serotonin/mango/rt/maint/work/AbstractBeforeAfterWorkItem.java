package com.serotonin.mango.rt.maint.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBeforeAfterWorkItem implements WorkItem, BeforeWork,
        AfterWork, AfterWork.WorkSuccessFail, AfterWork.WorkFinally {

    private static final Log LOG = LogFactory.getLog(AbstractBeforeAfterWorkItem.class);
    private volatile boolean executed = false;
    private volatile boolean success = false;
    private volatile boolean workSuccess = false;
    private volatile boolean beforeWorkSuccess = false;
    private volatile boolean afterWorkSuccess = false;
    private volatile boolean finallyWorkSuccess = false;
    private volatile boolean running = false;
    private volatile int executedMs = -1;

    @Override
    public final void execute() {
        this.executed = false;
        this.running = true;
        long startMs = System.currentTimeMillis();
        Map<String, Exception> exceptions = new HashMap<>();
        try {
            try {
                beforeWork();
            } catch (Exception beforeWorkException) {
                this.beforeWorkSuccess = false;
                exceptions.put("beforeWork", beforeWorkException);
                try {
                    beforeWorkFail(beforeWorkException);
                } catch (Exception beforeWorkFailException) {
                    LOG.error(beforeWorkFailException.getMessage(), beforeWorkFailException);
                    exceptions.put("beforeWorkFail", beforeWorkFailException);
                }
                return;
            }
            this.beforeWorkSuccess = true;
            try {
                work();
            } catch (Exception workException) {
                this.workSuccess = false;
                exceptions.put("work", workException);
                try {
                    workFail(workException);
                } catch (Exception workFailException) {
                    LOG.error(workFailException.getMessage(), workFailException);
                    exceptions.put("workFail", workFailException);
                }
                return;
            }
            this.workSuccess = true;
            try {
                workSuccess();
            } catch (Exception workSuccessException) {
                this.afterWorkSuccess = false;
                exceptions.put("workSuccess", workSuccessException);
                try {
                    workSuccessFail(workSuccessException);
                } catch (Exception workSuccessFailException) {
                    LOG.error(workSuccessFailException.getMessage(), workSuccessFailException);
                    exceptions.put("workSuccessFail", workSuccessFailException);
                }
                return;
            }
            this.afterWorkSuccess = true;
        } finally {
            try {
                workFinally(exceptions);
            } catch (Exception workFinallyException) {
                this.finallyWorkSuccess = false;
                exceptions.put("workFinally", workFinallyException);
                try {
                    workFinallyFail(workFinallyException, exceptions);
                } catch (Exception workFinallyFailException) {
                    LOG.error(workFinallyFailException.getMessage(), workFinallyFailException);
                    exceptions.put("workFinallyFail", workFinallyFailException);
                }
            }
            this.executed = true;
            this.success = exceptions.isEmpty();
            this.executedMs = (int)(System.currentTimeMillis() - startMs);
            this.finallyWorkSuccess = true;
            this.running = false;
        }
    }

    @Override
    public void beforeWork() {}

    @Override
    public void beforeWorkFail(Exception exception) {
        LOG.error(exception.getMessage(), exception);
    }

    public abstract void work();

    @Override
    public void workFail(Exception exception) {
        LOG.error(exception.getMessage(), exception);
    }

    @Override
    public void workSuccessFail(Exception exception) {
        LOG.error(exception.getMessage(), exception);
    }

    @Override
    public void workFinally(Map<String, Exception> exceptions) {}

    @Override
    public void workFinallyFail(Exception finallyException, Map<String, Exception> exceptions) {
        LOG.error(finallyException.getMessage(), finallyException);
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
    public boolean isWorkSuccess() {
        return workSuccess;
    }

    @Override
    public boolean isBeforeWorkSuccess() {
        return beforeWorkSuccess;
    }

    @Override
    public boolean isAfterWorkSuccess() {
        return afterWorkSuccess;
    }

    @Override
    public boolean isFinallyWorkSuccess() {
        return finallyWorkSuccess;
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
