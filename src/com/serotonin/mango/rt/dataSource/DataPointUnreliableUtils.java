package com.serotonin.mango.rt.dataSource;

import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.util.List;

public final class DataPointUnreliableUtils {

    public static final String ATTR_UNRELIABLE_KEY = "UNRELIABLE";

    private DataPointUnreliableUtils() {}

    public static void setUnreliableDataPoints(List<DataPointRT> dataPoints) {
        for (DataPointRT dp : dataPoints) {
            setUnreliableDataPoint(dp);
        }
    }

    public static void resetUnreliableDataPoints(List<DataPointRT> dataPoints) {
        for (DataPointRT dp : dataPoints) {
            resetUnreliableDataPoint(dp);
        }
    }

    public static void setUnreliableDataPoint(DataPointRT dataPoint) {
        setUnreliableDataPoint(ATTR_UNRELIABLE_KEY, dataPoint);
    }

    public static void resetUnreliableDataPoint(DataPointRT dataPoint) {
        resetUnreliableDataPoint(ATTR_UNRELIABLE_KEY, dataPoint);
    }

    public static void setUnreliableDataPoint(String key, DataPointRT dataPoint) {
        dataPoint.setAttribute(key, true);
    }

    public static void resetUnreliableDataPoint(String key, DataPointRT dataPoint) {
        dataPoint.setAttribute(key, false);
    }
}
