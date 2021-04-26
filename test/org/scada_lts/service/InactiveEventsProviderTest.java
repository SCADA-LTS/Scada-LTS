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
import utils.ScheduledExecuteInactiveEventDAOMemory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class InactiveEventsProviderTest {

    private ScheduledExecuteInactiveEventService scheduledInactiveEventService;
    private InactiveEventsProvider emailSubject;
    private InactiveEventsProvider smsSubject;
    private ScheduledExecuteInactiveEventDAO scheduledInactiveEventDAOMemory;
    private DateTime inactiveIntervalTime;
    private EventHandlerVO emailEventHandler;
    private EventHandlerVO smsEventHandler;
    private CommunicationChannel smsChannel;
    private CommunicationChannel emailChannel;
    private int limit = 4;
    private EventDAO eventDAOMock;

    private SystemSettingsService systemSettingsServiceMock;

    @Before
    public void config() {

        emailEventHandler = EventTestUtils.createEventHandler(1, EventHandlerVO.TYPE_EMAIL);
        smsEventHandler = EventTestUtils.createEventHandler(2, EventHandlerVO.TYPE_SMS);

        inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");
        MailingList mailingListWithInactiveInterval = MailingListTestUtils
                .createMailingListWithInactiveIntervalAndUser(1, inactiveIntervalTime, true, "Mark");

        systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getSMSDomain()).thenReturn("domain.com");

        MailingListService mailingListService = mock(MailingListService.class);
        when(mailingListService.getMailingLists(any())).thenReturn(Collections.emptyList());
        when(mailingListService.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingListWithInactiveInterval));

        eventDAOMock = mock(EventDAO.class);
        when(eventDAOMock.getAllStatusEvents(any())).thenReturn(Collections.emptyList());

        scheduledInactiveEventDAOMemory = new ScheduledExecuteInactiveEventDAOMemory();
        scheduledInactiveEventService = ScheduledExecuteInactiveEventService.newInstance(scheduledInactiveEventDAOMemory, mailingListService);

        emailChannel = CommunicationChannel.newEmailChannel(mailingListWithInactiveInterval, systemSettingsServiceMock);
        smsChannel = CommunicationChannel.newSmsChannel(mailingListWithInactiveInterval, systemSettingsServiceMock);

        emailSubject = InactiveEventsProvider.newInstance(eventDAOMock, scheduledInactiveEventDAOMemory,
                emailChannel, 4);
        smsSubject = InactiveEventsProvider.newInstance(eventDAOMock, scheduledInactiveEventDAOMemory,
                smsChannel, 4);
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(sameEvent));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(eventHandler1, eventHandler2));

        CommunicationChannel emailChannel = CommunicationChannel.newEmailChannel(sameMailingList, systemSettingsServiceMock);
        InactiveEventsProvider emailProvider = InactiveEventsProvider.newInstance(eventDAOMock,
                scheduledInactiveEventDAOMemory, emailChannel, 2);

        ScheduledEvent scheduledEvent1 = new ScheduledEvent(sameEvent,eventHandler1);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(sameEvent,eventHandler2);

        scheduledInactiveEventService.scheduleEvent(eventHandler1, sameEvent);
        scheduledInactiveEventService.scheduleEvent(eventHandler2, sameEvent);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(sameEvent));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(eventHandler1, eventHandler2));

        CommunicationChannel smsChannel = CommunicationChannel.newSmsChannel(sameMailingList, systemSettingsServiceMock);
        InactiveEventsProvider smsProvider = InactiveEventsProvider.newInstance(eventDAOMock,
                scheduledInactiveEventDAOMemory, smsChannel, 2);
        ScheduledEvent scheduledEvent1 = new ScheduledEvent(sameEvent,eventHandler1);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(sameEvent,eventHandler2);

        scheduledInactiveEventService.scheduleEvent(eventHandler1, sameEvent);
        scheduledInactiveEventService.scheduleEvent(eventHandler2, sameEvent);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(eventAsEmail1, eventAsEmail2, eventAsSms));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        ScheduledEvent scheduledEvent1 = new ScheduledEvent(eventAsEmail1, emailEventHandler);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(eventAsEmail2, emailEventHandler);
        ScheduledEvent scheduledEvent3 = new ScheduledEvent(eventAsSms, emailEventHandler);

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail1);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail2);
        scheduledInactiveEventService.scheduleEvent(smsEventHandler, eventAsSms);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(eventAsEmail1, eventAsEmail2, eventAsSms));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler, smsEventHandler));

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail1);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail2);
        scheduledInactiveEventService.scheduleEvent(smsEventHandler, eventAsSms);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(eventAsEmail1, eventAsEmail2, eventAsSms));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler, smsEventHandler));

        ScheduledEvent scheduledEvent = new ScheduledEvent(eventAsSms, smsEventHandler);

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail1);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail2);
        scheduledInactiveEventService.scheduleEvent(smsEventHandler, eventAsSms);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(eventAsEmail1, eventAsEmail2, eventAsSms));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler, smsEventHandler));

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail1);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, eventAsEmail2);
        scheduledInactiveEventService.scheduleEvent(smsEventHandler, eventAsSms);

        //when:
        List<ScheduledEvent> events = smsSubject.getScheduledEvents(limit);

        //then:
        assertEquals(1, events.size());
    }

    @Test
    public void test_unscheduleEvent_with_communicationChannelType_email_and_invoke_getScheduledEvents_then_size_zero() {

        //given:
        EventInstance event = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, emailEventHandler);

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event);

        //when:
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(limit);

        //then:
        assertEquals(1, events.size());
        assertTrue(events.contains(scheduledEvent));

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Collections.emptyList());
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        //and when:
        emailSubject.getScheduledEvents(limit);
        scheduledInactiveEventService.unscheduleEvent(scheduledEvent, emailChannel);
        events = emailSubject.getScheduledEvents(limit);

        //then:
        assertEquals(0, events.size());
    }

    @Test
    public void test_unscheduleEvent_with_communicationChannelType_sms_and_invoke_getScheduledEvents_then_size_zero() {

        //given:
        EventInstance event = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, smsEventHandler);

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(smsEventHandler));
        scheduledInactiveEventService.scheduleEvent(smsEventHandler, event);

        //when:
        List<ScheduledEvent> events = smsSubject.getScheduledEvents(limit);

        //then:
        assertEquals(1, events.size());
        assertTrue(events.contains(scheduledEvent));

        //and when:
        smsSubject.getScheduledEvents(limit);
        scheduledInactiveEventService.unscheduleEvent(scheduledEvent, smsChannel);
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(smsEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event3, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event4, smsEventHandler));

        scheduledInactiveEventService.scheduleEvent(smsEventHandler, event1);
        scheduledInactiveEventService.scheduleEvent(smsEventHandler, event2);
        scheduledInactiveEventService.scheduleEvent(smsEventHandler, event4);
        scheduledInactiveEventService.scheduleEvent(smsEventHandler, event3);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event3, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event4, emailEventHandler));

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event1);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event2);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event4);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event3);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event1);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event2);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event4);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event3);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, emailEventHandler));

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event1);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event2);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event4);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event3);

        //when:
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

        when(eventDAOMock.getAllStatusEvents(anySet())).thenReturn(Arrays.asList(event2, event1, event4, event3));
        when(eventDAOMock.getEventHandlers(anySet())).thenReturn(Arrays.asList(emailEventHandler));

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event3, emailEventHandler));

        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event1);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event2);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event4);
        scheduledInactiveEventService.scheduleEvent(emailEventHandler, event3);

        //when:
        List<ScheduledEvent> events = emailSubject.getScheduledEvents(3);

        //then:
        assertEquals(eventsExpected, events);
    }
}