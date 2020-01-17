package org.scada_lts.mango.service;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.UserEventDAO;
import org.scada_lts.mango.adapter.MangoEvent;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EventService.class})
public class EventServiceTest {

    private MangoEvent eventServiceSubject;
    private EventType eventType;
    private EventDAO eventDAO;
    private UserEventDAO userEventDAO;

    @Before
    public void beforeTest() throws Exception {
        eventType = new DataPointEventType();
        eventDAO = mock(EventDAO.class);
        userEventDAO = mock(UserEventDAO.class);
        whenNew(EventDAO.class).withNoArguments().thenReturn(eventDAO);
        whenNew(UserEventDAO.class).withNoArguments().thenReturn(userEventDAO);
    }

    @Test
    public void test_saveEvent_with_event_dataPointType_and_alarmLevelNone_then_not_invoke_create_and_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventNone = new EventInstance(eventType,
                0l, false, AlarmLevels.NONE,
                null, null);
        eventNone.setId(123);

        //when:
        eventServiceSubject.saveEvent(eventNone);

        //then:
        verify(eventDAO, times(0)).create(eq(eventNone));
        verify(eventDAO, times(0)).updateEvent(eq(eventNone));
    }

    @Test
    public void test_saveEvent_with_newEvent_dataPointType_and_alarmLevelNone_then_not_invoke_create_and_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventNone = new EventInstance(eventType,
                0l, false, AlarmLevels.NONE,
                null, null);
        eventNone.setId(Common.NEW_ID);

        //when:
        eventServiceSubject.saveEvent(eventNone);

        //then:
        verify(eventDAO, times(0)).create(eq(eventNone));
        verify(eventDAO, times(0)).updateEvent(eq(eventNone));
    }

    @Test
    public void test_saveEvent_with_newEvent_dataPointType_and_alarmLevelCritical_then_once_invoke_create_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,
                0l, false, AlarmLevels.CRITICAL,
                null, null);
        eventCritical.setId(Common.NEW_ID);

        //when:
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(1)).create(eventCritical);
        verify(eventDAO, times(0)).updateEvent(eventCritical);
    }

    @Test
    public void test_saveEvent_with_event_dataPointType_and_alarmLevelCritical_then_once_invoke_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,
                0l, false, AlarmLevels.CRITICAL,
                null, null);
        eventCritical.setId(123);

        //when:
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(0)).create(eventCritical);
        verify(eventDAO, times(1)).updateEvent(eventCritical);
    }

    @Test
    public void test_saveEvent_twice_invoke_with_event_dataPointType_and_alarmLevelCritical_then_twice_invoke_create_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,
                0l, false, AlarmLevels.CRITICAL,
                null, null);
        eventCritical.setId(Common.NEW_ID);

        //when:
        eventServiceSubject.saveEvent(eventCritical);
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(2)).create(eventCritical);
        verify(eventDAO, times(0)).updateEvent(eventCritical);
    }

    @Test
    public void test_saveEvent_twice_invoke_with_event_dataPointType_and_alarmLevelCritical_then_twice_invoke_updateEvent_with_eventDAO() {

        //given:
        eventServiceSubject = new EventService();
        EventInstance eventCritical = new EventInstance(eventType,
                0l, false, AlarmLevels.CRITICAL,
                null, null);
        eventCritical.setId(123);

        //when:
        eventServiceSubject.saveEvent(eventCritical);
        eventServiceSubject.saveEvent(eventCritical);

        //then:
        verify(eventDAO, times(0)).create(eventCritical);
        verify(eventDAO, times(2)).updateEvent(eventCritical);
    }

    @Test
    public void test_insertUserEvents_with_eventId_and_alarmTrue_then_invoke_batchUpdate_with_UserEventDAO() {

        //given:
        eventServiceSubject = new EventService();

        //when:
        eventServiceSubject.insertUserEvents(123,
                Collections.emptyList(), true);
        //then:
        verify(userEventDAO, times(1)).batchUpdate(123,
                Collections.emptyList(), true);
    }

    @Test
    public void test_insertUserEvents_with_newEventId_and_alarmTrue_then_not_invoke_batchUpdate_with_UserEventDAO() {

        //given:
        eventServiceSubject = new EventService();

        //when:
        eventServiceSubject.insertUserEvents(Common.NEW_ID,
                Collections.emptyList(), true);
        //then:
        verify(userEventDAO, times(0)).batchUpdate(Common.NEW_ID,
                Collections.emptyList(), true);
    }

    @Test
    public void test_insertUserEvents_with_eventId_and_alarmFalse_then_invoke_batchUpdate_with_UserEventDAO() {

        //given:
        eventServiceSubject = new EventService();

        //when:
        eventServiceSubject.insertUserEvents(123,
                Collections.emptyList(), false);
        //then:
        verify(userEventDAO, times(1)).batchUpdate(123,
                Collections.emptyList(), false);
    }

    @Test
    public void test_insertUserEvents_with_newEventId_and_alarmFalse_then_not_invoke_batchUpdate_with_UserEventDAO() {
        //given:
        eventServiceSubject = new EventService();

        //when:
        eventServiceSubject.insertUserEvents(Common.NEW_ID,
                Collections.emptyList(), false);
        //then:
        verify(userEventDAO, times(0)).batchUpdate(Common.NEW_ID,
                Collections.emptyList(), false);
    }
}