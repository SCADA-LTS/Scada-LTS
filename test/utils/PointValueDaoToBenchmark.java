package utils;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.bean.LongPair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PointValueDaoToBenchmark implements IPointValueDao {

    private final List<PointValueTime> values;

    public PointValueDaoToBenchmark(int maxSize) {
        values = _generate(maxSize);
    }

    @Override
    public void savePointValueAsync(int dataPointId, PointValueTime pvt, SetPointSource source) {
    }

    @Override
    public PointValueTime savePointValueSync(int dataPointId, PointValueTime pvt, SetPointSource source) {
        return pvt;
    }

    @Override
    public PointValueTime getLatestPointValue(int dataPointId) {
        return values.get(0);
    }

    @Override
    public List<PointValueTime> getLatestPointValues(int dataPointId, int size) {
        return values.subList(0, size);
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

    private List<PointValueTime> _generate(int maxSize) {
        return IntStream.range(0, maxSize)
                .mapToObj(a -> new PointValueTime(MangoValue.stringToValue(a + "", 3),
                        System.nanoTime()))
                .collect(Collectors.toList());
    }
}
