package org.scada_lts.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ScheduleExecutor<T extends Job> {
    private final List<T> toExecutes = new CopyOnWriteArrayList<>();

    public void schedule(T execute) {
        toExecutes.add(execute);
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        for(T toExecute: toExecutes) {
            toExecute.execute(jobExecutionContext);
        }
    }
}