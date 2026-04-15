package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.vo.DataPointVO;

import java.util.List;
import java.util.Map;

public interface IPointValueAmChartDAO {

    List<Map<String, Double>> getPointValuesFromRange(int[] points, long startTs, long endTs);

    List<Map<String, Double>> getPointValuesToCompareFromRange(int[] points, long startTs, long endTs);

    List<PointValueAmChartDAO.DataPointSimpleValue> aggregatePointValues(DataPointVO dataPoint, long startTs,
                                                                         long endTs, long intervalMs, int limit);

    List<PointValueAmChartDAO.DataPointSimpleValue> getPointValuesFromRangeWithLimit(int[] points, long startTs,
                                                                                      long endTs, int limit);

    List<Map<String, Double>> convertToAmChartCompareDataObject(List<PointValueAmChartDAO.DataPointSimpleValue> values,
                                                                int offset);

    List<Map<String, Double>> convertToAmChartDataObject(List<PointValueAmChartDAO.DataPointSimpleValue> values);
}
