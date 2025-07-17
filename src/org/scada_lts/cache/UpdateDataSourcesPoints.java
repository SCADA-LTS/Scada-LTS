package org.scada_lts.cache;

import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.scada_lts.dao.IDataPointDAO;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.List;
import java.util.Map;

public class UpdateDataSourcesPoints implements StatefulJob{

    private static final Log LOG = LogFactory.getLog(UpdateDataSourcesPoints.class);
    private final IDataPointDAO dao = ApplicationBeans.getBean("dataPointDAO", IDataPointDAO.class);


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            LOG.trace("UpdateEventDetectors");
            List<DataPointVO> dps = dao.getDataPoints();
            Map<Long, List<DataPointVO>> dss = DataSourcePointsCache.getInstance().composeCashData(dps);
            DataSourcePointsCache.getInstance().setData(dss);
        } catch (Exception ex) {
            LOG.error(LoggingUtils.causeInfo(ex), ex);
        }

    }
}
