package org.scada_lts.servicebrokers;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.model.PointEventDetectorCache;
import java.util.List;

public interface ServiceBrokerEventDetector {

    List<PointEventDetectorCache> getAllEventDetectors();

    List<PointEventDetectorVO> getEventDetectorsForGivenDataPointId(DataPointVO dataPointId);

    void updateEventDetectors(PointEventDetectorVO pointEventDetectorVO);

    void updateEventDetectors(List<PointEventDetectorVO> pointEventDetectorVOS);

    int insertEventDetector(PointEventDetectorVO pointEventDetectorVO);

    void insertEventDetectors(List<PointEventDetectorVO> pointEventDetectorVOS);

    void deleteEventDetector(DataPointVO dataPointVO, PointEventDetectorVO pointEventDetectorVO);

    void deleteEventDetectors(DataPointVO dataPointVO, List<PointEventDetectorVO> pointEventDetectorVOS);
}
