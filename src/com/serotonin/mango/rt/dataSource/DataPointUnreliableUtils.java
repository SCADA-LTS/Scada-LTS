package com.serotonin.mango.rt.dataSource;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.util.Collections;
import java.util.List;
import java.util.function.*;

public final class DataPointUnreliableUtils {

    public static final String ATTR_UNRELIABLE_KEY = "UNRELIABLE";

    private DataPointUnreliableUtils() {}


    public static void setUnreliableDataPoints(List<DataPointRT> dataPoints) {
        unreliable(dataPoints, DataPointUnreliableUtils::setAttributes, Common.ctx.getRuntimeManager()::getRunningMetaDataPoints, true);
    }

    public static void setUnreliableDataPoint(DataPointRT dataPoint) {
        unreliable(Collections.singletonList(dataPoint), DataPointUnreliableUtils::setAttributes, Common.ctx.getRuntimeManager()::getRunningMetaDataPoints, true);
    }

    public static void resetUnreliableDataPoints(List<DataPointRT> dataPoints) {
        unreliable(dataPoints, DataPointUnreliableUtils::setAttributes, Common.ctx.getRuntimeManager()::getRunningMetaDataPoints, false);
    }

    public static void resetUnreliableDataPoint(DataPointRT dataPoint) {
        unreliable(Collections.singletonList(dataPoint), DataPointUnreliableUtils::setAttributes, Common.ctx.getRuntimeManager()::getRunningMetaDataPoints, false);
    }

    private static void unreliable(List<DataPointRT> dataPoints, BiConsumer<List<DataPointRT>, Boolean> setList,
                                   IntFunction<List<DataPointRT>> getData, boolean unreliable) {
        setList.accept(dataPoints, unreliable);
        for(DataPointRT dataPoint: dataPoints) {
            List<DataPointRT> metaDataPoints = getData.apply(dataPoint.getId());
            setList.accept(metaDataPoints, unreliable);
        }
    }

    private static void setAttributes(List<DataPointRT> dataPoints, boolean unreliable) {
        for (DataPointRT dp : dataPoints) {
            setAttribute(dp, unreliable);
        }
    }

    private static void setAttribute(DataPointRT dataPoint, boolean unreliable) {
        dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, unreliable);
    }
}
