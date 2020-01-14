package org.scada_lts.servicebrokers;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.model.PointEventDetectorCache;

import java.util.List;

public interface ServiceBrokerEventDetector {

    List<PointEventDetectorCache> getAllEventDetectors();

    List<PointEventDetectorVO> getEventDetectorsForGivenDataPointId(DataPointVO dataPointId);
}
