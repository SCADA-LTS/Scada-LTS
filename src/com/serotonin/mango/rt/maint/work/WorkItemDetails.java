package com.serotonin.mango.rt.maint.work;

public interface WorkItemDetails {
    String getDetails();
    boolean isExecuted();
    boolean isSuccess();
    boolean isWorkFailed();
    boolean isRunning();
    int getExecutedMs();
}
