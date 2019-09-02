package org.scada_lts.mango.service;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scada_lts.dao.event.IEventDAO;
import org.scada_lts.dao.event.IUserEventDAO;
import org.scada_lts.mango.adapter.MangoEvent;

import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * @author kamil.jarmusik@gmail.com
 */

public class EventServiceUnitMockitoTest {

    private static MangoEvent eventService;
    private static EventType eventType;
    private static IEventDAO eventDaoMock;
    private static IUserEventDAO userEventDAOMock;

    @BeforeClass
    public static void beforeAllTests() throws Exception {
        eventType = new DataPointEventType();
        eventDaoMock = mock(IEventDAO.class);
        userEventDAOMock = mock(IUserEventDAO.class);
        eventService = new EventService(eventDaoMock, userEventDAOMock);
    }

    @Before
    public void beforeOneTest() throws Exception {
        reset(eventDaoMock);
        reset(userEventDAOMock);
    }

    @After
    public void afterOneTest() throws Exception {
        reset(eventDaoMock);
        reset(userEventDAOMock);
    }

    @Test
    public void invoke_saveEvent_with_event_dataPointType_and_alarmLevelNone_then_not_invoke_create_and_updateEvent_with_eventDAO() {
        //given:
        EventInstance eventNone = new EventInstance(eventType,
                0l, false, AlarmLevels.NONE,
                null, null);
        eventNone.setId(123);
        //when:
        eventService.saveEvent(eventNone);
         //then:
        verify(eventDaoMock, times(0)).create(eventNone);
        verify(eventDaoMock, times(0)).updateEvent(eventNone);
    }

    @Test
    public void invoke_saveEvent_with_newEvent_dataPointType_and_alarmLevelNone_then_not_invoke_create_and_updateEvent_with_eventDAO() {
        //given:
        EventInstance eventNone = new EventInstance(eventType,
                0l, false, AlarmLevels.NONE,
                null, null);
        eventNone.setId(Common.NEW_ID);
        //when:
        eventService.saveEvent(eventNone);
        //then:
        verify(eventDaoMock, times(0)).create(eventNone);
        verify(eventDaoMock, times(0)).updateEvent(eventNone);
    }

    @Test
    public void invoke_saveEvent_with_newEvent_dataPointType_and_alarmLevelCritical_then_once_invoke_create_with_eventDAO() {
        //given:

        EventInstance eventCritical = new EventInstance(eventType,
                0l, false, AlarmLevels.CRITICAL,
                null, null);
        eventCritical.setId(Common.NEW_ID);
        //when:
        eventService.saveEvent(eventCritical);
        //then:
        verify(eventDaoMock, times(1)).create(eventCritical);
        verify(eventDaoMock, times(0)).updateEvent(eventCritical);
    }

    @Test
    public void invoke_saveEvent_with_event_dataPointType_and_alarmLevelCritical_then_once_invoke_updateEvent_with_eventDAO() {
        //given:
        EventInstance eventCritical = new EventInstance(eventType,
                0l, false, AlarmLevels.CRITICAL,
                null, null);
        eventCritical.setId(123);
        //when:
        eventService.saveEvent(eventCritical);
        //then:
        verify(eventDaoMock, times(0)).create(eventCritical);
        verify(eventDaoMock, times(1)).updateEvent(eventCritical);
    }

    @Test
    public void twice_invoke_saveEvent_with_event_dataPointType_and_alarmLevelCritical_then_twice_invoke_create_with_eventDAO() {
        //given:
        EventInstance eventCritical = new EventInstance(eventType,
                0l, false, AlarmLevels.CRITICAL,
                null, null);
        eventCritical.setId(Common.NEW_ID);
        //when:
        eventService.saveEvent(eventCritical);
        eventService.saveEvent(eventCritical);
        //then:
        verify(eventDaoMock, times(2)).create(eventCritical);
        verify(eventDaoMock, times(0)).updateEvent(eventCritical);
    }

    @Test
    public void twice_invoke_saveEvent_with_event_dataPointType_and_alarmLevelCritical_then_twice_invoke_updateEvent_with_eventDAO() {
        //given:
        EventInstance eventCritical = new EventInstance(eventType,
                0l, false, AlarmLevels.CRITICAL,
                null, null);
        eventCritical.setId(123);
        //when:
        eventService.saveEvent(eventCritical);
        eventService.saveEvent(eventCritical);
        //then:
        verify(eventDaoMock, times(0)).create(eventCritical);
        verify(eventDaoMock, times(2)).updateEvent(eventCritical);
    }

    @Test
    public void invoke_insertUserEvents_with_eventId_and_alarmTrue_then_invoke_batchUpdate_with_UserEventDAO() {
        //when:
        eventService.insertUserEvents(123,
                Collections.emptyList(), true);
        //then:
        verify(userEventDAOMock, times(1)).batchUpdate(123,
                Collections.emptyList(), true);
    }

    @Test
    public void invoke_insertUserEvents_with_newEventId_and_alarmTrue_then_not_invoke_batchUpdate_with_UserEventDAO() {
        //when:
        eventService.insertUserEvents(Common.NEW_ID,
                Collections.emptyList(), true);
        //then:
        verify(userEventDAOMock, times(0)).batchUpdate(Common.NEW_ID,
                Collections.emptyList(), true);
    }

    @Test
    public void invoke_insertUserEvents_with_eventId_and_alarmFalse_then_invoke_batchUpdate_with_UserEventDAO() {
        //when:
        eventService.insertUserEvents(123,
                Collections.emptyList(), false);
        //then:
        verify(userEventDAOMock, times(1)).batchUpdate(123,
                Collections.emptyList(), false);
    }

    @Test
    public void invoke_insertUserEvents_with_newEventId_and_alarmFalse_then_not_invoke_batchUpdate_with_UserEventDAO() {
        //when:
        eventService.insertUserEvents(Common.NEW_ID,
                Collections.emptyList(), false);
        //then:
        verify(userEventDAOMock, times(0)).batchUpdate(Common.NEW_ID,
                Collections.emptyList(), false);
    }
}
