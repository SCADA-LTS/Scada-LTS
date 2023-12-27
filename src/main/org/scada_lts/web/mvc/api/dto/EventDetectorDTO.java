package org.scada_lts.web.mvc.api.dto;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

import static org.scada_lts.utils.EventDetectorApiUtils.updateValueEventDetector;

public class EventDetectorDTO {
    private String xid;
    private String alias;
    private Integer alarmLevel;
    private Integer detectorType;
    private Double limit;
    private Integer duration;
    private Integer durationType;
    private Boolean binaryState;
    private Integer multistateState;
    private Integer changeCount;
    private String alphanumericState;
    private Double weight;

    public EventDetectorDTO(){ }

    public EventDetectorDTO(String xid, String alias, Integer alarmLevel, Integer detectorType, Double limit, Integer duration, Integer durationType, Boolean binaryState, Integer multistateState, Integer changeCount, String alphanumericState, Double weight) {
        this.xid = xid;
        this.alias = alias;
        this.alarmLevel = alarmLevel;
        this.detectorType = detectorType;
        this.limit = limit;
        this.duration = duration;
        this.durationType = durationType;
        this.binaryState = binaryState;
        this.multistateState = multistateState;
        this.changeCount = changeCount;
        this.alphanumericState = alphanumericState;
        this.weight = weight;
    }

    public PointEventDetectorVO createPointEventDetectorVO(DataPointVO dataPointVO){
        PointEventDetectorVO ped = new PointEventDetectorVO();
        updateValueEventDetector(ped, this);
        ped.njbSetDataPoint(dataPointVO);
        return ped;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public Integer getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(Integer detectorType) {
        this.detectorType = detectorType;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDurationType() {
        return durationType;
    }

    public void setDurationType(Integer durationType) {
        this.durationType = durationType;
    }

    public Boolean getBinaryState() {
        return binaryState;
    }

    public void setBinaryState(Boolean binaryState) {
        this.binaryState = binaryState;
    }

    public Integer getMultistateState() {
        return multistateState;
    }

    public void setMultistateState(Integer multistateState) {
        this.multistateState = multistateState;
    }

    public Integer getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(Integer changeCount) {
        this.changeCount = changeCount;
    }

    public String getAlphanumericState() {
        return alphanumericState;
    }

    public void setAlphanumericState(String alphanumericState) {
        this.alphanumericState = alphanumericState;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
