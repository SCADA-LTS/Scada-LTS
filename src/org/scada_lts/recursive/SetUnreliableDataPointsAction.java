package org.scada_lts.recursive;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SetUnreliableDataPointsAction implements Callable<Void> {

    private static final Logger LOG = LogManager.getLogger(SetUnreliableDataPointsAction.class);

    private static final String ATTR_UNRELIABLE_KEY = "UNRELIABLE";

    private final List<DataPointRT> dataPoints;
    private final boolean unreliable;
    private final int executeInPoolIfExceeds;
    private int depth;

    public SetUnreliableDataPointsAction(List<DataPointRT> dataPoints, boolean unreliable, int depth, int executeInPoolIfExceeds) {
        this.dataPoints = dataPoints;
        this.unreliable = unreliable;
        this.depth = depth;
        this.executeInPoolIfExceeds = executeInPoolIfExceeds;
    }

    @Override
    public Void call() throws Exception {
        setAttributes(filter(dataPoints, unreliable), unreliable);
        int temp = --depth;
        if(temp < 0) {
            LOG.warn("The safe counter has been exceeded!: {}", dataPoints.toString());
            return null;
        }
        Map<Set<String>, Callable<Void>> tasks = new ConcurrentHashMap<>();
        for(DataPointRT dataPoint: dataPoints) {
            RuntimeManager runtimeManager = Common.ctx.getRuntimeManager();
            List<DataPointRT> metaDataPoints = unreliable ? runtimeManager.getRunningMetaDataPointsToSet(dataPoint.getId()) : runtimeManager.getRunningMetaDataPointsToReset(dataPoint.getId());
            if(!metaDataPoints.isEmpty()) {
                Set<String> key = metaDataPoints.stream().map(point -> point.getVO().getXid()).collect(Collectors.toSet());
                tasks.put(key, new SetUnreliableDataPointsAction(metaDataPoints, unreliable, temp, executeInPoolIfExceeds));
            }
        }
        if(!tasks.isEmpty()) {
            LOG.info("invoke for: {}", tasks.keySet());
            if(tasks.size() <= executeInPoolIfExceeds) {
                for(Callable<?> task: tasks.values()) {
                    task.call();
                }
            } else {
                Common.ctx.getBackgroundProcessing().getCommonPool().invokeAll(tasks.values());
            }
        }
        return null;
    }

    public static boolean isSetUnreliable(DataPointRT dataPoint, boolean unreliable) {
        return dataPoint.getAttribute(ATTR_UNRELIABLE_KEY) instanceof Boolean
                && ((boolean) dataPoint.getAttribute(ATTR_UNRELIABLE_KEY)) == unreliable;
    }

    private static List<DataPointRT> filter(List<DataPointRT> dataPoints, boolean unreliable) {
        return dataPoints.stream().filter(dataPoint -> !isSetUnreliable(dataPoint, unreliable))
                .collect(Collectors.toList());
    }

    private static void setAttributes(List<DataPointRT> dataPoints, boolean unreliable) {
        for (DataPointRT dataPoint : dataPoints) {
            dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, unreliable);
        }
    }
}