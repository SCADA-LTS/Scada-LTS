package com.serotonin.timer;

import org.apache.commons.lang3.StringUtils;
import org.scada_lts.utils.ThreadUtils;

import java.util.List;

abstract public class AbstractTimer {
    abstract public boolean isInitialized();

    abstract public long currentTimeMillis();

    abstract public void execute(Runnable command);

    public void execute(Runnable command, String name) {
        if (StringUtils.isBlank(name))
            execute(command);
        else
            execute(new NamedRunnable(command, ThreadUtils.reduceName(name)));
    }

    abstract public void execute(ScheduledRunnable command, long fireTime);

    public void execute(ScheduledRunnable command, long fireTime, String name) {
        if (StringUtils.isBlank(name))
            execute(command, fireTime);
        else
            execute(new ScheduledNamedRunnable(command, ThreadUtils.reduceName(name)), fireTime);
    }

    final public TimerTask schedule(TimerTask task) {
        if (task.getTimer() == this)
            throw new IllegalStateException("Task already scheduled or cancelled");

        task.setTimer(this);
        scheduleImpl(task);

        return task;
    }

    public void scheduleAll(AbstractTimer that) {
        for (TimerTask task : that.cancel())
            schedule(task);
    }

    abstract protected void scheduleImpl(TimerTask task);

    abstract public List<TimerTask> cancel();

    abstract public int purge();

    abstract public int size();

    abstract public List<TimerTask> getTasks();
}
