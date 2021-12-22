/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.*;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.model.point.PointValueAdnnotation;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
@Repository
class PointValueDenormalizedDAO implements IPointValueDenormalizedDAO {

	private static final Log LOG = LogFactory.getLog(PointValueDenormalizedDAO.class);

	private static IPointValueDenormalizedDAO instance;

	private final static String  COLUMN_NAME_DATA_TYPE = "dataType";
	private final static String  COLUMN_NAME_POINT_VALUE = "pointValue";
	private final static String  COLUMN_NAME_TEXT_POINT_VALUE_SHORT = "textPointValueShort";
	private final static String  COLUMN_NAME_TEXT_POINT_VALUE_LONG = "textPointValueLong";
	private final static String  COLUMN_NAME_SOURCE_TYPE = "sourceType";
	private final static String  COLUMN_NAME_TS = "ts";
	private final static String  COLUMN_NAME_TIMESTAMP = "timestamp";
	private final static String  COLUMN_NAME_SOURCE_ID = "sourceId";
	private final static String  COLUMN_NAME_DATA_POINT_ID = "dataPointId";
	private final static String  COLUMN_NAME_MIN_TIME_STAMP = "minTs";
	private final static String  COLUMN_NAME_MAX_TIME_STAMP = "maxTs";
	private final static String  COLUMN_NAME_USERNAME_IN_TABLE_USERS = "username";


	// @formatter:off
	private static final String POINT_VALUE_SELECT = ""
			+ "select "
			    + COLUMN_NAME_DATA_POINT_ID + ", "
				+ COLUMN_NAME_DATA_TYPE + ", "
				+ COLUMN_NAME_POINT_VALUE + ", "
				+ COLUMN_NAME_TS + ", "
				+ COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
				+ COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
				+ COLUMN_NAME_SOURCE_TYPE + ", "
				+ COLUMN_NAME_SOURCE_ID + ", "
				+ COLUMN_NAME_USERNAME_IN_TABLE_USERS + " "
			+ "from "
				+ "pointValuesDenormalized pv ";

	private static final String POINT_VALUE_SELECT_WITH_USERNAME = ""
			+ "select "
			+ COLUMN_NAME_DATA_POINT_ID + ", "
			+ COLUMN_NAME_DATA_TYPE + ", "
			+ COLUMN_NAME_POINT_VALUE + ", "
			+ COLUMN_NAME_TS + ", "
			+ COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
			+ COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
			+ COLUMN_NAME_SOURCE_TYPE + ", "
			+ COLUMN_NAME_SOURCE_ID + ", "
			+ COLUMN_NAME_USERNAME_IN_TABLE_USERS + " "
			+ "from "
			+ "pointValuesDenormalized pv ";
/*
	private static final String POINT_VALUE_FILTER_BASE_ON_ID = ""
			+ "pv."+COLUMN_NAME_ID+"=?";*/

	private static final String POINT_VALUE_SELECT_MIN_MAX_TS_BASE_ON_LIST_DATA_POINT = ""
			+"select "
				+"min(ts) as "+COLUMN_NAME_MIN_TIME_STAMP+", "
				+"max(ts) as "+COLUMN_NAME_MAX_TIME_STAMP+" "
			+"from "
				+ "pointValuesDenormalized "
			+ "where " + COLUMN_NAME_DATA_POINT_ID + " in (:ids)";

	private static final String POINT_VALUE_SELECT_MIN_TS_BASE_ON_LIST_DATA_POINT = ""
			+"select "
				+"min(ts) as "+ COLUMN_NAME_MIN_TIME_STAMP + " "
			+"from "
				+ "pointValuesDenormalized "
			+ "where "
				+ COLUMN_NAME_DATA_POINT_ID + " in (:ids)";

	private static final String POINT_VALUE_SELECT_MAX_TS_BASE_ON_LIST_DATA_POINT = ""
			+"select "
				+ "max(ts) as "+ COLUMN_NAME_MAX_TIME_STAMP+" "
			+"from "
				+ "pointValuesDenormalized "
			+ "where "
			+ COLUMN_NAME_DATA_POINT_ID + " in (:ids)";

