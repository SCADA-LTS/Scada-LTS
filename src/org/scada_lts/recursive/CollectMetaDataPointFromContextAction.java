package org.scada_lts.recursive;


import com.serotonin.db.IntValuePair;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static org.scada_lts.utils.ValidationUtils.isCyclicDependency;

public class CollectMetaDataPointFromContextAction implements Callable<Void> {

    private static final Log LOG = LogFactory.getLog(CollectMetaDataPointFromContextAction.class);


    private final Set<Integer> toCheck;
    private final List<DataPointVO> toRunning;
    private final DataPointVO startDataPoint;
    private int depth;
    private final Map<Integer, DataPointVO> dataPoints;
    private final  Predicate<Integer> isExecute;

    public CollectMetaDataPointFromContextAction(Set<Integer> toCheck, List<DataPointVO> toRunning,
                                                 DataPointVO startDataPoint, int depth, Map<Integer, DataPointVO> dataPoints,
                                                 Predicate<Integer> isExecute) {
        this.startDataPoint = startDataPoint;
        this.toCheck = toCheck;
        this.toRunning = toRunning;
        this.dataPoints = dataPoints;
        this.isExecute = isExecute;
        this.depth = depth;
    }

    @Override
    public Void call() throws Exception {
        if(depth < 0) {
            throw new IllegalStateException("Recursion level exceeded: " + LoggingUtils.dataPointInfo(startDataPoint));
        }
        int temp = --depth;
        if(startDataPoint.isEnabled()) {
            PointLocatorVO pointLocator = startDataPoint.getPointLocator();
            if(pointLocator instanceof MetaPointLocatorVO) {
                updateList(toCheck, toRunning, startDataPoint);
                MetaPointLocatorVO metaPointLocator = (MetaPointLocatorVO) pointLocator;
                List<IntValuePair> context = metaPointLocator.getContext();
                if(context != null && !context.isEmpty()) {
                    List<Callable<Void>> tasks = new CopyOnWriteArrayList<>();
                    for(IntValuePair intValuePair : context) {
                        if(intValuePair.getKey() > 0 && isExecute.test(intValuePair.getKey())) {
                            DataPointVO fromContextDataPoint = dataPoints.get(intValuePair.getKey());
                            if (fromContextDataPoint != null
                                    && (fromContextDataPoint.getPointLocator() instanceof MetaPointLocatorVO)
                            && !isCyclicDependency(startDataPoint.getId(), fromContextDataPoint.getId(), 10, dataPoints)) {
                                tasks.add(new CollectMetaDataPointFromContextAction(toCheck, toRunning, fromContextDataPoint, temp, dataPoints, isExecute));
                            }
                        }
                    }

                    for(Callable<Void> task: tasks) {
                        try {
                            task.call();
                        } catch (Exception e) {
                            LOG.error(e.getMessage());
                            break;
                        }

                    }
                }
            }
        }
        return null;
    }

    private static void updateList(Set<Integer> toCheck, List<DataPointVO> toRunning, DataPointVO dataPoint) {
        if (toCheck.contains(dataPoint.getId())) {
            toRunning.removeIf(toRunningPoint -> toRunningPoint.getId() == dataPoint.getId());
        }
        toCheck.add(dataPoint.getId());
        toRunning.add(dataPoint);
    }
}
