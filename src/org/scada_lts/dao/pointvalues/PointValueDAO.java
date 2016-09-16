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
import org.scada_lts.dao.GenericDaoCR;
import org.scada_lts.dao.model.point.PointValue;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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


/** 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class PointValueDAO implements GenericDaoCR<PointValue> {

	private static final Log LOG = LogFactory.getLog(PointValueDAO.class);
	
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
	
	private static final String POINT_VALUE_SELECT_ON_BASE_ID = "" 
			+ POINT_VALUE_SELECT 
			+ " where " 
				+ COLUMN_NAME_ID + "=?";
	
	private static final String POINT_VALUE_INSERT = ""
			+ "insert pointValues ("
				+ COLUMN_NAME_DATA_POINT_ID + "," 
				+ COLUMN_NAME_DATA_TYPE + "," 
				+ COLUMN_NAME_POINT_VALUE + "," 
				+ COLUMN_NAME_TIME_STAMP 
			+") "
			+ "values (?,?,?,?)";
	
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
 
	// @formatter:on
	
	//RowMapper
	private class PointValueRowMapper implements RowMapper<PointValue> {
		public PointValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			//TODO rewrite MangoValue
			MangoValue value = createMangoValue(rs);
			long time = rs.getLong(COLUMN_NAME_TIME_STAMP);
			int sourceType = rs.getInt(COLUMN_NAME_SOURCE_TYPE);
			PointValue pv = new PointValue();
			pv.setId(rs.getLong(COLUMN_NAME_ID));
			pv.setDataPointId(rs.getInt(COLUMN_NAME_DATA_POINT_ID));
			
			//TODO rewrite wasNull ?
			if (rs.wasNull()) {
				// No annotations, just return a point value.
				pv.setPointValue(new PointValueTime(value, time));
			} else {
				// There was a source for the point value, so return an annotated
				// version.
				pv.setPointValue(new AnnotatedPointValueTime(value, time, sourceType,
						rs.getInt(COLUMN_NAME_SOURCE_ID)));
			}
			return pv; 
		}
	}
	
	//TODO rewrite for new types
	MangoValue createMangoValue(ResultSet rs)
			throws SQLException {
		
		int dataType = rs.getInt(COLUMN_NAME_DATA_TYPE);
		MangoValue value;
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
		case (DataTypes.IMAGE):
			value = new ImageValue(Integer.parseInt(rs
					.getString(COLUMN_NAME_TEXT_POINT_VALUE_SHORT)),
					rs.getInt(COLUMN_NAME_TEXT_POINT_VALUE_LONG));
			break;
		default:
			value = null;
		}
		return value;
	}
	
	@Override
	public List<PointValue> findAll() {
		return (List<PointValue>) DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT, new Object[]{ }, new PointValueRowMapper());
	}

	@Override
	public PointValue findById(long id) {
		return (PointValue) DAO.getInstance().getJdbcTemp().queryForObject(POINT_VALUE_SELECT_ON_BASE_ID, new Object[]  { id }, new PointValueRowMapper());
	}
	
	@Override
	public List<PointValue> filtered(String filter, Object[] argsFilter, long limit) {
		String myLimit="";
		if (limit != NO_LIMIT) {
			myLimit = LIMIT+limit;
		}
		return (List<PointValue>) DAO.getInstance().getJdbcTemp().query(POINT_VALUE_SELECT+" where "+ filter + myLimit, argsFilter, new PointValueRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	@Override
	public long create(PointValue entity) {
		
		if (LOG.isTraceEnabled()) {
			LOG.trace(entity);
		}
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			 			@Override
			 			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			 				PreparedStatement ps = connection.prepareStatement(POINT_VALUE_INSERT, Statement.RETURN_GENERATED_KEYS);
			 				new ArgumentPreparedStatementSetter( new Object[] { 
			 						entity.getDataPointId(),
			 						entity.getPointValue().getValue().getDataType(),
			 						entity.getPointValue().getDoubleValue(),
			 						entity.getPointValue().getTime()
			 				}).setValues(ps);
			 				return ps;
			 			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
		
	}
	
}