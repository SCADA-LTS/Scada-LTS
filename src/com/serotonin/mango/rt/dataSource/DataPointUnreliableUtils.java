package com.serotonin.mango.rt.dataSource;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scada_lts.recursive.SetUnreliableDataPointsAction;
import org.scada_lts.utils.SystemSettingsUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class DataPointUnreliableUtils {

    private static final Logger LOG = LogManager.getLogger(DataPointUnreliableUtils.class);
    private static final int DEPTH = SystemSettingsUtils.getDataPointUnreliableDepthNumber();
    private static final int EXECUTE_IN_POOL_IF_EXCEEDS_NUMBER = SystemSettingsUtils.getDataPointUnreliableExecuteInPoolIfTasksExceedsNumber();

    private DataPointUnreliableUtils() {}


    public static void setUnreliableDataPoints(List<DataPointRT> dataPoints) {
        unreliable(dataPoints.stream().filter(dataPoint -> !dataPoint.isUnreliable()).collect(Collectors.toList()), true);
    }

    public static void setUnreliableDataPoint(DataPointRT dataPoint) {
        if(!dataPoint.isUnreliable()) {
            unreliable(Collections.singletonList(dataPoint), true);
        }
    }

    public static void resetUnreliableDataPoints(List<DataPointRT> dataPoints) {
        unreliable(dataPoints.stream().filter(DataPointRT::isUnreliable).collect(Collectors.toList()), false);
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

    public static void setUnreliableDataPoints(EventType type, DataSourceRT dataSourceRT) {
        doUnreliable(type, dataSourceRT, DataPointUnreliableUtils::setUnreliableDataPoints, DataPointUnreliableUtils::setUnreliableDataPoint);
    }

    public static void resetUnreliableDataPoints(EventType type, DataSourceRT dataSourceRT) {
        doUnreliable(type, dataSourceRT, DataPointUnreliableUtils::resetUnreliableDataPoints, DataPointUnreliableUtils::resetUnreliableDataPoint);
    }

    private static void doUnreliable(EventType type, DataSourceRT dataSourceRT,
                                    Consumer<List<DataPointRT>> list, Consumer<DataPointRT> single) {
        if(dataSourceRT != null && type instanceof DataSourceEventType) {
            List<DataPointRT> dataPoints = dataSourceRT.getDataPoints();
            DataSourceEventType dataSourceEventType = (DataSourceEventType) type;
            if(dataSourceRT.doSetUnreliableDataPoint(dataSourceEventType.getDataSourceEventTypeId())) {
                if (type.getDataPointId() == -1) {
                    list.accept(dataPoints);
                } else {
                    for (DataPointRT dataPoint : dataPoints) {
                        if (dataPoint.getId() == type.getDataPointId()) {
                            single.accept(dataPoint);
                        }
                    }
                }
            }
        }
    }
}