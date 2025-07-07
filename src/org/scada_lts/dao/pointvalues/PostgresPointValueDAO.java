package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.*;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static com.serotonin.mango.vo.DataPointVO.PurgeStrategy.LIMIT;
import static org.scada_lts.dao.GenericDaoCR.NO_LIMIT;

public class PostgresPointValueDAO implements IPointValueDAO {

    private static final Log LOG = LogFactory.getLog(PostgresPointValueDAO.class);

    private static PostgresPointValueDAO instance;

    private final static String  COLUMN_NAME_ID = "id";
    private final static String  COLUMN_NAME_DATA_TYPE = "dataType";
    private final static String  COLUMN_NAME_POINT_VALUE = "pointValue";
    private final static String  COLUMN_NAME_TEXT_POINT_VALUE_SHORT = "textPointValueShort";
    private final static String  COLUMN_NAME_TEXT_POINT_VALUE_LONG = "textPointValueLong";
    private final static String  COLUMN_NAME_SOURCE_TYPE = "sourceType";
    private final static String  COLUMN_NAME_TIME_STAMP = "ts";
    private final static String  COLUMN_NAME_SOURCE_ID = "sourceId";
    private final static String  COLUMN_NAME_POINT_VALUE_ID = "pointValueId";
    private final static String  COLUMN_NAME_DATA_POINT_ID = "dataPointId";
    private final static String  COLUMN_NAME_MIN_TIME_STAMP = "minTs";
    private final static String  COLUMN_NAME_MAX_TIME_STAMP = "maxTs";
    private final static String  COLUMN_NAME_USERNAME_IN_TABLE_USERS = "username";


    // @formatter:off
    private static final String POINT_VALUE_SELECT = ""
            + "select "
            + "pv."+COLUMN_NAME_ID + ","
            + "pv."+COLUMN_NAME_DATA_POINT_ID + ", "
            + "pv."+COLUMN_NAME_DATA_TYPE + ", "
            + "pv."+COLUMN_NAME_POINT_VALUE + ", "
            + "pv."+COLUMN_NAME_TIME_STAMP + ", "
            + "pva."+COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
            + "pva."+COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
            + "pva."+COLUMN_NAME_SOURCE_TYPE + ", "
            + "pva."+COLUMN_NAME_SOURCE_ID + " "
            + "from "
            + "pointValues pv "
            + "left join pointValueAnnotations pva on pv."+COLUMN_NAME_ID+"=pva."+COLUMN_NAME_POINT_VALUE_ID;

    private static final String POINT_VALUE_SELECT_WITH_USERNAME = ""
            + "select "
            + "pv."+COLUMN_NAME_ID + ","
            + "pv."+COLUMN_NAME_DATA_POINT_ID + ", "
            + "pv."+COLUMN_NAME_DATA_TYPE + ", "
            + "pv."+COLUMN_NAME_POINT_VALUE + ", "
            + "pv."+COLUMN_NAME_TIME_STAMP + ", "
            + "pva."+COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
            + "pva."+COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
            + "pva."+COLUMN_NAME_SOURCE_TYPE + ", "
            + "pva."+COLUMN_NAME_SOURCE_ID
            + ",us.username "
            + "from "
            + "pointValues pv "
            + "left join pointValueAnnotations pva on pv."+COLUMN_NAME_ID+"=pva."+COLUMN_NAME_POINT_VALUE_ID
            + "left join users us on pva.sourceId = us.id";

    private static final String POINT_VALUE_FILTER_BASE_ON_ID = ""
            + "pv."+COLUMN_NAME_ID+"=?";

    private static final String POINT_VALUE_SELECT_MIN_MAX_TS_BASE_ON_LIST_DATA_POINT = ""
            +"select "
            +"min(ts) as "+COLUMN_NAME_MIN_TIME_STAMP+", "
            +"max(ts) as "+COLUMN_NAME_MAX_TIME_STAMP+" "
            +"from "
            + "pointValues "
            + "where " + COLUMN_NAME_DATA_POINT_ID + " in (:ids)";

    private static final String POINT_VALUE_SELECT_MIN_TS_BASE_ON_LIST_DATA_POINT = ""
            +"select "
            +"min(ts) as "+ COLUMN_NAME_MIN_TIME_STAMP + " "
            +"from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID + " in (:ids)";

