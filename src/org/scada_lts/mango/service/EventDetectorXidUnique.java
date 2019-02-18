package org.scada_lts.mango.service;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.PointEventDetectorDAO;

class EventDetectorXidUnique {

    PointEventDetectorDAO pointEventDetectorDAO;

    EventDetectorXidUnique(PointEventDetectorDAO pointEventDetectorDAO){
        this.pointEventDetectorDAO = pointEventDetectorDAO;
    }
    String generateEventDetectorUniqueXid(int dataPointId){
        DAO temporatyDAOVariable = DAO.getInstance();
        String xid = temporatyDAOVariable.generateXid(PointEventDetectorVO.XID_PREFIX);
        while (!isEventDetectorXidUnique(dataPointId, xid, -1)) {
            xid = temporatyDAOVariable.generateXid(PointEventDetectorVO.XID_PREFIX);
        }

        return xid;
    }

    boolean isEventDetectorXidUnique( int dataPointId, String xid, int excludeId) {

        return pointEventDetectorDAO.isEventDetectorXidUnique(dataPointId, xid, excludeId);
    }
}
