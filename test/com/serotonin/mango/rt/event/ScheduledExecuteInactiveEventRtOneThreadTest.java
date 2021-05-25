package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.event.schedule.ScheduledExecuteInactiveEventRT;
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
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.*;
import utils.EventDAOMemory;
import utils.EventTestUtils;
import utils.MailingListTestUtils;
import utils.ScheduledExecuteInactiveEventDAOMemory;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static utils.MailingListTestUtils.createAddressEntry;

@RunWith(Parameterized.class)
public class ScheduledExecuteInactiveEventRtOneThreadTest {

    @Parameterized.Parameters(name= "{index}: dailyLimitSentEmailsNumber: {0}, " +
            "isDailyLimitSentEmails: {1}, CommunicationChannelType: {2}, invokeSendMsgTimes: {3}," +
            " communicateLimitTimes: {4}, eventsNumber: {5}, currentScheduledNumber: {6}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                { 3, true, CommunicationChannelType.EMAIL, 3, 1, 250, 3},
                { 3, true, CommunicationChannelType.SMS, 3, 1, 250, 3},
                { 20, true, CommunicationChannelType.EMAIL, 10, 0, 10, 10},
                { 3, false, CommunicationChannelType.EMAIL, 250, 0, 250, 250},
                { 3, false, CommunicationChannelType.SMS, 250, 0, 250, 250},
                { 20, false, CommunicationChannelType.EMAIL, 10, 0, 10, 10},
        });
    }


    private ScheduledExecuteInactiveEventRT testSubject;

    private int invokeSendMsgTimes;
    private int communicateLimitTimes;
    private int eventsNumber;
    private int currentScheduledNumber;

    private CommunicationChannelTypable channelType;
    private CommunicationChannel channel;
    private EventHandlerVO eventHandler;
    private MailingList mailingList;
    private InactiveEventsProvider inactiveEventsProvider;
    private ScheduledExecuteInactiveEventDAO scheduledInactiveEventDAOMemory;

    private CommunicationChannelTypable channelTypeMock;
    private MailingListService mailingListServiceMock;
    private SystemSettingsService systemSettingsServiceMock;

    private List<EventInstance> events;

    public ScheduledExecuteInactiveEventRtOneThreadTest(int dailyLimitSentEmailsNumber,
                                                        boolean dailyLimitSentEmails,
                                                        CommunicationChannelType type,
                                                        int invokeSendMsgTimes,
                                                        int communicateLimitTimes,
                                                        int eventsNumber,
                                                        int currentScheduledNumber) {

        this.invokeSendMsgTimes = invokeSendMsgTimes;
        this.channelType = type;
        this.communicateLimitTimes = communicateLimitTimes;
        this.eventsNumber = eventsNumber;
        this.currentScheduledNumber = currentScheduledNumber;

        eventHandler = new EventHandlerVO();
        eventHandler.setId(123);
        eventHandler.setAlias("event-handler-alias-test");
        eventHandler.setHandlerType(channelType.getEventHandlerType());

        DateTime inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");

        List<AddressEntry> addressEntries1 = createAddressEntry("test@test.com", "111111111");
        mailingList = MailingListTestUtils.createMailingList(1, inactiveIntervalTime, addressEntries1.toArray(new AddressEntry[]{}));
        mailingList.setCollectInactiveEmails(true);
        mailingList.setDailyLimitSentEmails(dailyLimitSentEmails);
        mailingList.setDailyLimitSentEmailsNumber(dailyLimitSentEmailsNumber);

        mailingListServiceMock = mock(MailingListService.class);
        when(mailingListServiceMock.getMailingLists(any())).thenReturn(Collections.emptyList());
        when(mailingListServiceMock.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingList));

        systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getSMSDomain()).thenReturn("domain.com");

        events = new ArrayList<>();
        for(int i = 0 ; i<eventsNumber;i++) {
            EventInstance eventInstance = EventTestUtils.createEventDataPointType(inactiveIntervalTime);
            events.add(eventInstance);
        }
    }

    @Before
    public void init() {

        this.channelTypeMock = mock(CommunicationChannelTypable.class);
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
        this.scheduledInactiveEventDAOMemory = new ScheduledExecuteInactiveEventDAOMemory();
        ScheduledExecuteInactiveEventService scheduledInactiveEventService =
                ScheduledExecuteInactiveEventService.newInstance(scheduledInactiveEventDAOMemory,
                        mailingListServiceMock);

        for(EventInstance event: events) {
            scheduledInactiveEventService.scheduleEvent(eventHandler, event);
        }
        EventDAOMemory eventDAOMemory = new EventDAOMemory(events, Arrays.asList(eventHandler));
        EventDAO eventDAOMock = mock(EventDAO.class);
        when(eventDAOMock.getAllStatusEvents(anySet())).thenAnswer(a ->
                eventDAOMemory.getAllStatusEvents((Set<Integer>)a.getArguments()[0]));
        when(eventDAOMock.getEventHandlers(anySet())).thenAnswer(a ->
                eventDAOMemory.getEventHandlers((Set<Integer>)a.getArguments()[0]));

        DataPointService dataPointServiceMock = mock(DataPointService.class);
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointServiceMock.getDataPoint(anyInt())).thenReturn(dataPointVO);

        DataSourceService dataSourceServiceMock = mock(DataSourceService.class);
        DataSourceVO dataSourceVO = mock(DataSourceVO.class);
        when(dataSourceServiceMock.getDataSource(anyInt())).thenReturn(dataSourceVO);

        inactiveEventsProvider = InactiveEventsProvider.newInstance(eventDAOMock, scheduledInactiveEventDAOMemory,
                channel, 600);

        this.testSubject = new ScheduledExecuteInactiveEventRT(scheduledInactiveEventService, inactiveEventsProvider,
                dataPointServiceMock, dataSourceServiceMock, 300);
    }

    @Test
    public void when_scheduleTimeout_for_sendMsg_success_then_verify_times_sendLimit() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(),
                anyString(), anyObject());
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendLimit(any(EventInstance.class), anySet(), anyString(), anyObject());


        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        //then:
        assertEquals(eventsNumber - invokeSendMsgTimes, result.size());
        assertEquals(result.size(), new HashSet<>(result).size());
        assertEquals(currentScheduledNumber, testSubject.getCurrentScheduledNumber());
        assertEquals(testSubject.getCurrentScheduledNumber(), testSubject.getCurrentExecutedNumber());

        //and then:
        verify(channelTypeMock, times(communicateLimitTimes)).sendLimit(any(EventInstance.class), anySet(), eq("Limit"), anyObject());
        verify(channelTypeMock, times(communicateLimitTimes)).sendLimit(any(EventInstance.class), eq(addresses),
                eq("Limit"), anyObject());
    }

    @Test
    public void when_scheduleTimeout_for_sendMsg_fail_then_verify_times_sendLimit() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workFail(new Exception("test exception"));
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(),
                anyString(), anyObject());
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workFail(new Exception("test exception"));
            return true;
        }).when(channelTypeMock).sendLimit(any(EventInstance.class), anySet(), anyString(), anyObject());



        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);
        //List<?> fromDatabase = scheduledInactiveEventDAOMemory.select(Integer.MAX_VALUE);

        //then:
        assertEquals(eventsNumber, result.size());
        assertEquals(result.size(), new HashSet<>(result).size());
        assertEquals(currentScheduledNumber, testSubject.getCurrentScheduledNumber());
        assertEquals(testSubject.getCurrentScheduledNumber(), testSubject.getCurrentExecutedNumber());

        //and then:
        verify(channelTypeMock, times(0)).sendLimit(any(EventInstance.class), anySet(), eq("Limit"), anyObject());
        verify(channelTypeMock, times(0)).sendLimit(any(EventInstance.class), eq(addresses),
                eq("Limit"), anyObject());
    }

    @Test
    public void when_scheduleTimeout_for_sendMsg_fail_then_verify_times_sendMsg() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        String alias = eventHandler.getAlias();
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workFail(new Exception("test exception"));
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(),
                anyString(), anyObject());

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);
        //List<?> fromDatabase = scheduledInactiveEventDAOMemory.select(Integer.MAX_VALUE);

        //then:
        assertEquals(eventsNumber, result.size());
        assertEquals(result.size(), new HashSet<>(result).size());
        assertEquals(currentScheduledNumber, testSubject.getCurrentScheduledNumber());
        assertEquals(testSubject.getCurrentScheduledNumber(), testSubject.getCurrentExecutedNumber());

        //and then:
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), anySet(), eq(alias), anyObject());
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), eq(addresses), eq(alias), anyObject());
    }

    @Test
    public void when_scheduleTimeout_for_sendMsg_success_then_verify_times_sendMsg() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        String alias = eventHandler.getAlias();
        doAnswer(a -> {
            ((AfterWork)a.getArguments()[3]).workSuccess();
            return true;
        }).when(channelTypeMock).sendMsg(any(EventInstance.class), anySet(),
                anyString(), anyObject());


        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        //then:
        assertEquals(eventsNumber - invokeSendMsgTimes, result.size());
        assertEquals(result.size(), new HashSet<>(result).size());
        assertEquals(currentScheduledNumber, testSubject.getCurrentScheduledNumber());
        assertEquals(testSubject.getCurrentScheduledNumber(), testSubject.getCurrentExecutedNumber());

        //and then:
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), anySet(), eq(alias), anyObject());
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), eq(addresses), eq(alias), anyObject());

    }

}
