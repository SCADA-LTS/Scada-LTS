package org.scada_lts.archiving;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.web.beans.ApplicationBeans;

public class ArchiveJob implements StatefulJob {
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
            return;
        }
        archiveService.runArchive();
    }
}
