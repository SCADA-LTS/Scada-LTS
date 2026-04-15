package org.scada_lts.dao.cache;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.*;
import org.scada_lts.utils.CacheUtils;
import org.scada_lts.utils.ObjectsPaginationUtils;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.*;
import java.util.stream.Collectors;

public class PointEventDetectorDaoWithCache implements IPointEventDetectorDAO {

    private final PointEventDetectorCacheable pointEventDetectorCache;

    public PointEventDetectorDaoWithCache(PointEventDetectorCacheable pointEventDetectorCache) {
        this.pointEventDetectorCache = pointEventDetectorCache;
    }

    @Override
    public void init() {
        IDataPointDAO dataPointDAO = ApplicationBeans.getDataPointDAOBean();
        IPointEventDetectorDAO pointEventDetectorDAO =
                ApplicationBeans.getBean("pointEventDetectorDAO", IPointEventDetectorDAO.class);

        List<DataPointVO> dataPoints = dataPointDAO.getDataPoints();
        Map<Integer, List<PointEventDetectorVO>> pointEventDetectors = pointEventDetectorDAO
                .getPointEventDetectors()
                .stream()
                .collect(Collectors.groupingBy(a -> a.njbGetDataPoint().getId()));

        for (DataPointVO dataPoint : dataPoints) {
            List<PointEventDetectorVO> detectors = pointEventDetectors.get(dataPoint.getId());
            if (detectors != null && !detectors.isEmpty()) {
                dataPoint.setEventDetectors(detectors);
                for (PointEventDetectorVO detector : detectors) {
                    detector.njbSetDataPoint(dataPoint);
                    pointEventDetectorCache.selectPointEventDetector(detector.getId());
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

    @Override
    public List<PointEventDetectorVO> getPointEventDetectors() {
        return CacheUtils.getAllValues("point_event_detector", Comparator.comparing(PointEventDetectorVO::getId));
    }

    @Override
    public List<PointEventDetectorVO> getPointEventDetectors(long offset, int limit) {
        return ObjectsPaginationUtils.pagination(getPointEventDetectors(), offset, limit);
    }
}
