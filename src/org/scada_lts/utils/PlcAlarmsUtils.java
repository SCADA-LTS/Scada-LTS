package org.scada_lts.utils;

import com.serotonin.mango.rt.event.AlarmLevels;

public class PlcAlarmsUtils {

    public static int getPlcAlarmLevelByDataPointName(String dataPointName) {

        if(dataPointName == null || dataPointName.isEmpty()) {
            return AlarmLevels.NONE;
        }
        if(dataPointName.contains(" AL ")) {
            return AlarmLevels.CRITICAL;
        }
        return dataPointName.contains(" ST ") ? AlarmLevels.URGENT : AlarmLevels.NONE;
    }
}
