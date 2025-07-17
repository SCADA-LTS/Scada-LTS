package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.web.i18n.LocalizableMessage;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.scada_lts.quartz.EverySecondTool;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeLocker implements Job {

    private final UUID serial = UUID.randomUUID();
    private final AtomicInteger attemptsCounter = new AtomicInteger(0);
    private final AtomicInteger timer = new AtomicInteger(0);
    private final int numberOfAttempts;
    private final int waitSeconds;

    public TimeLocker(int numberOfAttempts, int waitSeconds) {
        this.numberOfAttempts = numberOfAttempts;
        this.waitSeconds = waitSeconds;
    }

    public int remainingSeconds() {
        if(timer.get() <= 0) {
            if (attemptsCounter.getAndIncrement() >= this.numberOfAttempts) {
                attemptsCounter.getAndSet(0);
                timer.set(waitSeconds);
                EverySecondTool.schedule(this);
                return waitSeconds;
            }
        }
        return timer.get();
    }

    public void reset() {
        EverySecondTool.unschedule(this);
        attemptsCounter.getAndSet(0);
        timer.getAndSet(0);
    }

    public String getDetails() {
        return new LocalizableMessage("dsEdit.opcua.numberAttemptsExceeded", numberOfAttempts, timer.get()).getLocalizedMessage(Common.getBundle());
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (timer.getAndDecrement() < 0) {
            EverySecondTool.unschedule(this);
            timer.getAndSet(0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TimeLocker that = (TimeLocker) o;
        return Objects.equals(serial, that.serial);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serial);
    }

}
