package org.scada_lts;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.EventDetectorsCacheDAO;
import org.scada_lts.dao.model.PointEventDetectorCache;

import java.util.List;
import java.util.TreeMap;

public class BridgeBetweenEventDetectorCacheAndEventDetectorsCacheDAO {

    private EventDetectorsCacheDAO eventDetectorsCacheDAO = new EventDetectorsCacheDAO();

    public List<PointEventDetectorCache> getAllPointEventDetectorsByDataPointId() {

        return eventDetectorsCacheDAO.getAllPointEventDetectorsByDataPointId();

    }

    public TreeMap<Integer, List<PointEventDetectorVO>> getMapEventDetectorsForGivenEventDetectorsList(List<PointEventDetectorCache> pointEventDetectorCaches) {

        return eventDetectorsCacheDAO.getMapEventDetectorForGivenEventDetectorsList(pointEventDetectorCaches);

    }
}
