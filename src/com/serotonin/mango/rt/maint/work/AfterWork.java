package com.serotonin.mango.rt.maint.work;

import java.util.Map;

public interface AfterWork {
    void workFail(Exception exception);
    void workSuccess();

    interface WorkSuccessFail {
        void workSuccessFail(Exception exception);
    }

    interface WorkFinally {
        void workFinally(Map<String, Exception> exceptions);
        void workFinallyFail(Exception finallyException, Map<String, Exception> exceptions);
    }
}
