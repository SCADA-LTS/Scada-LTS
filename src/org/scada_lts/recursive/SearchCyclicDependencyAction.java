package org.scada_lts.recursive;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

public class SearchCyclicDependencyAction implements Callable<Void> {

    private final int starDataPointId;
    private final int findDataPointId;
    private final Map<Integer, DataPointVO> dataPoints;
    private final Set<Boolean> result;
    private int depth;

    public SearchCyclicDependencyAction(int starDataPointId, int findDataPointId, Map<Integer, DataPointVO> dataPoints,
                                        int depth, Set<Boolean> result) {
        this.dataPoints = dataPoints;
        this.starDataPointId = starDataPointId;
        this.findDataPointId = findDataPointId;
        this.depth = depth;
        this.result = result;
    }

    @Override
    public Void call() {

        if(starDataPointId == findDataPointId) {
            result.add(true);
            return null;
        }

        if(result.size() > 1 || result.contains(true)) {
            return null;
        }

        if(depth < 0) {
            result.add(false);
            return null;
        }

        DataPointVO dataPoint = dataPoints.get(starDataPointId);
        PointLocatorVO pointLocator = dataPoint.getPointLocator();
        if(pointLocator instanceof MetaPointLocatorVO) {
            MetaPointLocatorVO metaPointLocator = (MetaPointLocatorVO) pointLocator;
            List<IntValuePair> context = metaPointLocator.getContext();
            if (context == null || context.isEmpty()) {
                result.add(false);
                return null;
            }
            int temp = --depth;
            List<Callable<Void>> tasks = new CopyOnWriteArrayList<>();
            for (IntValuePair keyValue : context) {
                int contextDataPointId = keyValue.getKey();
                DataPointVO contextDataPoint = dataPoints.get(contextDataPointId);
                if(contextDataPoint != null && (contextDataPoint.getPointLocator() instanceof MetaPointLocatorVO)) {
                    if (contextDataPointId == findDataPointId) {
                        result.add(true);
                        return null;
                    } else {
                        tasks.add(new SearchCyclicDependencyAction(contextDataPointId, findDataPointId, dataPoints, temp, result));
                    }
                }
            }
            if(!tasks.isEmpty())
                Common.ctx.getBackgroundProcessing().getCommonPool().invokeAll(tasks);
        }
        result.add(false);
        return null;
    }
}