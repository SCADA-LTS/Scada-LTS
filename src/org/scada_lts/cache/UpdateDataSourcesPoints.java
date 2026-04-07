package org.scada_lts.cache;

import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class UpdateDataSourcesPoints implements StatefulJob{

    private static final Log LOG = LogFactory.getLog(UpdateDataSourcesPoints.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            LOG.trace("UpdateEventDetectors");
            DataSourcePointsCache.getInstance().cacheInitialize();
        } catch (Exception ex) {
            LOG.error(LoggingUtils.causeInfo(ex), ex);
        }

    }
}
