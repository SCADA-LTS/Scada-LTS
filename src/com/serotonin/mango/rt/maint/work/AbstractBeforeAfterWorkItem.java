package com.serotonin.mango.rt.maint.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractBeforeAfterWorkItem implements WorkItem, BeforeWork,
        AfterWork, AfterWork.WorkSuccessFail, AfterWork.WorkFinally {

    private static final Log LOG = LogFactory.getLog(AbstractBeforeAfterWorkItem.class);
    private boolean executed;
    private boolean success;
    private int executedMs = 0;

    @Override
    public final void execute() {
        long startMs = System.currentTimeMillis();
        setFlags(false, false);
        Map<String, Exception> exceptions = new HashMap<>();
        try {
            try {
                beforeWork();
            } catch (Exception beforeWorkException) {
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
                exceptions.put("workSuccess", workSuccessException);
                try {
                    workSuccessFail(workSuccessException);
                } catch (Exception workSuccessFailException) {
                    LOG.error(workSuccessFailException.getMessage(), workSuccessFailException);
                    exceptions.put("workSuccessFail", workSuccessFailException);
                }
            }
        } finally {
            try {
                workFinally(exceptions);
            } catch (Exception workFinallyException) {
                try {
                    workFinallyFail(workFinallyException, exceptions);
                } catch (Exception workFinallyFailException) {
                    LOG.error(workFinallyFailException.getMessage(), workFinallyFailException);
                }
            }
            setFlags(true, exceptions.isEmpty());
            this.executedMs = (int)(System.currentTimeMillis() - startMs);
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
    public int getExecutedMs() {
        return executedMs;
    }

    private void setFlags(boolean executed, boolean success) {
        this.executed = executed;
        this.success = success;
    }
}