    private static final String POINT_VALUE_SELECT_MAX_TS_BASE_ON_LIST_DATA_POINT = ""
            +"select "
            + "max(ts) as "+ COLUMN_NAME_MAX_TIME_STAMP+" "
            +"from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID + " in (:ids)";

    private static final String POINT_VALUE_SELECT_MAX_BASE_ON_DATA_POINT_ID = ""
            +"select "
            +"max(ts) as " + COLUMN_NAME_MAX_TIME_STAMP + " "
            +"from "
            + "pointValues "
            +"where "
            + COLUMN_NAME_DATA_POINT_ID + "=?";


    private static final String POINT_VALUE_SELECT_ON_BASE_ID = ""
            + POINT_VALUE_SELECT
            + " where "
            + COLUMN_NAME_ID + "=?";

    private static final String POINT_VALUE_SELECT_ON_BASE_ID_TS = ""
            + POINT_VALUE_SELECT
            + " where "
            + COLUMN_NAME_DATA_POINT_ID + "=? and "
            + COLUMN_NAME_TIME_STAMP +"=?";


    private static final String POINT_VALUE_INSERT = ""
            + "insert into pointvalues ("
            + COLUMN_NAME_DATA_POINT_ID + ","
            + COLUMN_NAME_DATA_TYPE + ","
            + COLUMN_NAME_POINT_VALUE + ","
            + COLUMN_NAME_TIME_STAMP
            +") "
            + "values (?,?,?,?)";


    private static final String POINT_VALUE_INCEPTION_DATA = " "
            +"select "
            + "min(ts) "
            + "from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=?";

    private static final String POINT_VALUE_DATA_RANGE_COUNT = " "
            +"select "
            + "count(*) "
            + "from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=? and "+COLUMN_NAME_TIME_STAMP+">=? and "+COLUMN_NAME_TIME_STAMP+"<=?";

    public static final String POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP = " "
            + "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
            + "pv."+COLUMN_NAME_TIME_STAMP+" >= ? order by pv."+COLUMN_NAME_TIME_STAMP+", pv." + COLUMN_NAME_ID;

    public static final String POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP_FROM_TO = " "
            + "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
            + "pv."+COLUMN_NAME_TIME_STAMP+">=? and pv."+COLUMN_NAME_TIME_STAMP+"<? order by pv."+COLUMN_NAME_TIME_STAMP+", pv." + COLUMN_NAME_ID;

    public static final String POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID = " "
            + "pv."+COLUMN_NAME_DATA_POINT_ID+"=? "
            + "order by pv."+COLUMN_NAME_TIME_STAMP+" desc, pv." + COLUMN_NAME_ID + " desc";

    public static final String POINT_VALUE_FILTER_LATEST_BASE_ON_DATA_POINT_ID = " "
            + "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
            + "pv."+COLUMN_NAME_TIME_STAMP+"<? "
            + "order by pv."+COLUMN_NAME_TIME_STAMP+" desc, pv." + COLUMN_NAME_ID + " desc";

    public static final String POINT_VALUE_FILTER_BEFORE_TIME_STAMP_BASE_ON_DATA_POINT_ID = " "
            + "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
            + "pv."+COLUMN_NAME_TIME_STAMP+"<? "
            + "order by pv."+COLUMN_NAME_TIME_STAMP+", pv." + COLUMN_NAME_ID;

    public static final String POINT_VALUE_FILTER_AT_TIME_STAMP_BASE_ON_DATA_POINT_ID = " "
            + "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
            + "pv."+COLUMN_NAME_TIME_STAMP+"=? "
            + "order by pv."+COLUMN_NAME_TIME_STAMP+", pv." + COLUMN_NAME_ID;

    public static final String POINT_VALUE_ID_OF_LAST_VALUE = ""
            + "select"
            + " max(id) "
            + "from pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=?";

    public static final String POINT_VALUE_DELETE_BEFORE = ""
            +"delete "
            + "from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=? and "+COLUMN_NAME_TIME_STAMP+"<? "
            + "and id not in (?) ";

    public static final String POINT_VALUE_DELETE_BEFORE_WITHOUT_LAST_TWO = ""
            +"delete "
            + "from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=? "
            + "and " + COLUMN_NAME_ID + " < "
            + "(select min(id) "
            + "from ( "
            + "select id "
            + "from pointValues "
            + "where dataPointId =? "
            + "order by ts DESC, id DESC "
            + "limit 2 "
            + ") lastId ) and " + COLUMN_NAME_TIME_STAMP + "<? ";

