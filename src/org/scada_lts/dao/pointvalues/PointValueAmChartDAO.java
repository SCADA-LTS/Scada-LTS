package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.DataPointDAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.scada_lts.utils.AggregateUtils.getNumberOfValuesLimit;

/**
 * Point Value DAO for AmChart Interface
 *
 * Improve performance of DataPoint Values access and reduce
 * unnecessary steps. Fetch values for all selected data points
 * in just one query to reduce database access. Operate only
 * on simple objects that does not contains any additional properties
 * to reduce the memory usage. Match result with AmChart data interface.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
public class PointValueAmChartDAO {

    private static final Log LOG = LogFactory.getLog(PointValueAmChartDAO.class);

    private static final DataPointDAO dataPointDAO = new DataPointDAO();

    private static final String TABLE_NAME = "pointValues";
    private static final String COLUMN_DATAPOINT_ID = "dataPointId";
    private static final String COLUMN_TIMESTAMP = "ts";
    private static final String COLUMN_VALUE = "pointValue";

    private static final String SELECT_VALUES = "SELECT " +
            COLUMN_DATAPOINT_ID + ", " +
            COLUMN_VALUE + ", " +
            COLUMN_TIMESTAMP + " FROM " + TABLE_NAME;
    private static final String SELECT_ORDER = " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
    private static final String SELECT_LIMIT = " LIMIT ";

    private static final String SELECT_VALUES_FOR_AGGREGATION = "SELECT " + COLUMN_DATAPOINT_ID + ", " +
            "avg(" + COLUMN_VALUE + ") AS " + COLUMN_VALUE + ", " +
            "max(" + COLUMN_TIMESTAMP + ") AS " + COLUMN_TIMESTAMP + ", " +
            "substring(from_unixtime(max(" + COLUMN_TIMESTAMP + ")/1000),1,19) AS time " +
            "FROM " + TABLE_NAME + " ";

    private static final String CONDITION_TIMESTAMP =" AND " + COLUMN_TIMESTAMP + " >=? AND " + COLUMN_TIMESTAMP + " <=?";
    private static final String GROUPBY_DATAPOINT_ID_TIMESTAMP = " GROUP BY " + COLUMN_DATAPOINT_ID + ", floor(" + COLUMN_TIMESTAMP + "/?)";


    /**
     * Get Point Values from Time Range
     *
     * Get Point Values for one or multiple Data Points
     * using just one SQL query. Limit result to match the
     * time range constraint.
     *
     * @param pointIds  Array of PointIDs
     * @param startTs   Starting Timestamp Range value
     * @param endTs     Ending Timestamp Range value
     * @return          List with dynamic Map of point values
     */
    public List<Map<String, Double>> getPointValuesFromRange(int[] pointIds, long startTs, long endTs) {

        String selectDataPoints = prepareWhereDataPointIdsStatement(pointIds);
        String selectTimestamp = prepareWhereTimestampStatement(startTs, endTs);

        String sqlQuery = SELECT_VALUES + selectDataPoints + selectTimestamp + SELECT_ORDER;

        List<DataPointSimpleValue> pvcList = DAO.getInstance().getJdbcTemp().query(sqlQuery, new PointValueChartRowMapper());

        return convertToAmChartDataObject(pvcList);

    }

    public List<Map<String, Double>> getPointValuesToCompareFromRange(int[] pointIds, long startTs, long endTs) {

        String selectDataPoints = prepareWhereDataPointIdsStatement(pointIds);
        String selectTimestamp = prepareWhereTimestampStatement(startTs, endTs);

        String sqlQuery = SELECT_VALUES + selectDataPoints + selectTimestamp + SELECT_ORDER;

        List<DataPointSimpleValue> pvcList = DAO.getInstance().getJdbcTemp().query(sqlQuery, new PointValueChartRowMapper());

        return convertToAmChartCompareDataObject(pvcList, pointIds[0]);

    }

    public List<Map<String, Double>> getPointValuesToCompareFromRange(int[] pointIds, long startTs, long endTs, long aggregateMinDiffMs) {

        List<DataPointSimpleValue> pvcList = getGroupedPvcList(pointIds, startTs, endTs, aggregateMinDiffMs);

        return convertToAmChartCompareDataObject(pvcList, pointIds[0]);
    }

    public List<Map<String, Double>> getPointValuesFromRange(int[] pointIds, long startTs, long endTs, long aggregateMinDiffMs) {

        List<DataPointSimpleValue> pvcList = getGroupedPvcList(pointIds, startTs, endTs, aggregateMinDiffMs);

        return convertToAmChartDataObject(pvcList);
    }

    public List<DataPointSimpleValue> getPointValuesFromRangeWithLimit(int[] pointIds, long startTs, long endTs, int limit) {

        String selectDataPoints = prepareWhereDataPointIdsStatement(pointIds);
        String selectTimestamp = prepareWhereTimestampStatement(startTs, endTs);

        String sqlQuery = SELECT_VALUES + selectDataPoints + selectTimestamp + SELECT_ORDER + SELECT_LIMIT + limit;

        List<DataPointSimpleValue> pvcList = DAO.getInstance().getJdbcTemp().query(sqlQuery, new PointValueChartRowMapper());

        return pvcList;
    }

    public List<Map<String, Double>> convertToAmChartCompareDataObject(List<DataPointSimpleValue> result, int basePointId) {
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

    private Map<Integer, List<Integer>> groupByDataType(int[] pointIds) {
        Map<Integer, List<Integer>> datapoints = new HashMap<>();
        for (int id : pointIds) {
            DataPointVO dp = dataPointDAO.getDataPoint(id);
            int dataPointType = dp.getPointLocator().getDataTypeId();
            if (!datapoints.containsKey(dataPointType))
                datapoints.put(dataPointType, new ArrayList<Integer>());
            datapoints.get(dataPointType).add(id);
        }
        return datapoints;
    }

    private List<DataPointSimpleValue> getGroupedPvcList(int[] pointIds, long startTs, long endTs, long aggregateMinDiffMs) {
        Map<Integer, List<Integer>> groupedByDataType = groupByDataType(pointIds);

        List<DataPointSimpleValue> pvcListAll = new ArrayList<DataPointSimpleValue>();

        for (Map.Entry<Integer, List<Integer>> entry : groupedByDataType.entrySet()) {
            String selectDataPointsGrouped = prepareWhereDataPointIdsStatement(entry.getValue().stream().mapToInt(i->i).toArray());

            String sqlQueryGrouped = SELECT_VALUES_FOR_AGGREGATION +
                    selectDataPointsGrouped +
                    CONDITION_TIMESTAMP +
                    GROUPBY_DATAPOINT_ID_TIMESTAMP +
                    SELECT_ORDER;

            if (entry.getKey() == DataTypes.BINARY) {
                sqlQueryGrouped = sqlQueryGrouped.replaceFirst("avg", "max");
            } else if (entry.getKey() == DataTypes.MULTISTATE){
                sqlQueryGrouped = sqlQueryGrouped + SELECT_LIMIT + getNumberOfValuesLimit();
            }

            List<DataPointSimpleValue> pvcListGrouped = DAO.getInstance().getJdbcTemp().query(sqlQueryGrouped,
                    new Object[]{startTs, endTs, aggregateMinDiffMs},
                    new PointValueChartRowMapper());

            pvcListAll.addAll(pvcListGrouped);
        }

        pvcListAll.sort(Comparator.comparing(DataPointSimpleValue::getTimestamp));

        return pvcListAll;
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
    public List<Map<String, Double>> convertToAmChartDataObject(List<DataPointSimpleValue> result) {
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

    private String prepareWhereDataPointIdsStatement(int[] pointIds) {
        if (pointIds.length == 0) {
            LOG.error("Requested PointList is empty!");
        }

        StringBuilder statement = new StringBuilder();
        statement.append(" WHERE (").append(COLUMN_DATAPOINT_ID).append("=").append(pointIds[0]);

        if(pointIds.length > 1) {
            for (int i = 1; i < pointIds.length; i++) {
                statement.append(" OR ").append(COLUMN_DATAPOINT_ID).append("=").append(pointIds[i]);
            }
        }

        statement.append(")");
        return statement.toString();
    }

    private String prepareWhereTimestampStatement(long startTs, long endTs) {
        StringBuilder statement = new StringBuilder();
        statement.append(" AND (").append(COLUMN_TIMESTAMP).append(" >= ").append(startTs)
                .append(" AND ").append(COLUMN_TIMESTAMP).append(" <= ").append(endTs).append(")");
        return statement.toString();
    }

    public static class DataPointSimpleValue {
        int pointId;
        double value;
        long timestamp;

        DataPointSimpleValue(int pointId, double value, long timestamp) {
            this.pointId = pointId;
            this.value = value;
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    private static class PointValueChartRowMapper implements RowMapper<DataPointSimpleValue> {
        @Override
        public DataPointSimpleValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DataPointSimpleValue(
                    rs.getInt(COLUMN_DATAPOINT_ID),
                    rs.getDouble(COLUMN_VALUE),
                    rs.getLong(COLUMN_TIMESTAMP)
            );
        }
    }

}
