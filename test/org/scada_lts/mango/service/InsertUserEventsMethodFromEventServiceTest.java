package org.scada_lts.mango.service;

import com.serotonin.mango.Common;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.UserEventDAO;
import org.scada_lts.mango.adapter.MangoEvent;

import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(value = PowerMockRunner.class)
@PrepareForTest({EventService.class})
public class InsertUserEventsMethodFromEventServiceTest {

    private MangoEvent eventServiceSubject;
    private EventDAO eventDAO;
    private UserEventDAO userEventDAO;

    @Before
    public void beforeTest() throws Exception {
        eventDAO = mock(EventDAO.class);
        userEventDAO = mock(UserEventDAO.class);
        whenNew(EventDAO.class).withNoArguments().thenReturn(eventDAO);
        whenNew(UserEventDAO.class).withNoArguments().thenReturn(userEventDAO);
    }

    @Test
    public void test_insertUserEvents_with_eventId_and_alarmTrue_then_invoke_batchUpdate_with_UserEventDAO() {

        //given:
        eventServiceSubject = new EventService();
        boolean alarm = true;
        int eventId = 123;

        //when:
        eventServiceSubject.insertUserEvents(eventId, Collections.emptyList(), alarm);

        //then:
        verify(userEventDAO, times(1)).batchUpdate(eq(eventId),
                eq(Collections.emptyList()), eq(alarm));
    }

    @Test
    public void test_insertUserEvents_with_newEventId_and_alarmTrue_then_not_invoke_batchUpdate_with_UserEventDAO() {

        //given:
        eventServiceSubject = new EventService();
        boolean alarm = true;
        int newEventId = Common.NEW_ID;

        //when:
        eventServiceSubject.insertUserEvents(newEventId, Collections.emptyList(), alarm);

        //then:
        verify(userEventDAO, times(0)).batchUpdate(eq(newEventId),
                eq(Collections.emptyList()), eq(alarm));
    }

    @Test
    public void test_insertUserEvents_with_eventId_and_alarmFalse_then_invoke_batchUpdate_with_UserEventDAO() {

        //given:
        eventServiceSubject = new EventService();
        boolean alarm = false;
        int eventId = 123;

        //when:
        eventServiceSubject.insertUserEvents(eventId, Collections.emptyList(), alarm);

        //then:
        verify(userEventDAO, times(1)).batchUpdate(eq(eventId),
                eq(Collections.emptyList()), eq(alarm));
    }

    @Test
    public void test_insertUserEvents_with_newEventId_and_alarmFalse_then_not_invoke_batchUpdate_with_UserEventDAO() {
        //given:
        eventServiceSubject = new EventService();
        boolean alarm = false;
        int newEventId = Common.NEW_ID;

        //when:
        eventServiceSubject.insertUserEvents(newEventId, Collections.emptyList(), alarm);
        //then:
        verify(userEventDAO, times(0)).batchUpdate(eq(newEventId),
                eq(Collections.emptyList()), eq(alarm));
    }
}
