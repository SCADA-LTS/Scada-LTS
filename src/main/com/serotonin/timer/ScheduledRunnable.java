package com.serotonin.timer;

/**
 * Same as {@link Runnable}, but has a fireTime (millis) parameter.
 */
public interface ScheduledRunnable {
    void run(long fireTime);
}
