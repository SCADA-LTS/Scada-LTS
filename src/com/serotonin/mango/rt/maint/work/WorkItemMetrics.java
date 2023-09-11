package com.serotonin.mango.rt.maint.work;

public interface WorkItemMetrics {
    boolean isExecuted();
    boolean isSuccess();
    boolean isWorkFailed();
    boolean isRunning();
    int getExecutedMs();
    String getFailedMessage();
    String getWorkFailedMessage();
    String getThreadName();
}