    public static final String POINT_VALUE_DELETE_BASE_ON_POINT_ID = ""
            +"delete "
            + "from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=?";

    public static final String POINT_VALUE_DELETE = ""
            +"delete "
            + "from "
            + "pointValues ";

    public static final String DELETE_POINT_VALUE_WITH_MISMATCHED_TYPE=""
            +"delete "
            + "from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=? and "+COLUMN_NAME_DATA_TYPE+"<>?";

    public static final String DELETE_POINT_VALUE_BASED_ON_DATAPOINT_WITH_VALUE_LIMIT = ""
            +"delete "
            + "from "
            + "pointValues "
            + "where "
            + COLUMN_NAME_DATA_POINT_ID+"=? and "+COLUMN_NAME_ID+"<= " +
            "(select " + COLUMN_NAME_ID + " from " +
            "(select " + COLUMN_NAME_ID +
            " from pointValues " +
            "where " + COLUMN_NAME_DATA_POINT_ID + " =? " +
            "order by " + COLUMN_NAME_TIME_STAMP + " desc, " + COLUMN_NAME_ID + " desc " +
            "limit 1 offset ?) lastId)";

    private static final String SELECT_MAX_TIME_WHERE_DATA_POINT_ID = ""
            + "select max("
            + COLUMN_NAME_TIME_STAMP + ") "
            + "from pointValues where "
            + COLUMN_NAME_DATA_POINT_ID + "=? ";

    private static final String SELECT_MIN_TIME_WHERE_DATA_POINT_ID = ""
            + "select min("
            + COLUMN_NAME_TIME_STAMP + ") "
            + "from pointValues where "
            + COLUMN_NAME_DATA_POINT_ID + "=? ";

    private static final String SELECT_DP_ID_AND_COUNT = ""
            + "select "
            + COLUMN_NAME_DATA_POINT_ID + ", "
            + "count(*) from pointValues group by "
            + COLUMN_NAME_DATA_POINT_ID + " "
            + "order by 2 desc ";

    private static final Map<String, String> QUERY_MAP = Map.of(
            "POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP", POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP,
            "POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP_FROM_TO", POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP_FROM_TO,
            "POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID", POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID
    );

    public static class PointValueRowMapper implements RowMapper<PointValue> {
        public PointValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            MangoValue value = createMangoValue(rs);
            long time = rs.getLong(COLUMN_NAME_TIME_STAMP);

            PointValue pv = new PointValue();
            pv.setId(rs.getLong(COLUMN_NAME_ID));
            pv.setDataPointId(rs.getInt(COLUMN_NAME_DATA_POINT_ID));
            int sourceType = rs.getInt(COLUMN_NAME_SOURCE_TYPE);

