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
public class PointValueAmChartDAO implements IAmChartDAO {

    private static final Log LOG = LogFactory.getLog(PointValueAmChartDAO.class);

    private static final String TABLE_NAME_DENORMALIZED = "pointValuesDenormalized";

    private static final String TABLE_NAME = "pointValues";
    private static final String COLUMN_DATAPOINT_ID = "dataPointId";
    private static final String COLUMN_TIMESTAMP = "ts";
    private static final String COLUMN_VALUE = "pointValue";
    private static final String COLUMN_DATA_TYPE = "dataType";


    private static final String SELECT_VALUES = "SELECT " +
            COLUMN_DATAPOINT_ID + ", " +
            COLUMN_VALUE + ", " +
            COLUMN_TIMESTAMP + " FROM ";
    private static final String SELECT_ORDER = " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
    private static final String SELECT_LIMIT = " LIMIT ?";

    private static final String SELECT_AVG_VALUES_FOR_AGGREGATION = "SELECT " + COLUMN_DATAPOINT_ID + ", " +
            "avg(" + COLUMN_VALUE + ") AS " + COLUMN_VALUE + ", " +
            "max(" + COLUMN_TIMESTAMP + ") AS " + COLUMN_TIMESTAMP + " " +
            "FROM ";

    private static final String SELECT_MAX_VALUES_FOR_AGGREGATION = "SELECT " + COLUMN_DATAPOINT_ID + ", " +
            "max(" + COLUMN_VALUE + ") AS " + COLUMN_VALUE + ", " +
            "max(" + COLUMN_TIMESTAMP + ") AS " + COLUMN_TIMESTAMP + " " +
            "FROM ";

    private static final String CONDITION_TIMESTAMP =" AND " + COLUMN_TIMESTAMP + " >=? AND " + COLUMN_TIMESTAMP + " <=?";
    private static final String GROUPBY_DATAPOINT_ID_TIMESTAMP = " GROUP BY " + COLUMN_DATAPOINT_ID + ", floor(" + COLUMN_TIMESTAMP + "/?)";

    private static final String CONDITION_DATA_TYPE = " AND " + COLUMN_DATA_TYPE + "=?";
    private static final String WHERE_DATA_POINT = " WHERE " + COLUMN_DATAPOINT_ID + "=?";

    private JdbcOperations jdbcTemplate;
    private boolean denormalized;

    public PointValueAmChartDAO() {
        this.jdbcTemplate = DAO.getInstance().getJdbcTemp();
    }

    public PointValueAmChartDAO(JdbcOperations jdbcTemplate, boolean denormalized) {
        this.jdbcTemplate = jdbcTemplate;
        this.denormalized = denormalized;
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

        String selectDataPoints = prepareWhereDataPointIdsStatement(pointIds);
        String selectTimestamp = prepareWhereTimestampStatement(startTs, endTs);

        String sqlQuery = SELECT_VALUES + getTableName(denormalized) + selectDataPoints + selectTimestamp + SELECT_ORDER;

        List<DataPointSimpleValue> pvcList = jdbcTemplate.query(sqlQuery, new PointValueChartRowMapper());

        return convertToAmChartDataObject(pvcList);

    }

    @Override
    public List<Map<String, Double>> getPointValuesToCompareFromRange(int[] pointIds, long startTs, long endTs) {

        String selectDataPoints = prepareWhereDataPointIdsStatement(pointIds);
        String selectTimestamp = prepareWhereTimestampStatement(startTs, endTs);

        String sqlQuery = SELECT_VALUES + getTableName(denormalized) + selectDataPoints + selectTimestamp + SELECT_ORDER;

        List<DataPointSimpleValue> pvcList = jdbcTemplate.query(sqlQuery, new PointValueChartRowMapper());

        return convertToAmChartCompareDataObject(pvcList, pointIds[0]);

    }

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
        QueryArgs aggregationQuery = toAggregationQuery(dataPoint.getId(), dataPoint.getPointLocator().getDataTypeId(),
                startTs, endTs, intervalMs, limit, denormalized);
        return jdbcTemplate.query(aggregationQuery.getQuery(), aggregationQuery.getArgs(), new PointValueChartRowMapper());
    }

    @Override
    public List<DataPointSimpleValue> getPointValuesFromRangeWithLimit(int[] pointIds, long startTs, long endTs, int limit) {

        String selectDataPoints = prepareWhereDataPointIdsStatement(pointIds);
        String selectTimestamp = prepareWhereTimestampStatement(startTs, endTs);

        String sqlQuery = SELECT_VALUES + getTableName(denormalized) + selectDataPoints + selectTimestamp + SELECT_ORDER + " LIMIT " + limit;

        return jdbcTemplate.query(sqlQuery, new PointValueChartRowMapper());
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

    public static QueryArgs toAggregationQuery(int pointId, int dataType, long startTs, long endTs,
                                               long intervalMs, int limit) {
        return toAggregationQuery(pointId, dataType, startTs, endTs, intervalMs, limit, false);
    }

    public static QueryArgs toAggregationQuery(int pointId, int dataType, long startTs, long endTs,
                                         long intervalMs, int limit, boolean denormalized) {

        String sqlQuery;

        List<Object> args = new ArrayList<>();

        args.add(pointId);
        args.add(startTs);
        args.add(endTs);
        args.add(dataType);

        switch (dataType) {
            case DataTypes.BINARY:
                sqlQuery = SELECT_MAX_VALUES_FOR_AGGREGATION +
                        getTableName(denormalized) +
                        WHERE_DATA_POINT +
                        CONDITION_TIMESTAMP +
                        CONDITION_DATA_TYPE +
                        GROUPBY_DATAPOINT_ID_TIMESTAMP +
                        SELECT_ORDER;
                args.add(intervalMs);
                break;
            case DataTypes.MULTISTATE:
                sqlQuery = SELECT_VALUES +
                        getTableName(denormalized) +
                        WHERE_DATA_POINT +
                        CONDITION_TIMESTAMP +
                        CONDITION_DATA_TYPE +
                        SELECT_ORDER;
                break;
            case DataTypes.NUMERIC:
                sqlQuery = SELECT_AVG_VALUES_FOR_AGGREGATION +
                        getTableName(denormalized) +
                        WHERE_DATA_POINT +
                        CONDITION_TIMESTAMP +
                        CONDITION_DATA_TYPE +
                        GROUPBY_DATAPOINT_ID_TIMESTAMP +
                        SELECT_ORDER;
                args.add(intervalMs);
                break;
            default:
                sqlQuery = SELECT_VALUES +
                        getTableName(denormalized) +
                        WHERE_DATA_POINT +
                        CONDITION_TIMESTAMP +
                        CONDITION_DATA_TYPE +
                        SELECT_ORDER;
        }

        if(limit > 0) {
            sqlQuery += SELECT_LIMIT;
            args.add(limit);
        }
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

    private static String getTableName(boolean denormalized) {
        return (denormalized ? TABLE_NAME_DENORMALIZED : TABLE_NAME);
    }
}