	private static final String POINT_VALUE_SELECT_MAX_BASE_ON_DATA_POINT_ID = ""
			+"select "
				+"max(ts) as " + COLUMN_NAME_MAX_TIME_STAMP + " "
			+"from "
			   + "pointValuesDenormalized "
			+"where "
			   + COLUMN_NAME_DATA_POINT_ID + "=?";

/*
	private static final String POINT_VALUE_SELECT_ON_BASE_ID = ""
			+ POINT_VALUE_SELECT
			+ " where "
				+ COLUMN_NAME_ID + "=?";*/

	private static final String POINT_VALUE_SELECT_ON_BASE_ID_TS = ""
			+ POINT_VALUE_SELECT
			+ " where "
				+ COLUMN_NAME_DATA_POINT_ID + "=? and "
				+ COLUMN_NAME_TS +"=?";

	private static final String POINT_VALUE_DENORMALIZED_INSERT = ""
			+ "insert into pointValuesDenormalized ("
			+ ""+COLUMN_NAME_DATA_POINT_ID + ", "
			+ ""+COLUMN_NAME_DATA_TYPE + ", "
			+ ""+COLUMN_NAME_POINT_VALUE + ", "
			+ ""+COLUMN_NAME_TS + ", "
			+ ""+COLUMN_NAME_TIMESTAMP + ", "
			+ ""+COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
			+ ""+COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
			+ ""+COLUMN_NAME_SOURCE_TYPE + ", "
			+ ""+COLUMN_NAME_SOURCE_ID + ", "
			+ ""+COLUMN_NAME_USERNAME_IN_TABLE_USERS
			+") "
			+ "values (?,?,?,?,?,?,?,?,?,?)";

	private static final String POINT_VALUE_INCEPTION_DATA = " "
			+"select "
				+ "min(ts) "
			+ "from "
				+ "pointValuesDenormalized "
			+ "where "
				+ COLUMN_NAME_DATA_POINT_ID+"=?";

	private static final String POINT_VALUE_DATA_RANGE_COUNT = " "
			+"select "
				+ "count(*) "
			+ "from "
				+ "pointValuesDenormalized "
			+ "where "
				+ COLUMN_NAME_DATA_POINT_ID+"=? and "+ COLUMN_NAME_TS +">=? and "+ COLUMN_NAME_TS +"<=?";

	public static final String POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+ COLUMN_NAME_TS +" >= ? order by "+ COLUMN_NAME_TS;

	public static final String POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP_FROM_TO = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+ COLUMN_NAME_TS +">=? and pv."+ COLUMN_NAME_TS +"<? order by "+ COLUMN_NAME_TS;

	public static final String POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? "
			+ "order by pv."+ COLUMN_NAME_TS +" desc";

	public static final String POINT_VALUE_FILTER_LATEST_BASE_ON_DATA_POINT_ID = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+ COLUMN_NAME_TS +"<? "
			+ "order by pv."+ COLUMN_NAME_TS +" desc";

	public static final String POINT_VALUE_FILTER_BEFORE_TIME_STAMP_BASE_ON_DATA_POINT_ID = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+ COLUMN_NAME_TS +"<? "
			+ "order by pv."+ COLUMN_NAME_TS;

	public static final String POINT_VALUE_FILTER_AT_TIME_STAMP_BASE_ON_DATA_POINT_ID = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+ COLUMN_NAME_TS +"=? "
			+ "order by pv."+ COLUMN_NAME_TS;

	public static final String POINT_VALUE_ID_OF_LAST_VALUE = ""
			+ "select"
			+ " max(ts) "
			+ "from pointValuesDenormalized "
			+ "where "
			+ COLUMN_NAME_DATA_POINT_ID+"=?";

	public static final String POINT_VALUE_DELETE_BEFORE = ""
			+"delete "
			+ "from "
				+ "pointValuesDenormalized "
			+ "where "
				+ COLUMN_NAME_DATA_POINT_ID+"=? and "+ COLUMN_NAME_TS +"<? "
			    + "and id not in (?) ";

