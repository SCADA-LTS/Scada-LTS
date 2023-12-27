package com.serotonin.mango.rt.maint.work;

import java.time.LocalDateTime;

public interface WorkItemMetrics {
    boolean isExecuted();
    boolean isSuccess();
    boolean isWorkFailed();
    boolean isRunning();
    long getExecutedNanos();
    long getExecutedMs();
    long getTimeInitNanos();
    long getTimeInitMs();
    String getFailedMessage();
    String getThreadName();
    LocalDateTime getCreatedDate();
    LocalDateTime getExecutedDate();
    LocalDateTime getStartedDate();
}
