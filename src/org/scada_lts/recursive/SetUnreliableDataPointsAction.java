package org.scada_lts.recursive;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class SetUnreliableDataPointsAction implements Callable<Void> {

    private static final Log LOG = LogFactory.getLog(SetUnreliableDataPointsAction.class);

    private static final String ATTR_UNRELIABLE_KEY = "UNRELIABLE";

    private final List<DataPointRT> dataPoints;
    private final boolean unreliable;
    private int depth;

    public SetUnreliableDataPointsAction(List<DataPointRT> dataPoints, boolean unreliable, int depth) {
        this.dataPoints = dataPoints;
        this.unreliable = unreliable;
        this.depth = depth;
    }

    @Override
    public Void call() throws Exception {
        setAttributes(filter(dataPoints, unreliable), unreliable);
        int temp = --depth;
        List<Callable<Void>> tasks = new ArrayList<>();
        for(DataPointRT dataPoint: dataPoints) {
            List<DataPointRT> metaDataPoints = Common.ctx.getRuntimeManager().getRunningMetaDataPoints(dataPoint.getId(), !unreliable);
            if(!metaDataPoints.isEmpty()) {
                if(temp > -1) {
                    tasks.add(new SetUnreliableDataPointsAction(metaDataPoints, unreliable, temp));
                } else {
                    LOG.warn("The safe counter has been exceeded!: " + LoggingUtils.dataPointInfo(dataPoint));
                    setAttributes(filter(metaDataPoints, unreliable), unreliable);
                    return null;
                }
            }
        }
        if(!tasks.isEmpty())
            Common.ctx.getBackgroundProcessing().getCommonPool().invokeAll(tasks);

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