package org.scada_lts.dao;

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

import com.mysql.jdbc.Statement;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.DAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for ScheduledEvent.
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */

public class ScheduledEventDAO {

	private static final Log LOG = LogFactory.getLog(ScheduledEventDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_XID = "xid";
	private static final String COLUMN_NAME_ALIAS = "alias";
	private static final String COLUMN_NAME_ALARM_LEVEL = "alarmLevel";
	private static final String COLUMN_NAME_SCHEDULE_TYPE = "scheduleType";
	private static final String COLUMN_NAME_RETURN_TO_NORMAL = "returnToNormal";
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

	// @formatter:off
	private static final String SCHEDULED_EVENT_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_ALIAS + ", "
				+ COLUMN_NAME_ALARM_LEVEL + ", "
				+ COLUMN_NAME_SCHEDULE_TYPE + ", "
				+ COLUMN_NAME_RETURN_TO_NORMAL + ", "
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
				+ COLUMN_NAME_INACTIVE_CRON + " "
			+ "from scheduledEvents ";

	private static final String SCHEDULED_EVENT_INSERT = ""
			+ "insert into scheduledEvents ("
				+ COLUMN_NAME_XID + ", "
				+ COLUMN_NAME_ALIAS + ", "
				+ COLUMN_NAME_ALARM_LEVEL + ", "
				+ COLUMN_NAME_SCHEDULE_TYPE + ", "
				+ COLUMN_NAME_RETURN_TO_NORMAL + ", "
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
				+ COLUMN_NAME_INACTIVE_CRON + " "
			+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

	private static final String SCHEDULED_EVENT_UPDATE = ""
			+ "update scheduledEvents set "
				+ COLUMN_NAME_XID + "=?, "
				+ COLUMN_NAME_ALIAS + "=?, "
				+ COLUMN_NAME_ALARM_LEVEL + "=?, "
				+ COLUMN_NAME_SCHEDULE_TYPE + "=?, "
				+ COLUMN_NAME_RETURN_TO_NORMAL + "=?, "
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

	private static final String SCHEDULED_EVENT_DELETE = ""
			+ "delete from scheduledEvents where "
				+ COLUMN_NAME_ID + "=? ";

	private static final String TEMPLATE_EVENT_HANDLER_DELETE = "delete from eventHandlers where eventTypeId="
			+ EventType.EventSources.SCHEDULED
			+ " and eventTypeRef1=? ";
	// @formatter:on

	private class ScheduledEventRowMapper implements RowMapper<ScheduledEventVO> {

		@Override
		public ScheduledEventVO mapRow(ResultSet rs, int rowNum) throws SQLException {
			ScheduledEventVO scheduledEventVO = new ScheduledEventVO();
			scheduledEventVO.setId(rs.getInt(COLUMN_NAME_ID));
			scheduledEventVO.setXid(rs.getString(COLUMN_NAME_XID));
			scheduledEventVO.setAlias(rs.getString(COLUMN_NAME_ALIAS));
			scheduledEventVO.setAlarmLevel(rs.getInt(COLUMN_NAME_ALARM_LEVEL));
			scheduledEventVO.setScheduleType(rs.getInt(COLUMN_NAME_SCHEDULE_TYPE));
			scheduledEventVO.setReturnToNormal(DAO.charToBool(rs.getString(COLUMN_NAME_RETURN_TO_NORMAL)));
			scheduledEventVO.setDisabled(DAO.charToBool(COLUMN_NAME_DISABLED));
			scheduledEventVO.setActiveYear(rs.getInt(COLUMN_NAME_ACTIVE_YEAR));
			scheduledEventVO.setActiveMonth(rs.getInt(COLUMN_NAME_ACTIVE_MONTH));
			scheduledEventVO.setActiveDay(rs.getInt(COLUMN_NAME_ACTIVE_DAY));
			scheduledEventVO.setActiveHour(rs.getInt(COLUMN_NAME_ACTIVE_HOUR));
			scheduledEventVO.setActiveMinute(rs.getInt(COLUMN_NAME_ACTIVE_MINUTE));
			scheduledEventVO.setActiveSecond(rs.getInt(COLUMN_NAME_ACTIVE_SECOND));
			scheduledEventVO.setActiveCron(rs.getString(COLUMN_NAME_ACTIVE_CRON));
			scheduledEventVO.setInactiveYear(rs.getInt(COLUMN_NAME_INACTIVE_YEAR));
			scheduledEventVO.setInactiveMonth(rs.getInt(COLUMN_NAME_INACTIVE_MONTH));
			scheduledEventVO.setInactiveDay(rs.getInt(COLUMN_NAME_INACTIVE_DAY));
			scheduledEventVO.setInactiveHour(rs.getInt(COLUMN_NAME_INACTIVE_HOUR));
			scheduledEventVO.setInactiveMinute(rs.getInt(COLUMN_NAME_INACTIVE_MINUTE));
			scheduledEventVO.setInactiveSecond(rs.getInt(COLUMN_NAME_INACTIVE_SECOND));
			scheduledEventVO.setInactiveCron(rs.getString(COLUMN_NAME_INACTIVE_CRON));
			return scheduledEventVO;
		}
	}

	public ScheduledEventVO getScheduledEvent(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getScheduledEvent(int id) id:" + id);
		}

		String templateSelectWhereId = SCHEDULED_EVENT_SELECT + "where " + COLUMN_NAME_ID + "=? ";
		
		return DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {id}, new ScheduledEventRowMapper());
		
	}

	public ScheduledEventVO getScheduledEvent(String xid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getScheduledEvent(int xid) xid:" + xid);
		}

		String templateSelectWhereId = SCHEDULED_EVENT_SELECT + "where " + COLUMN_NAME_XID + "=? ";

		ScheduledEventVO scheduledEvent;
		try {
			scheduledEvent = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[] {xid}, new ScheduledEventRowMapper());
		} catch (EmptyResultDataAccessException e) {
			scheduledEvent = null;
		}
		return scheduledEvent;
	}

	public List<ScheduledEventVO> getScheduledEvents() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getScheduledEvents()");
		}

		String templateSelectedOrderBy = SCHEDULED_EVENT_SELECT + " order by scheduleType";

		return DAO.getInstance().getJdbcTemp().query(templateSelectedOrderBy, new ScheduledEventRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final ScheduledEventVO scheduledEventVO) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert(ScheduledEventVO scheduledEventVO) scheduledEventVO:" + scheduledEventVO.toString());
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();

		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(SCHEDULED_EVENT_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {
						scheduledEventVO.getXid(),
						scheduledEventVO.getAlias(),
						scheduledEventVO.getAlarmLevel(),
						scheduledEventVO.getScheduleType(),
						DAO.boolToChar(scheduledEventVO.isReturnToNormal()),
						DAO.boolToChar(scheduledEventVO.isDisabled()),
						scheduledEventVO.getActiveYear(),
						scheduledEventVO.getActiveMonth(),
						scheduledEventVO.getActiveDay(),
						scheduledEventVO.getActiveHour(),
						scheduledEventVO.getActiveMinute(),
						scheduledEventVO.getActiveSecond(),
						scheduledEventVO.getActiveCron(),
						scheduledEventVO.getInactiveYear(),
						scheduledEventVO.getInactiveMonth(),
						scheduledEventVO.getInactiveDay(),
						scheduledEventVO.getInactiveHour(),
						scheduledEventVO.getInactiveMinute(),
						scheduledEventVO.getInactiveSecond(),
						scheduledEventVO.getInactiveCron()
				}).setValues(ps);
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void update(ScheduledEventVO scheduledEventVO) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("update(ScheduledEventVO scheduledEventVO) scheduledEventVO:"+  scheduledEventVO.toString());
		}

		DAO.getInstance().getJdbcTemp().update(SCHEDULED_EVENT_UPDATE,
				new Object[] {
					scheduledEventVO.getXid(),
					scheduledEventVO.getAlias(),
					scheduledEventVO.getAlarmLevel(),
					scheduledEventVO.getScheduleType(),
					DAO.boolToChar(scheduledEventVO.isReturnToNormal()),
					DAO.boolToChar(scheduledEventVO.isDisabled()),
					scheduledEventVO.getActiveYear(),
					scheduledEventVO.getActiveMonth(),
					scheduledEventVO.getActiveDay(),
					scheduledEventVO.getActiveHour(),
					scheduledEventVO.getActiveMinute(),
					scheduledEventVO.getActiveSecond(),
					scheduledEventVO.getActiveCron(),
					scheduledEventVO.getInactiveYear(),
					scheduledEventVO.getInactiveMonth(),
					scheduledEventVO.getInactiveDay(),
					scheduledEventVO.getInactiveHour(),
					scheduledEventVO.getInactiveMinute(),
					scheduledEventVO.getInactiveSecond(),
					scheduledEventVO.getInactiveCron(),
					scheduledEventVO.getId()
				}
		);
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("delete(int id) id:" +  id);
		}

		ScheduledEventVO scheduledEventVO = getScheduledEvent(id);
		if (scheduledEventVO != null) {

			DAO.getInstance().getJdbcTemp().update(TEMPLATE_EVENT_HANDLER_DELETE, new Object[] {id});
			DAO.getInstance().getJdbcTemp().update(SCHEDULED_EVENT_DELETE, new Object[] {id});
		}

	}
}
