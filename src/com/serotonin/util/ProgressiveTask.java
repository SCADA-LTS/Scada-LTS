package com.serotonin.util;

import com.serotonin.mango.rt.maint.work.AbstractBeforeAfterWorkItem;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;

public abstract class ProgressiveTask extends AbstractBeforeAfterWorkItem implements Runnable {

    private volatile boolean cancelled = false;
    protected volatile boolean completed = false;
    private ProgressiveTaskListener listener;

    public ProgressiveTask() {}

    public ProgressiveTask(ProgressiveTaskListener l) {
        this.listener = l;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public void run() {
        super.execute();
    }

    @Override
    public final void work() {
        while(true) {
            if (this.isCancelled()) {
                this.declareFinished(true);
            } else {
                this.runImpl();
                if (!this.isCompleted()) {
                    continue;
                }

                this.declareFinished(false);
            }

            this.completed = true;
            return;
        }
    }

    protected void declareProgress(float progress) {
        ProgressiveTaskListener l = this.listener;
        if (l != null) {
            l.progressUpdate(progress);
        }

    }

    private void declareFinished(boolean cancelled) {
        ProgressiveTaskListener l = this.listener;
        if (l != null) {
            if (cancelled) {
                l.taskCancelled();
            } else {
                l.taskCompleted();
            }
        }

    }

    protected abstract void runImpl();

    @Override
    public int getPriority() {
        return WorkItemPriority.HIGH.getPriority();
    }

    @Override
    public String getDetails() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "ProgressiveTask{" +
                "cancelled=" + cancelled +
                ", completed=" + completed +
                '}';
    }
}
