package com.serotonin.timer;

import java.util.Date;

public class FixedDelayTrigger extends AbstractTimerTrigger {
    private final long period;

    public FixedDelayTrigger(long delay, long period) {
        super(delay);
        this.period = period;
    }

    public FixedDelayTrigger(Date start, long period) {
        super(start);
        this.period = period;
    }

    @Override
    protected long calculateNextExecutionTimeImpl() {
        return timer.currentTimeMillis() + period;
    }

    @Override
    protected long calculateNextExecutionTimeImpl(long after) {
        return after + period;
    }

    @Override
    public long mostRecentExecutionTime() {
        return nextExecutionTime - period;
    }

    @Override
    public String toString() {
        return "FixedDelayTrigger{" +
                "period=" + period +
                ", " + super.toString() + '}';
    }
}
