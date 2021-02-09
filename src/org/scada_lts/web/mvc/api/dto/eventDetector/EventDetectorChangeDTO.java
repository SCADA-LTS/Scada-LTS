package org.scada_lts.web.mvc.api.dto.eventDetector;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorChangeDTO extends EventDetectorDTO{

    public EventDetectorChangeDTO() { }

    public EventDetectorChangeDTO(String xid, String alias, int alarmLevel) {
        super(xid, alias, alarmLevel);
    }

    @Override
    public PointEventDetectorVO createPointEventDetectorVO(DataPointVO dataPointVO) {
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(PointEventDetectorVO.TYPE_POINT_CHANGE);
        ped.njbSetDataPoint(dataPointVO);
        ped.setXid(getXid());
        ped.setAlias(getAlias());
        ped.setAlarmLevel(getAlarmLevel());

        return ped;
    }
}
