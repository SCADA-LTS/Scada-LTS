package com.serotonin.mango.rt.maint.work;

import java.time.LocalDateTime;

public interface WorkItemMetrics {
    boolean isExecuted();
    boolean isSuccess();
    boolean isWorkFailed();
    boolean isRunning();
    int getExecutedMs();
    String getFailedMessage();
    String getWorkFailedMessage();
    String getThreadName();
    LocalDateTime getCreatedDate();
    LocalDateTime getExecutedDate();
}
