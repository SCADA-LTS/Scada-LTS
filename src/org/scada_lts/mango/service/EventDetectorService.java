package org.scada_lts.mango.service;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.PointEventDetectorDAO;

import java.util.List;


public class EventDetectorService {

    private PointEventDetectorDAO pointEventDetectorDAO = new PointEventDetectorDAO();

    public List<PointEventDetectorVO> getP(DataPointVO dataPointId) {
        return pointEventDetectorDAO.getPointEventDetectors( dataPointId );
    }


}
