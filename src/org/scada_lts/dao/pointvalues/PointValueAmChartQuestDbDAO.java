package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.scada_lts.dao.pointvalues.PointValueAmChartUtils.*;

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
public class PointValueAmChartQuestDbDAO implements IAmChartDAO{

    private static final Log LOG = LogFactory.getLog(PointValueAmChartQuestDbDAO.class);

    private static final String COLUMN_DATAPOINT_ID = "dataPointId";
    private static final String COLUMN_TS = "ts";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_VALUE = "pointValue";

    private static final String TABLE_POINT_VALUES = "pointValues$dpId";

    private static final String DATAPOINT_ID = "$dpId";

    private static final String SELECT_VALUES = "SELECT " +
            DATAPOINT_ID + " as " + COLUMN_DATAPOINT_ID + ", " +
            COLUMN_VALUE + ", " +
            COLUMN_TS + " FROM ";

    private static final String SELECT_ORDER = " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
    private static final String SELECT_ORDER_TS = " ORDER BY " + COLUMN_TS + " ASC";
    private static final String SELECT_LIMIT = " LIMIT ?";

    private static final String SELECT_AVG_VALUES_FOR_AGGREGATION = "SELECT " + COLUMN_DATAPOINT_ID + ", " +
            "tsGrouped, " +
            "avg(" + COLUMN_VALUE + ") AS " + COLUMN_VALUE + ", " +
            "max(" + COLUMN_TS + ") AS " + COLUMN_TS + " " +
            "FROM ";

    private static final String SELECT_MAX_VALUES_FOR_AGGREGATION = "SELECT " + DATAPOINT_ID + " as " + COLUMN_DATAPOINT_ID + ", " +
            "tsGrouped, " +
            "max(" + COLUMN_VALUE + ") AS " + COLUMN_VALUE + ", " +
            "max(" + COLUMN_TS + ") AS " + COLUMN_TS + " " +
            "FROM ";

    private static final String CONDITION_TIMESTAMP =" WHERE " + COLUMN_TIMESTAMP + " >= to_timezone($from, 'CET') AND " + COLUMN_TIMESTAMP + " <= to_timezone($to, 'CET')";
    private static final String GROUPBY_DATAPOINT_ID_TIMESTAMP = " GROUP BY " + COLUMN_DATAPOINT_ID + ", tsGrouped ";

    private static final String SELECT_FOR_GROUP_BY = "(SELECT " +
            DATAPOINT_ID + " as " + COLUMN_DATAPOINT_ID + ", " +
            "cast(" + COLUMN_VALUE + " as double) as " + COLUMN_VALUE + ", " +
            " round_down(ts/$divider, 0) as tsGrouped, " + COLUMN_TS + ", " +
            COLUMN_TIMESTAMP + " " +
            "FROM " + TABLE_POINT_VALUES + ") ";


    private JdbcOperations jdbcTemplate;

    public PointValueAmChartQuestDbDAO() {
        this.jdbcTemplate = DAO.getInstance().getJdbcTemp();
    }

