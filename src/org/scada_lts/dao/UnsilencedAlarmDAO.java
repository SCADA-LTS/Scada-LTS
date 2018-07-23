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
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.UnsilencedAlarmLevelCache;
import org.springframework.jdbc.core.RowMapper;

/** 
 * DAO for Unsilenced Alarm.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class UnsilencedAlarmDAO {

	private static final Log LOG = LogFactory.getLog(UnsilencedAlarmDAO.class);
	private final static String COLUMN_NAME_MAX = "max";
	private final static String COLUMN_NAME_USERID = "userId";
	// @formatter:off
	private static final String SQL = ""
			+ "select "
			    + "max(e.alarmLevel) as max,"
			    + "userId "
			+ "from "
			    + "userEvents u "
			    + "join events e on u.eventId=e.id "
			+ "where "
				+ "u.silenced='N' "
			+ "group by "
				+ "userId";
	// @formatter:on

	@SuppressWarnings("rawtypes")
	protected List<UnsilencedAlarmLevelCache> getAll() {
		LOG.trace("SQL UnsilencedAlarm");
		try {
			@SuppressWarnings("unchecked")
			List<UnsilencedAlarmLevelCache> listUnsilencedAlarmLevel = DAO.getInstance().getJdbcTemp().query(SQL, new RowMapper() {
				@Override
				public UnsilencedAlarmLevelCache mapRow(ResultSet rs, int rownumber) throws SQLException {
					UnsilencedAlarmLevelCache unsilencedAlarmLevel = new UnsilencedAlarmLevelCache();
					unsilencedAlarmLevel.setAlarmLevel(rs.getInt(COLUMN_NAME_MAX));
					unsilencedAlarmLevel.setUserId(rs.getInt(COLUMN_NAME_USERID));
					return unsilencedAlarmLevel;
				}
			});

			return listUnsilencedAlarmLevel;
		} catch (Exception e) {
			LOG.error(e);
		}
		return null;
	}
	
	protected TreeMap<Integer, Integer> getMapUnsilencedAlarmLevelForUser(
			final List<UnsilencedAlarmLevelCache> listUnsilencedAlarmLevel) {
		LOG.trace("getMapUnsilencedAlarmLevelForUser");
		TreeMap<Integer, Integer> mappedUnsilencedAlarmLevelForUser = new TreeMap<Integer, Integer>();
		for (UnsilencedAlarmLevelCache unsilencedAlarmLevel : listUnsilencedAlarmLevel) {
			mappedUnsilencedAlarmLevelForUser.put(unsilencedAlarmLevel.getUserId(),
					unsilencedAlarmLevel.getAlarmLevel());
		}
		return mappedUnsilencedAlarmLevelForUser;
	}	

}
