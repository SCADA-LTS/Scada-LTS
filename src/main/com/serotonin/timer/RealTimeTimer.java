package com.serotonin.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RealTimeTimer extends AbstractTimer {
    private static final Log LOG = LogFactory.getLog(RealTimeTimer.class);

    /**
     * The timer task queue. This data structure is shared with the timer thread. The timer produces tasks, via its
     * various schedule calls, and the timer thread consumes, executing timer tasks as appropriate, and removing them
     * from the queue when they're obsolete.
     */
    private final TaskQueue queue = new TaskQueue();

    /**
     * The timer thread.
     */
    private TimerThread thread;

    // Do i own the executor?
    private boolean ownsExecutor;
    private Exception cancelStack;

    private TimeSource timeSource = new SystemTimeSource();

    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    public void init() {
        ownsExecutor = true;
        init(new ThreadPoolExecutor(0, 1000, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()));
    }

    public void init(ExecutorService executorService) {
        thread = new TimerThread(queue, executorService, timeSource);
        thread.setName("Serotonin Timer");
        thread.setDaemon(false);
        thread.start();
    }

    @Override
    public boolean isInitialized() {
        return thread != null;
    }

    /**
     * This object causes the timer's task execution thread to exit gracefully when there are no live references to the
     * Timer object and no tasks in the timer queue. It is used in preference to a finalizer on Timer as such a
     * finalizer would be susceptible to a subclass's finalizer forgetting to call it.
     */
    @Override
    protected void finalize() {
        synchronized (queue) {
            if (thread != null)
                thread.newTasksMayBeScheduled = false;
            if (cancelStack == null)
                cancelStack = new Exception();
            queue.notify();
        }
    }

    /**
     * A convenience method that executes the given command in the executor service immediately.
     * 
     * @param command
     * @throws ExecutionRejectedException
     */
    @Override
    public void execute(Runnable command) {
        if (thread == null)
            throw new IllegalStateException("Run init first");
        thread.execute(command);
    }

    /**
     * A convenience method that executes the given command in the executor service immediately.
     * 
     * @param command
     * @throws ExecutionRejectedException
     */
    @Override
    public void execute(ScheduledRunnable command, long fireTime) {
        if (thread == null)
            throw new IllegalStateException("Run init first");
        thread.execute(command, fireTime);
    }

    /**
     * Schedule the specified timer task for execution at the specified time with the specified period, in milliseconds.
     * If period is positive, the task is scheduled for repeated execution; if period is zero, the task is scheduled for
     * one-time execution. Time is specified in Date.getTime() format. This method checks timer state, task state, and
     * initial execution time, but not period.
     * 
     * @throws IllegalArgumentException
     *             if <tt>time()</tt> is negative.
     * @throws IllegalStateException
     *             if task was already scheduled or cancelled, timer was cancelled, or timer thread terminated.
     */
    @Override
    protected void scheduleImpl(TimerTask task) {
        if (thread == null)
            throw new IllegalStateException("Run init first");

        if (task.state == TimerTask.CANCELLED || task.state == TimerTask.EXECUTED)
            throw new IllegalStateException("Task already executed or cancelled");

        synchronized (queue) {
            if (!thread.newTasksMayBeScheduled) {
                if (cancelStack != null) {
                    LOG.error("Timer already cancelled.");
                    LOG.error("   Cancel stack:", cancelStack);
                    LOG.error("   Current stack:", new Exception());
                    throw new IllegalStateException("Timer already cancelled.", cancelStack);
                }
                throw new IllegalStateException("Timer already cancelled.");
            }

            synchronized (task.lock) {
                if (task.state == TimerTask.VIRGIN) {
                    long time = task.trigger.getFirstExecutionTime();

                    if (time < 0)
                        throw new IllegalArgumentException("Illegal execution time.");

                    task.trigger.nextExecutionTime = time;
                    task.state = TimerTask.SCHEDULED;
                }
            }

            queue.add(task);
            if (queue.getMin() == task)
                queue.notify();
        }
    }

    /**
     * Terminates this timer, discarding any currently scheduled tasks. Does not interfere with a currently executing
     * task (if it exists). Once a timer has been terminated, its execution thread terminates gracefully, and no more
     * tasks may be scheduled on it.
     * 
     * <p>
     * Note that calling this method from within the run method of a timer task that was invoked by this timer
     * absolutely guarantees that the ongoing task execution is the last task execution that will ever be performed by
     * this timer.
     * 
     * <p>
     * This method may be called repeatedly; the second and subsequent calls have no effect.
     */
    @Override
    public List<TimerTask> cancel() {
        List<TimerTask> tasks;
        synchronized (queue) {
            thread.newTasksMayBeScheduled = false;
            if (cancelStack == null)
                cancelStack = new Exception();
            tasks = getTasks();
            queue.clear();
            queue.notify(); // In case queue was already empty.
        }

        if (ownsExecutor)
            getExecutorService().shutdown();

        return tasks;
    }

    public ExecutorService getExecutorService() {
        return thread.getExecutorService();
    }

    /**
     * Removes all canceled tasks from this timer's task queue. <i>Calling this method has no effect on the behavior of
     * the timer</i>, but eliminates the references to the canceled tasks from the queue. If there are no external
     * references to these tasks, they become eligible for garbage collection.
     * 
     * <p>
     * Most programs will have no need to call this method. It is designed for use by the rare application that cancels
     * a large number of tasks. Calling this method trades time for space: the runtime of the method may be proportional
     * to n + c log n, where n is the number of tasks in the queue and c is the number of canceled tasks.
     * 
     * <p>
     * Note that it is permissible to call this method from within a a task scheduled on this timer.
     * 
     * @return the number of tasks removed from the queue.
     * @since 1.5
     */
    @Override
    public int purge() {
        int result = 0;

        synchronized (queue) {
            for (int i = queue.size(); i > 0; i--) {
                if (queue.get(i).state == TimerTask.CANCELLED) {
                    queue.quickRemove(i);
                    result++;
                }
            }

            if (result != 0)
                queue.heapify();
        }

        return result;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public List<TimerTask> getTasks() {
        List<TimerTask> result = new ArrayList<TimerTask>();
        synchronized (queue) {
            for (int i = 0; i < queue.size(); i++)
                result.add(queue.get(i + 1));
        }
        return result;
    }

    @Override
    public long currentTimeMillis() {
        return timeSource.currentTimeMillis();
    }
    //
    // @SuppressWarnings("unchecked")
    // public static void main(String[] args) throws Exception {
    // RealTimeTimer timer = new RealTimeTimer();
    // ExecutorService executorService = Executors.newCachedThreadPool();
    // timer.init(executorService);
    //
    // timer.schedule(new NamedTask("task 7", new OneTimeTrigger(25000)));
    // timer.schedule(new NamedTask("task 1", new OneTimeTrigger(1000)));
    // timer.schedule(new NamedTask("task 2", new OneTimeTrigger(2000)));
    // timer.schedule(new NamedTask("task 4", new OneTimeTrigger(20000)));
    // timer.schedule(new NamedTask("task 5", new OneTimeTrigger(21000)));
    // timer.schedule(new NamedTask("task 3", new OneTimeTrigger(10000)));
    // timer.schedule(new NamedTask("task 6", new OneTimeTrigger(22000)));
    // timer.schedule(new NamedTask("rate", new FixedRateTrigger(5000, 1800)));
    // timer.schedule(new NamedTask("delay", new FixedDelayTrigger(6000,
    // 2100)));
    // timer.schedule(new NamedTask("cron", new
    // CronTimerTrigger("0/6 * * * * ?")));
    //
    // Thread.sleep(15000);
    // System.out.println("Serializing");
    //
    // List<TimerTask> tasks = timer.cancel();
    // timer = null;
    //
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // ObjectOutputStream oos = new ObjectOutputStream(baos);
    // oos.writeObject(tasks);
    //
    // Thread.sleep(20000);
    //
    // ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    // ObjectInputStream ois = new ObjectInputStream(bais);
    // tasks = (List<TimerTask>) ois.readObject();
    //
    // System.out.println("Simulating");
    // SimulationTimer sim = new SimulationTimer();
    // for (TimerTask task : tasks)
    // sim.schedule(task);
    //
    // for (int i = 0; i < 10; i++) {
    // long start = System.currentTimeMillis();
    // sim.fastForwardTo(start);
    // System.out.println("Run " + i + " took " + (System.currentTimeMillis() -
    // start) + " ms");
    // }
    //
    // System.out.println("Real time");
    // timer = new RealTimeTimer();
    // timer.init(executorService);
    // timer.scheduleAll(sim);
    //
    // Thread.sleep(10000);
    //
    // timer.cancel();
    // executorService.shutdown();
    // }
    //
    // static class NamedTask extends TimerTask {
    // private static final long serialVersionUID = 1L;
    //
    // String name;
    //
    // NamedTask(String name, TimerTrigger trigger) {
    // super(trigger);
    // this.name = name;
    // }
    //
    // @Override
    // public String toString() {
    // return "NamedTask(" + name + ")";
    // }
    //
    // @Override
    // protected void run(long runtime) {
    // System.out.println(name + " ran at " + runtime);
    // try {
    // Thread.sleep(300);
    // }
    // catch (InterruptedException e) {
    // // no op
    // }
    // }
    // }
}
