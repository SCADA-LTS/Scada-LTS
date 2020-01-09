package org.scada_lts.servicebrokers;

import org.scada_lts.dao.model.PointEventDetectorCache;
import java.util.List;

public interface ServiceBrokerEventDetector {

    List<PointEventDetectorCache> getAllEventDetectors();
}
