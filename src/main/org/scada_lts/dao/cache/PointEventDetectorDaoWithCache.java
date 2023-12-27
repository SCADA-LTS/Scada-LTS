package org.scada_lts.dao.cache;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.IPointEventDetectorDAO;
import org.scada_lts.dao.PointEventDetectorDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PointEventDetectorDaoWithCache implements IPointEventDetectorDAO {

    private final PointEventDetectorCacheable pointEventDetectorCache;

    public PointEventDetectorDaoWithCache(PointEventDetectorCacheable pointEventDetectorCache) {
        this.pointEventDetectorCache = pointEventDetectorCache;
    }

    @Override
    public void init() {
        List<DataPointVO> dataPoints = new DataPointDAO().getDataPoints();
        Map<Integer, List<PointEventDetectorVO>> pointEventDetectors = new PointEventDetectorDAO()
                .getPointEventDetectors(Integer.MAX_VALUE, 0)
                .stream()
                .collect(Collectors.groupingBy(a -> a.njbGetDataPoint().getId()));
        for(DataPointVO dataPoint: dataPoints) {
            List<PointEventDetectorVO> detectors = pointEventDetectors.get(dataPoint.getId());
            if(detectors != null && !detectors.isEmpty()) {
                dataPoint.setEventDetectors(detectors);
                for(PointEventDetectorVO detector: detectors) {
                    detector.njbSetDataPoint(dataPoint);
                }
            }
            pointEventDetectorCache.put(dataPoint.getId(), detectors == null ? new ArrayList<>() : detectors);
        }
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
        PointEventDetectorVO pointEventDetectorVO = pointEventDetectorCache.selectPointEventDetector(xid, dataPointId);
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
    public PointEventDetectorVO getPointEventDetector(String pointEventDetectorXid, int dataPointId) {
        return pointEventDetectorCache.selectPointEventDetector(pointEventDetectorXid, dataPointId);
    }

    @Override
    public int getDataPointId(int pointEventDetectorId) {
        return pointEventDetectorCache.selectDataPointIdByEventDetectorId(pointEventDetectorId);
    }
}
