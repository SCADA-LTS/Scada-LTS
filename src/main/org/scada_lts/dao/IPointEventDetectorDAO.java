package org.scada_lts.dao;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

import java.util.List;

public interface IPointEventDetectorDAO {
    default void init() {};
    List<PointEventDetectorVO> getPointEventDetectors(DataPointVO dataPoint);
    boolean isEventDetectorXidUnique(int dataPointId, String xid, int excludeId);
    int insert(int dataPointId, PointEventDetectorVO pointEventDetector);
    void update(int dataPointId, PointEventDetectorVO pointEventDetector);
    void updateWithType(int dataPointId, PointEventDetectorVO pointEventDetector);
    void delete(int dataPointId, PointEventDetectorVO pointEventDetector);
    void deleteWithId(String dataPointIds);
    PointEventDetectorVO getPointEventDetector(int pointEventDetectorId);
    PointEventDetectorVO getPointEventDetector(String pointEventDetectorXid, int dataPointId);
    int getDataPointId(int pointEventDetectorId);
}
