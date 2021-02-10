package org.scada_lts.web.mvc.api.dto.eventDetector;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorStateChangeCounterDTO extends EventDetectorDTO{
    private int duration;
    private int durationType;
    private int changeCount;

    public EventDetectorStateChangeCounterDTO() { }

    public EventDetectorStateChangeCounterDTO(String xid, String alias, int alarmLevel, int duration, int durationType, int changeCount) {
        super(xid, alias, alarmLevel);
        this.duration = duration;
        this.durationType = durationType;
        this.changeCount = changeCount;
    }

    @Override
    public PointEventDetectorVO createPointEventDetectorVO(DataPointVO dataPointVO) {
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(PointEventDetectorVO.TYPE_STATE_CHANGE_COUNT);
        ped.njbSetDataPoint(dataPointVO);
        ped.setXid(getXid());
        ped.setAlias(getAlias());
        ped.setAlarmLevel(getAlarmLevel());

        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setChangeCount(changeCount);
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

    public int getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(int changeCount) {
        this.changeCount = changeCount;
    }
}