	public static final String POINT_VALUE_DELETE_BASE_ON_POINT_ID = ""
			+"delete "
			+ "from "
				+ "pointValuesDenormalized "
			+ "where "
				+ COLUMN_NAME_DATA_POINT_ID+"=?";

	public static final String POINT_VALUE_DELETE = ""
			+"delete "
			+ "from "
				+ "pointValuesDenormalized ";

	public static final String DELETE_POINT_VALUE_WITH_MISMATCHED_TYPE=""
			+"delete "
			+ "from "
				+ "pointValuesDenormalized "
			+ "where "
				+ COLUMN_NAME_DATA_POINT_ID+"=? and "+COLUMN_NAME_DATA_TYPE+"<>?";

	private static final String SELECT_MAX_TIME_WHERE_DATA_POINT_ID = ""
			+ "select max("
				+ COLUMN_NAME_TS + ") "
			+ "from pointValuesDenormalized where "
				+ COLUMN_NAME_DATA_POINT_ID + "=? ";

	private static final String SELECT_MIN_TIME_WHERE_DATA_POINT_ID = ""
			+ "select min("
				+ COLUMN_NAME_TS + ") "
			+ "from pointValuesDenormalized where "
				+ COLUMN_NAME_DATA_POINT_ID + "=? ";

	private static final String SELECT_DP_ID_AND_COUNT = ""
			+ "select "
				+ COLUMN_NAME_DATA_POINT_ID + ", "
			+ "count(*) from pointValuesDenormalized group by "
				+ COLUMN_NAME_DATA_POINT_ID + " "
			+ "order by 2 desc ";

	// @formatter:on

	//RowMappers
	private class PointValueRowMapper implements RowMapper<PointValue> {
		public PointValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			//TODO rewrite MangoValue
			MangoValue value = createMangoValue(rs);
			long time = rs.getLong(COLUMN_NAME_TS);

			PointValue pv = new PointValue();
			//pv.setId(rs.getLong(COLUMN_NAME_ID));
			pv.setDataPointId(rs.getInt(COLUMN_NAME_DATA_POINT_ID));
			int sourceId = rs.getInt(COLUMN_NAME_SOURCE_ID);

			int sourceType = rs.getInt(COLUMN_NAME_SOURCE_TYPE);