    public PointValueAmChartQuestDbDAO(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    @Override
    public List<Map<String, Double>> getPointValuesFromRange(int[] pointIds, long startTs, long endTs) {
        List<DataPointSimpleValue> pvcList = new ArrayList<>();

        String selectTimestamp = prepareWhereTimestampStatement(startTs*1000, endTs*1000);

        for (int i = 0; i < pointIds.length; i++) {
            String dpId = String.valueOf(pointIds[i]);
            String sqlQuery = SELECT_VALUES.replace(DATAPOINT_ID, dpId) + TABLE_POINT_VALUES.replace(DATAPOINT_ID, dpId) + selectTimestamp + SELECT_ORDER;
            List<DataPointSimpleValue> pvcListPerPoint = jdbcTemplate.query(sqlQuery, new PointValueChartRowMapper());
            pvcList.addAll(pvcListPerPoint);
        }

        return convertToAmChartDataObject(pvcList);
    }

    @Override
    public List<Map<String, Double>> getPointValuesToCompareFromRange(int[] pointIds, long startTs, long endTs) {

        List<DataPointSimpleValue> pvcList = new ArrayList<>();

        String selectTimestamp = prepareWhereTimestampStatement(startTs*1000, endTs*1000);

        for (int i = 0; i < pointIds.length; i++) {
            String dpId = String.valueOf(pointIds[i]);
            String sqlQuery = SELECT_VALUES.replace(DATAPOINT_ID, dpId) + TABLE_POINT_VALUES.replace(DATAPOINT_ID, dpId) + selectTimestamp + SELECT_ORDER;
            List<DataPointSimpleValue> pvcListPerPoint = jdbcTemplate.query(sqlQuery, new PointValueChartRowMapper());
            pvcList.addAll(pvcListPerPoint);
        }

        return convertToAmChartCompareDataObject(pvcList, pointIds[0]);
    }

    @Override
    public List<DataPointSimpleValue> aggregatePointValues(DataPointVO dataPoint, long startTs, long endTs,
                                                           long intervalMs, int limit) {

        if(dataPoint == null) {
            LOG.warn("dataPoint is null!");
            return Collections.emptyList();
        }
        if(dataPoint.getPointLocator() == null) {
            LOG.warn(dataPointInfo(dataPoint));
            return Collections.emptyList();
        }
        QueryArgs aggregationQuery = toAggregationQuery(dataPoint.getId(), dataPoint.getPointLocator().getDataTypeId(), startTs, endTs, intervalMs, limit);
        return jdbcTemplate.query(aggregationQuery.getQuery(),
                aggregationQuery.getArgs(), new PointValueChartRowMapper());
    }

    @Override
    public List<DataPointSimpleValue> getPointValuesFromRangeWithLimit(int[] pointIds, long startTs, long endTs, int limit) {

        List<DataPointSimpleValue> pvcList = new ArrayList<>();

        String selectTimestamp = prepareWhereTimestampStatement(startTs*1000, endTs*1000);

        for (int i = 0; i < pointIds.length; i++) {
            String dpId = String.valueOf(pointIds[i]);
            String sqlQuery = SELECT_VALUES.replace(DATAPOINT_ID, dpId) + TABLE_POINT_VALUES.replace(DATAPOINT_ID, dpId) + selectTimestamp + SELECT_ORDER;
            List<DataPointSimpleValue> pvcListPerPoint = jdbcTemplate.query(sqlQuery, new PointValueChartRowMapper());
            pvcList.addAll(pvcListPerPoint);
        }

        return pvcList.stream().limit(limit).collect(Collectors.toList());
    }

    private String prepareWhereTimestampStatement(long startTs, long endTs) {
        StringBuilder statement = new StringBuilder();
        statement.append(" WHERE ").append(COLUMN_TIMESTAMP).append(" >= ").append("to_timezone(").append(startTs).append(", 'CET')")
                .append(" AND ").append(COLUMN_TIMESTAMP).append(" <= ").append("to_timezone(").append(endTs).append(", 'CET')");
        return statement.toString();
    }

    private static class PointValueChartRowMapper implements RowMapper<DataPointSimpleValue> {
        @Override
        public DataPointSimpleValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DataPointSimpleValue(
                    rs.getInt(COLUMN_DATAPOINT_ID),
                    rs.getDouble(COLUMN_VALUE),
                    rs.getLong(COLUMN_TS)
            );
        }
    }

    public static QueryArgs toAggregationQuery(int pointId, int dataType, long startTs, long endTs,
                                               long intervalMs, int limit) {
        String sqlQuery;

        List<Object> args = new ArrayList<>();

        String conditionTimestamp = CONDITION_TIMESTAMP.replace("$from", String.valueOf(startTs * 1000))
                .replace("$to", String.valueOf(endTs * 1000));
        switch (dataType) {
            case DataTypes.BINARY:
                sqlQuery = SELECT_MAX_VALUES_FOR_AGGREGATION +
                        SELECT_FOR_GROUP_BY.replace(DATAPOINT_ID, String.valueOf(pointId))
                                .replace("$divider", String.valueOf(intervalMs)) +
                        conditionTimestamp +
                        GROUPBY_DATAPOINT_ID_TIMESTAMP +
                        SELECT_ORDER_TS;
                break;
            case DataTypes.MULTISTATE:
                sqlQuery = SELECT_VALUES.replace(DATAPOINT_ID, String.valueOf(pointId)) +
                        TABLE_POINT_VALUES.replace(DATAPOINT_ID, String.valueOf(pointId)) +
                        conditionTimestamp +
                        SELECT_ORDER_TS;
                break;
            case DataTypes.NUMERIC:
                sqlQuery = SELECT_AVG_VALUES_FOR_AGGREGATION +
                        SELECT_FOR_GROUP_BY.replace(DATAPOINT_ID, String.valueOf(pointId))
                                .replace("$divider", String.valueOf(intervalMs)) +
                        conditionTimestamp +
                        GROUPBY_DATAPOINT_ID_TIMESTAMP +
                        SELECT_ORDER_TS;
                break;
            default:
                sqlQuery = SELECT_VALUES.replace(DATAPOINT_ID, String.valueOf(pointId)) +
                        TABLE_POINT_VALUES.replace(DATAPOINT_ID, String.valueOf(pointId)) +
                        conditionTimestamp +
                        SELECT_ORDER_TS;
        }

//        if(limit > 0) {
//            sqlQuery += SELECT_LIMIT;
//            args.add(limit);
//        }
        return new QueryArgs(sqlQuery, args.toArray());
    }

    public static class QueryArgs {
        private String query;
        private Object[] args;

        QueryArgs(String query, Object[] args) {
            this.query = query;
            this.args = args;
        }

        public String getQuery() {
            return query;
        }

        public Object[] getArgs() {
            return args;
        }
    }

    public static String dataPointInfo(DataPointVO dataPoint) {
        return MessageFormat.format("PointLocator is null for dataPoint: {0} (id: {1}, xid: {2}, dataSource: {3} (id:{4}))", dataPoint.getName(), dataPoint.getId(), dataPoint.getXid(), dataPoint.getDataSourceName(), dataPoint.getDataSourceId());
    }
}
