package com.serotonin.timer.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.AbstractBeforeAfterWorkItem;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import org.apache.commons.lang3.StringUtils;

import com.serotonin.timer.AbstractTimer;
import com.serotonin.timer.RealTimeTimer;

/**
 * This class acts as a synchronizing monitor where an arbitrary number of tasks can be added with string identifiers.
 * Then, the tasks can be executed using the provided executor and the calling thread will be blocked until they are all
 * completed, after which then tasks can be asked for by name to get their results.
 * 
 * This allows parts of processing that would benefit from being asynchronous - such as database concurrency - to easily
 * be managed by a controlling thread.
 * 
 * @author Matthew Lohbihler
 * 
 * @param <T>
 *            a task object that implements Runnable
 */
public class Synchronizer<T extends Runnable> {
    private final List<TaskWrapper> tasks = new ArrayList<TaskWrapper>();

    // A lock that is used when there are more tasks to run than allowed concurrently. This monitor notifies the 
    // completion of any task.
    final Object multiLock = new Object();

    private final int maxConcurrent;

    public Synchronizer() {
        this(0);
    }

    public Synchronizer(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public void addTask(T task) {
        addTask(null, task);
    }

    public void addTask(String name, T task) {
        tasks.add(new TaskWrapper(name, task));
    }

    public void executeAndWait(AbstractTimer timer) {
        List<TaskWrapper> running;

        if (maxConcurrent <= 0 || tasks.size() <= maxConcurrent) {
            // Start all of the tasks.
            for (TaskWrapper tw : tasks)
                Common.ctx.getBackgroundProcessing().addWorkItem(tw);
            running = tasks;
        }
        else {
            List<TaskWrapper> remaining = new ArrayList<TaskWrapper>(tasks);
            running = new ArrayList<TaskWrapper>();

            // We have more tasks to run than we can run concurrently. We need a polling method to monitor the tasks.
            while (!remaining.isEmpty()) {
                // Start tasks
                while (running.size() < maxConcurrent && !remaining.isEmpty()) {
                    TaskWrapper tw = remaining.remove(remaining.size() - 1);
                    Common.ctx.getBackgroundProcessing().addWorkItem(tw);
                    running.add(tw);
                }

                // Wait for completed tasks.
                while (true) {
                    synchronized (multiLock) {
                        boolean completions = false;
                        for (int i = running.size() - 1; i >= 0; i--) {
                            TaskWrapper tw = running.get(i);
                            synchronized (tw) {
                                if (tw.isComplete()) {
                                    running.remove(i);
                                    completions = true;
                                }
                            }
                        }

                        if (completions)
                            // Completed task(s) were found, so break.
                            break;

                        // No tasks completed, so wait for a notification.
                        try {
                            multiLock.wait();
                        }
                        catch (InterruptedException e) {
                            // no op
                        }
                    }
                }
            }
        }

        // Wait for the tasks to complete.
        for (TaskWrapper tw : running) {
            synchronized (tw) {
                try {
                    if (!tw.isComplete()) {
                        // System.out.println("Waiting on " + tw.task);
                        tw.wait();
                    }
                }
                catch (InterruptedException e) {
                    // no op
                }
            }
        }
    }

    public T getTask(String name) {
        for (TaskWrapper tw : tasks) {
            if (StringUtils.equals(name, tw.name))
                return tw.task;
        }
        return null;
    }

    public int getSize() {
        return tasks.size();
    }

    public List<T> getTasks() {
        List<T> result = new ArrayList<T>();
        for (TaskWrapper tw : tasks)
            result.add(tw.task);
        return result;
    }

    class TaskWrapper extends AbstractBeforeAfterWorkItem {
        final String name;
        final T task;
        private volatile boolean complete;

        public TaskWrapper(String name, T task) {
            this.name = name;
            this.task = task;
        }

        public String getName() {
            return name;
        }

        @Override
        public void work() {
            try {
                task.run();
            }
            finally {
                synchronized (this) {
                    complete = true;
                    notify();
                }
                synchronized (multiLock) {
                    multiLock.notify();
                }
            }
        }

        public boolean isComplete() {
            return complete;
        }

        @Override
        public String toString() {
            return "TaskWrapper{" +
                    "name='" + name + '\'' +
                    ", task=" + task +
                    ", complete=" + complete +
                    '}';
        }

        @Override
        public int getPriority() {
            return WorkItemPriority.HIGH.getPriority();
        }

        @Override
        public String getDetails() {
            return this.toString();
        }
    }

    public static void main(String[] args) {
        Synchronizer<TestTask> synchronizer = new Synchronizer<TestTask>(20);
        synchronizer.addTask("A", new TestTask("a", 4000));
        synchronizer.addTask("B", new TestTask("b", 5000));
        synchronizer.addTask("C", new TestTask("c", 10000));
        synchronizer.addTask("D", new TestTask("d", 3000));
        synchronizer.addTask("E", new TestTask("e", 1000));
        synchronizer.addTask("F", new TestTask("f", 70));
        synchronizer.addTask("G", new TestTask("g", 1));
        synchronizer.addTask("H", new TestTask("h", 10));
        synchronizer.addTask("I", new TestTask("i", 100));
        synchronizer.addTask("J", new TestTask("j", 800));
        synchronizer.addTask("K", new TestTask("k", 40));
        synchronizer.addTask("L", new TestTask("l", 250));
        synchronizer.addTask("M", new TestTask("m", 300));
        synchronizer.addTask("N", new TestTask("n", 700));
        synchronizer.addTask("O", new TestTask("o", 700));
        synchronizer.addTask("P", new TestTask("p", 700));
        synchronizer.addTask("Q", new TestTask("q", 4));
        synchronizer.addTask("R", new TestTask("r", 5));
        synchronizer.addTask("S", new TestTask("s", 6));
        synchronizer.addTask("T", new TestTask("t", 7));

        RealTimeTimer timer = new RealTimeTimer();
        ExecutorService executor = Executors.newCachedThreadPool();
        timer.init(executor);

        System.out.println("*** Begin");
        synchronizer.executeAndWait(timer);
        System.out.println("*** Done");

        timer.cancel();
        executor.shutdown();
    }

    static long start = System.currentTimeMillis();

    static class TestTask implements Runnable {
        private final String id;
        private final long sleepTime;

        public TestTask(String id, long sleepTime) {
            this.id = id;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            System.out.println((System.currentTimeMillis() - start) + ": started " + id);
            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e) {
                // no op
            }
            System.out.println((System.currentTimeMillis() - start) + ": ended " + id);
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
