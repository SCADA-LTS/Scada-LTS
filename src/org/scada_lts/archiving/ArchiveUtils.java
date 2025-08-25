package org.scada_lts.archiving;

import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.quartz.CronTriggerScheduler;
import org.scada_lts.web.beans.ApplicationBeans;

import java.io.IOException;

public class ArchiveUtils {
    public static void init() throws IOException {
        final var scheduler = ApplicationBeans.getBean("archivingScheduler", CronTriggerScheduler.class);
        final boolean enabled = SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.ARCHIVE_ENABLED);
        final String cronExpression = ScadaConfig.getInstance().getArchiveCron();

        if (enabled) {
            scheduler.schedule(cronExpression);
        } else {
            scheduler.stop();
        }
    }
}
