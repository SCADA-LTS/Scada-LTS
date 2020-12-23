package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.event.schedule.ScheduledExecuteInactiveEventRT;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import com.serotonin.web.i18n.LocalizableMessage;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import utils.EventTestUtils;
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
    private MailingList mailingList;
    private DataPointService dataPointService;
    private DataSourceService dataSourceService;

    public ScheduledExecuteInactiveEventRtRaiseEventExceptionTest(int dailyLimitSentEmailsNumber,
                                                                  boolean dailyLimitSentEmails,
                                                                  CommunicationChannelType type,
                                                                  int times) {

        this.times = times;
        EventHandlerVO eventHandler = new EventHandlerVO();
        eventHandler.setHandlerType(type.getEventHandlerType());

        UserEntry user1 = createUserEntry("Ewa","111111111", "test1@test.com");
        List<AddressEntry> addressEntries1 = createAddressEntry("test2@test.com");

        mailingList = MailingListTestUtils.createMailingList(1, addressEntries1, user1);
        mailingList.setDailyLimitSentEmails(dailyLimitSentEmails);
        mailingList.setDailyLimitSentEmailsNumber(dailyLimitSentEmailsNumber);

        channel = CommunicationChannel.newChannel(mailingList, type);

        message = new LocalizableMessage("com.test");

        type1 = mock(DataPointEventType.class);
        when(type1.getDataPointId()).thenReturn(2);
        when(type1.getReferenceId1()).thenReturn(2);
        when(type1.getReferenceId2()).thenReturn(3);

        EventInstance event1 = EventTestUtils.createEventWithActiveTime(1, DateTime.now(),type1);

        ScheduledEvent scheduledEvent1 = new ScheduledEvent(event1, eventHandler);

        type2 = mock(DataPointEventType.class);
        when(type2.getDataPointId()).thenReturn(5);
        when(type2.getReferenceId1()).thenReturn(5);
        when(type2.getReferenceId2()).thenReturn(6);

        EventInstance event2 = EventTestUtils.createEventWithActiveTime(2, DateTime.now(),type2);

        ScheduledEvent scheduledEvent2 = new ScheduledEvent(event2, eventHandler);

        List<ScheduledEvent> events = new ArrayList<>();
        events.add(scheduledEvent1);
        events.add(scheduledEvent2);

        service = mock(ScheduledExecuteInactiveEventService.class);
        when(service.getScheduledEvents(channel)).thenReturn(events);

        eventManager = mock(EventManager.class);

        dataPointService = mock(DataPointService.class);
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointService.getDataPoint(anyInt())).thenReturn(dataPointVO);

        dataSourceService = mock(DataSourceService.class);
        DataSourceVO dataSourceVO = mock(DataSourceVO.class);
        when(dataSourceService.getDataSource(anyInt())).thenReturn(dataSourceVO);

        testSubject = new ScheduledExecuteInactiveEventRT(channel,service,eventManager,
                dataPointService, dataSourceService);
    }

    @Test
    public void test_scheduleTimeout_when_dataPoint_not_exists_then_verify_times_unscheduleEvent() {

        //given:
        when(dataPointService.getDataPoint(anyInt())).thenReturn(null);

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(service, times(times)).unscheduleEvent(any(ScheduledEvent.class), eq(channel));
    }

    @Test
    public void test_scheduleTimeout_when_dataSource_not_exists_then_verify_times_unscheduleEvent() {

        //given:
        when(dataSourceService.getDataSource(anyInt())).thenReturn(null);

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(service, times(times)).unscheduleEvent(any(ScheduledEvent.class), eq(channel));
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
        verify(service, times(0)).unscheduleEvent(any(ScheduledEvent.class), eq(channel));
    }
}