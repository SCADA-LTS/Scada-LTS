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

public class TimeLocker {

    private final AtomicInteger attempsCounter = new AtomicInteger(0);
    private final AtomicInteger timer = new AtomicInteger(0);
    private final int numberOfAttemps;
    private final int waitSeconds;

    public TimeLocker(int numberOfAttemps, int waitSeconds) {
        this.numberOfAttemps = numberOfAttemps;
        this.waitSeconds = waitSeconds;
    }

    public int remainingSeconds() {
        if(timer.get() <= 0) {
            if (attempsCounter.getAndIncrement() == this.numberOfAttemps) {
                attempsCounter.set(0);
                timer.set(waitSeconds);
                EverySecondTool.schedule(new TimerLockerJob(timer));
                return waitSeconds;
            }
        }
        return timer.get();
    }

    public String getDetails() {
        return new LocalizableMessage("dsEdit.opcua.numberAttemptsExceeded", numberOfAttemps, timer.get()).getLocalizedMessage(Common.getBundle());
    }

    static class TimerLockerJob implements Job {

        private final UUID serial = UUID.randomUUID();
        private final AtomicInteger timer;

        public TimerLockerJob(AtomicInteger timer) {
            this.timer = timer;
        }

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            if (timer.getAndDecrement() < 0) {
                EverySecondTool.unschedule(this);
                timer.set(0);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            TimerLockerJob that = (TimerLockerJob) o;
            return Objects.equals(serial, that.serial);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(serial);
        }
    }
}
