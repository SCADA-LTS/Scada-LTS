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
import org.scada_lts.dao.event.UserEventDAO;
import org.scada_lts.dao.model.event.UserEvent;
import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.mango.service.EventService;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

/**
 * Event DAO base on before version EventDao 
 *
 * @author Grzesiek Bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class EventServiceTest extends TestDAO {
	
	private MangoEvent eventService;
	private static final int ADMIN_USER_ID=1;
	
	
	private static final String XID = "dp xid";
	private static final int DATA_SOURCE_ID = 1;
	private static final String DATA_SOURCE_NAME = "1name";
	private static final String DATA_SOURCE_XID = "1DsXid";
	private static final int DATA_SOURCE_TYPE_ID = 1;
	
	private static final String ALIAS = "ped alias";
	private static final int DETECTOR_TYPE = 2;
	private static final int ALARM_LEVEL = 3;
	private static final double STATE_LIMIT = 1.5;
	private static final int DURATION = 5;
	private static final int DURATION_TYPE = 1;
	private static final boolean BINARY_STATE = true;
	private static final int MULTISTATE_STATE = 3;
	private static final int CHANGE_COUNT = 2;
	private static final String ALPHANUMERIC_STATE = "ped alphanumericState";
	private static final double WEIGHT = 2.7;
	
	
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
	
	//Test also getHighestUnsilencedAlarmLevel
	@Test
	public void insertUserEvents() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		int alarmLevel = 3;
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null,null);
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
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null,null);
		
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
		
		EventInstance e1 = new EventInstance(type, activeTS, true, alarmLevel, null,null);
		eventService.saveEvent(e1);
		List<EventInstance> lstAckEventsNext = eventService.getActiveEvents();
		boolean checkLstAckEventsNext = lstAckEventsNext.size()==1;
		
		assertEquals(true,checkLstAckEventsNext);

	}
	
	@Test
	public void getEventsForDataPoint() {
		
		//
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (xid, name, dataSourceType, data) values ('x1', 'dataName', 1, 0);");

		DataPointVO dataPoint = new DataPointVO(LoggingTypes.ON_CHANGE);
		dataPoint.setXid(XID);
		dataPoint.setDataSourceId(DATA_SOURCE_ID);
		dataPoint.setDataSourceName(DATA_SOURCE_NAME);
		dataPoint.setDataSourceXid(DATA_SOURCE_XID);
		dataPoint.setDataSourceTypeId(DATA_SOURCE_TYPE_ID);
		
		DataPointDAO dataPointDAO = new DataPointDAO();
		int id = dataPointDAO.insert(dataPoint);
		dataPoint.setId(id);
		
		PointEventDetectorVO pointEventDetector = new PointEventDetectorVO();
		pointEventDetector.setXid(XID);
		pointEventDetector.setAlias(ALIAS);
		pointEventDetector.njbSetDataPoint(dataPoint);
		pointEventDetector.setDetectorType(DETECTOR_TYPE);
		pointEventDetector.setAlarmLevel(ALARM_LEVEL);
		pointEventDetector.setLimit(STATE_LIMIT);
		pointEventDetector.setDuration(DURATION);
		pointEventDetector.setDurationType(DURATION_TYPE);
		pointEventDetector.setBinaryState(BINARY_STATE);
		pointEventDetector.setMultistateState(MULTISTATE_STATE);
		pointEventDetector.setChangeCount(CHANGE_COUNT);
		pointEventDetector.setAlphanumericState(ALPHANUMERIC_STATE);
		pointEventDetector.setWeight(WEIGHT);
		
		PointEventDetectorDAO pointEventDetectorDAO = new PointEventDetectorDAO();
		int idPointEventDetector = pointEventDetectorDAO.insert(dataPoint.getId(), pointEventDetector);
		
		EventType type = new DataPointEventType(id,idPointEventDetector);
		long activeTS = 0;
		boolean applicable = false;
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e);
		UserEvent userEvent = new UserEvent();
		
		userEvent.setEventId(1);
		userEvent.setSilenced(false);
		userEvent.setUserId(ADMIN_USER_ID);
		
		UserEventDAO userEventDAO = new UserEventDAO();
		userEventDAO.create(userEvent);
		List<EventInstance> eventsForDataPoint = eventService.getEventsForDataPoint(id, ADMIN_USER_ID);
		boolean checkEventsForDataPoint = eventsForDataPoint.size() == 1;
		
		assertEquals(true, checkEventsForDataPoint);
		
	}
	
	/*@Test //TODO rewrite Common.timer.execute(new UserPendingEventRetriever(userId));
	public void getPendingEventsForDataPoint() {
		
		//
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (xid, name, dataSourceType, data) values ('x1', 'dataName', 1, 0);");

		DataPointVO dataPoint = new DataPointVO();
		dataPoint.setXid(XID);
		dataPoint.setDataSourceId(DATA_SOURCE_ID);
		dataPoint.setDataSourceName(DATA_SOURCE_NAME);
		dataPoint.setDataSourceXid(DATA_SOURCE_XID);
		dataPoint.setDataSourceTypeId(DATA_SOURCE_TYPE_ID);
		
		DataPointDAO dataPointDAO = new DataPointDAO();
		int id = dataPointDAO.insert(dataPoint);
		dataPoint.setId(id);
		
		PointEventDetectorVO pointEventDetector = new PointEventDetectorVO();
		pointEventDetector.setXid(XID);
		pointEventDetector.setAlias(ALIAS);
		pointEventDetector.njbSetDataPoint(dataPoint);
		pointEventDetector.setDetectorType(DETECTOR_TYPE);
		pointEventDetector.setAlarmLevel(ALARM_LEVEL);
		pointEventDetector.setLimit(STATE_LIMIT);
		pointEventDetector.setDuration(DURATION);
		pointEventDetector.setDurationType(DURATION_TYPE);
		pointEventDetector.setBinaryState(BINARY_STATE);
		pointEventDetector.setMultistateState(MULTISTATE_STATE);
		pointEventDetector.setChangeCount(CHANGE_COUNT);
		pointEventDetector.setAlphanumericState(ALPHANUMERIC_STATE);
		pointEventDetector.setWeight(WEIGHT);
		
		PointEventDetectorDAO pointEventDetectorDAO = new PointEventDetectorDAO();
		int idPointEventDetector = pointEventDetectorDAO.insert(pointEventDetector);
		
		EventType type = new DataPointEventType(id,idPointEventDetector);
		long activeTS = 0;
		boolean applicable = false;
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		
		eventService.saveEvent(e);
		
		UserEvent userEvent = new UserEvent();
		
		userEvent.setEventId(1);
		userEvent.setSilenced(false);
		userEvent.setUserId(ADMIN_USER_ID);
		
		UserEventDAO userEventDAO = new UserEventDAO();
		userEventDAO.create(userEvent);
		List<EventInstance> eventsPendingForDataPoint = eventService.getPendingEventsForDataPoint(id, ADMIN_USER_ID);
		boolean checkEventsForPendingDataPoint = eventsPendingForDataPoint.size() == 1;
		
		assertEquals(true, checkEventsForPendingDataPoint);
		
	}*/
	
	@Test
	public void insertEventComment(){
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		int alarmLevel = 3;
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		
		eventService.saveEvent(e);
		
		UserComment comment = new UserComment();
		comment.setComment("test");
		//TODO change UserComment to must set comment and user
		comment.setUserId(ADMIN_USER_ID);
		comment.setTs(System.currentTimeMillis());
		comment.setUsername("admin");
		
		eventService.insertEventComment(e.getId(), comment);
		List<EventInstance> events = eventService.getActiveEvents();
		boolean checkComment = events.get(0).getEventComments().size()==1;
		
		assertEquals(true, checkComment);
		
	};
	
	@Test
	public void purgeEventsBefore() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		int alarmLevel = 3;
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e);
		
		EventInstance e1 = new EventInstance(type, activeTS+1,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e1);
		
		EventInstance e2 = new EventInstance(type, activeTS+2,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e2);
		
		eventService.purgeEventsBefore(activeTS+1);
		
		boolean checkCountEvent = eventService.getEventCount()==2;
		
		assertEquals(true, checkCountEvent);
	}
	
	@Test
	public void searchOldId() {
		//TODO for every type EventType
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		//TODO for every type AlarmLevel
		//AlarmLevel.CRITICAL 
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e);
		
		UserEvent userEvent = new UserEvent();
		userEvent.setEventId(1);
		userEvent.setSilenced(false);
		userEvent.setUserId(ADMIN_USER_ID);
		
		UserEventDAO userEventDAO = new UserEventDAO();
		userEventDAO.create(userEvent);
		
		List<EventInstance> events = eventService.searchOld(e.getId(),EventType.EventSources.DATA_SOURCE,"*",alarmLevel,null,0,ADMIN_USER_ID,null);
		boolean checkEvents = events.size()==1;
		
		assertEquals(true, checkEvents);
		
	}
	
	@Test
	public void search() {
		
		// Only one use for getAcknowledgedEvents
				
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		//AlarmLevel.CRITICAL 
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e);
		
		UserEvent userEvent = new UserEvent();
		userEvent.setEventId(1);
		userEvent.setSilenced(false);
		userEvent.setUserId(ADMIN_USER_ID);
		
		UserEventDAO userEventDAO = new UserEventDAO();
		userEventDAO.create(userEvent);

		List<EventInstance> events = eventService.search(0, -1, null, -1, null, 1, null, 0, 5000, null);
		
		boolean checkEvents = events.size()==1;
		
		assertEquals(true, checkEvents);
		
	}
	
 
	@Test
	public void searchRowCountStartRow() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		//AlarmLevel.CRITICAL 
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e);
		
		UserEvent userEvent = new UserEvent();
		userEvent.setEventId(1);
		userEvent.setSilenced(false);
		userEvent.setUserId(ADMIN_USER_ID);
		
		UserEventDAO userEventDAO = new UserEventDAO();
		userEventDAO.create(userEvent);

		eventService.search(0, -1, null, -1, null, 1, null, 0, 5000, null);
		
		boolean checkSearchRowCount = eventService.getSearchRowCount()==1;
		boolean checkStartRow = eventService.getStartRow()==-1;
		
		assertEquals(true, checkSearchRowCount);
		
		assertEquals(true, checkStartRow);
		
	}
	
	//TODO extrack EventHandlerDaoTest
	@Test
	public void testEventHandler() {
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		//AlarmLevel.CRITICAL 
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e);
		
		EventHandlerVO handler = new EventHandlerVO();
		handler.setXid("xid_test");
		handler.setAlias("alias_test");
		
		EventHandlerVO newHandler = eventService.saveEventHandler(type, handler);
		
		EventHandlerVO handlerBaseOnId = eventService.getEventHandler(newHandler.getId());
		
		assertEquals(true, handlerBaseOnId != null);
		
		EventHandlerVO handlerBaseOnXID = eventService.getEventHandler("xid_test");
		
		assertEquals(true, handlerBaseOnXID != null);
		
		List<EventHandlerVO> countEventHandlerBaseOnType = eventService.getEventHandlers(type);
		
		assertEquals(true, countEventHandlerBaseOnType.size()==1);
		
		EventType eventTypeBaseOnHandler = eventService.getEventHandlerType(newHandler.getId());
		
		assertEquals(true, eventTypeBaseOnHandler.equals(type));
		
		List<EventHandlerVO> lstEventTypeBaseOnHandler = eventService.getEventHandlers(type);
		
		assertEquals(true, lstEventTypeBaseOnHandler.size()==1);
		
		EventHandlerVO handlerVO = new EventHandlerVO();
		handlerVO.setXid("xid_test_vo");
		handlerVO.setAlias("alias_test_vo");
		
		EventTypeVO typeVO = new EventTypeVO(1, 1, 1);
		
		EventHandlerVO eventHandlerVO = eventService.saveEventHandler(typeVO,	handlerVO);
		
		assertEquals(true, eventHandlerVO != null);
		
		List<EventHandlerVO> lstEventTypeVOBaseOnHandler = eventService.getEventHandlers(typeVO);
		
		assertEquals(true, lstEventTypeVOBaseOnHandler.size()==1);
		
		List<EventHandlerVO> lstEventHandlers = eventService.getEventHandlers();
		
		// xid_test, xid_test_vo
		assertEquals(true, lstEventHandlers.size()==2);
		
		//TODO not run because can not call Context....
		/*eventService.deleteEventHandler(newHandler.getId());	
		List<EventHandlerVO> lstEventHandlers1 = eventService.getEventHandlers();
		assertEquals(true, lstEventHandlers1.size()==1);*/
		
	}
	
	
	@Test
	public void toggleSilence() {
		
		EventType type = new DataSourceEventType(1,1);
		long activeTS = 0;
		boolean applicable = true;
		//AlarmLevel.CRITICAL 
		int alarmLevel = 3;
		
		EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
		eventService.saveEvent(e);
		
		UserEvent userEvent = new UserEvent();
		userEvent.setEventId(1);
		userEvent.setSilenced(false);
		userEvent.setUserId(ADMIN_USER_ID);
		
		UserEventDAO userEventDAO = new UserEventDAO();
		userEventDAO.create(userEvent);
		
		eventService.toggleSilence(e.getId(), ADMIN_USER_ID);
		
		List<EventInstance> events = eventService.searchOld(e.getId(),EventType.EventSources.DATA_SOURCE,"*",alarmLevel,null,0,ADMIN_USER_ID,null);		
		
		assertEquals(true, events.get(0).isSilenced());
	}

	
}
