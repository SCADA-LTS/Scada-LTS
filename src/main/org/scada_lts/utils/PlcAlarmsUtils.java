package org.scada_lts.utils;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;

/**
 *
 * @author kamil.jarmusik@gmail.com
 *
 */

public final class PlcAlarmsUtils {

    private PlcAlarmsUtils() {}

    public static int getPlcAlarmLevelByDataPoint(DataPointVO dataPoint) {

        if(dataPoint == null || dataPoint.getPointLocator() == null) {
            return AlarmLevels.NONE;
        }
        PointLocatorVO pointLocator = dataPoint.getPointLocator();
        if(pointLocator.getDataTypeId() != DataTypes.BINARY) {
            return AlarmLevels.NONE;
        }
        return getPlcAlarmLevelByDataPointName(dataPoint.getName());
    }

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
