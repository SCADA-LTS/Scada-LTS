package org.scada_lts.cache;

import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.PostgresDataPointDAO;
import org.scada_lts.dao.IDataPointDAO;

import java.util.List;
import java.util.Map;

public class UpdateDataSourcesPoints implements StatefulJob{

    private static final Log LOG = LogFactory.getLog(UpdateDataSourcesPoints.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            IDataPointDAO dao = null;
            LOG.trace("UpdateEventDetectors");
            if (DAO.getType() == DatabaseAccess.DatabaseType.POSTGRES) {
                dao = new PostgresDataPointDAO();
            } else {
                dao = new DataPointDAO();
            }
            List<DataPointVO> dps = dao.getDataPoints();
            Map<Long, List<DataPointVO>> dss = DataSourcePointsCache.getInstance().composeCashData(dps);
            DataSourcePointsCache.getInstance().setData(dss);
        } catch (Exception ex) {
            LOG.error(LoggingUtils.causeInfo(ex), ex);
        }

    }
}
