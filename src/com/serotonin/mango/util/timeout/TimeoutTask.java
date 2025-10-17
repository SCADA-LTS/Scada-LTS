package com.serotonin.mango.util.timeout;

import java.util.Date;

import com.serotonin.mango.Common;
import com.serotonin.timer.OneTimeTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeoutTask extends TimerTask {
    private static final Log LOG = LogFactory.getLog(TimeoutTask.class);

    private final TimeoutClient client;

    public TimeoutTask(long delay, TimeoutClient client) {
        this(new OneTimeTrigger(delay), client);
    }

    public TimeoutTask(Date date, TimeoutClient client) {
        this(new OneTimeTrigger(date), client);
    }

    public TimeoutTask(TimerTrigger trigger, TimeoutClient client) {
        super(trigger);
        this.client = client;
        Common.timer.schedule(this);
    }

    @Override
    public void run(long runtime) {
        if(Common.isTerminating()) {
            LOG.info("Scada-LTS terminating! runtime: " + runtime);
            return;
        }
        client.scheduleTimeout(runtime);
    }
}