			if (rs.wasNull()) {
				pv.setPointValue(new PointValueTime(value, time));
			} else {
				pv.setPointValue(new AnnotatedPointValueTime(value, time, sourceType, sourceId));
			}
			return pv;
		}
	}
	public PointValue getPointValueRow(ResultSet rs, int rowNum){
		try {
			return new PointValueRowMapperWithUserName().mapRow(rs, rowNum);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	private class PointValueRowMapperWithUserName implements RowMapper<PointValue> {

		public PointValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			//TODO rewrite MangoValue
			MangoValue value = createMangoValue(rs);
			long time = rs.getLong(COLUMN_NAME_TS);

			PointValue pv = new PointValue();
			//pv.setId(rs.getLong(COLUMN_NAME_ID));
			pv.setDataPointId(rs.getInt(COLUMN_NAME_DATA_POINT_ID));
			int sourceId = rs.getInt(COLUMN_NAME_SOURCE_ID);

			int sourceType = rs.getInt(COLUMN_NAME_SOURCE_TYPE);
			int username = -1;
			String userName = null;
			try{
				username =  rs.getInt(COLUMN_NAME_USERNAME_IN_TABLE_USERS);
			}
			catch (Exception e){

				userName = rs.getString(COLUMN_NAME_USERNAME_IN_TABLE_USERS);
			}

			pv.setPointValue( rs.wasNull()
				? createPointValueTime( userName, value, time, username )
				: createAnnotatedPointValueTime( value,time, sourceType, sourceId, userName ));

			return pv;
		}
		private AnnotatedPointValueTime createAnnotatedPointValueTime(MangoValue value, long time, int sourceType, int sourceId, String userName){
			return ( userName!= null )
					?new AnnotatedPointValueTime(value, time, sourceType, sourceId, userName)
					:new AnnotatedPointValueTime(value, time, sourceType, sourceId);
		}
		private PointValueTime createPointValueTime(String userName, MangoValue value, long time, int username ){

			if(userName!= null){
				return new PointValueTime(value, time,userName);
			}
			else if( username!=0 ){
				return new PointValueTime(value, time,String.valueOf(username));
			}
			else {
				return new PointValueTime(value, time);
			}

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

	//TODO rewrite for new types
	MangoValue createMangoValue(ResultSet rs)
			throws SQLException {

		int dataType = rs.getInt(COLUMN_NAME_DATA_TYPE);
		MangoValue value = null;
		switch (dataType) {
		case (DataTypes.NUMERIC):
			value = new NumericValue(rs.getDouble(COLUMN_NAME_POINT_VALUE));
			break;
		case (DataTypes.BINARY):
			value = new BinaryValue(rs.getDouble(COLUMN_NAME_POINT_VALUE)==1);
			break;
		case (DataTypes.MULTISTATE):
			value = new MultistateValue(rs.getInt(COLUMN_NAME_POINT_VALUE));
			break;
		case (DataTypes.ALPHANUMERIC):
			String s = rs.getString(COLUMN_NAME_TEXT_POINT_VALUE_SHORT);
			if (s == null)
				s = rs.getString(COLUMN_NAME_TEXT_POINT_VALUE_LONG);
			value = new AlphanumericValue(s);
			break;
		case (DataTypes.IMAGE): {
			try {
				value = new ImageValue(Integer.parseInt(rs
						.getString(COLUMN_NAME_TEXT_POINT_VALUE_SHORT)),
						rs.getInt(COLUMN_NAME_TEXT_POINT_VALUE_LONG));
			} catch (NumberFormatException e) {
				LOG.info(e.getMessage());
			}
			break;
		}
		default:
			value = null;
		}
		return value;
	}

	public static IPointValueDenormalizedDAO getInstance() {
		if (instance == null) {
			instance = new PointValueDenormalizedDAO(DAO.query().getJdbcTemp());
		}
		return instance;
	}

	private JdbcOperations jdbcTemplate;

	public PointValueDenormalizedDAO() {
		this.jdbcTemplate = DAO.getInstance().getJdbcTemp();
	}

	public PointValueDenormalizedDAO(JdbcOperations jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<PointValue> findAllWithUserName(){
		return (List<PointValue>) jdbcTemplate.query(POINT_VALUE_SELECT_WITH_USERNAME, new Object[]{ }, new PointValueRowMapper());
	}
	@Override
	public List<PointValue> findAll() {

		return (List<PointValue>) jdbcTemplate.query(POINT_VALUE_SELECT, new Object[]{ }, new PointValueRowMapper());
	}
/*
	@Override
	public PointValue findById(Object[] pk) {
		return (PointValue) jdbcTemplate.queryForObject(POINT_VALUE_SELECT_ON_BASE_ID, pk, new PointValueRowMapper());
	}*/

	public List<PointValue> findByIdAndTs(long id, long ts) {
		return jdbcTemplate.query(POINT_VALUE_SELECT_ON_BASE_ID_TS, new Object[]  { id,ts }, new PointValueRowMapper());
	}
	
	@Override
	public List<PointValue> filtered(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		if (limit != NO_LIMIT) {
			//TODO rewrite limit adding in argsFilter
			myLimit = LIMIT+limit;
		}
		List<PointValue> res =
		 (List<PointValue>) jdbcTemplate.query(
				"select "
						+ "pv."+COLUMN_NAME_DATA_POINT_ID + ", "
						+ "pv."+COLUMN_NAME_DATA_TYPE + ", "
						+ "pv."+COLUMN_NAME_POINT_VALUE + ", "
						+ "pv."+ COLUMN_NAME_TS + ", "
						+ "pv."+COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
						+ "pv."+COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
						+ "pv."+COLUMN_NAME_SOURCE_TYPE + ", "
						+ "pv."+COLUMN_NAME_SOURCE_ID + ", "
						+ "pv." +COLUMN_NAME_USERNAME_IN_TABLE_USERS
						+ " from "
						+ "pointValuesDenormalized pv where "+ filter + myLimit, argsFilter, new PointValueRowMapperWithUserName());
		return res;
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public Object[] create(final PointValue entity) {

		if (LOG.isTraceEnabled()) {
			LOG.trace(entity);
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			 			@Override
			 			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			 				PreparedStatement ps = connection.prepareStatement(POINT_VALUE_DENORMALIZED_INSERT, Statement.RETURN_GENERATED_KEYS);
			 				new ArgumentPreparedStatementSetter( new Object[] {
			 						entity.getDataPointId(),
			 						entity.getPointValue().getValue().getDataType(),
			 						getValueBaseOnType(entity.getPointValue().getValue().getDataType(), entity.getPointValue()),
			 						entity.getPointValue().getTime(),
									new Date(entity.getPointValue().getTime()),
									null,
									null,
									null,
									null,
									null
			 				}).setValues(ps);
			 				return ps;
			 			}
		}, keyHolder);
		
		return new Object[] {keyHolder.getKey().longValue()};
		
	}
	
	
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public Object[] create(final int pointId,final int dataType,final double dvalue,final long time) {
		
		return createNoTransaction(pointId, dataType, dvalue, time);
		
	}

	@Override
	public void create(PointValue pointValue, PointValueAdnnotation pointValueAdnnotation, int dataType) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("pointId:"+pointValue.getDataPointId()+" dataType:"+pointValue.getPointValue()
					.getValue().getDataType()+" time:"+pointValue.getPointValue().getTime());
		}

		PointValueTime pointValueTime = pointValue.getPointValue();
		MangoValue mangoValue = pointValueTime.getValue();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(POINT_VALUE_DENORMALIZED_INSERT, Statement.RETURN_GENERATED_KEYS);
			new ArgumentPreparedStatementSetter( new Object[] {
					pointValue.getDataPointId(),
					mangoValue.getDataType(),
					getValueBaseOnType(mangoValue.getDataType(), pointValueTime),
					pointValueTime.getTime(),
					new Date(pointValueTime.getTime()),
					pointValueAdnnotation.getTextPointValueShort(),
					pointValueAdnnotation.getTextPointValueLong(),
					pointValueAdnnotation.getSourceType(),
					pointValueAdnnotation.getSourceId(),
					pointValueAdnnotation.getChangeOwner()
			}).setValues(ps);
			return ps;
		});
	}


	public Object[] createNoTransaction(final int pointId,final int dataType,final double dvalue,final long time) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("pointId:"+pointId+" dataType:"+dataType+" dvalue:"+time);
		}
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			 			@Override
			 			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException { 
			 				PreparedStatement ps = connection.prepareStatement(POINT_VALUE_DENORMALIZED_INSERT, Statement.RETURN_GENERATED_KEYS);
			 				new ArgumentPreparedStatementSetter( new Object[] {
			 						pointId,
			 						dataType,
			 						dvalue,
			 						time,
									new Date(time),
									null,
									null,
									null,
									null,
									null
			 				}).setValues(ps);
			 				return ps;
			 			}
		}, keyHolder);

		try {
			return new Object[]{keyHolder.getKey().longValue()};
		} catch (InvalidDataAccessApiUsageException ex) {
			return new Object[]{Long.parseLong(String.valueOf(keyHolder.getKeys().get("id")))};
		}
		
	}

	@Override
	public long deletePointValuesBeforeWithOutLastTwo(int dataPointId, long time) {
		return 0;
	}

	@Override
	public long deletePointValuesWithValueLimit(int dataPointId, int limit) {
		return 0;
	}

	@Override
	public void createTableForDatapoint(int pointId) {

	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void executeBatchUpdateInsert( List<Object[]> params) {
		
		if (LOG.isTraceEnabled()) {
			for (Object[] param : params) {
				for (Object arg :param) {
					LOG.trace("arg:"+arg);
				}
			}
		}
		if(params.isEmpty()) {
			return;
		}
		if(params.get(0).length == 4) {
			List<Object[]> paramsExt = new ArrayList<>();
			for (Object[] args : params) {
				paramsExt.add(new Object[]{args[0], args[1], args[2], args[3], new Date((long)args[3]), null, null, null, null, null});
			}
			jdbcTemplate.batchUpdate(POINT_VALUE_DENORMALIZED_INSERT,paramsExt);
		} else
			jdbcTemplate.batchUpdate(POINT_VALUE_DENORMALIZED_INSERT,params);

	}

	public Long getInceptionDate(int dataPointId) {
		return jdbcTemplate.queryForObject(POINT_VALUE_INCEPTION_DATA, new Object[] {dataPointId}, Long.class);
	}
	
	public long dateRangeCount(int dataPointId, long from, long to) {
		return jdbcTemplate.queryForObject(POINT_VALUE_DATA_RANGE_COUNT, new Object[] {dataPointId, from, to}, Long.class);
	}

	public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty())	return null;
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		LongPair longPair = jdbcTemplate.queryForObject(POINT_VALUE_SELECT_MIN_MAX_TS_BASE_ON_LIST_DATA_POINT,new LongPairRowMapper(),parameters);
			
		return longPair;
	}

	public long getStartTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty())	return -1;
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		Long minTs = jdbcTemplate.queryForObject(POINT_VALUE_SELECT_MIN_TS_BASE_ON_LIST_DATA_POINT,new LongRowMapper(),parameters);
			
		return minTs;
	}

	public long getEndTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty()) return -1;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		Long maxTs = jdbcTemplate.queryForObject(POINT_VALUE_SELECT_MAX_TS_BASE_ON_LIST_DATA_POINT, new LongRowMapper(),parameters);
			
		return maxTs;

	}

	public List<Long> getFiledataIds() {
		//TODO rewrite
		return jdbcTemplate.queryForList(
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

	public Long getLatestPointValue(int dataPointId) {
		try {
			return  jdbcTemplate.queryForObject(POINT_VALUE_SELECT_MAX_BASE_ON_DATA_POINT_ID, new Object[] {dataPointId}, Long.class);
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

	// Apply database specific bounds on double values.
    public double applyBounds(double value) {
        if (Double.isNaN(value))
            return 0;
        if (value == Double.POSITIVE_INFINITY)
            return Double.MAX_VALUE;
        if (value == Double.NEGATIVE_INFINITY)
            return -Double.MAX_VALUE;

        return value;
    }
    
    public long deletePointValuesBeforeWithOutLast(int dataPointId, long time) {
		Long lastId = jdbcTemplate.queryForObject(POINT_VALUE_ID_OF_LAST_VALUE, new Object[] { dataPointId }, Long.class );
    	return jdbcTemplate.update(POINT_VALUE_DELETE_BEFORE, new Object[] {dataPointId, time, lastId});
    }
    
    public long deletePointValue(int dataPointId) {
    	return jdbcTemplate.update(POINT_VALUE_DELETE_BASE_ON_POINT_ID, new Object[] {dataPointId});
    }
    
    public long deleteAllPointData() {
    	return jdbcTemplate.update(POINT_VALUE_DELETE, new Object[] {});
    }
    
    public long deletePointValuesWithMismatchedType(int dataPointId, int dataType) {
    	return jdbcTemplate.update(DELETE_POINT_VALUE_WITH_MISMATCHED_TYPE, new Object[] {dataPointId, dataType});
    }
    /*
    public PointValueTime getPointValue(long id) {
		return ((PointValue) jdbcTemplate.queryForObject(POINT_VALUE_SELECT + " where " + POINT_VALUE_FILTER_BASE_ON_ID, new Object[] {id}, new PointValueRowMapper())).getPointValue();
	}*/

	public long getMinTs(int dataPointId) {
		return jdbcTemplate.queryForObject(SELECT_MIN_TIME_WHERE_DATA_POINT_ID, new Object[] {dataPointId}, Long.TYPE);
	}

	public long getMaxTs(int dataPointId) {
		return jdbcTemplate.queryForObject(SELECT_MAX_TIME_WHERE_DATA_POINT_ID, new Object[] {dataPointId}, Long.TYPE);
	}

	public List<PointHistoryCount> getTopPointHistoryCounts() {
		return jdbcTemplate.query(SELECT_DP_ID_AND_COUNT, new RowMapper<PointHistoryCount>() {
			@Override
			public PointHistoryCount mapRow(ResultSet rs, int rowNum) throws SQLException {
				PointHistoryCount phc = new PointHistoryCount();
				phc.setPointId(rs.getInt(1));
				phc.setCount(rs.getInt(2));
				return phc;
			}
		});
	}

	// @formatter:off
	private static final String POINT_VALUE_ADNNOTATIONS_SELECT = ""
			+ "select "
		//	+COLUMN_NAME_POINT_VALUE_ID + ", "
			+COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
			+COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
			+COLUMN_NAME_SOURCE_TYPE + ", "
			+COLUMN_NAME_SOURCE_ID + " "
			+ "from "
			+"pointValuesDenormalized ";
	private static final String PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE = "pv.";
	private static final String PREFIX_NAME_FOR_USERS_TABLE = "us.";
	private static final String POINT_VALUE_ADNNOTATIONS_SELECT_WITH_OWNER_OF_CHANGE = ""
			+ "select "
			//+PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_POINT_VALUE_ID + ", "
			+PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
			+PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
			+PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_SOURCE_TYPE + ", "
			+PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_SOURCE_ID + ", "
			+PREFIX_NAME_FOR_USERS_TABLE+"username "
			+ "from "
			+"pointValuesDenormalized";
	private static final String POINT_VALUE_ADNNOTATIONS_INSERT = ""
			+ "insert into pointValuesDenormalized  ("
			//+ COLUMN_NAME_POINT_VALUE_ID + ","
			+ COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ","
			+ COLUMN_NAME_TEXT_POINT_VALUE_LONG + ","
			+ COLUMN_NAME_SOURCE_TYPE + ","
			+ COLUMN_NAME_SOURCE_ID
			+") "
			+ "values (?,?,?,?)";

	private static final String POINT_VALUE_ANNOTATIONS_UPDATE = ""
			+ "update pointValuesDenormalized set "
			+ COLUMN_NAME_SOURCE_ID + "=null "
			+ "where "
			+ COLUMN_NAME_SOURCE_ID + "=? "
			+ "and "
			+ COLUMN_NAME_SOURCE_TYPE + "="
			+ SetPointSource.Types.USER;
/*
	public static final String POINT_VALUE_ADNNOTATIONS_FILTER_BASE_ON_POINT_VALUES_ID = " "
			+COLUMN_NAME_POINT_VALUE_ID+"=? ";
*/
	// @formatter:on

	//RowMapper
	private class PointValueAdnnotationRowMapper implements RowMapper<PointValueAdnnotation> {

		public PointValueAdnnotation mapRow(ResultSet rs, int rowNum) throws SQLException {
			PointValueAdnnotation pva = new PointValueAdnnotation();

			//pva.setPointValueId(rs.getLong(COLUMN_NAME_POINT_VALUE_ID));
			pva.setTextPointValueShort(rs.getString(COLUMN_NAME_TEXT_POINT_VALUE_SHORT));
			pva.setTextPointValueLong(rs.getString(COLUMN_NAME_TEXT_POINT_VALUE_LONG));
			pva.setSourceType(rs.getInt(COLUMN_NAME_SOURCE_TYPE));
			pva.setSourceId(rs.getLong(COLUMN_NAME_SOURCE_ID));
			return pva;
		}
	}
	private class PointValueAdnnotationRowMapperWithAdnnotationAboutChangeOwner implements RowMapper<PointValueAdnnotation> {

		public PointValueAdnnotation mapRow(ResultSet rs, int rowNum) throws SQLException {
			PointValueAdnnotation pva = new PointValueAdnnotation();

			//pva.setPointValueId(rs.getLong(PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_POINT_VALUE_ID));
			pva.setTextPointValueShort(rs.getString(PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_TEXT_POINT_VALUE_SHORT));
			pva.setTextPointValueLong(rs.getString(PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_TEXT_POINT_VALUE_LONG));
			pva.setSourceType(rs.getInt(PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_SOURCE_TYPE));
			pva.setSourceId(rs.getLong(PREFIX_NAME_FOR_POINTVALUEADNNOTATION_TABLE+COLUMN_NAME_SOURCE_ID));
			pva.setChangeOwner(rs.getString(PREFIX_NAME_FOR_USERS_TABLE+"username"));
			return pva;
		}
	}

	@Override
	public List<PointValueAdnnotation> findAllWithUserNamePointValueAdnnotations() {
		return (List<PointValueAdnnotation>) jdbcTemplate.query(POINT_VALUE_ADNNOTATIONS_SELECT_WITH_OWNER_OF_CHANGE, new Object[]{ }, new PointValueAdnnotationRowMapperWithAdnnotationAboutChangeOwner());

	}

	@Override
	public List<PointValueAdnnotation> findAllPointValueAdnnotations() {
		return (List<PointValueAdnnotation>) jdbcTemplate.query(POINT_VALUE_ADNNOTATIONS_SELECT_WITH_OWNER_OF_CHANGE/*POINT_VALUE_ADNNOTATIONS_SELECT*/, new Object[]{ }, new PointValueAdnnotationRowMapper());
	}

	/*
	 * @arguments id - idPointValue
	 * @see org.scada_lts.dao.GenericDaoCR#findById(long)
	 */
	/*@Override
	public PointValueAdnnotation findByIdPointValueAdnnotation(Object[] pk) {
		return (PointValueAdnnotation) jdbcTemplate.queryForObject(POINT_VALUE_ADNNOTATIONS_SELECT +" where "+POINT_VALUE_ADNNOTATIONS_FILTER_BASE_ON_POINT_VALUES_ID, pk, new PointValueAdnnotationRowMapper());
	}*/

	@Override
	public List<PointValueAdnnotation> filteredPointValueAdnnotations(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		if (limit != NO_LIMIT) {
			myLimit = LIMIT+limit;
		}
		return (List<PointValueAdnnotation>) jdbcTemplate.query(POINT_VALUE_ADNNOTATIONS_SELECT+" where "+ filter + myLimit, argsFilter, new PointValueAdnnotationRowMapper());
	}


	@Override
	public Object[] create(final PointValueAdnnotation entity) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(entity);
		}

		jdbcTemplate.update(POINT_VALUE_ADNNOTATIONS_INSERT, new Object[] {
		        entity.getPointValueId(),
				entity.getPointValueId(),
				entity.getTextPointValueShort(),
				entity.getTextPointValueLong(),
				entity.getSourceType(),
				entity.getSourceId()
		});


		// table hav'nt pk //TODO add key
		return new Object[] {0};
	}

	@Override
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(int userId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(int userId) userId:" + userId);
		}

		jdbcTemplate.update(POINT_VALUE_ANNOTATIONS_UPDATE, new Object[]{userId});
	}

	@Override
	public void updateAnnotations(List<PointValue> values) {
		Map<Integer, List<AnnotatedPointValueTime>> userIds = new HashMap<Integer, List<AnnotatedPointValueTime>>();
		List<AnnotatedPointValueTime> alist;

		// Look for annotated point values.
		AnnotatedPointValueTime apv;
		for (PointValue pv : values) {
			if (pv.getPointValue() instanceof AnnotatedPointValueTime) {
				apv = (AnnotatedPointValueTime) pv.getPointValue();
				if (apv.getSourceType() == SetPointSource.Types.USER) {
					alist = userIds.get(apv.getSourceId());
					if (alist == null) {
						alist = new ArrayList<AnnotatedPointValueTime>();
						userIds.put(apv.getSourceId(), alist);
					}
					alist.add(apv);
				}
			}
		}
	}

}