package org.scada_lts.archive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.quartz.CronTriggerScheduler;
import org.scada_lts.web.beans.ApplicationBeans;

import java.io.IOException;

public class ArchiveUtils {

    private static final Log LOG = LogFactory.getLog(ArchiveUtils.class);

    private static volatile String lastCronApplied = null;
    private static volatile boolean lastEnabled = false;

    public static void init() throws IOException {
        final CronTriggerScheduler scheduler = ApplicationBeans.getBean("archiveScheduler", CronTriggerScheduler.class);

        final boolean enabled = SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.ARCHIVE_ENABLED);
        final String cronExpression = ScadaConfig.getInstance().getArchiveCron();

        if (!enabled) {
            if (lastEnabled) {
                LOG.info("[ARCHIVER] Disabling scheduler (ARCHIVE_ENABLED=false)");
            }
            scheduler.stop();
            lastEnabled = false;
            lastCronApplied = null;
            return;
        }

        if (enabled == lastEnabled && cronExpression.equals(lastCronApplied)) {
            return;
        }

        try {
            LOG.info("[ARCHIVER] (Re)Scheduling archiver with cron: " + cronExpression);
            scheduler.schedule(cronExpression);
            lastCronApplied = cronExpression;
            lastEnabled = true;
        } catch (Exception e) {
            LOG.error("[ARCHIVER] Failed to (re)schedule archiver: " + e.getMessage(), e);
            scheduler.stop();
            lastCronApplied = null;
            lastEnabled = false;
        }
    }

    public static void refresh() {
        try {
            init();
        } catch (IOException e) {
            LOG.error("[ARCHIVER] Refresh failed: " + e.getMessage(), e);
        }
    }
}
