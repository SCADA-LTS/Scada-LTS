package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.vo.DataPointVO;

import java.util.List;
import java.util.Map;

public interface IAmChartDAO {
    List<Map<String, Double>> getPointValuesFromRange(int[] pointIds, long startTs, long endTs);
    List<Map<String, Double>> getPointValuesToCompareFromRange(int[] pointIds, long startTs, long endTs);
    List<DataPointSimpleValue> getPointValuesFromRangeWithLimit(int[] pointIds, long startTs, long endTs, int limit);
    List<DataPointSimpleValue> aggregatePointValues(DataPointVO dataPoint, long startTs, long endTs,
                                                    long intervalMs, int limit);
}
