package com.serotonin.mango.rt.maint.work;

public class IdleWorkItem extends AbstractBeforeAfterWorkItem {

    public IdleWorkItem() {}

    @Override
    public void work() {}

    @Override
    public int getPriority() {
        return WorkItemPriority.IDLE.getPriority();
    }

    @Override
    public String getDetails() {
        return "idle";
    }
}
