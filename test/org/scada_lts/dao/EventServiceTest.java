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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.mango.service.EventService;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;

/**
 * Event DAO base on before version EventDao 
 *
 * @author Grzesiek Bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class EventServiceTest extends TestDAO {
	
	private MangoEvent eventService;
	private static final int ADMIN_USER_ID=1;
	
	
	@Before
	public void init() {
		eventService = new EventService();
		Properties confTest = new Properties();
		confTest.setProperty(ScadaConfig.ENABLE_CACHE, "false");
		ScadaConfig.getInstanceTest(confTest);
	}
	
	@Test
	public void saveEvent() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 10;
		boolean applicable = true;
		int alarmLevel = 3;
					
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		
		eventService.saveEvent(e);
	
		int checkCountSaveEvent = eventService.getEventCount();
		
		boolean resSaveEvent = checkCountSaveEvent == 1;
		
		assertEquals(true,resSaveEvent);
		
		//TODO save update event
		
	}
	
	@Test
	public void ackEvent() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		int alarmLevel = 3;
					
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		
		eventService.saveEvent(e);
		
		long currentTime = System.currentTimeMillis();
		
		eventService.ackEvent(e.getId(), currentTime, ADMIN_USER_ID, -1, false);
		
		List<EventInstance> lstAckEvents = eventService.getActiveEvents();
		
		boolean checkAckEvent = lstAckEvents.get(0).getAcknowledgedTimestamp() == currentTime;
		
		assertEquals(true, checkAckEvent);
		
	}
	
	@Test
	public void ackEventSingleAlarmLevelChange() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		int alarmLevel = 3;
					
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		
		eventService.saveEvent(e);
		
		long currentTime = System.currentTimeMillis();
		
		eventService.ackEvent(e.getId(), currentTime, ADMIN_USER_ID, -1);
		
		List<EventInstance> lstAckEvents = eventService.getActiveEvents();
		
		boolean checkAckEventSingleAlarmLevelChange = lstAckEvents.size() == 1;
		
		assertEquals(true, checkAckEventSingleAlarmLevelChange);
		
	}
	
	@Test
	public void insertUserEvents() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		int alarmLevel = 3;
					
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		
		eventService.saveEvent(e);
		
		List<Integer> userIds = new ArrayList<Integer>();
		
		userIds.add(Integer.valueOf(ADMIN_USER_ID));
		eventService.insertUserEvents(e.getId(), userIds, true);

		boolean checkInsertUserEvents = eventService.getHighestUnsilencedAlarmLevel(ADMIN_USER_ID)==3;
		
		assertEquals(true, checkInsertUserEvents);
		
	}
	
	
	@Test
	public void attachRelationInfo() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		
		// Add User comments
		DAO.getInstance().getJdbcTemp().update("INSERT userComments (`userId`,`commentType`,`typeKey`,`ts`,`commentText`) VALUES (1,1,1,1,'test')");
		
		eventService.saveEvent(e);
		
		eventService.attachRelationalInfo(e);
		
		boolean check = e.getEventComments().size()==1;
		
		assertEquals(true, check);
		
	}
	
	@Test
	public void getActiveEvents() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = false;
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		
		eventService.saveEvent(e);
		
		List<EventInstance> lstAckEvents = eventService.getActiveEvents();
		
		boolean checkLstAckEvents = lstAckEvents.size()==0;
		
		assertEquals(true, checkLstAckEvents);
		
		EventInstance e1 = new EventInstance(type, activeTS, true, alarmLevel, null, null);
		
		eventService.saveEvent(e1);
		
		List<EventInstance> lstAckEventsNext = eventService.getActiveEvents();
		
		boolean checkLstAckEventsNext = lstAckEventsNext.size()==1;
		
		assertEquals(true,checkLstAckEventsNext);

	}
	
	

}
