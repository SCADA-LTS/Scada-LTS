package org.scada_lts.archiving;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class ArchiverJob implements Job {
    @Autowired
    private ArchiveService archiveService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        archiveService.runArchiving();
    }
}
