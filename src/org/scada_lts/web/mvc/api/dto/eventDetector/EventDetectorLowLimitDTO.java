package org.scada_lts.web.mvc.api.dto.eventDetector;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorLowLimitDTO extends EventDetectorDTO{
    private int duration;
    private int durationType;
    private int lowLimit;

    public EventDetectorLowLimitDTO() {
    }

    public EventDetectorLowLimitDTO(String xid, String alias, int alarmLevel, int duration, int durationType, int lowLimit) {
        super(xid, alias, alarmLevel);
        this.duration = duration;
        this.durationType = durationType;
        this.lowLimit = lowLimit;
    }

    @Override
    public PointEventDetectorVO createPointEventDetectorVO(DataPointVO dataPointVO) {
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(PointEventDetectorVO.TYPE_ANALOG_LOW_LIMIT);
        ped.njbSetDataPoint(dataPointVO);
        ped.setXid(getXid());
        ped.setAlias(getAlias());
        ped.setAlarmLevel(getAlarmLevel());

        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setLimit(lowLimit);
        return ped;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDurationType() {
        return durationType;
    }

    public void setDurationType(int durationType) {
        this.durationType = durationType;
    }

    public int getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(int lowLimit) {
        this.lowLimit = lowLimit;
    }
}
