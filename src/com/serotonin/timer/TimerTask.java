package com.serotonin.timer;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;

public abstract class TimerTask implements Runnable {
    /**
     * This object is used to control access to the TimerTask internals.
     */
    Object lock = new Object();

    /**
     * The state of this task, chosen from the constants below.
     */
    volatile int state = VIRGIN;

    /**
     * This task has not yet been scheduled.
     */
    static final int VIRGIN = 0;

    /**
     * This task is scheduled for execution. If it is a non-repeating task, it has not yet been executed.
     */
    static final int SCHEDULED = 1;

    /**
     * This non-repeating task has already executed (or is currently executing) and has not been cancelled.
     */
    static final int EXECUTED = 2;

    /**
     * This task has been cancelled (with a call to TimerTask.cancel).
     */
    static final int CANCELLED = 3;

    TimerTrigger trigger;
    private final String name;

    /**
     * Indicates that if the task is running at the moment it is cancelled, the cancellation should wait until the task
     * is done. This is useful if the task uses resources that need to be shut down before the timer is shutdown.
     */
    private boolean completeBeforeCancel;

    private final ReadWriteLock cancelLock = new ReentrantReadWriteLock();

    public TimerTask(TimerTrigger trigger) {
        this(trigger, null);
    }

    public TimerTask(TimerTrigger trigger, String name) {
        if (trigger == null)
            throw new NullPointerException();
        this.trigger = trigger;
        this.name = name;
    }

    public boolean isCompleteBeforeCancel() {
        return completeBeforeCancel;
    }

    public void setCompleteBeforeCancel(boolean completeBeforeCancel) {
        this.completeBeforeCancel = completeBeforeCancel;
    }

    /**
     * Cancels this timer task. If the task has been scheduled for one-time execution and has not yet run, or has not
     * yet been scheduled, it will never run. If the task has been scheduled for repeated execution, it will never run
     * again. (If the task is running when this call occurs, the task will run to completion, but will never run again.)
     * 
     * <p>
     * Note that calling this method from within the <tt>run</tt> method of a repeating timer task absolutely guarantees
     * that the timer task will not run again.
     * 
     * <p>
     * This method may be called repeatedly; the second and subsequent calls have no effect.
     * 
     * @return true if this task is scheduled for one-time execution and has not yet run, or this task is scheduled for
     *         repeated execution. Returns false if the task was scheduled for one-time execution and has already run,
     *         or if the task was never scheduled, or if the task was already cancelled. (Loosely speaking, this method
     *         returns <tt>true</tt> if it prevents one or more scheduled executions from taking place.)
     */
    public boolean cancel() {
        synchronized (lock) {
            boolean result = (state == SCHEDULED);

            if (completeBeforeCancel) {
                cancelLock.writeLock().lock();
                try {
                    state = CANCELLED;
                }
                finally {
                    cancelLock.writeLock().unlock();
                }
            }
            else
                state = CANCELLED;

            return result;
        }
    }

    abstract public void run(long runtime);

    @Override
    final public void run() {
        // TODO if the task is not called quickly enough, the nextExecutionTime
        // may have already been changed. It would
        // be better to have the value assigned at the moment the task is
        // submitted to the thread pool, but the
        // interface doesn't allow it. For now, this will have to do.
        long t;
        synchronized (lock) {
            t = trigger.mostRecentExecutionTime();
        }

        String originalName = null;
        try {
            if (!StringUtils.isBlank(name)) {
                // This uses roughly the same code as in NamedRunnable to rename
                // the thread for the duration of the task execution.
                originalName = Thread.currentThread().getName();

                // Append the given name to the original name.
                Thread.currentThread().setName(originalName + " --> " + name);
            }

            if (completeBeforeCancel) {
                try {
                    cancelLock.readLock().lock();
                    if (state != CANCELLED)
                        run(t);
                }
                finally {
                    cancelLock.readLock().unlock();
                }
            }
            else
                // Ok, go ahead and run the thingy.
                run(t);
        }
        finally {
            if (originalName != null)
                // Return the name to its original.
                Thread.currentThread().setName(originalName);
        }
    }

    public boolean isCancelled() {
        return state == CANCELLED;
    }

    public long getNextExecutionTime() {
        return trigger.nextExecutionTime;
    }

    void setTimer(AbstractTimer timer) {
        trigger.setTimer(timer);
    }

    AbstractTimer getTimer() {
        return trigger.getTimer();
    }

    int getState() {
        return state;
    }

    @Override
    public String toString() {
        return "TimerTask{" +
                "state=" + state +
                ", trigger=" + trigger +
                ", name='" + name + '\'' +
                ", completeBeforeCancel=" + completeBeforeCancel +
                ", cancelLock=" + cancelLock +
                '}';
    }
}
