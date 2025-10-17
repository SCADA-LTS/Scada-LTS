package utils;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class StartStopDataPointsTestUtils {

    private StartStopDataPointsTestUtils() {}

    public static Consumer<DataPointVO> startPoints(Map<Integer, DataPointRT> runningDataPoints) {
        return point -> {
            if (isRunning(runningDataPoints, point.getId()))
                throw new IllegalStateException("Already running!");
            if (point.getPointLocator() instanceof MetaPointLocatorVO) {
                MetaPointLocatorVO pointLocator = point.getPointLocator();
                List<IntValuePair> context = pointLocator.getContext();
                if (Objects.nonNull(context) && !context.isEmpty() && !isRunningContext(runningDataPoints, context)) {
                    throw new IllegalStateException("Context not running!");
                }
            }
            point.setEnabled(true);
            runningDataPoints.put(point.getId(), new DataPointRT(point, null));
        };
    }

    public static Consumer<DataPointVO> stopPoints(Map<Integer, DataPointRT> runningDataPoints) {
        return point -> {
            if (!isRunning(runningDataPoints, point.getId()))
                throw new IllegalStateException("Not running!");
            if (point.getPointLocator() instanceof MetaPointLocatorVO) {
                MetaPointLocatorVO pointLocator = point.getPointLocator();
                List<IntValuePair> context = pointLocator.getContext();
                if (Objects.nonNull(context) && !context.isEmpty() && !isRunningContext(runningDataPoints, context)) {
                    throw new IllegalStateException("Context not running!");
                }
            }
            point.setEnabled(false);
            runningDataPoints.entrySet().removeIf(a -> a.getKey() == point.getId());
        };
    }

    private static boolean isRunningContext(Map<Integer, DataPointRT> runningDataPoints, List<IntValuePair> context) {
        return context.stream().map(IntValuePair::getKey).allMatch(id -> isRunning(runningDataPoints, id));
    }

    private static boolean isRunning(Map<Integer, DataPointRT> runningDataPoints, int dataPointId) {
        return Objects.nonNull(runningDataPoints.get(dataPointId));
    }
}
