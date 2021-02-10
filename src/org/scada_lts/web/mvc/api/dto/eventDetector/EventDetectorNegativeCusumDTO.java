package org.scada_lts.web.mvc.api.dto.eventDetector;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorNegativeCusumDTO extends EventDetectorDTO {
    private int duration;
    private int durationType;
    private int weight;
    private int negativeLimit;

    public EventDetectorNegativeCusumDTO() {
    }

    public EventDetectorNegativeCusumDTO(String xid, String alias, int alarmLevel, int duration, int durationType, int weight, int negativeLimit) {
        super(xid, alias, alarmLevel);
        this.duration = duration;
        this.durationType = durationType;
        this.weight = weight;
        this.negativeLimit = negativeLimit;
    }

    @Override
    public PointEventDetectorVO createPointEventDetectorVO(DataPointVO dataPointVO) {
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(PointEventDetectorVO.TYPE_NEGATIVE_CUSUM);
        ped.njbSetDataPoint(dataPointVO);
        ped.setXid(getXid());
        ped.setAlias(getAlias());
        ped.setAlarmLevel(getAlarmLevel());

        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setLimit(negativeLimit);
        ped.setWeight(weight);
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getNegativeLimit() {
        return negativeLimit;
    }

    public void setNegativeLimit(int negativeLimit) {
        this.negativeLimit = negativeLimit;
    }
}
