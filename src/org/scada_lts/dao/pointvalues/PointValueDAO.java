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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.vo.bean.LongPair;
import com.serotonin.mango.vo.bean.PointHistoryCount;


/** 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
@Repository
public class PointValueDAO implements IPointValueDAO {

	private static final Log LOG = LogFactory.getLog(PointValueDAO.class);
	
	private static IPointValueDAO instance;
	
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
			+ "insert pointValues ("
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
			+ "pv."+COLUMN_NAME_TIME_STAMP+" >= ? order by "+COLUMN_NAME_TIME_STAMP;
	
	public static final String POINT_VALUE_FILTER_BASE_ON_DATA_POINT_ID_AND_TIME_STAMP_FROM_TO = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+COLUMN_NAME_TIME_STAMP+">=? and pv."+COLUMN_NAME_TIME_STAMP+"<? order by "+COLUMN_NAME_TIME_STAMP;
	
	public static final String POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? "
			+ "order by pv."+COLUMN_NAME_TIME_STAMP+" desc";
	
	public static final String POINT_VALUE_FILTER_LATEST_BASE_ON_DATA_POINT_ID = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+COLUMN_NAME_TIME_STAMP+"<? "
			+ "order by pv."+COLUMN_NAME_TIME_STAMP+" desc";
	
	public static final String POINT_VALUE_FILTER_BEFORE_TIME_STAMP_BASE_ON_DATA_POINT_ID = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+COLUMN_NAME_TIME_STAMP+"<? "
			+ "order by pv."+COLUMN_NAME_TIME_STAMP;
	
	public static final String POINT_VALUE_FILTER_AT_TIME_STAMP_BASE_ON_DATA_POINT_ID = " "
			+ "pv."+COLUMN_NAME_DATA_POINT_ID+"=? and "
			+ "pv."+COLUMN_NAME_TIME_STAMP+"=? "
			+ "order by pv."+COLUMN_NAME_TIME_STAMP;

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
			+ "order by id DESC "
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
			"order by " + COLUMN_NAME_ID + " desc " +
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

	// @formatter:on

	private JdbcOperations jdbcTemplate;

	public PointValueDAO() {
		this.jdbcTemplate = DAO.getInstance().getJdbcTemp();
	}

	public PointValueDAO(JdbcOperations jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public static IPointValueDAO getInstance() {
		if (instance == null) {
			instance = new PointValueDAO(DAO.getInstance().getJdbcTemp());
		}
		return instance;
	}

	//RowMappers
	private class PointValueRowMapper implements RowMapper<PointValue> {
		public PointValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			//TODO rewrite MangoValue
			MangoValue value = createMangoValue(rs);
			long time = rs.getLong(COLUMN_NAME_TIME_STAMP);
			
			PointValue pv = new PointValue();
			pv.setId(rs.getLong(COLUMN_NAME_ID));
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
	@Override
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
			long time = rs.getLong(COLUMN_NAME_TIME_STAMP);

			PointValue pv = new PointValue();
			pv.setId(rs.getLong(COLUMN_NAME_ID));
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

	@Override
	public List<PointValue> findAllWithUserName(){
		return (List<PointValue>) jdbcTemplate.query(POINT_VALUE_SELECT_WITH_USERNAME, new Object[]{ }, new PointValueRowMapper());
	}
	@Override
	public List<PointValue> findAll() {

		return (List<PointValue>) jdbcTemplate.query(POINT_VALUE_SELECT, new Object[]{ }, new PointValueRowMapper());
	}

	public PointValue findById(Object[] pk) {
		return (PointValue) jdbcTemplate.queryForObject(POINT_VALUE_SELECT_ON_BASE_ID, pk, new PointValueRowMapper());
	}

	@Override
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
						+ "pv."+COLUMN_NAME_ID + ","
						+ "pv."+COLUMN_NAME_DATA_POINT_ID + ", "
						+ "pv."+COLUMN_NAME_DATA_TYPE + ", "
						+ "pv."+COLUMN_NAME_POINT_VALUE + ", "
						+ "pv."+COLUMN_NAME_TIME_STAMP + ", "
						+ "pva."+COLUMN_NAME_TEXT_POINT_VALUE_SHORT + ", "
						+ "pva."+COLUMN_NAME_TEXT_POINT_VALUE_LONG + ", "
						+ "pva."+COLUMN_NAME_SOURCE_TYPE + ", "
						+ "pva."+COLUMN_NAME_SOURCE_ID + ", "
						+ "us." +COLUMN_NAME_USERNAME_IN_TABLE_USERS
						+ " from "
						+ "pointValues pv "
						+ "left join pointValueAnnotations pva on pv.id=pva.pointValueId "
						+ "left join users us on pva.sourceId = us.id " +" where "+ filter + myLimit, argsFilter, new PointValueRowMapperWithUserName());
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
			 				PreparedStatement ps = connection.prepareStatement(POINT_VALUE_INSERT, Statement.RETURN_GENERATED_KEYS);
			 				new ArgumentPreparedStatementSetter( new Object[] { 
			 						entity.getDataPointId(),
			 						entity.getPointValue().getValue().getDataType(),
			 						getValueBaseOnType( entity.getPointValue().getValue().getDataType(), entity.getPointValue()),
			 						entity.getPointValue().getTime()
			 				}).setValues(ps);
			 				return ps;
			 			}
		}, keyHolder);
		
		return new Object[] {keyHolder.getKey().longValue()};
		
	}
	
	
	@Override
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public Object[] create(final int pointId,final int dataType,final double dvalue,final long time) {
		
		return createNoTransaction(pointId, dataType, dvalue, time);
		
	}

	@Override
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void create(PointValue pointValue, PointValueAdnnotation pointValueAdnnotation, int dataType) {

		PointValueTime pointValueTime = pointValue.getPointValue();
		MangoValue mangoValue = pointValueTime.getValue();

		if (LOG.isTraceEnabled()) {
			LOG.trace("pointId:"+pointValue.getDataPointId()+" dataType:"+mangoValue.getDataType()+" dvalue:"+mangoValue.getObjectValue());
		}

		create((int)pointValue.getDataPointId(), dataType, mangoValue.getDoubleValue(), pointValueTime.getTime());
		IPointValueAdnnotationsDAO.newCommandRepository().create(pointValueAdnnotation);
	}


	@Override
	public Object[] createNoTransaction(final int pointId, final int dataType, final double dvalue, final long time) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("pointId:"+pointId+" dataType:"+dataType+" dvalue:"+time);
		}
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			 			@Override
			 			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException { 
			 				PreparedStatement ps = connection.prepareStatement(POINT_VALUE_INSERT, Statement.RETURN_GENERATED_KEYS);
			 				new ArgumentPreparedStatementSetter( new Object[] { 
			 						pointId,
			 						dataType,
			 						dvalue,
			 						time,
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
	public void createTableForDatapoint(int pointId) {
	}


	@Override
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void executeBatchUpdateInsert( List<Object[]> params) {
		if (LOG.isTraceEnabled()) {
			for (Object[] param : params) {
				for (Object arg :param) {
					LOG.trace("arg:"+arg);
				}
			}
		}

        jdbcTemplate.batchUpdate(POINT_VALUE_INSERT,params);

	}

	@Override
	public Long getInceptionDate(int dataPointId) {
		return jdbcTemplate.queryForObject(POINT_VALUE_INCEPTION_DATA, new Object[] {dataPointId}, Long.class);
	}
	
	@Override
	public long dateRangeCount(int dataPointId, long from, long to) {
		return jdbcTemplate.queryForObject(POINT_VALUE_DATA_RANGE_COUNT, new Object[] {dataPointId, from, to}, Long.class);
	}

	@Override
	public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty())	return null;
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		LongPair longPair = jdbcTemplate.queryForObject(POINT_VALUE_SELECT_MIN_MAX_TS_BASE_ON_LIST_DATA_POINT,new LongPairRowMapper(),parameters);
			
		return longPair;
	}

	@Override
	public long getStartTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty())	return -1;
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		Long minTs = jdbcTemplate.queryForObject(POINT_VALUE_SELECT_MIN_TS_BASE_ON_LIST_DATA_POINT,new LongRowMapper(),parameters);
			
		return minTs;
	}

	@Override
	public long getEndTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty()) return -1;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		Long maxTs = jdbcTemplate.queryForObject(POINT_VALUE_SELECT_MAX_TS_BASE_ON_LIST_DATA_POINT, new LongRowMapper(),parameters);
			
		return maxTs;

	}

	@Override
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

	@Override
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
		Long lastId = jdbcTemplate.queryForObject(POINT_VALUE_ID_OF_LAST_VALUE, new Object[] { dataPointId }, Long.class );
    	return jdbcTemplate.update(POINT_VALUE_DELETE_BEFORE, new Object[] {dataPointId, time, lastId});
    }

	public long deletePointValuesBeforeWithOutLastTwo(int dataPointId, long time) {
		return jdbcTemplate.update(POINT_VALUE_DELETE_BEFORE_WITHOUT_LAST_TWO, new Object[] {dataPointId, dataPointId, time});
	}

    @Override
	public long deletePointValue(int dataPointId) {
    	return jdbcTemplate.update(POINT_VALUE_DELETE_BASE_ON_POINT_ID, new Object[] {dataPointId});
    }
    
    @Override
	public long deleteAllPointData() {
    	return jdbcTemplate.update(POINT_VALUE_DELETE, new Object[] {});
    }
    
    @Override
	public long deletePointValuesWithMismatchedType(int dataPointId, int dataType) {
    	return jdbcTemplate.update(DELETE_POINT_VALUE_WITH_MISMATCHED_TYPE, new Object[] {dataPointId, dataType});
    }

	public long deletePointValuesWithValueLimit(int dataPointId, int limit){
		return jdbcTemplate.update(DELETE_POINT_VALUE_BASED_ON_DATAPOINT_WITH_VALUE_LIMIT, new Object[] {dataPointId, dataPointId, limit});
	}

	public PointValueTime getPointValue(long id) {
		return ((PointValue) jdbcTemplate.queryForObject(POINT_VALUE_SELECT + " where " + POINT_VALUE_FILTER_BASE_ON_ID, new Object[] {id}, new PointValueRowMapper())).getPointValue();
	}

	@Override
	public long getMinTs(int dataPointId) {
		return jdbcTemplate.queryForObject(SELECT_MIN_TIME_WHERE_DATA_POINT_ID, new Object[] {dataPointId}, Long.TYPE);
	}

	@Override
	public long getMaxTs(int dataPointId) {
		return jdbcTemplate.queryForObject(SELECT_MAX_TIME_WHERE_DATA_POINT_ID, new Object[] {dataPointId}, Long.TYPE);
	}

	@Override
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
}