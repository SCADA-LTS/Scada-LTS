package com.serotonin.timer;

/**
 * A class that wraps a Runnable and sets the thread name to the given name.
 * 
 * @author Matthew Lohbihler
 */
public class NamedRunnable implements Runnable {
    private final Runnable runnable;
    private final String name;

    public NamedRunnable(Runnable runnable, String name) {
        this.runnable = runnable;
        this.name = name;
    }

    public void run() {
        String originalName = Thread.currentThread().getName();

        // Append the given name to the original name.
        Thread.currentThread().setName(originalName + " --> " + name);

        try {
            // Ok, go ahead and run the thingy.
            runnable.run();
        }
        finally {
            // Return the name to its original.
            Thread.currentThread().setName(originalName);
        }
    }
}