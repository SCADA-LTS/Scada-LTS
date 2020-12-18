package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.ScheduledInactiveEventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import com.serotonin.web.i18n.LocalizableMessage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;
import utils.EventTestUtils;
import utils.MailingListTestUtils;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static utils.MailingListTestUtils.createUserEntry;

public class ScheduledExecuteInactiveEventServiceTest {

    private ScheduledExecuteInactiveEventService testSubject;
    private MailingList mailingListWithInactiveInterval;
    private ScheduledExecuteInactiveEventDAO dao;
    private DateTime inactiveIntervalTime;
    private EventHandlerVO eventHandler;
    private LocalizableMessage message;


    @Before
    public void config() {

        eventHandler = new EventHandlerVO();
        eventHandler.setId(123);

        String tel = "111111111";
        String email = "test1@test.com";
        UserEntry user = createUserEntry("Ada", tel, email);

        inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");
        mailingListWithInactiveInterval = MailingListTestUtils
                .createMailingListWithInactiveInterval(inactiveIntervalTime, true, user);

        MailingListService mailingListService = mock(MailingListService.class);
        when(mailingListService.getMailingLists(any())).thenReturn(Collections.emptyList());
        when(mailingListService.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingListWithInactiveInterval));

        EventDAO eventDAO = mock(EventDAO.class);
        when(eventDAO.getAllStatusEvents(any())).thenReturn(Collections.emptyList());

        dao = mock(ScheduledExecuteInactiveEventDAO.class);
        when(dao.select()).thenReturn(Collections.emptyList());

        testSubject = ScheduledExecuteInactiveEventService.newInstance(eventDAO, dao, mailingListService);
        message = new LocalizableMessage("com.test");

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
    public void test_scheduleEvent_with_eventHandlerType_email_and_invoke_getScheduledEvents_then_contains_email() {

        //given:
        CommunicationChannel communicationChannelEmail = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.EMAIL);

        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        EventInstance eventAsEmail2 = EventTestUtils.createEventWithActiveTime(2, inactiveIntervalTime, message);
        EventInstance eventAsSms = EventTestUtils.createEventWithActiveTime(3, inactiveIntervalTime, message);

