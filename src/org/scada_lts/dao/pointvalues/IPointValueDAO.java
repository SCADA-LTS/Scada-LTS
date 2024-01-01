package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import org.scada_lts.dao.model.point.PointValue;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IPointValueDAO {
    List<PointValue> findAll();

    PointValue findById(Object[] pk);

    List<PointValue> findByIdAndTs(long id, long ts);

    List<PointValue> filtered(String filter, Object[] argsFilter, long limit);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    Object[] create(PointValue entity);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    Object[] create(int pointId, int dataType, double dvalue, long time);

    Object[] createNoTransaction(int pointId, int dataType, double dvalue, long time);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void executeBatchUpdateInsert(List<Object[]> params);

    Long getInceptionDate(int dataPointId);

    long dateRangeCount(int dataPointId, long from, long to);

    LongPair getStartAndEndTime(List<Integer> dataPointIds);

    long getStartTime(List<Integer> dataPointIds);

    long getEndTime(List<Integer> dataPointIds);

    List<Long> getFiledataIds();

    Long getLatestPointValue(int dataPointId);

    // Apply database specific bounds on double values.
    double applyBounds(double value);

    @Deprecated
    long deletePointValuesBeforeWithOutLast(int dataPointId, long time);

    long deletePointValuesBeforeWithOutLastTwo(int dataPointId, long time);

    long deletePointValue(int dataPointId);

    long deleteAllPointData();

    long deletePointValuesWithMismatchedType(int dataPointId, int dataType);

    long deletePointValuesWithValueLimit(int dataPointId, int limit);

    PointValueTime getPointValue(long id);

    long getMinTs(int dataPointId);

    long getMaxTs(int dataPointId);

    List<PointHistoryCount> getTopPointHistoryCounts();

    int createAnnotation(long pointValueId, String textPointValueShort, String textPointValueLong, int sourceType, int sourceId);

    void updateAnnotation(int userId);
}
