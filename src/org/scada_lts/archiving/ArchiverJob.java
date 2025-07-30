package org.scada_lts.archiving;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ArchiverJob implements Job {
    private final ArchiveService archiveService;

    public ArchiverJob() {
        this.archiveService = new ArchiveService();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        archiveService.runArchiving();
    }
}
