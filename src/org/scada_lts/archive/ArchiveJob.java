package org.scada_lts.archive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.web.beans.ApplicationBeans;

public class ArchiveJob implements StatefulJob {

    private static final Log LOG = LogFactory.getLog(ArchiveJob.class);

    private final ArchiveService archiveService;

    public ArchiveJob() {
        this.archiveService = ApplicationBeans.getBean("archiveService", ArchiveService.class);
    }

    public ArchiveJob(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (!SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.ARCHIVE_ENABLED)) {
            LOG.debug("[ARCHIVER] Skipped: ARCHIVE_ENABLED=false");
            return;
        }

        long start = System.currentTimeMillis();
        try {
            LOG.info("[ARCHIVER] Job start");
            archiveService.runArchive();
            long ms = System.currentTimeMillis() - start;
            LOG.info("[ARCHIVER] Job finished in " + ms + " ms");
        } catch (Throwable t) {
            LOG.error("[ARCHIVER] Job execution failed: " + t.getMessage(), t);
            throw new JobExecutionException(t, false);
        }
    }
}
