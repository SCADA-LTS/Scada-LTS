/*
    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
 	@author Matthew Lohbihler
 */
package com.serotonin.timer.sync;

import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.timer.AbstractTimer;

/**
 * This class is useful when an exclusive, long-running task gets called more often than is practical. For example, a
 * large object needs to be persisted, but it changes fairly often. This class will execute the submitted runnable
 * if there is currently nothing running. If another task is already running, the new task will wait. If there is
 * already a task waiting, the new task will replace the waiting task. So, tasks that are started are always allowed to
 * complete, but tasks that are no necessary to run are discarded.
 * 
 * @author Matthew Lohbihler
 */
public class SingleExecutorSingleWaiter {
    final AbstractTimer timer;
    final Object lock = new Object();

    Runnable executing;
    Runnable waiting;

    public SingleExecutorSingleWaiter(AbstractTimer timer) {
        this.timer = timer;
    }

    public void execute(Runnable task) {
        synchronized (lock) {
            if (executing != null) {
                waiting = task;
                return;
            }

            executing = task;
            executeImpl();
        }
    }

    void executeImpl() {
        timer.execute(new TaskWrapper(executing), WorkItemPriority.HIGH + " - " + this.getClass().getName());
    }

    class TaskWrapper implements Runnable {
        private final Runnable command;

        public TaskWrapper(Runnable command) {
            this.command = command;
        }

        @Override
        public void run() {
            try {
                command.run();
            }
            finally {
                synchronized (lock) {
                    if (waiting != null) {
                        executing = waiting;
                        waiting = null;
                        executeImpl();
                    }
                    else
                        executing = null;
                }
            }
        }
    }

}
