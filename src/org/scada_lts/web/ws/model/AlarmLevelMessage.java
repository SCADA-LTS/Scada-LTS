package org.scada_lts.web.ws.model;

public class AlarmLevelMessage {
    private final int alarmLevel;

    private static final AlarmLevelMessage EMPTY = new AlarmLevelMessage(-1);

    public AlarmLevelMessage(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public static AlarmLevelMessage empty() {
        return EMPTY;
    }

    public int getAlarmlevel() {
        return alarmLevel;
    }
}
