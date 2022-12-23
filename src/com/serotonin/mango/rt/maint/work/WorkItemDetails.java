package com.serotonin.mango.rt.maint.work;

public interface WorkItemDetails {
    String getDetails();
    boolean isExecuted();
    boolean isSuccess();
    int getExecutedMs();
}
