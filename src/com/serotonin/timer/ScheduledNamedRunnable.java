package com.serotonin.timer;


/**
 * A class that wraps a Runnable and sets the thread name to the given name.
 * 
 * @author Matthew Lohbihler
 */
public class ScheduledNamedRunnable implements ScheduledRunnable {
    private final ScheduledRunnable runnable;
    private final String name;

    public ScheduledNamedRunnable(ScheduledRunnable runnable, String name) {
        this.runnable = runnable;
        this.name = name;
    }

    @Override
    public void run(long fireTime) {
        String originalName = Thread.currentThread().getName();

        // Append the given name to the original name.
        Thread.currentThread().setName(originalName + " --> " + name);

        try {
            // Ok, go ahead and run the thingy.
            runnable.run(fireTime);
        }
        finally {
            // Return the name to its original.
            Thread.currentThread().setName(originalName);
        }
    }
}