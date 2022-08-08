package com.serotonin.mango.rt.maint.work;

import java.util.Map;

public interface AfterWork {
    default void workFail(Exception exception) {}
    default void workSuccess() {}

    @FunctionalInterface
    interface WorkSuccessFail {
        void workSuccessFail(Exception exception);
    }

    interface WorkFinally {
        default void workFinally(Map<String, Exception> exceptions) {}
        default void workFinallyFail(Exception finallyException, Map<String, Exception> exceptions) {}
    }
}
