package org.scada_lts.servicebrokers;

import org.scada_lts.dao.EventDetectorsDAO;
import org.scada_lts.dao.model.PointEventDetectorCache;

import java.util.List;

public class ServiceBrokerEventDetectorImpl implements ServiceBrokerEventDetector{

    private EventDetectorsDAO eventDetectorsDAO = new EventDetectorsDAO();


    @Override
    public List<PointEventDetectorCache> getAllEventDetectors() {
        return eventDetectorsDAO.getAllPointEventDetectorcache();
    }
}
