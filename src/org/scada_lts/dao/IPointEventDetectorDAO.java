package org.scada_lts.dao;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

import java.util.List;

public interface IPointEventDetectorDAO {
    int getDataPointId(int pointEventDetectorId);
    List<PointEventDetectorVO> getPointEventDetectors(DataPointVO dataPoint);
    int getId(String pointEventDetectorXid, int dataPointId);
    String getXid(int pointEventDetectorId);
    boolean isEventDetectorXidUnique(int dataPointId, String xid, int excludeId);
    int insert(PointEventDetectorVO pointEventDetector);
    void update(PointEventDetectorVO pointEventDetector);
    void updateWithType(PointEventDetectorVO pointEventDetector);
    void delete(int dataPointId, int pointEventDetectorId);
    void deleteWithId(String dataPointIds);
}
