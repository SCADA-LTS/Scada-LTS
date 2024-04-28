package com.serotonin.mango.util;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class StartStopDataPointsUtils {

    private static final Log LOG = LogFactory.getLog(StartStopDataPointsUtils.class);

    private StartStopDataPointsUtils() {}

    public static void startPoints(DataPointService dataPointService, Consumer<DataPointVO> doExecute,
                                   IntFunction<DataPointRT> getDataPointRunning, IntFunction<DataSourceRT> getDataSourceRunning) {

        List<DataPointVO> dataPoints = dataPointService.getDataPoints(null, true);
        Predicate<Integer> isExecute = id -> isNull(getDataPointRunning.apply(id));

        List<DataPointVO> nonMetaDataPoints = getNonMetaDataPoints(dataPoints);
        List<DataPointVO> metaDataPoints = getMetaDataPoints(dataPoints);
        List<DataPointVO> sequenceMetaDataPoints = getSequenceMetaDataPoints(isExecute, metaDataPoints);

        Collections.reverse(sequenceMetaDataPoints);
        execute(nonMetaDataPoints, sequenceMetaDataPoints, metaDataPoints,
                point -> point.isEnabled() && isExecute.test(point.getId())
                        && nonNull(getDataSourceRunning.apply(point.getDataSourceId())), doExecute);
    }

    public static void stopPoints(Collection<DataPointRT> running, Consumer<DataPointVO> doExecute,
                                  IntFunction<DataPointRT> getDataPointRunning) {

        List<DataPointRT> runningDataPoints = new ArrayList<>(running);
        List<DataPointVO> dataPoints = runningDataPoints.stream().map(DataPointRT::getVO).collect(Collectors.toList());
        Predicate<Integer> isExecute = id -> nonNull(getDataPointRunning.apply(id));

        List<DataPointVO> nonMetaDataPoints = getNonMetaDataPoints(dataPoints);
        List<DataPointVO> metaDataPoints = getMetaDataPoints(dataPoints);
        List<DataPointVO> sequenceMetaDataPoints = getSequenceMetaDataPoints(isExecute, metaDataPoints);

        execute(sequenceMetaDataPoints, metaDataPoints, nonMetaDataPoints,
                point -> isExecute.test(point.getId()), doExecute);
    }

    private static void execute(List<DataPointVO> firstExecute, List<DataPointVO> secondExecute,
                                List<DataPointVO> thirdExecute, Predicate<DataPointVO> isExecute,
                                Consumer<DataPointVO> doExecute) {
        execute(firstExecute, isExecute, doExecute);
        execute(secondExecute, isExecute, doExecute);
        execute(thirdExecute, isExecute, doExecute);
    }

    private static List<DataPointVO> getSequenceMetaDataPoints(Predicate<Integer> isExecute, List<DataPointVO> metaDataPoints) {
        List<DataPointVO> sequenceDataPoints = new ArrayList<>();
        Set<Integer> toCheck = new HashSet<>();
        int safe = 10;
        for(DataPointVO dataPoint: metaDataPoints) {
            collectMetaDataPointsFromContext(toCheck, sequenceDataPoints, dataPoint, safe, metaDataPoints, isExecute);
        }
        return sequenceDataPoints;
    }

    private static List<DataPointVO> getMetaDataPoints(List<DataPointVO> dataPoints) {
        return filter(dataPoints, point -> point.getPointLocator() instanceof MetaPointLocatorVO);
    }

    private static List<DataPointVO> getNonMetaDataPoints(List<DataPointVO> dataPoints) {
        return filter(dataPoints, point -> !(point.getPointLocator() instanceof MetaPointLocatorVO));
    }

    private static <T> List<T> filter(List<T> objects, Predicate<T> by) {
        return objects.stream()
                .filter(by)
                .collect(Collectors.toList());
    }

    private static <T> void execute(List<T> objects, Predicate<T> isExecute, Consumer<T> doExecute) {
        for(T dataPoint: objects) {
            if(isExecute.test(dataPoint)) {
                doExecute.accept(dataPoint);
            }
        }
    }

    private static void collectMetaDataPointsFromContext(Set<Integer> toCheck, List<DataPointVO> toRunning,
                                                         DataPointVO dataPoint, int safe, List<DataPointVO> dataPoints,
                                                         Predicate<Integer> isExecute) {
        if(safe < 0) {
            LOG.error("Recursion level exceeded: " + LoggingUtils.dataPointInfo(dataPoint));
            return;
        }
        if(dataPoint.isEnabled()) {
            PointLocatorVO pointLocator = dataPoint.getPointLocator();
            if(pointLocator instanceof MetaPointLocatorVO) {
                updateList(toCheck, toRunning, dataPoint);
                MetaPointLocatorVO metaPointLocator = (MetaPointLocatorVO) pointLocator;
                List<IntValuePair> context = metaPointLocator.getContext();
                if(context != null && !context.isEmpty()) {
                    for(IntValuePair intValuePair : context) {
                        if(intValuePair.getKey() > 0 && isExecute.test(intValuePair.getKey())) {
                            DataPointVO fromContextDataPoint = dataPoints.stream()
                                    .filter(point -> point.getId() == intValuePair.getKey())
                                    .findAny()
                                    .orElse(null);
                            if (fromContextDataPoint != null && (fromContextDataPoint.getPointLocator() instanceof MetaPointLocatorVO)) {
                                collectMetaDataPointsFromContext(toCheck, toRunning, fromContextDataPoint, --safe, dataPoints, isExecute);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void updateList(Set<Integer> toCheck, List<DataPointVO> toRunning, DataPointVO dataPoint) {
        if (toCheck.contains(dataPoint.getId())) {
            toRunning.removeIf(toRunningPoint -> toRunningPoint.getId() == dataPoint.getId());
        }
        toCheck.add(dataPoint.getId());
        toRunning.add(dataPoint);
    }
}
