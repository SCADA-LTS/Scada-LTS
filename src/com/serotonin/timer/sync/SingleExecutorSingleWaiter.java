/*
    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
 	@author Matthew Lohbihler
 */
package com.serotonin.timer.sync;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.AbstractBeforeAfterWorkItem;
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
        Common.ctx.getBackgroundProcessing().addWorkItem(new TaskWrapper(executing, this.getClass().getName()));
    }

    class TaskWrapper extends AbstractBeforeAfterWorkItem implements Runnable {
        private final Runnable command;
        private final String details;

        @Deprecated
        public TaskWrapper(Runnable command) {
            this.command = command;
            this.details = "";
        }

        public TaskWrapper(Runnable command, String details) {
            this.command = command;
            this.details = details;
        }

        @Override
        public void run() {
            super.execute();
        }

        @Override
        public void work() {
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

        @Override
        public WorkItemPriority getPriorityType() {
            return WorkItemPriority.HIGH;
        }

        @Override
        public String toString() {
            return "TaskWrapper{details='" + details + '\'' +
                    '}';
        }

        @Override
        public String getDetails() {
            return this.toString();
        }
    }

}
