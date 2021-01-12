package com.serotonin.mango.rt.event;

import com.serotonin.mango.rt.event.schedule.ScheduledExecuteInactiveEventRT;
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
import org.mockito.Mockito;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelTypable;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.InactiveEventsProvider;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;
import utils.EventDAOMemory;
import utils.EventTestUtils;
import utils.MailingListTestUtils;
import utils.ScheduledExecuteInactiveEventDAOMemory;
import utils.TestConcurrentUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static utils.MailingListTestUtils.createAddressEntry;

@RunWith(Parameterized.class)
public class ScheduledExecuteInactiveEventRtMultiThreadTest {

    @Parameterized.Parameters(name= "{index}: dailyLimitSentEmailsNumber: {0}, " +
            "isDailyLimitSentEmails: {1}, CommunicationChannelType: {2}, invokeSendMsgTimes: {3}," +
            " communicateLimitTimes: {4}, scheduledEventsNumber: {5}, launchesNumber: {6}, " +
            "currentScheduledNumber: {7}")
    public static Collection data() {
        return Arrays.asList(new Object[][] {
                { 3, true, CommunicationChannelType.EMAIL, 3, 1, 300, 5, 3},
                { 3, true, CommunicationChannelType.SMS, 3, 1, 300, 5, 3},
                { 20, true, CommunicationChannelType.EMAIL, 10, 0, 10, 5, 10},
                { 3, false, CommunicationChannelType.EMAIL, 300, 0, 300, 5, 300},
                { 3, false, CommunicationChannelType.SMS, 300, 0, 300, 5, 300},
                { 20, false, CommunicationChannelType.EMAIL, 10, 0, 10, 5, 10},
        });
    }


    private ScheduledExecuteInactiveEventRT testSubject;

    private int invokeSendMsgTimes;
    private int communicateLimitTimes;
    private int scheduledEventsNumber;
    private int launchesNumber;
    private int currentScheduledNumber;

    private CommunicationChannelTypable channelType;
    private CommunicationChannel channel;
    private EventHandlerVO eventHandler;
    private MailingList mailingList;
    private InactiveEventsProvider inactiveEventsProvider;

    private CommunicationChannelTypable channelTypeMock;
    private MailingListService mailingListServiceMock;
    private SystemSettingsService systemSettingsServiceMock;

    private List<EventInstance> events;


    public ScheduledExecuteInactiveEventRtMultiThreadTest(int dailyLimitSentEmailsNumber,
                                                          boolean dailyLimitSentEmails,
                                                          CommunicationChannelType type,
                                                          int invokeSendMsgTimes,
                                                          int communicateLimitTimes,
                                                          int scheduledEventsNumber,
                                                          int launchesNumber,
                                                          int currentScheduledNumber) {

        this.invokeSendMsgTimes = invokeSendMsgTimes;
        this.channelType = type;
        this.communicateLimitTimes = communicateLimitTimes;
        this.scheduledEventsNumber = scheduledEventsNumber;
        this.launchesNumber = launchesNumber;
        this.currentScheduledNumber = currentScheduledNumber;

        eventHandler = new EventHandlerVO();
        eventHandler.setId(123);
        eventHandler.setAlias("event-handler-alias-test");
        eventHandler.setHandlerType(channelType.getEventHandlerType());

        DateTime inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");

        List<AddressEntry> addressEntries1 = createAddressEntry("test@test.com", "111111111");
        mailingList = MailingListTestUtils.createMailingList(1, inactiveIntervalTime,
                addressEntries1.toArray(new AddressEntry[]{}));
        mailingList.setCollectInactiveEmails(true);
        mailingList.setDailyLimitSentEmails(dailyLimitSentEmails);
        mailingList.setDailyLimitSentEmailsNumber(dailyLimitSentEmailsNumber);

        mailingListServiceMock = mock(MailingListService.class);
        Mockito.when(mailingListServiceMock.getMailingLists(any())).thenReturn(Collections.emptyList());
        Mockito.when(mailingListServiceMock.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingList));

        systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getSMSDomain()).thenReturn("domain.com");

        events = new ArrayList<>();
        for(int i = 0 ; i<scheduledEventsNumber;i++) {
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

        ScheduledExecuteInactiveEventDAO scheduledInactiveEventDAOMemory = new ScheduledExecuteInactiveEventDAOMemory();

        ScheduledExecuteInactiveEventService scheduledInactiveEventService = ScheduledExecuteInactiveEventService
                .newInstance(scheduledInactiveEventDAOMemory, mailingListServiceMock);

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
                dataPointServiceMock, dataSourceServiceMock);
    }

    @Test
    public void when_scheduleTimeout_for_sendMsg_return_true_then_verify_times_sendMsg_limit() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(true);
        when(channelTypeMock.sendLimit(any(EventInstance.class), anySet(), anyString())).thenReturn(true);

        //when:
        TestConcurrentUtils.biConsumer(launchesNumber, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        //then:
        assertEquals(scheduledEventsNumber - invokeSendMsgTimes, result.size());
        assertEquals(currentScheduledNumber, testSubject.getCurrentScheduledNumber());
        assertEquals(testSubject.getCurrentScheduledNumber(), testSubject.getCurrentExecutedNumber());

        //and then:
        verify(channelTypeMock, times(communicateLimitTimes)).sendLimit(any(EventInstance.class), anySet(),
                eq("Limit"));
        verify(channelTypeMock, times(communicateLimitTimes)).sendLimit(any(EventInstance.class), eq(addresses),
                eq("Limit"));
    }

    @Test
    public void when_scheduleTimeout_for_sendMsg_return_false_then_verify_times_sendMsg_limit() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(false);

        //when:
        TestConcurrentUtils.biConsumer(launchesNumber, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        //then:
        assertEquals(scheduledEventsNumber, result.size());
        assertTrue(testSubject.getCurrentScheduledNumber() <= invokeSendMsgTimes * launchesNumber);
        assertEquals(testSubject.getCurrentScheduledNumber(), testSubject.getCurrentExecutedNumber());

        //and then:
        verify(channelTypeMock, times(0)).sendLimit(any(EventInstance.class), anySet(),
                eq("Limit"));
        verify(channelTypeMock, times(0)).sendLimit(any(EventInstance.class), eq(addresses),
                eq("Limit"));
    }

    @Test
    public void when_scheduleTimeout_for_sendMsg_return_false_then_verify_times_sendMsg() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        String alias = eventHandler.getAlias();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(false);

        //when:
        TestConcurrentUtils.biConsumer(launchesNumber, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        //then:
        assertEquals(scheduledEventsNumber, result.size());
        assertTrue(testSubject.getCurrentScheduledNumber() <= invokeSendMsgTimes * launchesNumber);
        assertEquals(testSubject.getCurrentScheduledNumber(), testSubject.getCurrentExecutedNumber());

        //and then:
        verify(channelTypeMock, times(testSubject.getCurrentExecutedNumber())).sendMsg(any(EventInstance.class),
                anySet(), eq(alias));
        verify(channelTypeMock, times(testSubject.getCurrentExecutedNumber())).sendMsg(any(EventInstance.class),
                eq(addresses), eq(alias));
    }

    @Test
    public void when_scheduleTimeout_for_sendMsg_return_true_then_verify_times_sendMsg() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        String alias = eventHandler.getAlias();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(true);

        //when:
        TestConcurrentUtils.biConsumer(launchesNumber, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        //then:
        assertEquals(scheduledEventsNumber - invokeSendMsgTimes, result.size());
        assertEquals(currentScheduledNumber, testSubject.getCurrentScheduledNumber());
        assertEquals(testSubject.getCurrentScheduledNumber(), testSubject.getCurrentExecutedNumber());

        //and then:
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), anySet(), eq(alias));
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), eq(addresses), eq(alias));
    }
}
