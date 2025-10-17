package org.scada_lts.web.ws.model;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;

public class WsAlarmLevelMessage {
    private final int alarmLevel;

    private static final WsAlarmLevelMessage EMPTY = new WsAlarmLevelMessage(-1);

    public WsAlarmLevelMessage(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public static WsAlarmLevelMessage empty() {
        return EMPTY;
    }

    public static WsAlarmLevelMessage noneAlarmLevel() {
        return new WsAlarmLevelMessage(AlarmLevels.NONE);
    }

    public static WsAlarmLevelMessage alarmLevelFromEvent(EventInstance eventInstance) {
        return new WsAlarmLevelMessage(eventInstance.getAlarmLevel());
    }

    @Deprecated(since = "2.7.8")
    public int getAlarmlevel() {
        return alarmLevel;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }
}
