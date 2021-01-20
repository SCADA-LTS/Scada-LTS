package org.scada_lts.web.mvc.api.dto.eventDetector;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorBinaryStateDTO extends EventDetectorDTO{
    private int duration;
    private int durationType;
    private boolean binaryState;

    public EventDetectorBinaryStateDTO() { }

    public EventDetectorBinaryStateDTO(String xid, String alias, int alarmLevel, int duration, int durationType, boolean binaryState) {
        super(xid, alias, alarmLevel);
        this.duration = duration;
        this.durationType = durationType;
        this.binaryState = binaryState;
    }

    @Override
    public PointEventDetectorVO createPointEventDetectorVO(DataPointVO dataPointVO) {
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(PointEventDetectorVO.TYPE_BINARY_STATE);
        ped.njbSetDataPoint(dataPointVO);
        ped.setXid(getXid());
        ped.setAlias(getAlias());
        ped.setAlarmLevel(getAlarmLevel());

        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setBinaryState(binaryState);
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

    public boolean isBinaryState() {
        return binaryState;
    }

    public void setBinaryState(boolean binaryState) {
        this.binaryState = binaryState;
    }
}
