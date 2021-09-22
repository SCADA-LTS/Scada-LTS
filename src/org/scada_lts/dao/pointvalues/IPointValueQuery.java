package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import org.scada_lts.dao.model.point.PointValue;

import java.sql.ResultSet;
import java.util.List;

public interface IPointValueQuery {

    double applyBounds(double value);
    long dateRangeCount(int dataPointId, long from, long to);
    long getStartTime(List<Integer> dataPointIds);
    long getEndTime(List<Integer> dataPointIds);
    long getMinTs(int dataPointId);
    long getMaxTs(int dataPointId);
    Long getInceptionDate(int dataPointId);
    Long getLatestPointValue(int dataPointId);
    //PointValue findById(Object[] pk);
    PointValue getPointValueRow(ResultSet rs, int rowNum);
  //  PointValueTime getPointValue(long id);
    LongPair getStartAndEndTime(List<Integer> dataPointIds);
    List<Long> getFiledataIds();
    List<PointHistoryCount> getTopPointHistoryCounts();

    List<PointValue> findAll();
    List<PointValue> findAllWithUserName();
    List<PointValue> findByIdAndTs(long id, long ts);
    List<PointValue> filtered(String filter, Object[] argsFilter, long limit);

    void createTableForDatapoint(int pointId);
}
