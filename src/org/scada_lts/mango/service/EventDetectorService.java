package org.scada_lts.mango.service;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.scada_lts.dao.PointEventDetectorDAO;
import org.scada_lts.mango.adapter.MangoEventDetector;

import java.util.List;


public class EventDetectorService implements MangoEventDetector {

    private PointEventDetectorDAO pointEventDetectorDAO = new PointEventDetectorDAO();

    @Override
    public List<PointEventDetectorVO> getP(DataPointVO dataPointId) {
        return pointEventDetectorDAO.getPointEventDetectors( dataPointId );
    }


}
