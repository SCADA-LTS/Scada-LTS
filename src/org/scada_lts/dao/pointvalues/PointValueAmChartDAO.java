package org.scada_lts.dao.pointvalues;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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

    private static final String TABLE_NAME = "pointValues";
    private static final String COLUMN_DATAPOINT_ID = "dataPointId";
    private static final String COLUMN_TIMESTAMP = "ts";
    private static final String COLUMN_VALUE = "pointValue";

    private static final String SELECT_VALUES = "SELECT " +
            COLUMN_DATAPOINT_ID + ", " +
            COLUMN_VALUE + ", " +
            COLUMN_TIMESTAMP + " FROM " + TABLE_NAME;
    private static final String SELECT_ORDER = " ORDER BY " + COLUMN_TIMESTAMP + " ASC";

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
    private List<Map<String, Double>> convertToAmChartDataObject(List<DataPointSimpleValue> result) {
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

    private static class DataPointSimpleValue {
        int pointId;
        double value;
        long timestamp;

        DataPointSimpleValue(int pointId, double value, long timestamp) {
            this.pointId = pointId;
            this.value = value;
            this.timestamp = timestamp;
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
