package com.serotonin.mango.rt.dataSource;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scada_lts.recursive.SetUnreliableDataPointsAction;
import org.scada_lts.utils.SystemSettingsUtils;

import java.util.Collections;
import java.util.List;

public final class DataPointUnreliableUtils {

    private static final Logger LOG = LogManager.getLogger(DataPointUnreliableUtils.class);
    private static final int DEPTH = SystemSettingsUtils.getDataPointUnreliableDepthNumber();
    private static final int EXECUTE_IN_POOL_IF_EXCEEDS_NUMBER = SystemSettingsUtils.getDataPointUnreliableExecuteInPoolIfTasksExceedsNumber();

    private DataPointUnreliableUtils() {}


    public static void setUnreliableDataPoints(List<DataPointRT> dataPoints) {
        for(DataPointRT dataPoint: dataPoints) {
            setUnreliableDataPoint(dataPoint);
        }
    }

    public static void setUnreliableDataPoint(DataPointRT dataPoint) {
        if(!dataPoint.isUnreliable()) {
            unreliable(Collections.singletonList(dataPoint), true);
        }
    }

    public static void resetUnreliableDataPoints(List<DataPointRT> dataPoints) {
        for(DataPointRT dataPoint: dataPoints) {
            resetUnreliableDataPoint(dataPoint);
        }
    }

    public static void resetUnreliableDataPoint(DataPointRT dataPoint) {
        if(dataPoint.isUnreliable()) {
            unreliable(Collections.singletonList(dataPoint), false);
        }
    }

    private static void unreliable(List<DataPointRT> dataPoints, boolean unreliable) {
        SetUnreliableDataPointsAction setUnreliableDataPointsAction = new SetUnreliableDataPointsAction(dataPoints, unreliable, DEPTH, EXECUTE_IN_POOL_IF_EXCEEDS_NUMBER);
        try {
            setUnreliableDataPointsAction.call();
        } catch (Exception e) {
            LOG.error(LoggingUtils.exceptionInfo(e));
        }
    }

    public static boolean isSetUnreliable(DataPointRT dataPointRT, boolean unreliable) {
        return SetUnreliableDataPointsAction.isSetUnreliable(dataPointRT, unreliable);
    }
}