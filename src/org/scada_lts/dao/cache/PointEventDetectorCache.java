package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.PointEventDetectorDAO;
import org.scada_lts.dao.model.PointEventDetectorCacheEntry;

import java.util.List;

public class PointEventDetectorCache implements PointEventDetectorCacheable {

    private final PointEventDetectorDAO pointEventDetectorDAO;

    public PointEventDetectorCache(PointEventDetectorDAO pointEventDetectorDAO) {
        this.pointEventDetectorDAO = pointEventDetectorDAO;
    }

    @Override
    public List<PointEventDetectorCacheEntry> findAll() {
        return pointEventDetectorDAO.getAll();
    }

    @Override
    public int insert(PointEventDetectorVO pointEventDetector) {
        return pointEventDetectorDAO.insert(pointEventDetector);
    }

    @Override
    public void update(PointEventDetectorVO pointEventDetector) {
        pointEventDetectorDAO.update(pointEventDetector);
    }

    @Override
    public void updateWithType(PointEventDetectorVO pointEventDetector) {
        pointEventDetectorDAO.updateWithType(pointEventDetector);
    }

    @Override
    public void delete(int dataPointId, int pointEventDetectorId) {
        pointEventDetectorDAO.delete(dataPointId, pointEventDetectorId);
    }

    @Override
    public void deleteWithId(String dataPointIds) {
        pointEventDetectorDAO.deleteWithId(dataPointIds);
    }
}
