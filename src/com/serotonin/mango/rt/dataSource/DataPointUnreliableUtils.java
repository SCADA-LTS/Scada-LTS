package com.serotonin.mango.rt.dataSource;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class DataPointUnreliableUtils {

    private static final Log LOG = LogFactory.getLog(DataPointUnreliableUtils.class);

    public static final String ATTR_UNRELIABLE_KEY = "UNRELIABLE";

    private DataPointUnreliableUtils() {}

    public static boolean isSetUnreliable(DataPointRT dataPoint, boolean unreliable) {
        return dataPoint.getAttribute(ATTR_UNRELIABLE_KEY) != null
                && ((boolean) dataPoint.getAttribute(ATTR_UNRELIABLE_KEY)) == unreliable;
    }

    public static void setUnreliableDataPoints(List<DataPointRT> dataPoints) {
        unreliable(dataPoints, true, 9);
    }

    public static void setUnreliableDataPoint(DataPointRT dataPoint) {
        unreliable(Collections.singletonList(dataPoint), true, 9);
    }

    public static void resetUnreliableDataPoints(List<DataPointRT> dataPoints) {
        unreliable(dataPoints, false, 9);
    }

    public static void resetUnreliableDataPoint(DataPointRT dataPoint) {
        unreliable(Collections.singletonList(dataPoint), false, 9);
    }

    private static void unreliable(List<DataPointRT> dataPoints, boolean unreliable, int safe) {
        setAttributes(filter(dataPoints, unreliable), unreliable);
        for(DataPointRT dataPoint: dataPoints) {
            List<DataPointRT> metaDataPoints = Common.ctx.getRuntimeManager().getRunningMetaDataPoints(dataPoint.getId());
            if(!metaDataPoints.isEmpty()) {
                if(safe > -1)
                    unreliable(metaDataPoints, unreliable, --safe);
                else {
                    LOG.warn("The safe counter has been exceeded!");
                    setAttributes(filter(metaDataPoints, unreliable), unreliable);
                }
            }
        }
    }

    private static void setAttributes(List<DataPointRT> dataPoints, boolean unreliable) {
        for (DataPointRT dataPoint : dataPoints) {
            dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, unreliable);
        }
    }

    private static List<DataPointRT> filter(List<DataPointRT> dataPoints, boolean unreliable) {
        return dataPoints.stream().filter(dataPoint -> !isSetUnreliable(dataPoint, unreliable))
                .collect(Collectors.toList());
    }


}