package com.serotonin.timer;


import java.text.ParseException;
import java.util.Date;

public class CronTimerTrigger extends TimerTrigger {
    private final CronExpression cronExpression;
    private long mostRecent;

    public CronTimerTrigger(String pattern) throws ParseException {
        cronExpression = new CronExpression(pattern);
    }

    @Override
    protected long calculateNextExecutionTimeImpl() {
        mostRecent = nextExecutionTime;
        return cronExpression.getNextValidTimeAfter(new Date(nextExecutionTime)).getTime();
    }

    @Override
    protected long calculateNextExecutionTimeImpl(long after) {
        return cronExpression.getNextValidTimeAfter(new Date(after)).getTime();
    }

    @Override
    protected long getFirstExecutionTime() {
        return cronExpression.getNextValidTimeAfter(new Date(timer.currentTimeMillis())).getTime();
    }

    @Override
    public long mostRecentExecutionTime() {
        return mostRecent;
    }

    @Override
    public String toString() {
        return "CronTimerTrigger{" +
                "cronExpression=" + cronExpression +
                ", mostRecent=" + mostRecent +
                ", " + super.toString() + '}';
    }
}
