package utils;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.vo.bean.LongPair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PointValueDaoTestImpl implements IPointValueDao {

    private final Map<Integer, Map<Integer, PointValueTime>> values = new HashMap<>();

    private static final AtomicInteger SERIAL = new AtomicInteger();

    @Override
    public void savePointValueAsync(int dataPointId, PointValueTime pvt, SetPointSource source) {
        values.putIfAbsent(dataPointId, new HashMap<>());
        values.get(dataPointId).put(SERIAL.getAndIncrement(), pvt);
    }

    @Override
    public PointValueTime savePointValueSync(int dataPointId, PointValueTime pvt, SetPointSource source) {
        values.putIfAbsent(dataPointId, new HashMap<>());
        values.get(dataPointId).put(SERIAL.getAndIncrement(), pvt);
        return pvt;
    }

    @Override
    public PointValueTime getLatestPointValue(int dataPointId) {
        if(!values.containsKey(dataPointId) || values.get(dataPointId).isEmpty())
            return null;
        return values.get(dataPointId).get(values.get(dataPointId).size() - 1);
    }

    @Override
    public List<PointValueTime> getLatestPointValues(int dataPointId, int size) {
        if(values.isEmpty())
            return Collections.emptyList();
        if(!values.containsKey(dataPointId))
            return Collections.emptyList();
        if(values.get(dataPointId).isEmpty())
            return Collections.emptyList();
        int valuesSize = values.get(dataPointId).size();
        if(valuesSize < size)
            return getLatestPointValueTimes(dataPointId, valuesSize);
        return getLatestPointValueTimes(dataPointId, size);
    }

    private List<PointValueTime> getLatestPointValueTimes(int dataPointId, int valuesSize) {
        return values.entrySet()
                .stream()
                .filter(a -> a.getKey().equals(dataPointId))
                .flatMap(a -> a.getValue().entrySet().stream())
                .sorted((a, b) -> b.getKey().compareTo(a.getKey()))
                .limit(valuesSize)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void savePointValue(int pointId, PointValueTime pointValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PointValueTime> getPointValues(int dataPointId, long since) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PointValueTime> getPointValuesBetween(int dataPointId, long from, long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PointValueTime getPointValueBefore(int dataPointId, long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PointValueTime getPointValueAt(int dataPointId, long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deletePointValuesBefore(int dataPointId, long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deletePointValues(int dataPointId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deleteAllPointData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long deletePointValuesWithMismatchedType(int dataPointId, int dataType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void compressTables() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long dateRangeCount(int dataPointId, long from, long to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getInceptionDate(int dataPointId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getStartTime(List<Integer> dataPointIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getEndTime(List<Integer> dataPointIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Long> getFiledataIds() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PointValueTime> getLatestPointValues(int dataPointId, int limit, long before) {
        throw new UnsupportedOperationException();
    }
}
