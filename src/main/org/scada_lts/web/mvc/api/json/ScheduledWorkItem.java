package org.scada_lts.web.mvc.api.json;

import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTaskState;

public class ScheduledWorkItem {
    private final String className;
    private final String details;
    private final TimerTaskState state;

    public ScheduledWorkItem(TimerTask timerTask) {
        this.className = timerTask.getClass().getName();
        this.details = timerTask.toString();
        this.state = TimerTaskState.stateOf(timerTask);
    }

    public String getClassName() {
        return className;
    }

    public String getDetails() {
        return details;
    }

    public TimerTaskState getState() {
        return state;
    }
}
