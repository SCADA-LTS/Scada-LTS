package com.serotonin.timer;

import java.util.Date;

public class FixedRateTrigger extends AbstractTimerTrigger {
    private final long period;

    /**
     * Use this constructor to quantize the start of the trigger to the period.
     * 
     * @param period
     * @param now
     * @param quantize
     *            unused. Required for signature uniqueness.
     */
    public FixedRateTrigger(long period, long now, boolean quantize) {
        this(new Date(now + period - (now % period)), period);
    }

    public FixedRateTrigger(long delay, long period) {
        super(delay);
        this.period = period;
    }

    public FixedRateTrigger(Date start, long period) {
        super(start);
        this.period = period;
    }

    @Override
    protected long calculateNextExecutionTimeImpl() {
        return nextExecutionTime + period;
    }

    @Override
    protected long calculateNextExecutionTimeImpl(long after) {
        long d = after - nextExecutionTime;
        if (d < 0)
            return nextExecutionTime + period;

        long periods = d / period;
        return nextExecutionTime + period * (periods + 1);
    }

    @Override
    public long mostRecentExecutionTime() {
        return nextExecutionTime - period;
    }
}
