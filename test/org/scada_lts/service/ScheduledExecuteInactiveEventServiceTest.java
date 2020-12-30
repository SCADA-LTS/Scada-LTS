package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.ScheduledInactiveEventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.web.i18n.LocalizableMessage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEvent;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;
import utils.EventTestUtils;
import utils.MailingListTestUtils;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class ScheduledExecuteInactiveEventServiceTest {

    private ScheduledExecuteInactiveEventService serviceSubject;
    private MailingList mailingListWithInactiveInterval;
    private ScheduledExecuteInactiveEventDAO dao;
    private DateTime inactiveIntervalTime;
    private EventHandlerVO emailEventHandler;
    private EventHandlerVO smsEventHandler;
    private LocalizableMessage message;
    private CommunicationChannel smsChannel;
    private CommunicationChannel emailChannel;

    @Before
    public void config() {

        emailEventHandler = EventTestUtils.createEventHandler(1, EventHandlerVO.TYPE_EMAIL);
        smsEventHandler = EventTestUtils.createEventHandler(2, EventHandlerVO.TYPE_SMS);

        inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");
        mailingListWithInactiveInterval = MailingListTestUtils
                .createMailingListWithInactiveIntervalAndUser(1, inactiveIntervalTime, true, "Mark");

        MailingListService mailingListService = mock(MailingListService.class);
        when(mailingListService.getMailingLists(any())).thenReturn(Collections.emptyList());
        when(mailingListService.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingListWithInactiveInterval));

        EventDAO eventDAO = mock(EventDAO.class);
        when(eventDAO.getAllStatusEvents(any())).thenReturn(Collections.emptyList());

        dao = mock(ScheduledExecuteInactiveEventDAO.class);
        when(dao.select()).thenReturn(Collections.emptyList());

        serviceSubject = ScheduledExecuteInactiveEventService.newInstance(eventDAO, dao, mailingListService);
        message = new LocalizableMessage("com.test");

        emailChannel = CommunicationChannel.newEmailChannel(mailingListWithInactiveInterval);
        smsChannel = CommunicationChannel.newSmsChannel(mailingListWithInactiveInterval);
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

        EventInstance sameEvent = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        MailingList sameMailingList = MailingListTestUtils
                .createMailingListWithInactiveIntervalAndUser(1, inactiveIntervalTime,
                        true, "John");

        CommunicationChannel emailChannel = CommunicationChannel.newEmailChannel(sameMailingList);
        ScheduledEvent scheduledEvent1 = new ScheduledEvent(sameEvent,eventHandler1);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(sameEvent,eventHandler2);

        //when:
        serviceSubject.scheduleEvent(eventHandler1, sameEvent);
        serviceSubject.scheduleEvent(eventHandler2, sameEvent);

        //and:
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(emailChannel);

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

        EventInstance sameEvent = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        MailingList sameMailingList = MailingListTestUtils
                .createMailingListWithInactiveIntervalAndUser(1, inactiveIntervalTime, true, "John");

        CommunicationChannel smsChannel = CommunicationChannel.newSmsChannel(sameMailingList);
        ScheduledEvent scheduledEvent1 = new ScheduledEvent(sameEvent,eventHandler1);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(sameEvent,eventHandler2);

        //when:
        serviceSubject.scheduleEvent(eventHandler1, sameEvent);
        serviceSubject.scheduleEvent(eventHandler2, sameEvent);

        //and:
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(smsChannel);

        //then:
        assertTrue(events.contains(scheduledEvent1));
        assertTrue(events.contains(scheduledEvent2));
    }

    @Test
    public void test_scheduleEvent_with_eventHandlerType_email_and_invoke_getScheduledEvents_then_contains_email() {

        //given:
        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        EventInstance eventAsEmail2 = EventTestUtils.createEventWithActiveTime(2, inactiveIntervalTime, message);
        EventInstance eventAsSms = EventTestUtils.createEventWithActiveTime(3, inactiveIntervalTime, message);

        ScheduledEvent scheduledEvent1 = new ScheduledEvent(eventAsEmail1, emailEventHandler);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(eventAsEmail2, emailEventHandler);
        ScheduledEvent scheduledEvent3 = new ScheduledEvent(eventAsSms, emailEventHandler);

        //when:
        serviceSubject.scheduleEvent(emailEventHandler, eventAsEmail1);
        serviceSubject.scheduleEvent(emailEventHandler, eventAsEmail2);

        //and:
        serviceSubject.scheduleEvent(smsEventHandler, eventAsSms);

        //and:
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(emailChannel);

        //then:
        assertTrue(events.contains(scheduledEvent2));
        assertTrue(events.contains(scheduledEvent1));
        assertFalse(events.contains(scheduledEvent3));
    }

    @Test
    public void test_scheduleEvent_with_eventHandlerType_email_and_invoke_getScheduledEvents_then_size() {

        //given:
        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        EventInstance eventAsEmail2 = EventTestUtils.createEventWithActiveTime(2, inactiveIntervalTime, message);
        EventInstance eventAsSms = EventTestUtils.createEventWithActiveTime(3, inactiveIntervalTime, message);

        //when:
        serviceSubject.scheduleEvent(emailEventHandler, eventAsEmail1);
        serviceSubject.scheduleEvent(emailEventHandler, eventAsEmail2);

        //and:
        serviceSubject.scheduleEvent(smsEventHandler, eventAsSms);

        //and:
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(emailChannel);

        //then:
        assertEquals(2, events.size());
    }

    @Test
    public void test_scheduleEvent_with_eventHandlerType_sms_and_invoke_getScheduledEvents_then_contains_sms() {

        //given:
        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        EventInstance eventAsEmail2 = EventTestUtils.createEventWithActiveTime(2, inactiveIntervalTime, message);
        EventInstance eventAsSms = EventTestUtils.createEventWithActiveTime(3, inactiveIntervalTime, message);

        ScheduledEvent scheduledEvent = new ScheduledEvent(eventAsSms, smsEventHandler);

        //when:
        serviceSubject.scheduleEvent(emailEventHandler, eventAsEmail1);
        serviceSubject.scheduleEvent(emailEventHandler, eventAsEmail2);

        //and:
        serviceSubject.scheduleEvent(smsEventHandler, eventAsSms);

        //and:
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(smsChannel);

        //then:
        assertTrue(events.contains(scheduledEvent));
    }


    @Test
    public void test_scheduleEvent_with_eventHandlerType_sms_and_invoke_getScheduledEvents_then_size_one() {

        //given:
        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        EventInstance eventAsEmail2 = EventTestUtils.createEventWithActiveTime(2, inactiveIntervalTime, message);
        EventInstance eventAsSms = EventTestUtils.createEventWithActiveTime(3, inactiveIntervalTime, message);

        //when:
        serviceSubject.scheduleEvent(emailEventHandler, eventAsEmail1);
        serviceSubject.scheduleEvent(emailEventHandler, eventAsEmail2);

        //and:
        serviceSubject.scheduleEvent(smsEventHandler, eventAsSms);

        //and:
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(smsChannel);

        //then:
        assertEquals(1, events.size());
    }

    @Test
    public void test_unscheduleEvent_with_communicationChannelType_email_and_invoke_getScheduledEvents_then_size_zero() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, emailEventHandler);

        //when:
        serviceSubject.scheduleEvent(emailEventHandler, event);
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(emailChannel);

        //then:
        assertEquals(1, events.size());
        assertTrue(events.contains(scheduledEvent));

        //and when:
        serviceSubject.unscheduleEvent(scheduledEvent, emailChannel);
        events = serviceSubject.getScheduledEvents(emailChannel);

        //then:
        assertEquals(0, events.size());
    }

    @Test
    public void test_unscheduleEvent_with_communicationChannelType_sms_and_invoke_getScheduledEvents_then_size_zero() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, smsEventHandler);

        //when:
        serviceSubject.scheduleEvent(smsEventHandler, event);
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(smsChannel);

        //then:
        assertEquals(1, events.size());
        assertTrue(events.contains(scheduledEvent));

        //and when:
        serviceSubject.unscheduleEvent(scheduledEvent, smsChannel);
        events = serviceSubject.getScheduledEvents(smsChannel);

        //then:
        assertEquals(0, events.size());
    }


    @Test
    public void test_isScheduledInactiveEventType_for_email_then_true() {

        //given:
        EventType eventType = new ScheduledInactiveEventType(new DataPointEventType(), emailChannel);
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,eventType);

        //when:
        boolean result = serviceSubject.isScheduledInactiveEventType(event);

        //then:
        assertTrue(result);
    }

    @Test
    public void test_isScheduledInactiveEventType_for_sms_then_true() {

        //given:
        EventType eventType = new ScheduledInactiveEventType(new DataPointEventType(), smsChannel);
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,eventType);

        //when:
        boolean result = serviceSubject.isScheduledInactiveEventType(event);

        //then:
        assertTrue(result);
    }

    @Test
    public void test_isScheduledInactiveEventType_then_false() {

        //given:
        EventType eventType = new DataPointEventType();
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,eventType);

        //when:
        boolean result = serviceSubject.isScheduledInactiveEventType(event);

        //then:
        assertFalse(result);
    }

    @Test
    public void test_unscheduleEvent_for_sms_then_verify_times_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, smsEventHandler);
        serviceSubject.scheduleEvent(smsEventHandler, event);

        //when:
        serviceSubject.unscheduleEvent(scheduledEvent, smsChannel);

        //then:
        verify(dao, times(1)).delete(any());
    }

    @Test
    public void test_unscheduleEvent_for_email_then_verify_times_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, emailEventHandler);
        serviceSubject.scheduleEvent(emailEventHandler, event);

        ScheduledExecuteInactiveEvent key = new ScheduledExecuteInactiveEvent(emailEventHandler,
                event, mailingListWithInactiveInterval);

        //when:
        serviceSubject.unscheduleEvent(scheduledEvent, emailChannel);

        //then:
        verify(dao, times(1)).delete(eq(key));
    }

    @Test
    public void test_scheduleEvent_for_sms_then_verify_times_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);
        ScheduledExecuteInactiveEvent key = new ScheduledExecuteInactiveEvent(smsEventHandler,
                event, mailingListWithInactiveInterval);

        //when:
        serviceSubject.scheduleEvent(smsEventHandler, event);

        //then:
        verify(dao, times(1)).insert(eq(key));
    }

    @Test
    public void test_scheduleEvent_for_email_then_verify_times_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);
        ScheduledExecuteInactiveEvent key = new ScheduledExecuteInactiveEvent(emailEventHandler,
                event, mailingListWithInactiveInterval);

        //when:
        serviceSubject.scheduleEvent(emailEventHandler, event);

        //then:
        verify(dao, times(1)).insert(eq(key));
    }

    @Test
    public void test_getScheduledEvents_for_sms_order_by_eventId_desc() {

        //given:
        EventInstance event1 = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime, message);
        EventInstance event2 = EventTestUtils.createEventWithActiveTime(2,inactiveIntervalTime, message);
        EventInstance event3 = EventTestUtils.createEventWithActiveTime(3,inactiveIntervalTime, message);
        EventInstance event4 = EventTestUtils.createEventWithActiveTime(4,inactiveIntervalTime, message);

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event3, smsEventHandler));
        eventsExpected.add(new ScheduledEvent(event4, smsEventHandler));

        //when:
        serviceSubject.scheduleEvent(smsEventHandler, event1);
        serviceSubject.scheduleEvent(smsEventHandler, event2);
        serviceSubject.scheduleEvent(smsEventHandler, event4);
        serviceSubject.scheduleEvent(smsEventHandler, event3);

        //and:
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(smsChannel);

        //then:
        assertEquals(eventsExpected, events);
    }


    @Test
    public void test_getScheduledEvents_for_email_order_by_eventId_desc() {

        //given:
        EventInstance event1 = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime, message);
        EventInstance event2 = EventTestUtils.createEventWithActiveTime(2,inactiveIntervalTime, message);
        EventInstance event3 = EventTestUtils.createEventWithActiveTime(3,inactiveIntervalTime, message);
        EventInstance event4 = EventTestUtils.createEventWithActiveTime(4,inactiveIntervalTime, message);

        List<ScheduledEvent> eventsExpected = new ArrayList<>();
        eventsExpected.add(new ScheduledEvent(event1, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event2, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event3, emailEventHandler));
        eventsExpected.add(new ScheduledEvent(event4, emailEventHandler));

        //when:
        serviceSubject.scheduleEvent(emailEventHandler, event1);
        serviceSubject.scheduleEvent(emailEventHandler, event2);
        serviceSubject.scheduleEvent(emailEventHandler, event4);
        serviceSubject.scheduleEvent(emailEventHandler, event3);

        //and:
        List<ScheduledEvent> events = serviceSubject.getScheduledEvents(emailChannel);

        //then:
        assertEquals(eventsExpected, events);
    }

}