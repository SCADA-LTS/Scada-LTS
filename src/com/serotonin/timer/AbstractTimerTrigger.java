package com.serotonin.timer;


import java.util.Date;

abstract public class AbstractTimerTrigger extends TimerTrigger {
    private final boolean delayed;
    private final long first;

    public AbstractTimerTrigger(long delay) {
        this.delayed = true;
        first = delay;
    }

    public AbstractTimerTrigger(Date start) {
        delayed = false;
        first = start.getTime();
    }

    @Override
    final protected long getFirstExecutionTime() {
        if (delayed)
            return timer.currentTimeMillis() + first;
        return first;
    }

    @Override
    public String toString() {
        return "AbstractTimerTrigger{" +
                "delayed=" + delayed +
                ", first=" + first +
                ", " + super.toString() + '}';
    }
}