        //when:
        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);
        testSubject.scheduleEvent(eventHandler, eventAsEmail1);
        testSubject.scheduleEvent(eventHandler, eventAsEmail2);

        //and:
        eventHandler.setHandlerType(EventHandlerVO.TYPE_SMS);
        testSubject.scheduleEvent(eventHandler, eventAsSms);

        //and:
        List<EventInstance> events = testSubject.getScheduledEvents(communicationChannelEmail);

        //then:
        assertTrue(events.contains(eventAsEmail1));
        assertTrue(events.contains(eventAsEmail2));
    }

    @Test
    public void test_scheduleEvent_with_eventHandlerType_email_and_invoke_getScheduledEvents_then_size() {

        //given:
        CommunicationChannel communicationChannelEmail = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.EMAIL);

        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        EventInstance eventAsEmail2 = EventTestUtils.createEventWithActiveTime(2, inactiveIntervalTime, message);
        EventInstance eventAsSms = EventTestUtils.createEventWithActiveTime(3, inactiveIntervalTime, message);

        //when:
        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);
        testSubject.scheduleEvent(eventHandler, eventAsEmail1);
        testSubject.scheduleEvent(eventHandler, eventAsEmail2);

        //and:
        eventHandler.setHandlerType(EventHandlerVO.TYPE_SMS);
        testSubject.scheduleEvent(eventHandler, eventAsSms);

        //and:
        List<EventInstance> events = testSubject.getScheduledEvents(communicationChannelEmail);

        //then:
        assertEquals(2, events.size());
    }

    @Test
    public void test_scheduleEvent_with_eventHandlerType_sms_and_invoke_getScheduledEvents_then_contains_sms() {

        //given:
        CommunicationChannel communicationChannelSms = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.SMS);

        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        EventInstance eventAsEmail2 = EventTestUtils.createEventWithActiveTime(2, inactiveIntervalTime, message);
        EventInstance eventAsSms = EventTestUtils.createEventWithActiveTime(3, inactiveIntervalTime, message);

        //when:
        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);
        testSubject.scheduleEvent(eventHandler, eventAsEmail1);
        testSubject.scheduleEvent(eventHandler, eventAsEmail2);

        //and:
        eventHandler.setHandlerType(EventHandlerVO.TYPE_SMS);
        testSubject.scheduleEvent(eventHandler, eventAsSms);

        //and:
        List<EventInstance> events = testSubject.getScheduledEvents(communicationChannelSms);

        //then:
        assertTrue(events.contains(eventAsSms));
    }


    @Test
    public void test_scheduleEvent_with_eventHandlerType_sms_and_invoke_getScheduledEvents_then_size_one() {

        //given:
        CommunicationChannel communicationChannelSms = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.SMS);

        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);
        EventInstance eventAsEmail2 = EventTestUtils.createEventWithActiveTime(2, inactiveIntervalTime, message);
        EventInstance eventAsSms = EventTestUtils.createEventWithActiveTime(3, inactiveIntervalTime, message);

        //when:
        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);
        testSubject.scheduleEvent(eventHandler, eventAsEmail1);
        testSubject.scheduleEvent(eventHandler, eventAsEmail2);

        //and:
        eventHandler.setHandlerType(EventHandlerVO.TYPE_SMS);
        testSubject.scheduleEvent(eventHandler, eventAsSms);

        //and:
        List<EventInstance> events = testSubject.getScheduledEvents(communicationChannelSms);

        //then:
        assertEquals(1, events.size());
    }

    @Test
    public void test_unscheduleEvent_with_communicationChannelType_email_and_invoke_getScheduledEvents_then_size_zero() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);
        CommunicationChannel communicationChannelEmail = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.EMAIL);

        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);

        //when:
        testSubject.scheduleEvent(eventHandler, event);
        List<EventInstance> events = testSubject.getScheduledEvents(communicationChannelEmail);

        //then:
        assertEquals(1, events.size());
        assertTrue(events.contains(event));

        //and when:
        testSubject.unscheduleEvent(communicationChannelEmail, event);
        events = testSubject.getScheduledEvents(communicationChannelEmail);

        //then:
        assertEquals(0, events.size());
    }

    @Test
    public void test_unscheduleEvent_with_communicationChannelType_sms_and_invoke_getScheduledEvents_then_size_zero() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);
        CommunicationChannel communicationChannelSms = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.SMS);

        eventHandler.setHandlerType(EventHandlerVO.TYPE_SMS);

        //when:
        testSubject.scheduleEvent(eventHandler, event);
        List<EventInstance> events = testSubject.getScheduledEvents(communicationChannelSms);

        //then:
        assertEquals(1, events.size());
        assertTrue(events.contains(event));

        //and when:
        testSubject.unscheduleEvent(communicationChannelSms, event);
        events = testSubject.getScheduledEvents(communicationChannelSms);

        //then:
        assertEquals(0, events.size());
    }


    @Test
    public void test_isScheduledInactiveEventType_for_email_then_true() {

        //given:
        CommunicationChannel communicationChannelEmail = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.EMAIL);

        EventType eventType = new ScheduledInactiveEventType(new DataPointEventType(), communicationChannelEmail);
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,eventType);

        //when:
        boolean result = testSubject.isScheduledInactiveEventType(event);

        //then:
        assertTrue(result);
    }

    @Test
    public void test_isScheduledInactiveEventType_for_sms_then_true() {

        //given:
        CommunicationChannel communicationChannelSms = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.SMS);

        EventType eventType = new ScheduledInactiveEventType(new DataPointEventType(), communicationChannelSms);
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,eventType);

        //when:
        boolean result = testSubject.isScheduledInactiveEventType(event);

        //then:
        assertTrue(result);
    }

    @Test
    public void test_isScheduledInactiveEventType_then_false() {

        //given:
        EventType eventType = new DataPointEventType();
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,eventType);

        //when:
        boolean result = testSubject.isScheduledInactiveEventType(event);

        //then:
        assertFalse(result);
    }

    @Test
    public void test_unscheduleEvent_for_sms_then_verify_times_method_dao() {

        //given:
        CommunicationChannel communicationChannelSms = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.SMS);

        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);

        eventHandler.setHandlerType(EventHandlerVO.TYPE_SMS);
        testSubject.scheduleEvent(eventHandler, event);

        //when:
        testSubject.unscheduleEvent(communicationChannelSms, event);

        //then:
        verify(dao, times(1)).delete(any());
    }

    @Test
    public void test_unscheduleEvent_for_email_then_verify_times_method_dao() {

        //given:
        CommunicationChannel communicationChannelEmail = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.EMAIL);

        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);


        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);
        testSubject.scheduleEvent(eventHandler, event);

        //when:
        testSubject.unscheduleEvent(communicationChannelEmail, event);

        //then:
        verify(dao, times(1)).delete(any());
    }

    @Test
    public void test_scheduleEvent_for_sms_then_verify_times_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);

        eventHandler.setHandlerType(EventHandlerVO.TYPE_SMS);

        //when:
        testSubject.scheduleEvent(eventHandler, event);

        //then:
        verify(dao, times(1)).insert(any());
    }

    @Test
    public void test_scheduleEvent_for_email_then_verify_times_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime,message);

        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);

        //when:
        testSubject.scheduleEvent(eventHandler, event);

        //then:
        verify(dao, times(1)).insert(any());
    }

    @Test
    public void test_getScheduledEvents_for_sms_order_by_eventId_desc() {

        //given:
        CommunicationChannel communicationChannelSms = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.SMS);

        EventInstance event1 = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime, message);
        EventInstance event2 = EventTestUtils.createEventWithActiveTime(2,inactiveIntervalTime, message);
        EventInstance event3 = EventTestUtils.createEventWithActiveTime(3,inactiveIntervalTime, message);
        EventInstance event4 = EventTestUtils.createEventWithActiveTime(4,inactiveIntervalTime, message);

        List<EventInstance> eventsExpected = new ArrayList<>();
        eventsExpected.add(event4);
        eventsExpected.add(event3);
        eventsExpected.add(event2);
        eventsExpected.add(event1);

        eventHandler.setHandlerType(EventHandlerVO.TYPE_SMS);

        //when:
        testSubject.scheduleEvent(eventHandler, event1);
        testSubject.scheduleEvent(eventHandler, event2);
        testSubject.scheduleEvent(eventHandler, event4);
        testSubject.scheduleEvent(eventHandler, event3);
        
        //and:
        List<EventInstance> events = testSubject.getScheduledEvents(communicationChannelSms);

        //then:
        assertEquals(eventsExpected, events);
    }


    @Test
    public void test_getScheduledEvents_for_email_order_by_eventId_desc() {

        //given:
        CommunicationChannel communicationChannelEmail = CommunicationChannel
                .newChannel(mailingListWithInactiveInterval, CommunicationChannelType.EMAIL);

        EventInstance event1 = EventTestUtils.createEventWithActiveTime(1,inactiveIntervalTime, message);
        EventInstance event2 = EventTestUtils.createEventWithActiveTime(2,inactiveIntervalTime, message);
        EventInstance event3 = EventTestUtils.createEventWithActiveTime(3,inactiveIntervalTime, message);
        EventInstance event4 = EventTestUtils.createEventWithActiveTime(4,inactiveIntervalTime, message);


        List<EventInstance> eventsExpected = new ArrayList<>();
        eventsExpected.add(event4);
        eventsExpected.add(event3);
        eventsExpected.add(event2);
        eventsExpected.add(event1);

        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);

        //when:
        testSubject.scheduleEvent(eventHandler, event1);
        testSubject.scheduleEvent(eventHandler, event2);
        testSubject.scheduleEvent(eventHandler, event4);
        testSubject.scheduleEvent(eventHandler, event3);

        //and:
        List<EventInstance> events = testSubject.getScheduledEvents(communicationChannelEmail);

        //then:
        assertEquals(eventsExpected, events);
    }

}