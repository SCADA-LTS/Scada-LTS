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
import org.scada_lts.web.beans.GetApplicationBeans;
import org.springframework.context.ApplicationContext;
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
public class ScheduledExecuteInactiveEventRtUnscheduleEventTest {

    @Parameterized.Parameters(name= "{index}: dailyLimitSentEmailsNumber: {0}, " +
            "isDailyLimitSentEmails: {1}, CommunicationChannelType: {2}, times: {3}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                { 1, true, CommunicationChannelType.EMAIL, 1},
                //{ 2, true, CommunicationChannelType.EMAIL, 2},
                { 3, true, CommunicationChannelType.EMAIL, 2},
                { 1, true, CommunicationChannelType.SMS, 1},
                //{ 2, true, CommunicationChannelType.SMS, 2},
                { 3, true, CommunicationChannelType.SMS, 2},
                { 1, false, CommunicationChannelType.EMAIL, 2},
                //{ 2, false, CommunicationChannelType.EMAIL, 2},
                { 3, false, CommunicationChannelType.EMAIL, 2},
                { 1, false, CommunicationChannelType.SMS, 2},
                //{ 2, false, CommunicationChannelType.SMS, 2},
                { 3, false, CommunicationChannelType.SMS, 2},
        });
    }

    private ScheduledExecuteInactiveEventRT testSubject;

    private int invokeUnscheduleEventTimes;
    private EventInstance event1;
    private EventInstance event2;
    private ScheduledEvent scheduledEvent1;
    private ScheduledEvent scheduledEvent2;

    private CommunicationChannelTypable channelType;
    private CommunicationChannel channel;
    private MailingList mailingList;
    private boolean dailyLimitSentEmails;

    private ScheduledExecuteInactiveEventService scheduledInactiveEventServiceMock;
    private CommunicationChannelTypable channelTypeMock;
    private DataPointService dataPointServiceMock;
    private DataSourceService dataSourceServiceMock;
    private SystemSettingsService systemSettingsServiceMock;

    public ScheduledExecuteInactiveEventRtUnscheduleEventTest(int dailyLimitSentEmailsNumber,
                                                              boolean dailyLimitSentEmails,
                                                              CommunicationChannelType type,
                                                              int invokeUnscheduleEventTimes) {
        GetApplicationBeans getApplicationBeans = new GetApplicationBeans();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        getApplicationBeans.setApplicationContext(applicationContext);
        this.invokeUnscheduleEventTimes = invokeUnscheduleEventTimes;
        this.channelType = type;
        this.dailyLimitSentEmails = dailyLimitSentEmails;

        EventHandlerVO eventHandler = new EventHandlerVO();
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
        event1 = EventTestUtils.createEventCriticalWithActiveTime(1, DateTime.now(), type1);
        scheduledEvent1 = new ScheduledEvent(event1, eventHandler);

        EventType type2 = mock(DataPointEventType.class);
        when(type2.getDataPointId()).thenReturn(5);
        when(type2.getReferenceId1()).thenReturn(5);
        when(type2.getReferenceId2()).thenReturn(6);
        event2 = EventTestUtils.createEventCriticalWithActiveTime(2, DateTime.now(),type2);
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

        this.dataPointServiceMock = mock(DataPointService.class);
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointServiceMock.getDataPoint(anyInt())).thenReturn(dataPointVO);

        this.dataSourceServiceMock = mock(DataSourceService.class);
        DataSourceVO dataSourceVO = mock(DataSourceVO.class);
        when(dataSourceServiceMock.getDataSource(anyInt())).thenReturn(dataSourceVO);

        this.testSubject = new ScheduledExecuteInactiveEventRT(scheduledInactiveEventServiceMock, providerMock, dataPointServiceMock,
                dataSourceServiceMock, 250);
    }

    @Test
    public void when_scheduleTimeout_and_dataPoint_not_exists_then_verify_times_unscheduleEvent() {

        //given:
        when(dataPointServiceMock.getDataPoint(anyInt())).thenReturn(null);

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(invokeUnscheduleEventTimes))
                .unscheduleEvent(any(ScheduledEvent.class), same(channel));
    }

    @Test
    public void when_scheduleTimeout_and_dataSource_not_exists_then_verify_times_unscheduleEvent() {

        //given:
        when(dataSourceServiceMock.getDataSource(anyInt())).thenReturn(null);

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(invokeUnscheduleEventTimes))
                .unscheduleEvent(any(ScheduledEvent.class), same(channel));
    }

    @Test
    public void when_scheduleTimeout_and_dataPoint_not_exists_for_even1_then_verify_times_unscheduleEvent() {

        //given:
        int dataPointId1 = event1.getEventType().getDataPointId();
        int dataPointId2 = event2.getEventType().getDataPointId();
        when(dataPointServiceMock.getDataPoint(dataPointId1)).thenReturn(null);
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointServiceMock.getDataPoint(dataPointId2)).thenReturn(dataPointVO);

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(1)).unscheduleEvent(same(scheduledEvent1),
                same(channel));
        if(invokeUnscheduleEventTimes == 1) {
            verify(scheduledInactiveEventServiceMock, times(0)).unscheduleEvent(same(scheduledEvent2),
                    same(channel));
        } else {
            verify(scheduledInactiveEventServiceMock, times(2)).unscheduleEvent(any(), any());
            verify(scheduledInactiveEventServiceMock, times(1)).unscheduleEvent(same(scheduledEvent2),
                    same(channel));
        }
    }

    @Test
    public void when_scheduleTimeout_and_dataSource_not_exists_for_even2_then_verify_times_unscheduleEvent() {

        //given:
        int event1DataPointId = event1.getEventType().getDataPointId();
        int event2DataPointId = event2.getEventType().getDataPointId();
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointServiceMock.getDataPoint(event1DataPointId)).thenReturn(dataPointVO);
        when(dataPointServiceMock.getDataPoint(event2DataPointId)).thenReturn(null);

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(1)).unscheduleEvent(same(scheduledEvent1),
                same(channel));
        if(invokeUnscheduleEventTimes == 1) {
            verify(scheduledInactiveEventServiceMock, times(0)).unscheduleEvent(same(scheduledEvent2),
                    same(channel));
        } else {
            verify(scheduledInactiveEventServiceMock, times(2)).unscheduleEvent(any(), any());
            verify(scheduledInactiveEventServiceMock, times(1)).unscheduleEvent(same(scheduledEvent2),
                    same(channel));
        }
    }

    @Test
    public void when_scheduleTimeout_and_sendMsg_return_false_for_all_events_then_0_times_unscheduleEvent() {

        //given:
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(), anyString(), anyObject());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(invokeUnscheduleEventTimes)).unscheduleEvent(any(ScheduledEvent.class), eq(channel));
    }

    @Test
    public void when_scheduleTimeout_and_sendMsg_fail_for_all_events_then_0_times_unscheduleEvent() {

        //given:
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workFail(new Exception("test exception"));
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(), anyString(), anyObject());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(0)).unscheduleEvent(any(ScheduledEvent.class), eq(channel));
    }

    @Test
    public void when_scheduleTimeout_and_sendMsg_fail_for_event1_then_verify_times_unscheduleEvent_for_event2() {

        //given:
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workFail(new Exception("test exception"));
            return true;
        }).when(channelTypeMock).sendMsg(refEq(event1, "context"), anySet(), anyString(), anyObject());
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendMsg(refEq(event2, "context"), anySet(), anyString(), anyObject());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(0)).unscheduleEvent(eq(scheduledEvent1), eq(channel));
        if(invokeUnscheduleEventTimes == 1) {
            verify(scheduledInactiveEventServiceMock, times(0)).unscheduleEvent(eq(scheduledEvent2), eq(channel));
        } else {
            verify(scheduledInactiveEventServiceMock, times(1)).unscheduleEvent(any(), any());
            verify(scheduledInactiveEventServiceMock, times(1)).unscheduleEvent(eq(scheduledEvent2), eq(channel));
        }
    }


    @Test
    public void when_scheduleTimeout_and_sendMsg_return_false_for_event2_then_1_times_unscheduleEvent_for_event1() {

        //given:
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendMsg(refEq(event1, "context"), anySet(), anyString(), anyObject());
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workFail(new Exception("test execption"));
            return true;
        }).when(channelTypeMock).sendMsg(refEq(event2, "context"), anySet(), anyString(), anyObject());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(scheduledInactiveEventServiceMock, times(1)).unscheduleEvent(any(), any());
        verify(scheduledInactiveEventServiceMock, times(1)).unscheduleEvent(same(scheduledEvent1),
                same(channel));
    }

    @Test
    public void when_scheduleTimeout_and_sendMsg_fail_for_all_events_then_0_times_sendLimit() {

        //given:
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workFail(new Exception("test exception"));
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(), anyString(), any(AfterWork.class));

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(channelTypeMock, times(0)).sendLimit(any(EventInstance.class), anySet(),
                eq("Limit"), anyObject());
    }
}