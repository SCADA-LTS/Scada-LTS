/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.Statement;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.MaintenanceEventVO;

/**
 * DAO for MaintenanceEvent
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class MaintenanceEventDAO {

	private static final Log LOG = LogFactory.getLog(MaintenanceEventDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_DATA_SOURCE_ID = "dataSourceId";
	private static final String COLUMN_NAME_ALIAS = "alias";
	private static final String COLUMN_NAME_ALARM_LEVEL = "alarmLevel";
	private static final String COLUMN_NAME_SCHEDULE_TYPE = "scheduleType";
	private static final String COLUMN_NAME_DISABLED = "disabled";
	private static final String COLUMN_NAME_ACTIVE_YEAR = "activeYear";
	private static final String COLUMN_NAME_ACTIVE_MONTH = "activeMonth";
	private static final String COLUMN_NAME_ACTIVE_DAY = "activeDay";
	private static final String COLUMN_NAME_ACTIVE_HOUR = "activeHour";
	private static final String COLUMN_NAME_ACTIVE_MINUTE = "activeMinute";
	private static final String COLUMN_NAME_ACTIVE_SECOND = "activeSecond";
	private static final String COLUMN_NAME_ACTIVE_CRON = "activeCron";
	private static final String COLUMN_NAME_INACTIVE_YEAR = "inactiveYear";
	private static final String COLUMN_NAME_INACTIVE_MONTH = "inactiveMonth";
	private static final String COLUMN_NAME_INACTIVE_DAY = "inactiveDay";
	private static final String COLUMN_NAME_INACTIVE_HOUR = "inactiveHour";
	private static final String COLUMN_NAME_INACTIVE_MINUTE = "inactiveMinute";
	private static final String COLUMN_NAME_INACTIVE_SECOND = "inactiveSecond";
	private static final String COLUMN_NAME_INACTIVE_CRON = "inactiveCron";

	private static final String COLUMN_NAME_DS_ID = "id";
	private static final String COLUMN_NAME_DS_XID = "xid";
	private static final String COLUMN_NAME_DS_NAME = "name";
	private static final String COLUMN_NAME_DS_TYPE = "dataSourceType";

	private static final String COLUMN_NAME_ET_ID = "eventTypeId";
	private static final String COLUMN_NAME_ET_REF1 = "eventTypeRef1";

	// @formatter:off
	private static final String MAINTENANCE_EVENT_SELECT = ""
			+ "select "
				+ "m." + COLUMN_NAME_ID + ", "
				+ "m." + COLUMN_NAME_XID + ", "
				+ "m." + COLUMN_NAME_DATA_SOURCE_ID + ", "
				+ "m." + COLUMN_NAME_ALIAS + ", "
				+ "m." + COLUMN_NAME_ALARM_LEVEL + ", "
				+ "m." + COLUMN_NAME_SCHEDULE_TYPE + ", "
				+ "m." + COLUMN_NAME_DISABLED + ", "
				+ "m." + COLUMN_NAME_ACTIVE_YEAR + ", "
				+ "m." + COLUMN_NAME_ACTIVE_MONTH + ", "
				+ "m." + COLUMN_NAME_ACTIVE_DAY + ", "
				+ "m." + COLUMN_NAME_ACTIVE_HOUR + ", "
				+ "m." + COLUMN_NAME_ACTIVE_MINUTE + ", "
				+ "m." + COLUMN_NAME_ACTIVE_SECOND + ", "
				+ "m." + COLUMN_NAME_ACTIVE_CRON + ", "
				+ "m." + COLUMN_NAME_INACTIVE_YEAR + ", "
				+ "m." + COLUMN_NAME_INACTIVE_MONTH + ", "
				+ "m." + COLUMN_NAME_INACTIVE_DAY + ", "
				+ "m." + COLUMN_NAME_INACTIVE_HOUR + ", "
				+ "m." + COLUMN_NAME_INACTIVE_MINUTE + ", "
				+ "m." + COLUMN_NAME_INACTIVE_SECOND + ", "
				+ "m." + COLUMN_NAME_INACTIVE_CRON + ", "
				+ "d." + COLUMN_NAME_DS_TYPE + ", "
				+ "d." + COLUMN_NAME_DS_NAME + ", "
				+ "d." + COLUMN_NAME_DS_XID + " "
			+ "from maintenanceEvents m join dataSources d on "
				+ "m." + COLUMN_NAME_DATA_SOURCE_ID + "="
				+ "d." + COLUMN_NAME_DS_ID + " ";

	private static final String MAINTENANCE_EVENT_INSERT = ""
			+ "insert into maintenanceEvents ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_DATA_SOURCE_ID + ", "
				+ COLUMN_NAME_ALIAS + ", "
				+ COLUMN_NAME_ALARM_LEVEL + ", "
				+ COLUMN_NAME_SCHEDULE_TYPE + ", "
				+ COLUMN_NAME_DISABLED + ", "
				+ COLUMN_NAME_ACTIVE_YEAR + ", "
				+ COLUMN_NAME_ACTIVE_MONTH + ", "
				+ COLUMN_NAME_ACTIVE_DAY + ", "
				+ COLUMN_NAME_ACTIVE_HOUR + ", "
				+ COLUMN_NAME_ACTIVE_MINUTE + ", "
				+ COLUMN_NAME_ACTIVE_SECOND + ", "
				+ COLUMN_NAME_ACTIVE_CRON + ", "
				+ COLUMN_NAME_INACTIVE_YEAR + ", "
				+ COLUMN_NAME_INACTIVE_MONTH + ", "
				+ COLUMN_NAME_INACTIVE_DAY + ", "
				+ COLUMN_NAME_INACTIVE_HOUR + ", "
				+ COLUMN_NAME_INACTIVE_MINUTE + ", "
				+ COLUMN_NAME_INACTIVE_SECOND + ", "
				+ COLUMN_NAME_INACTIVE_CRON
			+ ") "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String MAINTENANCE_EVENT_UPDATE = ""
			+ "update maintenanceEvents set "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_DATA_SOURCE_ID + "=?, "
				+ COLUMN_NAME_ALIAS + "=?, "
				+ COLUMN_NAME_ALARM_LEVEL + "=?, "
				+ COLUMN_NAME_SCHEDULE_TYPE + "=?, "
				+ COLUMN_NAME_DISABLED + "=?, "
				+ COLUMN_NAME_ACTIVE_YEAR + "=?, "
				+ COLUMN_NAME_ACTIVE_MONTH + "=?, "
				+ COLUMN_NAME_ACTIVE_DAY + "=?, "
				+ COLUMN_NAME_ACTIVE_HOUR + "=?, "
				+ COLUMN_NAME_ACTIVE_MINUTE + "=?, "
				+ COLUMN_NAME_ACTIVE_SECOND + "=?, "
				+ COLUMN_NAME_ACTIVE_CRON + "=?, "
				+ COLUMN_NAME_INACTIVE_YEAR + "=?, "
				+ COLUMN_NAME_INACTIVE_MONTH + "=?, "
				+ COLUMN_NAME_INACTIVE_DAY + "=?, "
				+ COLUMN_NAME_INACTIVE_HOUR + "=?, "
				+ COLUMN_NAME_INACTIVE_MINUTE + "=?, "
				+ COLUMN_NAME_INACTIVE_SECOND + "=?, "
				+ COLUMN_NAME_INACTIVE_CRON + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String MAINTENANCE_EVENT_DELETE = ""
			+ "delete from maintenanceEvents where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String MAINTENANCE_EVENT_SELECT_DATA_SOURCE = ""
			+ "select "
				+ COLUMN_NAME_ID + " "
			+ "from maintenanceEvents where "
				+ COLUMN_NAME_DATA_SOURCE_ID + "=? ";

	private static final String EVENT_HANDLER_DELETE = ""
			+ "delete from eventHandlers where "
				+ COLUMN_NAME_ET_ID + "="
				+ EventType.EventSources.SCHEDULED
			+ " and "
				+ COLUMN_NAME_ET_REF1 + "=? ";
	// @formatter:on

	private class MaintenanceEventRowMapper implements RowMapper<MaintenanceEventVO> {

		@Override
		public MaintenanceEventVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			MaintenanceEventVO maintenanceEvent = new MaintenanceEventVO();
			maintenanceEvent.setId(rs.getInt(COLUMN_NAME_ID));
			maintenanceEvent.setXid(rs.getString(COLUMN_NAME_XID));
			maintenanceEvent.setDataSourceId(rs.getInt(COLUMN_NAME_DATA_SOURCE_ID));
			maintenanceEvent.setAlias(rs.getString(COLUMN_NAME_ALIAS));
			maintenanceEvent.setAlarmLevel(rs.getInt(COLUMN_NAME_ALARM_LEVEL));
			maintenanceEvent.setScheduleType(rs.getInt(COLUMN_NAME_SCHEDULE_TYPE));
			maintenanceEvent.setDisabled(DAO.charToBool(COLUMN_NAME_DISABLED));
			maintenanceEvent.setActiveYear(rs.getInt(COLUMN_NAME_ACTIVE_YEAR));
			maintenanceEvent.setActiveMonth(rs.getInt(COLUMN_NAME_ACTIVE_MONTH));
			maintenanceEvent.setActiveDay(rs.getInt(COLUMN_NAME_ACTIVE_DAY));
			maintenanceEvent.setActiveHour(rs.getInt(COLUMN_NAME_ACTIVE_HOUR));
			maintenanceEvent.setActiveMinute(rs.getInt(COLUMN_NAME_ACTIVE_MINUTE));
			maintenanceEvent.setActiveSecond(rs.getInt(COLUMN_NAME_ACTIVE_SECOND));
			maintenanceEvent.setActiveCron(rs.getString(COLUMN_NAME_ACTIVE_CRON));
			maintenanceEvent.setInactiveYear(rs.getInt(COLUMN_NAME_INACTIVE_YEAR));
			maintenanceEvent.setInactiveMonth(rs.getInt(COLUMN_NAME_INACTIVE_MONTH));
			maintenanceEvent.setInactiveDay(rs.getInt(COLUMN_NAME_INACTIVE_DAY));
			maintenanceEvent.setInactiveHour(rs.getInt(COLUMN_NAME_INACTIVE_HOUR));
			maintenanceEvent.setInactiveMinute(rs.getInt(COLUMN_NAME_INACTIVE_MINUTE));
			maintenanceEvent.setInactiveSecond(rs.getInt(COLUMN_NAME_INACTIVE_SECOND));
			maintenanceEvent.setInactiveCron(rs.getString(COLUMN_NAME_INACTIVE_CRON));
			return maintenanceEvent;
		}
	}

	public MaintenanceEventVO getMaintenanceEvent(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getMaintenanceEvent(int id) id:" + id);
		}

		String templateSelectWhereId = MAINTENANCE_EVENT_SELECT + "where m." + COLUMN_NAME_ID + "=?";
		
		return DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {id}, new MaintenanceEventRowMapper());
		
	}

	public MaintenanceEventVO getMaintenanceEvent(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getMaintenanceEvent(String xid) xid:" + xid);
		}

		String templateSelectWhereId = MAINTENANCE_EVENT_SELECT + "where m." + COLUMN_NAME_XID + "=?";

		MaintenanceEventVO maintenanceEvent;
		try {
			maintenanceEvent = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {xid}, new MaintenanceEventRowMapper());
		} catch (EmptyResultDataAccessException e) {
			maintenanceEvent = null;
		}
		return maintenanceEvent;
	}

	public List<MaintenanceEventVO> getMaintenanceEvents() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getMaintenanceEvents()");
		}

		return DAO.getInstance().getJdbcTemp().query(MAINTENANCE_EVENT_SELECT, new MaintenanceEventRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final MaintenanceEventVO maintenanceEvent) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(MaintenanceEventVO maintenanceEvent) maintenanceEvent:" + maintenanceEvent.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(MAINTENANCE_EVENT_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {
						maintenanceEvent.getXid(),
						maintenanceEvent.getDataSourceId(),
						maintenanceEvent.getAlias(),
						maintenanceEvent.getAlarmLevel(),
						maintenanceEvent.getScheduleType(),
						DAO.boolToChar(maintenanceEvent.isDisabled()),
						maintenanceEvent.getActiveYear(),
						maintenanceEvent.getActiveMonth(),
						maintenanceEvent.getActiveDay(),
						maintenanceEvent.getActiveHour(),
						maintenanceEvent.getActiveMinute(),
						maintenanceEvent.getActiveSecond(),
						maintenanceEvent.getActiveCron(),
						maintenanceEvent.getInactiveYear(),
						maintenanceEvent.getInactiveMonth(),
						maintenanceEvent.getInactiveDay(),
						maintenanceEvent.getInactiveHour(),
						maintenanceEvent.getInactiveMinute(),
						maintenanceEvent.getInactiveSecond(),
						maintenanceEvent.getInactiveCron()

				}).setValues(ps);
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(MaintenanceEventVO maintenanceEvent) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(MaintenanceEventVO maintenanceEvent) maintenanceEvent:" + maintenanceEvent.toString());
		}

		DAO.getInstance().getJdbcTemp().update(MAINTENANCE_EVENT_UPDATE,
				new Object[] {
						maintenanceEvent.getXid(),
						maintenanceEvent.getDataSourceId(),
						maintenanceEvent.getAlias(),
						maintenanceEvent.getAlarmLevel(),
						maintenanceEvent.getScheduleType(),
						DAO.boolToChar(maintenanceEvent.isDisabled()),
						maintenanceEvent.getActiveYear(),
						maintenanceEvent.getActiveMonth(),
						maintenanceEvent.getActiveDay(),
						maintenanceEvent.getActiveHour(),
						maintenanceEvent.getActiveMinute(),
						maintenanceEvent.getActiveSecond(),
						maintenanceEvent.getActiveCron(),
						maintenanceEvent.getInactiveYear(),
						maintenanceEvent.getInactiveMonth(),
						maintenanceEvent.getInactiveDay(),
						maintenanceEvent.getInactiveHour(),
						maintenanceEvent.getInactiveMinute(),
						maintenanceEvent.getInactiveSecond(),
						maintenanceEvent.getInactiveCron(),
						maintenanceEvent.getId()
				}
		);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" + id);
		}

		MaintenanceEventVO maintenanceEvent = getMaintenanceEvent(id);
		if (maintenanceEvent != null) {
			DAO.getInstance().getJdbcTemp().update(EVENT_HANDLER_DELETE, new Object[] {id});
			DAO.getInstance().getJdbcTemp().update(MAINTENANCE_EVENT_DELETE, new Object[] {id});
		}
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void deleteMaintenanceEventsForDataSource(int dataSourceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteMaintenanceEventsForDataSource(int dataSourceId) dataSourceId:" + dataSourceId);
		}

		List<Integer> ids = DAO.getInstance().getJdbcTemp().queryForList(MAINTENANCE_EVENT_SELECT_DATA_SOURCE,
				new Object[] {dataSourceId}, Integer.class);

		for (Integer id: ids) {
			delete(id);
		}
	}
}
