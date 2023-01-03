package org.scada_lts.cache;

import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.scada_lts.dao.impl.DataPointDAO;

import java.util.List;
import java.util.Map;

public class UpdateDataSourcesPoints implements StatefulJob{

    private static final Log LOG = LogFactory.getLog(UpdateDataSourcesPoints.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        LOG.trace("UpdateEventDetectors");
            List<DataPointVO> dps = new DataPointDAO().getDataPoints();
            Map<Long, List<DataPointVO>> dss = DataSourcePointsCache.getInstance().composeCashData(dps);
            DataSourcePointsCache.getInstance().setData(dss);

    }
}
