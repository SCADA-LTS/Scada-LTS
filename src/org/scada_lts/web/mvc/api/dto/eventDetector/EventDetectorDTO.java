package org.scada_lts.web.mvc.api.dto.eventDetector;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "detectorType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EventDetectorHighLimitDTO.class, name = "1"),
        @JsonSubTypes.Type(value = EventDetectorLowLimitDTO.class, name = "2"),
        @JsonSubTypes.Type(value = EventDetectorBinaryStateDTO.class, name = "3"),
        @JsonSubTypes.Type(value = EventDetectorMultistateStateDTO.class, name = "4"),
        @JsonSubTypes.Type(value = EventDetectorChangeDTO.class, name = "5"),
        @JsonSubTypes.Type(value = EventDetectorStateChangeCounterDTO.class, name = "6"),
        @JsonSubTypes.Type(value = EventDetectorNoChangeDTO.class, name = "7"),
        @JsonSubTypes.Type(value = EventDetectorNoUpdateDTO.class, name = "8"),
        @JsonSubTypes.Type(value = EventDetectorAlphanumericStateDTO.class, name = "9"),
        @JsonSubTypes.Type(value = EventDetectorPositiveCusumDTO.class, name = "10"),
        @JsonSubTypes.Type(value = EventDetectorNegativeCusumDTO.class, name = "11")
})
public abstract class EventDetectorDTO {
    private String xid;
    private String alias;
    private int alarmLevel;

    public EventDetectorDTO(){ }

    public EventDetectorDTO(String xid, String alias, int alarmLevel) {
        this.xid = xid;
        this.alias = alias;
        this.alarmLevel = alarmLevel;
    }

    public PointEventDetectorVO createPointEventDetectorVO(DataPointVO dataPointVO){
        return null;
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

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }
}
