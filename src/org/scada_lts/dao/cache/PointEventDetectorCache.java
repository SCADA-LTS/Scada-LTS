package org.scada_lts.dao.cache;

import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.PointEventDetectorDAO;

import java.util.List;

public class PointEventDetectorCache implements PointEventDetectorCacheable {

    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(PointEventDetectorCache.class);

    private final PointEventDetectorDAO pointEventDetectorDAO;

    public PointEventDetectorCache(PointEventDetectorDAO pointEventDetectorDAO) {
        this.pointEventDetectorDAO = pointEventDetectorDAO;
    }

    @Override
    public int insert(int dataPointId, PointEventDetectorVO pointEventDetector) {
        LOG.error("no cache: dataPointId: " + dataPointId + ", pointEventDetector: " + LoggingUtils.pointEventDetectorInfo(pointEventDetector));
        return pointEventDetectorDAO.insert(dataPointId, pointEventDetector);
    }

    @Override
    public void update(int dataPointId, PointEventDetectorVO pointEventDetector) {
        LOG.error("no cache: dataPointId: " + dataPointId + ", pointEventDetector: " + LoggingUtils.pointEventDetectorInfo(pointEventDetector));
        pointEventDetectorDAO.update(dataPointId, pointEventDetector);
    }

    @Override
    public void updateWithType(int dataPointId, PointEventDetectorVO pointEventDetector) {
        LOG.error("no cache: dataPointId: " + dataPointId + ", pointEventDetector: " + LoggingUtils.pointEventDetectorInfo(pointEventDetector));
        pointEventDetectorDAO.updateWithType(dataPointId, pointEventDetector);
    }

    @Override
    public List<PointEventDetectorVO> selectPointEventDetectors(DataPointVO dataPoint) {
        LOG.error("no cache: " + LoggingUtils.dataPointInfo(dataPoint));
        return pointEventDetectorDAO.getPointEventDetectors(dataPoint);
    }

    @Override
    public void delete(int dataPointId, PointEventDetectorVO pointEventDetector) {
        LOG.error("no cache: dataPointId: " + dataPointId + ", pointEventDetector: " + LoggingUtils.pointEventDetectorInfo(pointEventDetector));
        pointEventDetectorDAO.delete(dataPointId, pointEventDetector);
    }

    @Override
    public void deleteWithId(String dataPointIds) {
        LOG.error("no cache: dataPointIds: " + dataPointIds);
        pointEventDetectorDAO.deleteWithId(dataPointIds);
    }

    @Override
    public PointEventDetectorVO selectPointEventDetector(int pointEventDetectorId) {
        LOG.error("no cache: pointEventDetectorId: " + pointEventDetectorId);
        return pointEventDetectorDAO.getPointEventDetector(pointEventDetectorId);
    }

    @Override
    public PointEventDetectorVO selectPointEventDetector(String pointEventDetectorXid, int dataPointId) {
        LOG.error("no cache: dataPointId: " + dataPointId + ", pointEventDetectorXid: " + pointEventDetectorXid);
        return pointEventDetectorDAO.getPointEventDetector(pointEventDetectorXid, dataPointId);
    }

    @Override
    public int selectDataPointIdByEventDetectorId(int pointEventDetectorId) {
        LOG.error("no cache: pointEventDetectorId: " + pointEventDetectorId);
        return pointEventDetectorDAO.getDataPointId(pointEventDetectorId);
    }
}
