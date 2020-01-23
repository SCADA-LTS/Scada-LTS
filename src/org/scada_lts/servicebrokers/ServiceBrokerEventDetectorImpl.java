package org.scada_lts.servicebrokers;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.PointEventDetectorDAO;
import org.scada_lts.dao.model.PointEventDetectorCache;

import java.util.List;

public class ServiceBrokerEventDetectorImpl implements ServiceBrokerEventDetector{

    private static final Log LOG = LogFactory.getLog(ServiceBrokerEventDetectorImpl.class);

    private PointEventDetectorDAO pointEventDetectorDAO = new PointEventDetectorDAO();

    @Override
    public List<PointEventDetectorCache> getAllEventDetectors() {
        return pointEventDetectorDAO.getAllPointEventDetectorCache();
    }
    @Override
    public List<PointEventDetectorVO> getEventDetectorsForGivenDataPointId(DataPointVO dataPointId ){
        return pointEventDetectorDAO.getPointEventDetectors( dataPointId );
    }

    @Override
    public void updateEventDetectors(PointEventDetectorVO pointEventDetectorVO){
        pointEventDetectorDAO.update(pointEventDetectorVO);
    }

    @Override
    public void updateEventDetectors(List<PointEventDetectorVO> pointEventDetectorVOS) {
        for(PointEventDetectorVO pointEventDetectorVO :pointEventDetectorVOS){
            updateEventDetectors( pointEventDetectorVO);
        }
    }

    @Override
    public int insertEventDetector(PointEventDetectorVO pointEventDetectorVO) {
        int pointEventDetectorId = Common.NEW_ID;
        pointEventDetectorId = pointEventDetectorDAO.insert( pointEventDetectorVO );
        if( pointEventDetectorId != Common.NEW_ID){
            LOG.trace("PointEventDetectorVO has been inserted into dataase with id = "+pointEventDetectorId);
        }

        return pointEventDetectorId;
    }

    @Override
    public void insertEventDetectors(List<PointEventDetectorVO> pointEventDetectorVOS) {
        for(PointEventDetectorVO pointEventDetectorVO : pointEventDetectorVOS){
            insertEventDetector( pointEventDetectorVO );
        }
    }

    @Override
    public void deleteEventDetector(DataPointVO dataPointVO, PointEventDetectorVO pointEventDetectorVO) {
        pointEventDetectorDAO.delete(dataPointVO.getId(),pointEventDetectorVO.getId());
    }

    @Override
    public void deleteEventDetectors(DataPointVO dataPointVO, List<PointEventDetectorVO> pointEventDetectorVOS) {
        for(PointEventDetectorVO pointEventDetectorVO : pointEventDetectorVOS){
            deleteEventDetector( dataPointVO, pointEventDetectorVO );
        }
    }
}
