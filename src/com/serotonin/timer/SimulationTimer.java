package com.serotonin.timer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The simulation timer is a single threaded timer under the temporal control of the next and fastForward methods. Tasks
 * are run in the same thread as the timer, so they will seem to complete instantly. Running them in an executor has the
 * opposite effect of making them appear to take an awful long time to complete.
 * 
 * @author Matthew Lohbihler
 */
public class SimulationTimer extends AbstractTimer {
    private final List<TimerTask> queue = new ArrayList<TimerTask>();
    private boolean cancelled;
    private long currentTime;

    @Override
    public boolean isInitialized() {
        return true;
    }

    public void setStartTime(long startTime) {
        currentTime = startTime;
    }

    public void next() {
        fastForwardTo(currentTime + 1);
    }

    public void fastForwardTo(long time) {
        while (!queue.isEmpty() && queue.get(0).trigger.nextExecutionTime <= time) {
            TimerTask task = queue.get(0);

            currentTime = task.trigger.nextExecutionTime;

            if (task.state == TimerTask.CANCELLED)
                queue.remove(0);
            else {
                long next = task.trigger.calculateNextExecutionTime();
                if (next <= 0) { // Non-repeating, remove
                    queue.remove(0);
                    task.state = TimerTask.EXECUTED;
                }
                else {
                    // Repeating task, reschedule
                    task.trigger.nextExecutionTime = next;
                    updateQueue();
                }

                task.run();
            }
        }

        currentTime = time;
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public void execute(ScheduledRunnable command, long fireTime) {
        command.run(fireTime);
    }

    @Override
    protected void scheduleImpl(TimerTask task) {
        if (cancelled)
            throw new IllegalStateException("Timer already cancelled.");

        if (task.state == TimerTask.CANCELLED || task.state == TimerTask.EXECUTED)
            throw new IllegalStateException("Task already executed or cancelled");

        if (task.state == TimerTask.VIRGIN) {
            long time = task.trigger.getFirstExecutionTime();
            if (time < 0)
                throw new IllegalArgumentException("Illegal execution time.");

            task.trigger.nextExecutionTime = time;
            task.state = TimerTask.SCHEDULED;
        }

        queue.add(task);
        updateQueue();
    }

    private void updateQueue() {
        Collections.sort(queue, new Comparator<TimerTask>() {
            @Override
            public int compare(TimerTask t1, TimerTask t2) {
                long diff = t1.trigger.nextExecutionTime - t2.trigger.nextExecutionTime;
                if (diff < 0)
                    return -1;
                if (diff == 0)
                    return 0;
                return 1;
            }
        });
    }

    @Override
    public List<TimerTask> cancel() {
        cancelled = true;
        List<TimerTask> tasks = getTasks();
        queue.clear();
        return tasks;
    }

    @Override
    public int purge() {
        int result = 0;

        for (int i = queue.size(); i > 0; i--) {
            if (queue.get(i).state == TimerTask.CANCELLED) {
                queue.remove(i);
                result++;
            }
        }

        return result;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public List<TimerTask> getTasks() {
        return new ArrayList<TimerTask>(queue);
    }

    @Override
    public long currentTimeMillis() {
        return currentTime;
    }
    //
    // public static void main(String[] args) throws Exception {
    // long startTime = System.currentTimeMillis() - 32000;
    // SimulationTimer simTimer = new SimulationTimer();
    // simTimer.setStartTime(startTime);
    //
    // simTimer.schedule(new NamedTask("task 7", new OneTimeTrigger(25000)));
    // simTimer.schedule(new NamedTask("task 1", new OneTimeTrigger(1000)));
    // simTimer.schedule(new NamedTask("task 2", new OneTimeTrigger(2000)));
    // simTimer.schedule(new NamedTask("task 4", new OneTimeTrigger(20000)));
    // simTimer.schedule(new NamedTask("task 5", new OneTimeTrigger(21000)));
    // simTimer.schedule(new NamedTask("task 3", new OneTimeTrigger(10000)));
    // simTimer.schedule(new NamedTask("task 6", new OneTimeTrigger(22000)));
    // simTimer.schedule(new NamedTask("rate", new FixedRateTrigger(5000,
    // 1800)));
    // simTimer.schedule(new NamedTask("delay", new FixedDelayTrigger(6000,
    // 2100)));
    // simTimer.schedule(new NamedTask("cron", new
    // CronTimerTrigger("0/6 * * * * ?")));
    //
    // simTimer.fastForwardTo(System.currentTimeMillis());
    //
    // System.out.println("Rescheduling");
    //
    // RealTimeTimer rtTimer = new RealTimeTimer();
    // ExecutorService executorService = Executors.newCachedThreadPool();
    // rtTimer.init(executorService);
    // rtTimer.scheduleAll(simTimer);
    //
    // Thread.sleep(20000);
    //
    // rtTimer.cancel();
    // executorService.shutdown();
    // }
    //
    // static class NamedTask extends TimerTask {
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
    // }
    // }
}
