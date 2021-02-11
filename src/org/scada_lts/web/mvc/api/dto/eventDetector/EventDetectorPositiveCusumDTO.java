package org.scada_lts.web.mvc.api.dto.eventDetector;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorPositiveCusumDTO extends EventDetectorDTO {
    private int duration;
    private int durationType;
    private int weight;
    private int limit;

    public EventDetectorPositiveCusumDTO() {
    }

    public EventDetectorPositiveCusumDTO(String xid, String alias, int alarmLevel, int duration, int durationType, int weight, int positiveLimit) {
        super(xid, alias, alarmLevel);
        this.duration = duration;
        this.durationType = durationType;
        this.weight = weight;
        this.limit = positiveLimit;
    }

    @Override
    public PointEventDetectorVO createPointEventDetectorVO(DataPointVO dataPointVO) {
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(PointEventDetectorVO.TYPE_POSITIVE_CUSUM);
        ped.njbSetDataPoint(dataPointVO);
        ped.setXid(getXid());
        ped.setAlias(getAlias());
        ped.setAlarmLevel(getAlarmLevel());

        ped.setDuration(duration);
        ped.setDurationType(durationType);
        ped.setLimit(limit);
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

    public int getPositiveLimit() {
        return limit;
    }

    public void setPositiveLimit(int positiveLimit) {
        this.limit = positiveLimit;
    }
}
