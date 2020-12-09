package org.scada_lts.web.mvc.api.dto.eventDetector;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

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
