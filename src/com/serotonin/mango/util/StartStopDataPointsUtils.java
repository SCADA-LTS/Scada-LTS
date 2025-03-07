package com.serotonin.mango.util;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.recursive.CollectMetaDataPointFromContextAction;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
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
        List<DataPointVO> sequenceDataPoints = new CopyOnWriteArrayList<>();
        Set<Integer> toCheck = new CopyOnWriteArraySet<>();
        int depth = 100;
        for(DataPointVO dataPoint: metaDataPoints) {
            collectMetaDataPointsFromContext(toCheck, sequenceDataPoints, dataPoint, depth, metaDataPoints, isExecute);
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
                                                         DataPointVO dataPoint, int depth, List<DataPointVO> dataPoints,
                                                         Predicate<Integer> isExecute) {

        CollectMetaDataPointFromContextAction metaDataPointCollector =
                new CollectMetaDataPointFromContextAction(toCheck, toRunning, dataPoint, depth, dataPoints, isExecute);
        try {
            metaDataPointCollector.call();
        } catch (Exception e) {
            LOG.error(LoggingUtils.exceptionInfo(e));
        }
    }
}
