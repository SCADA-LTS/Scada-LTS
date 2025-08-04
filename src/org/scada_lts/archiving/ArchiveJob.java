package org.scada_lts.archiving;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.scada_lts.web.beans.ApplicationBeans;

public class ArchiveJob implements Job {
    private final ArchiveService archiveService;

    public ArchiveJob() {
        this.archiveService = ApplicationBeans.getBean("archiveService", ArchiveService.class);
    }

    public ArchiveJob(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        archiveService.runArchive();
    }
}
