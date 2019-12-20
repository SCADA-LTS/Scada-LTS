package utils;

import com.serotonin.mango.db.dao.IPointValueDao;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.bean.LongPair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PointValueDaoTestImpl implements IPointValueDao {

    private Map<Integer, List<PointValueTime>> values = new HashMap<>();

    @Override
    public void savePointValueAsync(int dataPointId, PointValueTime pvt, SetPointSource source) {
        values.putIfAbsent(dataPointId, new ArrayList<>());
        values.get(dataPointId).add(pvt);
    }

    @Override
    public PointValueTime savePointValueSync(int dataPointId, PointValueTime pvt, SetPointSource source) {
        values.putIfAbsent(dataPointId, new ArrayList<>());
        values.get(dataPointId).add(pvt);
        return pvt;
    }

    @Override
    public PointValueTime getLatestPointValue(int dataPointId) {
        if(!values.containsKey(dataPointId) || values.get(dataPointId).isEmpty())
            return null;
        return values.get(dataPointId).get(0);
    }

    @Override
    public List<PointValueTime> getLatestPointValues(int dataPointId, int size) {
        if(values.isEmpty())
            return Collections.emptyList();
        if(!values.containsKey(dataPointId))
            return Collections.emptyList();
        if(values.get(dataPointId).isEmpty())
            return Collections.emptyList();
        if(values.size() < size)
            return new ArrayList<>(values.get(dataPointId)).subList(0, values.size() - 1);
        return new ArrayList<>(values.get(dataPointId)).subList(0, size);
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