            if (rs.wasNull()) {
                pv.setPointValue(new PointValueTime(value, time));
            } else {
                int sourceId = rs.getInt(COLUMN_NAME_SOURCE_ID);
                AnnotatedPointValueTime annotatedPointValueTime = new AnnotatedPointValueTime(value, time, sourceType, sourceId);
                if (sourceType == SetPointSource.Types.USER && sourceId > 0) {
                    User user = ApplicationBeans.getLoggedUsersBean().getUser(sourceId);
                    if (user == null) {
                        user = ApplicationBeans.getUserDaoBean().getUser(sourceId);
                    }
                    if (user != null) {
                        annotatedPointValueTime.setSourceDescriptionArgument(user.getUsername());
                    }
                }
                pv.setPointValue(annotatedPointValueTime);
            }
            return pv;
        }
    }

    private class LongPairRowMapper implements RowMapper<LongPair> {
        public LongPair mapRow(ResultSet rs, int index) throws SQLException {
            long myLongValue = rs.getLong(COLUMN_NAME_MIN_TIME_STAMP);
            if (rs.wasNull())
                return null;
            return new LongPair(myLongValue, rs.getLong(COLUMN_NAME_MAX_TIME_STAMP));
        }
    }

    private class LongRowMapper implements RowMapper<Long> {
        public Long mapRow(ResultSet rs, int index) throws SQLException {
            return rs.getLong(COLUMN_NAME_MIN_TIME_STAMP);
        }
    }

    static MangoValue createMangoValue(ResultSet rs) throws SQLException {
        int dataType = rs.getInt(COLUMN_NAME_DATA_TYPE);
        switch (dataType) {
            case DataTypes.NUMERIC:
                return new NumericValue(rs.getDouble(COLUMN_NAME_POINT_VALUE));
            case DataTypes.BINARY:
                return new BinaryValue(rs.getDouble(COLUMN_NAME_POINT_VALUE) == 1);
            case DataTypes.MULTISTATE:
                return new MultistateValue(rs.getInt(COLUMN_NAME_POINT_VALUE));
            case DataTypes.ALPHANUMERIC:
                String s = rs.getString(COLUMN_NAME_TEXT_POINT_VALUE_SHORT);
                if (s == null)
                    s = rs.getString(COLUMN_NAME_TEXT_POINT_VALUE_LONG);
                return new AlphanumericValue(s);
            case DataTypes.IMAGE:
                try {
                    return new ImageValue(Integer.parseInt(rs.getString(COLUMN_NAME_TEXT_POINT_VALUE_SHORT)), rs.getInt(COLUMN_NAME_TEXT_POINT_VALUE_LONG));
                } catch (NumberFormatException e) {
                    LOG.info(e.getMessage());
                }
        }
        return null;
    }

    public static IPointValueDAO getInstance() {
        if (instance == null) {
            instance = new PostgresPointValueDAO();
        }
        return instance;
    }

    @Override
    public List<PointValue> findAll() {
        return DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT, new PointValueRowMapper());
    }

    @Override
    public PointValue findById(Object[] pk) {
        return DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_ON_BASE_ID, pk, new PointValueRowMapper());
    }

    @Override
    public List<PointValue> findByIdAndTs(long id, long ts) {
        return DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT_ON_BASE_ID_TS, new Object[]{id, ts}, new PointValueRowMapper());
    }

    @Override
    public List<PointValue> filtered(String filter, Object[] argsFilter, long limit) {
        String myLimit = "";
        if (limit != NO_LIMIT) {
            myLimit = String.valueOf(LIMIT + limit);
        }
        return DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT + " where " + filter + myLimit, argsFilter, new PointValueRowMapper());
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public Object[] create(final PointValue entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        DAO.getInstance().getJdbcTemp().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(POINT_VALUE_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, (int) entity.getDataPointId());
            ps.setInt(2, entity.getPointValue().getValue().getDataType());
            ps.setDouble(3, getValueBaseOnType(entity.getPointValue().getValue().getDataType(), entity.getPointValue()));
            ps.setLong(4, entity.getPointValue().getTime());
            return ps;
        }, keyHolder);
        return new Object[]{getId(keyHolder)};
    }


    @Override
    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    public Object[] create(final int pointId, final int dataType, final double dvalue, final long time) {

        return createNoTransaction(pointId, dataType, dvalue, time);

    }


    @Override
    public Object[] createNoTransaction(final int pointId, final int dataType, final double dvalue, final long time) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("pointId:"+pointId+" dataType:"+dataType+" dvalue:"+time);
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(POINT_VALUE_INSERT, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter( new Object[] {
                        pointId,
                        dataType,
                        dvalue,
                        time
                }).setValues(ps);
                return ps;
            }
        }, keyHolder);

        long id = getId(keyHolder);
        return new Object[]{id};

    }



    @Override
    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    public void executeBatchUpdateInsert(List<Object[]> params) {
        if (LOG.isTraceEnabled()) {
            for (Object[] param : params) {
                for (Object arg :param) {
                    LOG.trace("arg:"+arg);
                }
            }
        }

        DAO.getInstance().getJdbcTemp().batchUpdate(POINT_VALUE_INSERT,params);

    }

    @Override
    public Long getInceptionDate(int dataPointId) {
        return DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_INCEPTION_DATA, new Object[] {dataPointId}, Long.class);
    }

    @Override
    public long dateRangeCount(int dataPointId, long from, long to) {
        return DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_DATA_RANGE_COUNT, new Object[] {dataPointId, from, to}, Long.class);
    }

    @Override
    public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
        if (dataPointIds.isEmpty())	return null;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids",dataPointIds);

        LongPair longPair = DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_MIN_MAX_TS_BASE_ON_LIST_DATA_POINT,new PostgresPointValueDAO.LongPairRowMapper(),parameters);

        return longPair;
    }

    @Override
    public long getStartTime(List<Integer> dataPointIds) {
        if (dataPointIds.isEmpty())	return -1;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids",dataPointIds);

        Long minTs = DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_MIN_TS_BASE_ON_LIST_DATA_POINT,new PostgresPointValueDAO.LongRowMapper(),parameters);

        return minTs;
    }

    @Override
    public long getEndTime(List<Integer> dataPointIds) {
        if (dataPointIds.isEmpty()) return -1;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids",dataPointIds);

        Long maxTs = DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_MAX_TS_BASE_ON_LIST_DATA_POINT, new PostgresPointValueDAO.LongRowMapper(),parameters);

        return maxTs;

    }

    @Override
    public List<Long> getFiledataIds() {
        //TODO rewrite
        return DAO.getInstance().getJdbcTemp().queryForList(
                "select distinct id from ( " //
                        + "  select id as id from pointValues where dataType="
                        + DataTypes.IMAGE
                        + "  union"
                        + "  select d.pointValueId as id from reportInstanceData d "
                        + "    join reportInstancePoints p on d.reportInstancePointId=p.id"
                        + "  where p.dataType="
                        + DataTypes.IMAGE
                        + ") a order by 1", new Object[] {}, Long.class);
    }

    @Override
    public Long getLatestPointValue(int dataPointId) {
        try {
            return  DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_MAX_BASE_ON_DATA_POINT_ID, new Object[] {dataPointId}, Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * When save value ALPHANUMERIC or IMAGE then data save in adnnotations
     * @param dataType
     * @param value
     * @return
     */
    private double getValueBaseOnType(int dataType, PointValueTime value) {
        Double avalue = null;
        if ( (dataType==DataTypes.ALPHANUMERIC) || (dataType==DataTypes.IMAGE) || value == null) {
            avalue = 0.0;
        } else {
            avalue = value.getDoubleValue();
        }
        return avalue;
    }

    @Override
    public double applyBounds(double value) {
        if (Double.isNaN(value))
            return 0;
        if (value == Double.POSITIVE_INFINITY)
            return Double.MAX_VALUE;
        if (value == Double.NEGATIVE_INFINITY)
            return -Double.MAX_VALUE;

        return value;
    }

    @Override
    @Deprecated
    public long deletePointValuesBeforeWithOutLast(int dataPointId, long time) {
        Long lastId = DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_ID_OF_LAST_VALUE, new Object[] { dataPointId }, Long.class );
        return DAO.getInstance().getJdbcTemp().update(POINT_VALUE_DELETE_BEFORE, new Object[] {dataPointId, time, lastId});
    }

    @Override
    public long deletePointValuesBeforeWithOutLastTwo(int dataPointId, long time) {
        return DAO.getInstance().getJdbcTemp().update(POINT_VALUE_DELETE_BEFORE_WITHOUT_LAST_TWO, new Object[] {dataPointId, dataPointId, time});
    }

    @Override
    public long deletePointValue(int dataPointId) {
        return DAO.getInstance().getJdbcTemp().update(POINT_VALUE_DELETE_BASE_ON_POINT_ID, new Object[] {dataPointId});
    }

    @Override
    public long deleteAllPointData() {
        return DAO.getInstance().getJdbcTemp().update(POINT_VALUE_DELETE, new Object[] {});
    }

    @Override
    public long deletePointValuesWithMismatchedType(int dataPointId, int dataType) {
        return DAO.getInstance().getJdbcTemp().update(DELETE_POINT_VALUE_WITH_MISMATCHED_TYPE, new Object[] {dataPointId, dataType});
    }

    @Override
    public long deletePointValuesWithValueLimit(int dataPointId, int limit){
        return DAO.getInstance().getJdbcTemp().update(DELETE_POINT_VALUE_BASED_ON_DATAPOINT_WITH_VALUE_LIMIT, new Object[] {dataPointId, dataPointId, limit});
    }

    @Override
    public PointValueTime getPointValue(long id) {
        return ((PointValue) DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT + " where " + POINT_VALUE_FILTER_BASE_ON_ID, new Object[] {id}, new PointValueDAO.PointValueRowMapper())).getPointValue();
    }

    @Override
    public long getMinTs(int dataPointId) {
        return DAO.getInstance().getJdbcTemp().queryForObject(SELECT_MIN_TIME_WHERE_DATA_POINT_ID, new Object[] {dataPointId}, Long.TYPE);
    }

    @Override
    public long getMaxTs(int dataPointId) {
        return DAO.getInstance().getJdbcTemp().queryForObject(SELECT_MAX_TIME_WHERE_DATA_POINT_ID, new Object[] {dataPointId}, Long.TYPE);
    }

    @Override
    public List<PointHistoryCount> getTopPointHistoryCounts() {
        return DAO.getInstance().getJdbcTemp().query(SELECT_DP_ID_AND_COUNT, new RowMapper<PointHistoryCount>() {
            @Override
            public PointHistoryCount mapRow(ResultSet rs, int rowNum) throws SQLException {
                PointHistoryCount phc = new PointHistoryCount();
                phc.setPointId(rs.getInt(1));
                phc.setCount(rs.getInt(2));
                return phc;
            }
        });
    }

    private static final String POINT_VALUE_ADNNOTATIONS_INSERT = ""
            + "insert into pointValueAnnotations  ("
            + COLUMN_NAME_POINT_VALUE_ID + ","
            + COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ","
            + COLUMN_NAME_TEXT_POINT_VALUE_LONG + ","
            + COLUMN_NAME_SOURCE_TYPE + ","
            + COLUMN_NAME_SOURCE_ID
            +") "
            + "values (?,?,?,?,?)";

    @Override
    public int createAnnotation(long pointValueId, String textPointValueShort, String textPointValueLong,
                                int sourceType, int sourceId) {
        return DAO.getInstance().getJdbcTemp().update(POINT_VALUE_ADNNOTATIONS_INSERT, new Object[] {
                pointValueId,
                textPointValueShort,
                textPointValueLong,
                sourceType,
                sourceId
        });
    }

    private static final String POINT_VALUE_ANNOTATIONS_UPDATE = ""
            + "update pointValueAnnotations set "
            + COLUMN_NAME_SOURCE_ID + "=null "
            + "where "
            + COLUMN_NAME_SOURCE_ID + "=? "
            + "and "
            + COLUMN_NAME_SOURCE_TYPE + "="
            + SetPointSource.Types.USER;

    @Override
    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    public void updateAnnotation(int userId) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("update(int userId) userId:" + userId);
        }

        DAO.getInstance().getJdbcTemp().update(POINT_VALUE_ANNOTATIONS_UPDATE, new Object[]{userId});
    }

    @Override
    public PointValueTime getPointValueBefore(int dataPointId, long time) {
        try {
            Long valueTime = DAO.getInstance().getJdbcTemp().queryForObject("select max(ts) from pointValues where dataPointId=? and ts<?",
                    new Object[] { dataPointId, time }, Long.class);
            if (valueTime == null)
                return null;
            return getPointValueAt(dataPointId, valueTime);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public PointValueTime getPointValueAt(int dataPointId, long time) {
        List<PointValue> pointValues = DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT + " where pv.dataPointId=? and pv.ts=?",
                new Object[]{dataPointId, time}, new PointValueDAO.PointValueRowMapper());
        if(pointValues.isEmpty())
            return null;
        long maxId = pointValues.stream().mapToLong(PointValue::getId).max().orElse(-1);
        PointValue pointValue = pointValues.stream().filter(a -> a.getId() == maxId).findAny().orElse(null);
        if(pointValue == null)
            return null;
        return pointValue.getPointValue();
    }

    private static long getId(KeyHolder keyHolder) {
        long id = -1;
        Number key = keyHolder.getKey();
        if(key != null) {
            id = key.longValue();
        }
        return id;
    }

    @Override
    public List<PointValue> getPointValues(int dataPointId, long since) {
        return filtered(
                POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP,
                new Object[]{dataPointId, since},
                GenericDaoCR.NO_LIMIT
        );
    }

    @Override
    public List<PointValue> getPointValuesBetween(int dataPointId, long from, long to) {
        return filtered(
                POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP_FROM_TO,
                new Object[]{dataPointId, from, to},
                GenericDaoCR.NO_LIMIT
        );
    }

    @Override
    public List<PointValue> getLatestPointValues(int dataPointId, int limit) {
        return filtered(
                POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID,
                new Object[]{dataPointId},
                limit
        );
    }

    @Override
    public List<PointValue> getLatestPointValues(int dataPointId, int limit, long before) {
        return filtered(
                POINT_VALUE_FILTER_LATEST_BASE_ON_DATA_POINT_ID,
                new Object[]{dataPointId, before},
                limit
        );
    }
}
