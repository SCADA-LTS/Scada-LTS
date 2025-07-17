package com.serotonin.mango.rt.dataSource;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.recursive.SetUnreliableDataPointsAction;

import java.util.Collections;
import java.util.List;

public final class DataPointUnreliableUtils {

    private static final Log LOG = LogFactory.getLog(DataPointUnreliableUtils.class);
    private static final int SAFE = 10;

    private DataPointUnreliableUtils() {}


    public static void setUnreliableDataPoints(List<DataPointRT> dataPoints) {
        unreliable(dataPoints, true, SAFE);
    }

    public static void setUnreliableDataPoint(DataPointRT dataPoint) {
        unreliable(Collections.singletonList(dataPoint), true, SAFE);
    }

    public static void resetUnreliableDataPoints(List<DataPointRT> dataPoints) {
        unreliable(dataPoints, false, SAFE);
    }

    public static void resetUnreliableDataPoint(DataPointRT dataPoint) {
        unreliable(Collections.singletonList(dataPoint), false, SAFE);
    }

    private static void unreliable(List<DataPointRT> dataPoints, boolean unreliable, int depth) {
        SetUnreliableDataPointsAction setUnreliableDataPointsAction = new SetUnreliableDataPointsAction(dataPoints, unreliable, depth);
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