package org.scada_lts.web.ws.model;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;

public class AlarmLevelMessage {
    private final int alarmLevel;

    private static final AlarmLevelMessage EMPTY = new AlarmLevelMessage(-1);

    public AlarmLevelMessage(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public static AlarmLevelMessage empty() {
        return EMPTY;
    }

    public static AlarmLevelMessage noneAlarmLevel() {
        return new AlarmLevelMessage(AlarmLevels.NONE);
    }

    public static AlarmLevelMessage alarmLevelFromEvent(EventInstance eventInstance) {
        return new AlarmLevelMessage(eventInstance.getAlarmLevel());
    }

    public int getAlarmlevel() {
        return alarmLevel;
    }
}
