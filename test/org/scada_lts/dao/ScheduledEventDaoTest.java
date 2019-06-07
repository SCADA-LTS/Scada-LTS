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
package org.scada_lts.dao;

import com.serotonin.mango.vo.event.ScheduledEventVO;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test ScheduledEventDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ScheduledEventDaoTest extends TestDAO {

	private static final String XID = "schedule xid";
	private static final String ALIAS = "schedule alias";
	private static final int ALARM_LEVEL = 2;
	private static final int SCHEDULE_TYPE = 1;
	private static final boolean RETURN_TO_NORMAL = true;
	private static final boolean DISABLED = false;
	private static final int ACTIVE_YEAR = 2000;
	private static final int ACTIVE_MONTH = 10;
	private static final int ACTIVE_DAY = 5;
	private static final int ACTIVE_HOUR = 7;
	private static final int ACTIVE_MINUTE = 12;
	private static final int ACTIVE_SECOND = 15;
	private static final String ACTIVE_CRON = "schedule activeCron";
	private static final int INACTIVE_YEAR = 1500;
	private static final int INACTIVE_MONTH = 1;
	private static final int INACTIVE_DAY = 4;
	private static final int INACTIVE_HOUR = 8;
	private static final int INACTIVE_MINUTE = 45;
	private static final int INACTIVE_SECOND = 3;
	private static final String INACTIVE_CRON = "schedule inactiveCron";

	private static final String SECOND_XID = "second xid";
	private static final String SECOND_ALIAS = "second alias";
	private static final int SECOND_ALARM_LEVEL = 1;
	private static final int SECOND_SCHEDULE_TYPE = 3;
	private static final boolean SECOND_RETURN_TO_NORMAL = false;
	private static final boolean SECOND_DISABLED = true;
	private static final int SECOND_ACTIVE_YEAR = 1457;
	private static final int SECOND_ACTIVE_MONTH = 8;
	private static final int SECOND_ACTIVE_DAY = 1;
	private static final int SECOND_ACTIVE_HOUR = 4;
	private static final int SECOND_ACTIVE_MINUTE = 2;
	private static final int SECOND_ACTIVE_SECOND = 6;
	private static final String SECOND_ACTIVE_CRON = "second activeCron";
	private static final int SECOND_INACTIVE_YEAR = 1647;
	private static final int SECOND_INACTIVE_MONTH = 6;
	private static final int SECOND_INACTIVE_DAY = 3;
	private static final int SECOND_INACTIVE_HOUR = 5;
	private static final int SECOND_INACTIVE_MINUTE = 34;
	private static final int SECOND_INACTIVE_SECOND = 21;
	private static final String SECOND_INACTIVE_CRON = "second inactiveCron";

	private static final String UPDATE_XID = "update xid";
	private static final String UPDATE_ALIAS = "update alias";
	private static final int UPDATE_ALARM_LEVEL = 1;
	private static final int UPDATE_SCHEDULE_TYPE = 3;
	private static final boolean UPDATE_RETURN_TO_NORMAL = false;
	private static final boolean UPDATE_DISABLED = true;
	private static final int UPDATE_ACTIVE_YEAR = 200;
	private static final int UPDATE_ACTIVE_MONTH = 2;
	private static final int UPDATE_ACTIVE_DAY = 6;
	private static final int UPDATE_ACTIVE_HOUR = 4;
	private static final int UPDATE_ACTIVE_MINUTE = 1;
	private static final int UPDATE_ACTIVE_SECOND = 1;
	private static final String UPDATE_ACTIVE_CRON = "update activeCron";
	private static final int UPDATE_INACTIVE_YEAR = 1554;
	private static final int UPDATE_INACTIVE_MONTH = 3;
	private static final int UPDATE_INACTIVE_DAY = 2;
	private static final int UPDATE_INACTIVE_HOUR = 1;
	private static final int UPDATE_INACTIVE_MINUTE = 4;
	private static final int UPDATE_INACTIVE_SECOND = 4;
	private static final String UPDATE_INACTIVE_CRON = "update inactiveCron";

	private static final int LIST_SIZE = 2;

	@Test
	public void test() {

		ScheduledEventVO scheduledEventVO = new ScheduledEventVO();
		scheduledEventVO.setXid(XID);
		scheduledEventVO.setAlias(ALIAS);
		scheduledEventVO.setAlarmLevel(ALARM_LEVEL);
		scheduledEventVO.setScheduleType(SCHEDULE_TYPE);
		scheduledEventVO.setReturnToNormal(RETURN_TO_NORMAL);
		scheduledEventVO.setDisabled(DISABLED);
		scheduledEventVO.setActiveYear(ACTIVE_YEAR);
		scheduledEventVO.setActiveMonth(ACTIVE_MONTH);
		scheduledEventVO.setActiveDay(ACTIVE_DAY);
		scheduledEventVO.setActiveHour(ACTIVE_HOUR);
		scheduledEventVO.setActiveMinute(ACTIVE_MINUTE);
		scheduledEventVO.setActiveSecond(ACTIVE_SECOND);
		scheduledEventVO.setActiveCron(ACTIVE_CRON);
		scheduledEventVO.setInactiveYear(INACTIVE_YEAR);
		scheduledEventVO.setInactiveMonth(INACTIVE_MONTH);
		scheduledEventVO.setInactiveDay(INACTIVE_DAY);
		scheduledEventVO.setInactiveHour(INACTIVE_HOUR);
		scheduledEventVO.setInactiveMinute(INACTIVE_MINUTE);
		scheduledEventVO.setInactiveSecond(INACTIVE_SECOND);
		scheduledEventVO.setInactiveCron(INACTIVE_CRON);

		ScheduledEventVO secondScheduledEventVO = new ScheduledEventVO();
		secondScheduledEventVO.setXid(SECOND_XID);
		secondScheduledEventVO.setAlias(SECOND_ALIAS);
		secondScheduledEventVO.setAlarmLevel(SECOND_ALARM_LEVEL);
		secondScheduledEventVO.setScheduleType(SECOND_SCHEDULE_TYPE);
		secondScheduledEventVO.setReturnToNormal(SECOND_RETURN_TO_NORMAL);
		secondScheduledEventVO.setDisabled(SECOND_DISABLED);
		secondScheduledEventVO.setActiveYear(SECOND_ACTIVE_YEAR);
		secondScheduledEventVO.setActiveMonth(SECOND_ACTIVE_MONTH);
		secondScheduledEventVO.setActiveDay(SECOND_ACTIVE_DAY);
		secondScheduledEventVO.setActiveHour(SECOND_ACTIVE_HOUR);
		secondScheduledEventVO.setActiveMinute(SECOND_ACTIVE_MINUTE);
		secondScheduledEventVO.setActiveSecond(SECOND_ACTIVE_SECOND);
		secondScheduledEventVO.setActiveCron(SECOND_ACTIVE_CRON);
		secondScheduledEventVO.setInactiveYear(SECOND_INACTIVE_YEAR);
		secondScheduledEventVO.setInactiveMonth(SECOND_INACTIVE_MONTH);
		secondScheduledEventVO.setInactiveDay(SECOND_INACTIVE_DAY);
		secondScheduledEventVO.setInactiveHour(SECOND_INACTIVE_HOUR);
		secondScheduledEventVO.setInactiveMinute(SECOND_INACTIVE_MINUTE);
		secondScheduledEventVO.setInactiveSecond(SECOND_INACTIVE_SECOND);
		secondScheduledEventVO.setInactiveCron(SECOND_INACTIVE_CRON);

		//Insert
		ScheduledEventDAO scheduledEventDAO = new ScheduledEventDAO();
		int firstId = scheduledEventDAO.insert(scheduledEventVO);
		int secondId = scheduledEventDAO.insert(secondScheduledEventVO);

		//Select single object
		ScheduledEventVO scheduledEventVoSelect = scheduledEventDAO.getScheduledEvent(firstId);
		assertTrue(scheduledEventVoSelect.getId() == firstId);
		assertTrue(scheduledEventVoSelect.getXid().equals(XID));
		assertTrue(scheduledEventVoSelect.getAlias().equals(ALIAS));
		assertTrue(scheduledEventVoSelect.getAlarmLevel() == ALARM_LEVEL);
		assertTrue(scheduledEventVoSelect.getScheduleType() == SCHEDULE_TYPE);
		assertTrue(scheduledEventVoSelect.isReturnToNormal() == RETURN_TO_NORMAL);
		assertTrue(scheduledEventVoSelect.isDisabled() == DISABLED);
		assertTrue(scheduledEventVoSelect.getActiveYear() == ACTIVE_YEAR);
		assertTrue(scheduledEventVoSelect.getActiveMonth() == ACTIVE_MONTH);
		assertTrue(scheduledEventVoSelect.getActiveDay() == ACTIVE_DAY);
		assertTrue(scheduledEventVoSelect.getActiveHour() == ACTIVE_HOUR);
		assertTrue(scheduledEventVoSelect.getActiveMinute() == ACTIVE_MINUTE);
		assertTrue(scheduledEventVoSelect.getActiveSecond() == ACTIVE_SECOND);
		assertTrue(scheduledEventVoSelect.getActiveCron().equals(ACTIVE_CRON));
		assertTrue(scheduledEventVoSelect.getInactiveYear() == INACTIVE_YEAR);
		assertTrue(scheduledEventVoSelect.getInactiveMonth() == INACTIVE_MONTH);
		assertTrue(scheduledEventVoSelect.getInactiveDay() == INACTIVE_DAY);
		assertTrue(scheduledEventVoSelect.getInactiveHour() == INACTIVE_HOUR);
		assertTrue(scheduledEventVoSelect.getInactiveMinute() == INACTIVE_MINUTE);
		assertTrue(scheduledEventVoSelect.getInactiveSecond() == INACTIVE_SECOND);
		assertTrue(scheduledEventVoSelect.getInactiveCron().equals(INACTIVE_CRON));

		//Select all objects
		List<ScheduledEventVO> scheduledEventVOList = scheduledEventDAO.getScheduledEvents();
		//Check list size
		assertTrue(scheduledEventVOList.size() == LIST_SIZE);
		//Check IDs
		assertTrue(scheduledEventVOList.get(0).getId() == firstId);
		assertTrue(scheduledEventVOList.get(1).getId() == secondId);

		//Update
		ScheduledEventVO scheduledEventVoUpdate = new ScheduledEventVO();
		scheduledEventVoUpdate.setId(firstId);
		scheduledEventVoUpdate.setXid(UPDATE_XID);
		scheduledEventVoUpdate.setAlias(UPDATE_ALIAS);
		scheduledEventVoUpdate.setAlarmLevel(UPDATE_ALARM_LEVEL);
		scheduledEventVoUpdate.setScheduleType(UPDATE_SCHEDULE_TYPE);
		scheduledEventVoUpdate.setReturnToNormal(UPDATE_RETURN_TO_NORMAL);
		scheduledEventVoUpdate.setDisabled(UPDATE_DISABLED);
		scheduledEventVoUpdate.setActiveYear(UPDATE_ACTIVE_YEAR);
		scheduledEventVoUpdate.setActiveMonth(UPDATE_ACTIVE_MONTH);
		scheduledEventVoUpdate.setActiveDay(UPDATE_ACTIVE_DAY);
		scheduledEventVoUpdate.setActiveHour(UPDATE_ACTIVE_HOUR);
		scheduledEventVoUpdate.setActiveMinute(UPDATE_ACTIVE_MINUTE);
		scheduledEventVoUpdate.setActiveSecond(UPDATE_ACTIVE_SECOND);
		scheduledEventVoUpdate.setActiveCron(UPDATE_ACTIVE_CRON);
		scheduledEventVoUpdate.setInactiveYear(UPDATE_INACTIVE_YEAR);
		scheduledEventVoUpdate.setInactiveMonth(UPDATE_INACTIVE_MONTH);
		scheduledEventVoUpdate.setInactiveDay(UPDATE_INACTIVE_DAY);
		scheduledEventVoUpdate.setInactiveHour(UPDATE_INACTIVE_HOUR);
		scheduledEventVoUpdate.setInactiveMinute(UPDATE_INACTIVE_MINUTE);
		scheduledEventVoUpdate.setInactiveSecond(UPDATE_INACTIVE_SECOND);
		scheduledEventVoUpdate.setInactiveCron(UPDATE_INACTIVE_CRON);

		scheduledEventDAO.update(scheduledEventVoUpdate);

		//Delete
		scheduledEventDAO.delete(firstId);
		try {
			scheduledEventDAO.getScheduledEvent(firstId);
		} catch(Exception e) {
			assertTrue(e.getClass().equals(EmptyResultDataAccessException.class));
			assertTrue(e.getMessage().equals("Incorrect result size: expected 1, actual 0"));
		}
	}
}
