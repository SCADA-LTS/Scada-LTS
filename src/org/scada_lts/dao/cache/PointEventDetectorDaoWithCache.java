package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.IPointEventDetectorDAO;
import org.scada_lts.dao.model.PointEventDetectorCacheEntry;

import java.util.List;
import java.util.stream.Collectors;

public class PointEventDetectorDaoWithCache implements IPointEventDetectorDAO {

    private final PointEventDetectorCacheable pointEventDetectorCache;

    public PointEventDetectorDaoWithCache(PointEventDetectorCacheable pointEventDetectorCache) {
        this.pointEventDetectorCache = pointEventDetectorCache;
    }

    @Override
    public int getDataPointId(int pointEventDetectorId) {
        return pointEventDetectorCache.findAll().stream()
                .filter(a -> a.getPointEventDetector() != null)
                .filter(a -> a.getPointEventDetector().getId() == pointEventDetectorId)
                .findAny()
                .map(PointEventDetectorCacheEntry::getDataPointId)
                .orElse(0);
    }

    @Override
    public List<PointEventDetectorVO> getPointEventDetectors(DataPointVO dataPoint) {
        return pointEventDetectorCache.findAll().stream()
                .filter(a -> a.getDataPointId() == dataPoint.getId())
                .map(PointEventDetectorCacheEntry::getPointEventDetector)
                .map(PointEventDetectorVO::copy)
                .peek(a -> a.njbSetDataPoint(dataPoint))
                .collect(Collectors.toList());
    }

    @Override
    public int getId(String pointEventDetectorXid, int dataPointId) {
        return pointEventDetectorCache.findAll().stream()
                .filter(a -> a.getPointEventDetector() != null)
                .filter(a -> a.getPointEventDetector().getXid() != null)
                .filter(a -> a.getPointEventDetector().getXid().equals(pointEventDetectorXid))
                .findAny()
                .map(a -> a.getPointEventDetector().getId())
                .orElse(-1);
    }

    @Override
    public String getXid(int pointEventDetectorId) {
        return pointEventDetectorCache.findAll().stream()
                .filter(a -> a.getPointEventDetector() != null)
                .filter(a -> a.getPointEventDetector().getXid() != null)
                .filter(a -> a.getPointEventDetector().getId() == pointEventDetectorId)
                .findAny()
                .map(a -> a.getPointEventDetector().getXid())
                .orElse(null);
    }

    @Override
    public boolean isEventDetectorXidUnique(int dataPointId, String xid, int excludeId) {
        return pointEventDetectorCache.findAll().stream()
                .filter(a -> a.getPointEventDetector() != null)
                .filter(a -> a.getPointEventDetector().getId() != excludeId)
                .filter(a -> a.getPointEventDetector().getXid() != null)
                .noneMatch(a -> a.getPointEventDetector().getXid().equals(xid));
    }

    @Override
    public int insert(PointEventDetectorVO pointEventDetector) {
        return pointEventDetectorCache.insert(pointEventDetector);
    }

    @Override
    public void update(PointEventDetectorVO pointEventDetector) {
        pointEventDetectorCache.update(pointEventDetector);
    }

    @Override
    public void updateWithType(PointEventDetectorVO pointEventDetector) {
        pointEventDetectorCache.updateWithType(pointEventDetector);
    }

    @Override
    public void delete(int dataPointId, int pointEventDetectorId) {
        pointEventDetectorCache.delete(dataPointId, pointEventDetectorId);
    }

    @Override
    public void deleteWithId(String dataPointIds) {
        pointEventDetectorCache.deleteWithId(dataPointIds);
    }
}
