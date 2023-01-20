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
package org.scada_lts.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.PointEventDetectorCacheEntry;
import org.springframework.jdbc.core.RowMapper;

import com.serotonin.mango.vo.event.PointEventDetectorVO;

/** 
 * DAO for EventDetectors 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
@Deprecated
public class EventDetectorsCacheDAO {
	
	private static final Log LOG = LogFactory.getLog(EventDetectorsCacheDAO.class);
	private final static String  COLUMN_NAME_ID = "id";
	private final static String  COLUMN_NAME_XID = "xid";
	private final static String  COLUMN_NAME_ALIAS = "alias";
	private final static String  COLUMN_NAME_DETECTOR_TYPE = "detectorType";
	private final static String  COLUMN_NAME_ALARM_LEVEL = "alarmLevel";
	private final static String  COLUMN_NAME_STATE_LIMIT = "stateLimit";
	private final static String  COLUMN_NAME_DURATION = "duration";
	private final static String  COLUMN_NAME_DURATION_TYPE = "durationType";
	private final static String  COLUMN_NAME_BINARY_STATE = "binaryState";
	private final static String  COLUMN_NAME_MYLTISTATE_STATE = "multistateState";
	private final static String  COLUMN_NAME_CHANGE_COUNT = "changeCount";
	private final static String  COLUMN_NAME_ALPHANUMERIC_STATE = "alphanumericState";
	private final static String  COLUMN_NAME_WEIGHT = "weight";
	private final static String  COLUMN_NAME_DATA_POINT_ID = "dataPointId";
	
	// @formatter:off 
	private static final String SQL = ""
			+ "select "
				+ "id, "
				+ "xid, "
				+ "alias, "
				+ "detectorType, "
				+ "alarmLevel, "
				+ "stateLimit, "
				+ "duration, "
				+ "durationType, "
				+ "binaryState,   "
				+ "multistateState, "
				+ "changeCount, "
				+ "alphanumericState, "
				+ "weight, "
				+ "dataPointId "
			+ "from "
				+ "pointEventDetectors "
			+ "order by dataPointId";
	// @formatter:on
	
	@SuppressWarnings("rawtypes")
	protected List<PointEventDetectorCacheEntry> getAll() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("SQL EventDetectors");
		}
		try {
			@SuppressWarnings("unchecked")
			List<PointEventDetectorCacheEntry> listPointEventDetectorVO = DAO.getInstance().getJdbcTemp().query(SQL, new RowMapper() {
				@Override
				public PointEventDetectorCacheEntry mapRow(ResultSet rs, int rownumber) throws SQLException {
					PointEventDetectorVO eventDetector = new PointEventDetectorVO();
					eventDetector.setId(rs.getInt(COLUMN_NAME_ID));
					eventDetector.setXid(rs.getString(COLUMN_NAME_XID));
					eventDetector.setAlias(rs.getString(COLUMN_NAME_ALIAS));
					eventDetector.setDetectorType(rs.getInt(COLUMN_NAME_DETECTOR_TYPE));
					eventDetector.setAlarmLevel(rs.getInt(COLUMN_NAME_ALARM_LEVEL));
					eventDetector.setLimit(rs.getDouble(COLUMN_NAME_STATE_LIMIT));
					eventDetector.setDuration(rs.getInt(COLUMN_NAME_DURATION));
					eventDetector.setDurationType(rs.getInt(COLUMN_NAME_DURATION_TYPE));
					eventDetector.setBinaryState(rs.getBoolean(COLUMN_NAME_BINARY_STATE));
					eventDetector.setMultistateState(rs.getInt(COLUMN_NAME_MYLTISTATE_STATE));
					eventDetector.setChangeCount(rs.getInt(COLUMN_NAME_CHANGE_COUNT));
					eventDetector.setAlphanumericState(rs.getString(COLUMN_NAME_ALPHANUMERIC_STATE));
					eventDetector.setWeight(rs.getDouble(COLUMN_NAME_WEIGHT));
					
					PointEventDetectorCacheEntry pedc = new PointEventDetectorCacheEntry();
					pedc.setPointEventDetector(eventDetector);
					pedc.setDataPointId(rs.getInt(COLUMN_NAME_DATA_POINT_ID));
					
					return pedc;
				}
			});

			return listPointEventDetectorVO;
		} catch (Exception e) {
			LOG.error(e);
		}
		return null;
	}
	

	protected TreeMap<Integer, List<PointEventDetectorVO>> getMapEventDetectors(
			final List<PointEventDetectorCacheEntry> listEventDetectorCache) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("getMapEventDetectorForUser");
		}
		TreeMap<Integer, List<PointEventDetectorVO>> mappedEventDetectorForUser = new TreeMap<Integer, List<PointEventDetectorVO>>();
		
		for (PointEventDetectorCacheEntry eventDetector : listEventDetectorCache) {
		    int key = eventDetector.getDataPointId();
			if (mappedEventDetectorForUser.get(key) == null) {
				mappedEventDetectorForUser.put(key, new ArrayList<PointEventDetectorVO>());
			}
			mappedEventDetectorForUser.get(key).add(eventDetector.getPointEventDetector());
		}
		return mappedEventDetectorForUser;
	}	
}
