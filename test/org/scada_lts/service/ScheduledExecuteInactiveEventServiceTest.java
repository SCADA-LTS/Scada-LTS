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
import org.scada_lts.dao.event.ScheduledExecuteInactiveEvent;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import utils.EventTestUtils;
import utils.MailingListTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class ScheduledExecuteInactiveEventServiceTest {

    private ScheduledExecuteInactiveEventService subject;
    private MailingList mailingListWithInactiveInterval;
    private ScheduledExecuteInactiveEventDAO dao;
    private DateTime inactiveIntervalTime;
    private EventHandlerVO emailEventHandler;
    private EventHandlerVO smsEventHandler;
    private CommunicationChannel smsChannel;
    private CommunicationChannel emailChannel;

    @Before
    public void config() {

        emailEventHandler = EventTestUtils.createEventHandler(1, EventHandlerVO.TYPE_EMAIL);
        smsEventHandler = EventTestUtils.createEventHandler(2, EventHandlerVO.TYPE_SMS);

        inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");
        mailingListWithInactiveInterval = MailingListTestUtils
                .createMailingListWithInactiveIntervalAndUser(1, inactiveIntervalTime, true, "Mark");

        SystemSettingsService systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getSMSDomain()).thenReturn("domain.com");

        MailingListService mailingListService = mock(MailingListService.class);
        when(mailingListService.getMailingLists(any())).thenReturn(Collections.emptyList());
        when(mailingListService.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingListWithInactiveInterval));

        dao = mock(ScheduledExecuteInactiveEventDAO.class);

        subject = ScheduledExecuteInactiveEventService.newInstance(dao, mailingListService);
        emailChannel = CommunicationChannel.newEmailChannel(mailingListWithInactiveInterval, systemSettingsServiceMock);
        smsChannel = CommunicationChannel.newSmsChannel(mailingListWithInactiveInterval, systemSettingsServiceMock);
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
    public void test_unscheduleEvent_for_sms_then_verify_times_delete_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, smsEventHandler);
        subject.scheduleEvent(smsEventHandler, event);

        ScheduledExecuteInactiveEvent key = new ScheduledExecuteInactiveEvent(smsEventHandler,
                event, mailingListWithInactiveInterval);

        //when:
        subject.unscheduleEvent(scheduledEvent, smsChannel);

        //then:
        verify(dao, times(1)).delete(eq(key));
    }

    @Test
    public void test_unscheduleEvent_for_email_then_verify_times_delete_method_dao() {

        //given:
        EventInstance event = EventTestUtils
                .createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        ScheduledEvent scheduledEvent = new ScheduledEvent(event, emailEventHandler);
        subject.scheduleEvent(emailEventHandler, event);

        ScheduledExecuteInactiveEvent key = new ScheduledExecuteInactiveEvent(emailEventHandler,
                event, mailingListWithInactiveInterval);

        //when:
        subject.unscheduleEvent(scheduledEvent, emailChannel);

        //then:
        verify(dao, times(1)).delete(eq(key));
    }

    @Test
    public void test_scheduleEvent_for_sms_then_verify_times_insert_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        ScheduledExecuteInactiveEvent key = new ScheduledExecuteInactiveEvent(smsEventHandler,
                event, mailingListWithInactiveInterval);

        //when:
        subject.scheduleEvent(smsEventHandler, event);

        //then:
        verify(dao, times(1)).insert(eq(key));
    }

    @Test
    public void test_scheduleEvent_for_email_then_verify_times_method_dao() {

        //given:
        EventInstance event = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1,inactiveIntervalTime);
        ScheduledExecuteInactiveEvent key = new ScheduledExecuteInactiveEvent(emailEventHandler,
                event, mailingListWithInactiveInterval);

        //when:
        subject.scheduleEvent(emailEventHandler, event);

        //then:
        verify(dao, times(1)).insert(eq(key));
    }
}