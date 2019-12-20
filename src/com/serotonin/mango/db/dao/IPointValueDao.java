package com.serotonin.mango.db.dao;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.vo.bean.LongPair;

import java.util.List;

public interface IPointValueDao {
    PointValueTime savePointValueSync(int pointId,
                                      PointValueTime pointValue, SetPointSource source);

    void savePointValueAsync(int pointId, PointValueTime pointValue,
                             SetPointSource source);

    void savePointValue(int pointId, PointValueTime pointValue);

    List<PointValueTime> getPointValues(int dataPointId, long since);

    List<PointValueTime> getPointValuesBetween(int dataPointId,
                                               long from, long to);

    List<PointValueTime> getLatestPointValues(int dataPointId, int limit);

    List<PointValueTime> getLatestPointValues(int dataPointId,
                                              int limit, long before);

    PointValueTime getLatestPointValue(int dataPointId);

    PointValueTime getPointValueBefore(int dataPointId, long time);

    PointValueTime getPointValueAt(int dataPointId, long time);

    long deletePointValuesBefore(int dataPointId, long time);

    long deletePointValues(int dataPointId);

    long deleteAllPointData();

    long deletePointValuesWithMismatchedType(int dataPointId,
                                             int dataType);

    void compressTables();

    long dateRangeCount(int dataPointId, long from, long to);

    long getInceptionDate(int dataPointId);

    long getStartTime(List<Integer> dataPointIds);

    long getEndTime(List<Integer> dataPointIds);

    LongPair getStartAndEndTime(List<Integer> dataPointIds);

    List<Long> getFiledataIds();
}
