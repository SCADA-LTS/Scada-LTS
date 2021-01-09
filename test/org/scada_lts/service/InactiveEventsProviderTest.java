package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import utils.EventTestUtils;
import utils.MailingListTestUtils;
import utils.ScheduledExecuteInactiveEventDAOMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class InactiveEventsProviderTest {

    private ScheduledExecuteInactiveEventService scheduledExecuteInactiveEventService;
    private InactiveEventsProvider emailSubject;
    private InactiveEventsProvider smsSubject;
    private MailingList mailingListWithInactiveInterval;
    private ScheduledExecuteInactiveEventDAO dao;
    private DateTime inactiveIntervalTime;
    private EventHandlerVO emailEventHandler;
    private EventHandlerVO smsEventHandler;
    private CommunicationChannel smsChannel;
    private CommunicationChannel emailChannel;
    private int limit = Integer.MAX_VALUE;
    private EventDAO eventDAO;

    private SystemSettingsService systemSettingsServiceMock;

    @Before
    public void config() {

        emailEventHandler = EventTestUtils.createEventHandler(1, EventHandlerVO.TYPE_EMAIL);
        smsEventHandler = EventTestUtils.createEventHandler(2, EventHandlerVO.TYPE_SMS);

        inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");
        mailingListWithInactiveInterval = MailingListTestUtils
                .createMailingListWithInactiveIntervalAndUser(1, inactiveIntervalTime, true, "Mark");

        systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getSMSDomain()).thenReturn("domain.com");

        MailingListService mailingListService = mock(MailingListService.class);
        when(mailingListService.getMailingLists(any())).thenReturn(Collections.emptyList());
        when(mailingListService.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingListWithInactiveInterval));

        eventDAO = mock(EventDAO.class);
        when(eventDAO.getAllStatusEvents(any())).thenReturn(Collections.emptyList());

        dao = new ScheduledExecuteInactiveEventDAOMock();

        scheduledExecuteInactiveEventService = ScheduledExecuteInactiveEventService.newInstance(dao, mailingListService);


        emailChannel = CommunicationChannel.newEmailChannel(mailingListWithInactiveInterval, systemSettingsServiceMock);
        smsChannel = CommunicationChannel.newSmsChannel(mailingListWithInactiveInterval, systemSettingsServiceMock);

        emailSubject = InactiveEventsProvider.newInstance(eventDAO, dao, emailChannel);
        smsSubject = InactiveEventsProvider.newInstance(eventDAO, dao, smsChannel);
    }

    @Test
    public void test_date_convert() {

        //given:
        DateTime expected = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2020-12-13 21:30:00");

        //when:
        DateTime result = new DateTime(expected.getMillis());

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void test_scheduleEvent_given_same_type_eventHandler_and_event_and_mailingList_for_email() {

        //given:
        int sameEventHandlerType = EventHandlerVO.TYPE_EMAIL;
        EventHandlerVO eventHandler1 = EventTestUtils.createEventHandler(2, sameEventHandlerType);
        EventHandlerVO eventHandler2 = EventTestUtils.createEventHandler(3, sameEventHandlerType);

        EventInstance sameEvent = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1, inactiveIntervalTime);
        MailingList sameMailingList = MailingListTestUtils
                .createMailingListWithInactiveIntervalAndUser(1, inactiveIntervalTime,
                        true, "John");

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(sameEvent));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(eventHandler1, eventHandler2));

        CommunicationChannel emailChannel = CommunicationChannel.newEmailChannel(sameMailingList, systemSettingsServiceMock);
        InactiveEventsProvider emailProvider = InactiveEventsProvider.newInstance(eventDAO,dao,emailChannel);

        ScheduledEvent scheduledEvent1 = new ScheduledEvent(sameEvent,eventHandler1);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(sameEvent,eventHandler2);

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(eventHandler1, sameEvent);
        scheduledExecuteInactiveEventService.scheduleEvent(eventHandler2, sameEvent);

        //and:
        List<ScheduledEvent> events = emailProvider.getScheduledEvents(limit);

        //then:
        assertTrue(events.contains(scheduledEvent1));
        assertTrue(events.contains(scheduledEvent2));
    }

    @Test
    public void test_scheduleEvent_given_same_type_eventHandler_and_event_and_mailingList_for_sms() {

        //given:
        int sameEventHandlerType = EventHandlerVO.TYPE_SMS;
        EventHandlerVO eventHandler1 = EventTestUtils.createEventHandler(2, sameEventHandlerType);
        EventHandlerVO eventHandler2 = EventTestUtils.createEventHandler(3, sameEventHandlerType);

        EventInstance sameEvent = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1, inactiveIntervalTime);
        MailingList sameMailingList = MailingListTestUtils
                .createMailingListWithInactiveIntervalAndUser(1, inactiveIntervalTime, true, "John");

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(sameEvent));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(eventHandler1, eventHandler2));

        CommunicationChannel smsChannel = CommunicationChannel.newSmsChannel(sameMailingList, systemSettingsServiceMock);
        InactiveEventsProvider smsProvider = InactiveEventsProvider.newInstance(eventDAO,dao,smsChannel);
        ScheduledEvent scheduledEvent1 = new ScheduledEvent(sameEvent,eventHandler1);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(sameEvent,eventHandler2);

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(eventHandler1, sameEvent);
        scheduledExecuteInactiveEventService.scheduleEvent(eventHandler2, sameEvent);

        //and:
        List<ScheduledEvent> events = smsProvider.getScheduledEvents(limit);

        //then:
        assertTrue(events.contains(scheduledEvent1));
        assertTrue(events.contains(scheduledEvent2));
    }

    @Test
    public void test_scheduleEvent_with_eventHandlerType_email_and_invoke_getScheduledEvents_then_contains_email() {

        //given:
        EventInstance eventAsEmail1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1, inactiveIntervalTime);
        EventInstance eventAsEmail2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2, inactiveIntervalTime);
        EventInstance eventAsSms = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3, inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(eventAsEmail1, eventAsEmail2, eventAsSms));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        ScheduledEvent scheduledEvent1 = new ScheduledEvent(eventAsEmail1, emailEventHandler);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(eventAsEmail2, emailEventHandler);
        ScheduledEvent scheduledEvent3 = new ScheduledEvent(eventAsSms, emailEventHandler);

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail1);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail2);

        //and:
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, eventAsSms);

        //and:
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(limit);

        //then:
        assertTrue(events.contains(scheduledEvent2));
        assertTrue(events.contains(scheduledEvent1));
        assertFalse(events.contains(scheduledEvent3));
    }

    @Test
    public void test_scheduleEvent_with_eventHandlerType_email_and_invoke_getScheduledEvents_then_size() {

        //given:
        EventInstance eventAsEmail1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1, inactiveIntervalTime);
        EventInstance eventAsEmail2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2, inactiveIntervalTime);
        EventInstance eventAsSms = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3, inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(eventAsEmail1, eventAsEmail2, eventAsSms));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler, smsEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail1);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail2);

        //and:
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, eventAsSms);

        //and:
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(limit);

        //then:
        assertEquals(2, events.size());
    }

    @Test
    public void test_scheduleEvent_with_eventHandlerType_sms_and_invoke_getScheduledEvents_then_contains_sms() {

        //given:
        EventInstance eventAsEmail1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1, inactiveIntervalTime);
        EventInstance eventAsEmail2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2, inactiveIntervalTime);
        EventInstance eventAsSms = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3, inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(eventAsEmail1, eventAsEmail2, eventAsSms));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler, smsEventHandler));

        ScheduledEvent scheduledEvent = new ScheduledEvent(eventAsSms, smsEventHandler);

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail1);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail2);

        //and:
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, eventAsSms);

        //and:
        List<ScheduledEvent> events = smsSubject.getScheduledEvents(limit);

        //then:
        assertTrue(events.contains(scheduledEvent));
    }


    @Test
    public void test_scheduleEvent_with_eventHandlerType_sms_and_invoke_getScheduledEvents_then_size_one() {

        //given:
        EventInstance eventAsEmail1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1, inactiveIntervalTime);
        EventInstance eventAsEmail2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2, inactiveIntervalTime);
        EventInstance eventAsSms = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3, inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(eventAsEmail1, eventAsEmail2, eventAsSms));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler, smsEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail1);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail2);

        //and:
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, eventAsSms);

        //and:
        List<ScheduledEvent> events = smsSubject.getScheduledEvents(limit);

        //then:
        assertEquals(1, events.size());
    }

    @Test
    public void test_unscheduleEvent_with_communicationChannelType_email_and_invoke_getScheduledEvents_then_size_zero() {

        //given:
        EventInstance event = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, emailEventHandler);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event);
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(limit);

        //then:
        assertEquals(1, events.size());
        assertTrue(events.contains(scheduledEvent));

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Collections.emptyList());
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        //and when:
        emailSubject.getScheduledEvents(limit);
        scheduledExecuteInactiveEventService.unscheduleEvent(scheduledEvent, emailChannel);
        events = emailSubject.getScheduledEvents(limit);

        //then:
        assertEquals(0, events.size());
    }

    @Test
    public void test_unscheduleEvent_with_communicationChannelType_sms_and_invoke_getScheduledEvents_then_size_zero() {

        //given:
        EventInstance event = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, smsEventHandler);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(smsEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, event);
        List<ScheduledEvent> events = smsSubject.getScheduledEvents(limit);

        //then:
        assertEquals(1, events.size());
        assertTrue(events.contains(scheduledEvent));

        //and when:
        smsSubject.getScheduledEvents(limit);
        scheduledExecuteInactiveEventService.unscheduleEvent(scheduledEvent, smsChannel);
        events = smsSubject.getScheduledEvents(limit);

        //then:
        assertEquals(0, events.size());
    }

    @Test
    public void test_getScheduledEvents_for_sms_order_by_eventId_desc() {

        //given:
        EventInstance event1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        EventInstance event2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2,inactiveIntervalTime);
        EventInstance event3 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3,inactiveIntervalTime);
        EventInstance event4 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(4,inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(smsEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event3, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event4, smsEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, event1);
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, event2);
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, event4);
        scheduledExecuteInactiveEventService.scheduleEvent(smsEventHandler, event3);

        //and:
        List<ScheduledEvent> events = smsSubject.getScheduledEvents(limit);

        //then:
        assertEquals(eventsExpected, events);
    }


    @Test
    public void test_getScheduledEvents_for_email_order_by_eventId_desc() {

        //given:
        EventInstance event1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        EventInstance event2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2,inactiveIntervalTime);
        EventInstance event3 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3,inactiveIntervalTime);
        EventInstance event4 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(4,inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event3, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event4, emailEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event1);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event2);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event4);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event3);

        //and:
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(limit);

        //then:
        assertEquals(eventsExpected, events);
    }

    @Test
    public void test_getScheduledEvents_for_email_order_by_eventId_desc_with_limit_1() {

        //given:
        EventInstance event1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        EventInstance event2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2,inactiveIntervalTime);
        EventInstance event3 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3,inactiveIntervalTime);
        EventInstance event4 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(4,inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event1);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event2);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event4);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event3);

        //and:
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(1);

        //then:
        assertEquals(eventsExpected, events);
    }

    @Test
    public void test_getScheduledEvents_for_email_order_by_eventId_desc_with_limit_2() {

        //given:
        EventInstance event1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        EventInstance event2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2,inactiveIntervalTime);
        EventInstance event3 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3,inactiveIntervalTime);
        EventInstance event4 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(4,inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, emailEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event1);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event2);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event4);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event3);

        //and:
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(2);

        //then:
        assertEquals(eventsExpected, events);
    }

    @Test
    public void test_getScheduledEvents_for_email_order_by_eventId_desc_with_limit_3() {

        //given:
        EventInstance event1 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        EventInstance event2 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(2,inactiveIntervalTime);
        EventInstance event3 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(3,inactiveIntervalTime);
        EventInstance event4 = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(4,inactiveIntervalTime);

        when(eventDAO.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAO.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event3, emailEventHandler));

        //when:
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event1);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event2);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event4);
        scheduledExecuteInactiveEventService.scheduleEvent(emailEventHandler, event3);

        //and:
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(3);

        //then:
        assertEquals(eventsExpected, events);
    }
}