package org.scada_lts.dao.cache;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.IPointEventDetectorDAO;

import java.util.List;
import java.util.stream.Collectors;

public class PointEventDetectorDaoWithCache implements IPointEventDetectorDAO {

    private final PointEventDetectorCacheable pointEventDetectorCache;

    public PointEventDetectorDaoWithCache(PointEventDetectorCacheable pointEventDetectorCache) {
        this.pointEventDetectorCache = pointEventDetectorCache;
    }

    @Override
    public List<PointEventDetectorVO> getPointEventDetectors(DataPointVO dataPoint) {
        return pointEventDetectorCache.selectPointEventDetectors(dataPoint).stream()
                .map(PointEventDetectorVO::copy)
                .peek(a -> a.njbSetDataPoint(dataPoint))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEventDetectorXidUnique(int dataPointId, String xid, int excludeId) {
        PointEventDetectorVO pointEventDetectorVO = pointEventDetectorCache.selectPointEventDetector(xid);
        if(pointEventDetectorVO == null)
            return true;
        if(pointEventDetectorVO.getId() == Common.NEW_ID)
            return false;
        return pointEventDetectorVO.getId() == excludeId;
    }

    @Override
    public int insert(int dataPointId, PointEventDetectorVO pointEventDetector) {
        return pointEventDetectorCache.insert(dataPointId, pointEventDetector);
    }

    @Override
    public void update(int dataPointId, PointEventDetectorVO pointEventDetector) {
        pointEventDetectorCache.update(dataPointId, pointEventDetector);
    }

    @Override
    public void updateWithType(int dataPointId, PointEventDetectorVO pointEventDetector) {
        pointEventDetectorCache.updateWithType(dataPointId, pointEventDetector);
    }

    @Override
    public void delete(int dataPointId, PointEventDetectorVO pointEventDetector) {
        pointEventDetectorCache.delete(dataPointId, pointEventDetector);
    }

    @Override
    public void deleteWithId(String dataPointIds) {
        pointEventDetectorCache.deleteWithId(dataPointIds);
    }

    @Override
    public PointEventDetectorVO getPointEventDetector(int pointEventDetectorId) {
        return pointEventDetectorCache.selectPointEventDetector(pointEventDetectorId);
    }

    @Override
    public PointEventDetectorVO getPointEventDetector(String pointEventDetectorXid) {
        return pointEventDetectorCache.selectPointEventDetector(pointEventDetectorXid);
    }

    @Override
    public int getDataPointId(int pointEventDetectorId) {
        return pointEventDetectorCache.selectDataPointIdByEventDetectorId(pointEventDetectorId);
    }
}
