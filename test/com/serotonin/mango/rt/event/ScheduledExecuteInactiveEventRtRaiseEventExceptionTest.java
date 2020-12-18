package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.event.schedule.ScheduledExecuteInactiveEventRT;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import com.serotonin.web.i18n.LocalizableMessage;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;
import org.springframework.dao.EmptyResultDataAccessException;
import utils.MailingListTestUtils;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static utils.MailingListTestUtils.createAddressEntry;
import static utils.MailingListTestUtils.createUserEntry;

@RunWith(Parameterized.class)
public class ScheduledExecuteInactiveEventRtRaiseEventExceptionTest {

    @Parameterized.Parameters(name= "{index}: dailyLimitSentEmailsNumber: {0}, " +
            "isDailyLimitSentEmails: {1}, CommunicationChannelType: {2}, times: {3}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                { 1, true, CommunicationChannelType.EMAIL, 2},
                { 2, true, CommunicationChannelType.EMAIL, 2},
                { 3, true, CommunicationChannelType.EMAIL, 2},
                { 1, true, CommunicationChannelType.SMS, 2},
                { 2, true, CommunicationChannelType.SMS, 2},
                { 3, true, CommunicationChannelType.SMS, 2},
                { 1, false, CommunicationChannelType.EMAIL, 2},
                { 2, false, CommunicationChannelType.EMAIL, 2},
                { 3, false, CommunicationChannelType.EMAIL, 2},
                { 1, false, CommunicationChannelType.SMS, 2},
                { 2, false, CommunicationChannelType.SMS, 2},
                { 3, false, CommunicationChannelType.SMS, 2},
        });
    }

    private int times;
    private ScheduledExecuteInactiveEventRT testSubject;
    private EventManager eventManager;
    private LocalizableMessage message;
    private ScheduledExecuteInactiveEventService service;
    private CommunicationChannel channel;
    private DataPointEventType type1;
    private DataPointEventType type2;

    public ScheduledExecuteInactiveEventRtRaiseEventExceptionTest(int dailyLimitSentEmailsNumber,
                                                                  boolean dailyLimitSentEmails,
                                                                  CommunicationChannelType type,
                                                                  int times) {

        this.times = times;

        UserEntry user1 = createUserEntry("Ewa","111111111", "test1@test.com");
        List<AddressEntry> addressEntries1 = createAddressEntry("test2@test.com");

        MailingList mailingList = MailingListTestUtils.createMailingList(addressEntries1, user1);
        mailingList.setDailyLimitSentEmails(dailyLimitSentEmails);
        mailingList.setDailyLimitSentEmailsNumber(dailyLimitSentEmailsNumber);

        channel = CommunicationChannel.newChannel(mailingList, type);

        message = new LocalizableMessage("com.test");

        type1 = mock(DataPointEventType.class);
        when(type1.getDataPointId()).thenReturn(2);
        when(type1.getReferenceId1()).thenReturn(2);
        when(type1.getReferenceId2()).thenReturn(3);

        EventInstance event1 = new EventInstance(type1, 123, false, 123,
                message, Collections.emptyMap());

        type2 = mock(DataPointEventType.class);
        when(type1.getDataPointId()).thenReturn(5);
        when(type1.getReferenceId1()).thenReturn(5);
        when(type1.getReferenceId2()).thenReturn(6);

        EventInstance event2 = new EventInstance(type2, 123, false, 123,
                message, Collections.emptyMap());


        List<EventInstance> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        service = mock(ScheduledExecuteInactiveEventService.class);
        when(service.getScheduledEvents(channel)).thenReturn(events);

        eventManager = mock(EventManager.class);

        testSubject = new ScheduledExecuteInactiveEventRT(channel,service,eventManager);
    }

    @Test
    public void test_scheduleTimeout_when_dataPoint_not_exists_then_verify_times_unscheduleEvent() {

        //given:
        doThrow(Exception.class)
                .when(type1)
                .getDataSourceId();

        doThrow(Exception.class)
                .when(type2)
                .getDataSourceId();

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(service, times(times)).unscheduleEvent(eq(channel), any());
    }

    @Test
    public void test_scheduleTimeout_when_raiseEvent_with_exception_then_0_times_unscheduleEvent() {

        //given:
        doThrow(Exception.class)
                .when(eventManager)
                .raiseEvent(any(), anyLong(), anyBoolean(), anyInt(), any(), anyMap());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(service, times(0)).unscheduleEvent(eq(channel), any());
    }
}