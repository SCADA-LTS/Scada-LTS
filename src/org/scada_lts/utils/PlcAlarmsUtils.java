package org.scada_lts.utils;

import com.serotonin.mango.rt.event.AlarmLevels;

/**
 *
 * @author kamil.jarmusik@gmail.com
 *
 */

public class PlcAlarmsUtils {

    public static int getPlcAlarmLevelByDataPointName(String dataPointName) {

        if(dataPointName == null || dataPointName.isEmpty()) {
            return AlarmLevels.NONE;
        }
        if(dataPointName.contains(" AL ")) {
            return AlarmLevels.URGENT;
        }
        return dataPointName.contains(" ST ") ? AlarmLevels.INFORMATION : AlarmLevels.NONE;
    }
}
