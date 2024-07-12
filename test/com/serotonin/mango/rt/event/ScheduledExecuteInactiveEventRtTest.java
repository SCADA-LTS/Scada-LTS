package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.event.schedule.ScheduledExecuteInactiveEventRT;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.maint.work.AfterWork;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.MailingList;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.*;
import utils.EventTestUtils;
import utils.MailingListTestUtils;
import utils.ScheduledInactiveEventTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static utils.MailingListTestUtils.createAddressEntry;

@RunWith(Parameterized.class)
public class ScheduledExecuteInactiveEventRtTest {

    @Parameterized.Parameters(name= "{index}: dailyLimitSentEmailsNumber: {0}, " +
            "isDailyLimitSentEmails: {1}, CommunicationChannelType: {2}, times: {3}," +
            " communicateLimitTimes: {4}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                { 1, true, CommunicationChannelType.EMAIL, 1, 1},
                //{ 2, true, CommunicationChannelType.EMAIL, 2, 1},
                { 3, true, CommunicationChannelType.EMAIL, 2, 0},
                { 1, true, CommunicationChannelType.SMS, 1, 1},
                //{ 2, true, CommunicationChannelType.SMS, 2, 1},
                { 3, true, CommunicationChannelType.SMS, 2, 0},
                { 1, false, CommunicationChannelType.EMAIL, 2, 0},
                //{ 2, false, CommunicationChannelType.EMAIL, 2, 0},
                { 3, false, CommunicationChannelType.EMAIL, 2, 0},
                { 1, false, CommunicationChannelType.SMS, 2, 0},
                //{ 2, false, CommunicationChannelType.SMS, 2, 0},
                { 3, false, CommunicationChannelType.SMS, 2, 0},
        });
    }


    private ScheduledExecuteInactiveEventRT testSubject;

    private int times;
    private int communicateLimitTimes;
    private boolean dailyLimitSentEmails;
    private ScheduledEvent scheduledEvent1;
    private ScheduledEvent scheduledEvent2;

    private CommunicationChannelTypable channelType;
    private CommunicationChannel channel;
    private EventHandlerVO eventHandler;
    private MailingList mailingList;

    private ScheduledExecuteInactiveEventService scheduledInactiveEventServiceMock;
    private CommunicationChannelTypable channelTypeMock;
    private SystemSettingsService systemSettingsServiceMock;


    public ScheduledExecuteInactiveEventRtTest(int dailyLimitSentEmailsNumber,
                                               boolean dailyLimitSentEmails,
                                               CommunicationChannelType type,
                                               int times, int communicateLimitTimes) {

        this.times = times;
        this.channelType = type;
        this.dailyLimitSentEmails = dailyLimitSentEmails;
        this.communicateLimitTimes = communicateLimitTimes;

        eventHandler = new EventHandlerVO();
        eventHandler.setAlias("event-handler-alias-test");
        eventHandler.setHandlerType(channelType.getEventHandlerType());

        List<AddressEntry> addressEntries1 = createAddressEntry("test2@test.com", "111111111");
        mailingList = MailingListTestUtils.createMailingList(1, addressEntries1);
        mailingList.setDailyLimitSentEmails(dailyLimitSentEmails);
        mailingList.setDailyLimitSentEmailsNumber(dailyLimitSentEmailsNumber);

        EventType type1 = mock(DataPointEventType.class);
        when(type1.getDataPointId()).thenReturn(2);
        when(type1.getReferenceId1()).thenReturn(2);
        when(type1.getReferenceId2()).thenReturn(3);
        EventInstance event1 = EventTestUtils.createEventCriticalWithActiveTime(1, DateTime.now(), type1);
        scheduledEvent1 = new ScheduledEvent(event1, eventHandler);

        EventType type2 = mock(DataPointEventType.class);
        when(type2.getDataPointId()).thenReturn(5);
        when(type2.getReferenceId1()).thenReturn(5);
        when(type2.getReferenceId2()).thenReturn(6);
        EventInstance event2 = EventTestUtils.createEventCriticalWithActiveTime(2, DateTime.now(), type2);
        scheduledEvent2 = new ScheduledEvent(event2, eventHandler);


        systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getSMSDomain()).thenReturn("domain.com");
    }

    @Before
    public void init() {

        this.channelTypeMock = mock(CommunicationChannelTypable.class);
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(),
                anyString(), anyObject());
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendLimit(any(EventInstance.class), anySet(),
                anyString(), anyObject());
        when(channelTypeMock.getEventHandlerType()).thenReturn(channelType.getEventHandlerType());
        when(channelTypeMock.validateAddress(anyString())).thenAnswer(a -> {
            Object[] args = a.getArguments();
            return channelType.validateAddress((String)args[0]);
        });
        when(channelTypeMock.getReplaceRegex()).thenReturn(channelType.getReplaceRegex());
        when(channelTypeMock.formatAddresses(any(), any(), any())).thenAnswer(a -> channelType
                .formatAddresses((Set<String>)a.getArguments()[0], (String)a.getArguments()[1],
                        (String)a.getArguments()[2]));

        this.channel = CommunicationChannel.newChannel(mailingList, channelTypeMock, systemSettingsServiceMock);
        InactiveEventsProvider providerMock = ScheduledInactiveEventTestUtils.createProviderMock(dailyLimitSentEmails, channel,
                scheduledEvent1, scheduledEvent2);

        this.scheduledInactiveEventServiceMock = mock(ScheduledExecuteInactiveEventService.class);

        DataPointService dataPointServiceMock = mock(DataPointService.class);
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointServiceMock.getDataPoint(anyInt())).thenReturn(dataPointVO);

        DataSourceService dataSourceServiceMock = mock(DataSourceService.class);
        DataSourceVO dataSourceVO = mock(DataSourceVO.class);
        when(dataSourceServiceMock.getDataSource(anyInt())).thenReturn(dataSourceVO);

        this.testSubject = new ScheduledExecuteInactiveEventRT(scheduledInactiveEventServiceMock, providerMock, dataPointServiceMock,
                dataSourceServiceMock, 250);
    }

    @Test
    public void when_scheduleTimeout_and_sendMsg_success_for_all_events_then_verify_times_unscheduleEvent() {

        //given:
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(), anyString(), anyObject());
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendLimit(any(EventInstance.class), anySet(), anyString(), anyObject());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(times)).unscheduleEvent(any(ScheduledEvent.class), any());
        verify(scheduledInactiveEventServiceMock, times(times)).unscheduleEvent(any(ScheduledEvent.class), same(channel));
    }

    @Test
    public void when_scheduleTimeout_and_sendMsg_success_for_all_events_then_verify_times_sendLimit() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(), anyString(), anyObject());
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendLimit(any(EventInstance.class), anySet(), anyString(), anyObject());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(channelTypeMock, times(communicateLimitTimes)).sendLimit(any(EventInstance.class), anySet(),
                eq("Limit"), anyObject());
        verify(channelTypeMock, times(communicateLimitTimes)).sendLimit(any(EventInstance.class), eq(addresses),
                eq("Limit"), anyObject());

    }

    @Test
    public void when_scheduleTimeout_and_sendMsg_fail_for_all_events_then_verify_times_sendMsg() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        String alias = eventHandler.getAlias();
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workFail(new Exception("test exception"));
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(), anyString(), anyObject());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(channelTypeMock, times(times)).sendMsg(any(EventInstance.class), anySet(),
                eq(alias), anyObject());
        verify(channelTypeMock, times(times)).sendMsg(any(EventInstance.class), eq(addresses),
                eq(alias), anyObject());
    }
}