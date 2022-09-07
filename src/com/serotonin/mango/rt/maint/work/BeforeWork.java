package com.serotonin.mango.rt.maint.work;

public interface BeforeWork {
    void beforeWork();
    void beforeWorkFail(Exception exception);

    interface NotExecuted {
        default void workNotExecuted(String msg) {}
        default void workNotExecuted(Exception exception) {}
    }
}
