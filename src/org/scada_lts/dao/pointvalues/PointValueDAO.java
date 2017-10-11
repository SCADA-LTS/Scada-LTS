/*
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
import org.scada_lts.config.Configurations;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.point.PointValue;
import org.slf4j.profiler.Profiler;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
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
public class PointValueDAO implements GenericDaoCR<PointValue> {

	private static final Log LOG = LogFactory.getLog(PointValueDAO.class);
	
	private static PointValueDAO instance;
	
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
	
	public static final String POINT_VALUE_DELETE_BEFORE = ""
			+"delete "
			+ "from "
				+ "pointValues "
			+ "where "
				+ COLUMN_NAME_DATA_POINT_ID+"=? and "+COLUMN_NAME_TIME_STAMP+"<?";
	
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
	
	public static PointValueDAO getInstance() {
		if (instance == null) {
			instance = new PointValueDAO();
		}
		return instance;
	}
	
	@Override
	public List<PointValue> findAll() {
		return (List<PointValue>) DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT, new Object[]{ }, new PointValueRowMapper());
	}

	@Override
	public PointValue findById(Object[] pk) {
		return (PointValue) DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_ON_BASE_ID, pk, new PointValueRowMapper());
	}
	
	public List<PointValue> findByIdAndTs(long id, long ts) {
		return DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT_ON_BASE_ID_TS, new Object[]  { id,ts }, new PointValueRowMapper());
	}
	
	@Override
	public List<PointValue> filtered(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		if (limit != NO_LIMIT) {
			//TODO rewrite limit adding in argsFilter
			myLimit = LIMIT+limit;
		}
		return (List<PointValue>) DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT+" where "+ filter + myLimit, argsFilter, new PointValueRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public Object[] create(final PointValue entity) {
		
		Profiler profiler = null;
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler = new Profiler("create point value");
			profiler.start("create");
			if (LOG.isTraceEnabled()) {
				LOG.trace(entity);
			}
		}
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
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
		
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler.stop().print();
		}
		return new Object[] {keyHolder.getKey().longValue()};
		
	}
	
	
	
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public Object[] create(final int pointId,final int dataType,final double dvalue,final long time) {
		
		return createNoTransaction(pointId, dataType, dvalue, time);
		
	}
	
	
	public Object[] createNoTransaction(final int pointId,final int dataType,final double dvalue,final long time) {
		
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
		
		return new Object[] {keyHolder.getKey().longValue()};
		
	}
	
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void executeBatchUpdateInsertInTransaction( List<Object[]> params) {
		executeBatchUpdateInsert( params );
	}
	
	//@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void executeBatchUpdateInsert( List<Object[]> params) {
		
		Profiler profiler = null;
		
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler = new Profiler("executeBatchUpdateInsert:" + params.size());
			profiler.start("prepare parameter");
		}
			
		if (LOG.isTraceEnabled()) {
			for (Object[] param : params) {
				for (Object arg :param) {
					LOG.trace("arg:"+arg);
				}
			}
		}
		
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler.start("execute optymalization");
		}
		if (Configurations.getInstance().getCheckOptimalizationBatchUpdateInsertConfig().getConfig()) {
			//set innodb_flush_log_at_trx_commit = 2;
			// SESION set
			// bulk_insert_buffer_size = 256M in config
			DAO.getInstance().getJdbcTemp().update("SET autocommit=0");
			DAO.getInstance().getJdbcTemp().update("SET unique_checks=0");
			DAO.getInstance().getJdbcTemp().update("SET foreign_key_checks=0");
		}
		
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler.start("batchUpdate");
		}
		
		DAO.getInstance().getJdbcTemp().batchUpdate(POINT_VALUE_INSERT,params);
		
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			profiler.start("disable optymalization");
		}
		if (Configurations.getInstance().getCheckOptimalizationBatchUpdateInsertConfig().getConfig()) {
			DAO.getInstance().getJdbcTemp().update("SET foreign_key_checks=1");
			DAO.getInstance().getJdbcTemp().update("SET unique_checks=1");
			DAO.getInstance().getJdbcTemp().update("SET autocommit=1");
			//set innodb_flush_log_at_trx_commit = 1;
		}
		
		if (Configurations.getInstance().getCheckPerformance().getConfig()) {
			LOG.info(profiler.stop().toString());	
		}
		
	}
		
	public Long getInceptionDate(int dataPointId) {
		return DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_INCEPTION_DATA, new Object[] {dataPointId}, Long.class);
	}
	
	public long dateRangeCount(int dataPointId, long from, long to) {
		return DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_DATA_RANGE_COUNT, new Object[] {dataPointId, from, to}, Long.class);
	}
	
	public LongPair getStartAndEndTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty())	return null;
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		LongPair longPair = DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_MIN_MAX_TS_BASE_ON_LIST_DATA_POINT,new LongPairRowMapper(),parameters);
			
		return longPair;
	}
	
	public long getStartTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty())	return -1;
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		Long minTs = DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_MIN_TS_BASE_ON_LIST_DATA_POINT,new LongRowMapper(),parameters);
			
		return minTs;
	}
	
	public long getEndTime(List<Integer> dataPointIds) {
		if (dataPointIds.isEmpty()) return -1;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids",dataPointIds);

		Long maxTs = DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_MAX_TS_BASE_ON_LIST_DATA_POINT, new LongRowMapper(),parameters);
			
		return maxTs;

	}
	
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
    
    public long deletePointValuesBefore(int dataPointId, long time) {
    	return DAO.getInstance().getJdbcTemp().update(POINT_VALUE_DELETE_BEFORE, new Object[] {dataPointId, time});
    }
    
    public long deletePointValue(int dataPointId) {
    	return DAO.getInstance().getJdbcTemp().update(POINT_VALUE_DELETE_BASE_ON_POINT_ID, new Object[] {dataPointId});
    }
    
    public long deleteAllPointData() {
    	return DAO.getInstance().getJdbcTemp().update(POINT_VALUE_DELETE, new Object[] {});
    }
    
    public long deletePointValuesWithMismatchedType(int dataPointId, int dataType) {
    	return DAO.getInstance().getJdbcTemp().update(DELETE_POINT_VALUE_WITH_MISMATCHED_TYPE, new Object[] {dataPointId, dataType});
    }
    
    public PointValueTime getPointValue(long id) {
		return ((PointValue) DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT + " where " + POINT_VALUE_FILTER_BASE_ON_ID, new Object[] {id}, new PointValueRowMapper())).getPointValue();
	}

	public long getMinTs(int dataPointId) {
		return DAO.getInstance().getJdbcTemp().queryForObject(SELECT_MIN_TIME_WHERE_DATA_POINT_ID, new Object[] {dataPointId}, Long.TYPE);
	}

	public long getMaxTs(int dataPointId) {
		return DAO.getInstance().getJdbcTemp().queryForObject(SELECT_MAX_TIME_WHERE_DATA_POINT_ID, new Object[] {dataPointId}, Long.TYPE);
	}

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
}