package org.scada_lts.dao.pointvalues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class PointValueAmChartUtils {

    private PointValueAmChartUtils() {}

    public static List<Map<String, Double>> convertToAmChartCompareDataObject(List<DataPointSimpleValue> result, int basePointId) {
        List<Map<String, Double>> chartData = new ArrayList<>();
        Map<String, Double> entry = new HashMap<>();

        for (DataPointSimpleValue pvc: result) {
            if(pvc.pointId == basePointId) {
                entry.put("date", (double) pvc.timestamp);
            } else {
                entry.put("date2", (double) pvc.timestamp);
            }
            entry.put(String.valueOf(pvc.pointId), pvc.value);
            chartData.add(entry);
            entry = new HashMap<>();
        }
        return chartData;
    }

    /**
     * Convert from SQL objects to AmChart Data interface
     *
     * Convert single SQL rows to match AmChart Data object Interface
     * that looks like example below:
     * [
     *  {
     *    date: timestamp
     *    seriesName1: value
     *    seriesName2: value
     *    ...
     *    seriesNameN: value
     *  }
     * ]
     *
     * @param result    SQL result of Statement
     * @return          List of Java Maps containing values for data points in specific timestamp
     */
    public static List<Map<String, Double>> convertToAmChartDataObject(List<DataPointSimpleValue> result) {
        List<Map<String, Double>> chartData = new ArrayList<>();
        Map<String, Double> entry = new HashMap<>();
        AtomicLong lastTimestamp = new AtomicLong();

        for (DataPointSimpleValue pvc: result) {
            if (lastTimestamp.get() != pvc.timestamp) {
                if(!entry.isEmpty()) {
                    chartData.add(entry);
                    entry = new HashMap<>();
                }
                entry.put("date", (double) pvc.timestamp);
            }
            entry.put(String.valueOf(pvc.pointId), pvc.value);
            lastTimestamp.set(pvc.timestamp);
        }
        chartData.add(entry);
        return chartData;
    }
}
