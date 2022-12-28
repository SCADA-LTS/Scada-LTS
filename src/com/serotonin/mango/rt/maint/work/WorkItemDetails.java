package com.serotonin.mango.rt.maint.work;

public interface WorkItemDetails {
    String getDetails();
    boolean isExecuted();
    boolean isSuccess();
    boolean isWorkSuccess();
    boolean isBeforeWorkSuccess();
    boolean isAfterWorkSuccess();
    boolean isFinallyWorkSuccess();
    int getExecutedMs();
}
