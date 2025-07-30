package org.scada_lts.archiving;

import org.scada_lts.config.ScadaConfig;
import org.scada_lts.quartz.CronTriggerScheduler;
import org.scada_lts.web.beans.ApplicationBeans;

import java.io.IOException;

public class ArchiveUtils {
    public static void init() throws IOException {
        String cronExpression = ScadaConfig.getInstance().getArchiveCron();
        ApplicationBeans.getBean("archivingScheduler", CronTriggerScheduler.class).schedule(cronExpression);
    }
}
