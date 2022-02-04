package org.scada_lts.web.ws.model;

public class AlarmLevelMessage {
    private int alarmLevel;

    public AlarmLevelMessage(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public int getAlarmlevel() {
        return alarmLevel;
    }
}
