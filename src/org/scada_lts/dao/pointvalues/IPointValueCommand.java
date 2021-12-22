package org.scada_lts.dao.pointvalues;

import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.model.point.PointValueAdnnotation;

import java.util.List;

public interface IPointValueCommand {

    long deletePointValuesBeforeWithOutLast(int dataPointId, long time);
    long deletePointValue(int dataPointId);
    long deleteAllPointData();
    long deletePointValuesWithMismatchedType(int dataPointId, int dataType);
    void executeBatchUpdateInsert(List<Object[]> params);
    void create(PointValue pointValue, PointValueAdnnotation pointValueAdnnotation, int dataType);
    Object[] create(int pointId, int dataType, double dvalue, long time);
    Object[] create(PointValue entity);
    Object[] createNoTransaction(int pointId, int dataType, double dvalue, long time);
    long deletePointValuesBeforeWithOutLastTwo(int dataPointId, long time);
    long deletePointValuesWithValueLimit(int dataPointId, int limit);
}
