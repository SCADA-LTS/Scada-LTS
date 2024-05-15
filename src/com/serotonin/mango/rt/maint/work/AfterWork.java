package com.serotonin.mango.rt.maint.work;

import java.util.Map;

public interface AfterWork {
    default void workFail(Throwable exception) {}
    default void workSuccess() {}

    @FunctionalInterface
    interface WorkSuccessFail {
        void workSuccessFail(Throwable exception);
    }

    interface WorkFinally {
        default void workFinally(Map<String, Throwable> exceptions) {}
        default void workFinallyFail(Throwable finallyException, Map<String, Throwable> exceptions) {}
    }
}
