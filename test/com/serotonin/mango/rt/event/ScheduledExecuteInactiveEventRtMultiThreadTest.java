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
import org.scada_lts.service.*;
import utils.*;

import java.util.*;

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
            " communicateLimitTimes: {4}, scheduledEventsNumber: {5}, numberOfLaunches: {6}, " +
            "currentNumberScheduled: {7}")
    public static Collection data() {
        return Arrays.asList(new Object[][] {
                { 3, true, CommunicationChannelType.EMAIL, 3, 1, 100, 111, 3},
                { 3, true, CommunicationChannelType.SMS, 3, 1, 100, 111, 3},
                { 20, true, CommunicationChannelType.EMAIL, 10, 0, 10, 111, 10},
                { 3, false, CommunicationChannelType.EMAIL, 100, 0, 100, 111, 100},
                { 3, false, CommunicationChannelType.SMS, 100, 0, 100, 111, 100},
                { 20, false, CommunicationChannelType.EMAIL, 10, 0, 10, 111, 10},
        });
    }


    private ScheduledExecuteInactiveEventRT testSubject;

    private int invokeSendMsgTimes;
    private int communicateLimitTimes;
    private int scheduledEventsNumber;
    private int numberOfLaunches;
    private int currentNumberScheduled;

    private CommunicationChannelTypable channelType;
    private CommunicationChannel channel;
    private EventHandlerVO eventHandler;
    private MailingList mailingList;
    private DateTime inactiveIntervalTime;
    private ScheduledExecuteInactiveEventService service;
    private InactiveEventsProvider inactiveEventsProvider;

    private CommunicationChannelTypable channelTypeMock;
    private MailingListService mailingListServiceMock;
    private SystemSettingsService systemSettingsServiceMock;


    public ScheduledExecuteInactiveEventRtMultiThreadTest(int dailyLimitSentEmailsNumber,
                                                          boolean dailyLimitSentEmails,
                                                          CommunicationChannelType type,
                                                          int invokeSendMsgTimes,
                                                          int communicateLimitTimes,
                                                          int scheduledEventsNumber,
                                                          int numberOfLaunches,
                                                          int currentNumberScheduled) {

        this.invokeSendMsgTimes = invokeSendMsgTimes;
        this.channelType = type;
        this.communicateLimitTimes = communicateLimitTimes;
        this.scheduledEventsNumber = scheduledEventsNumber;
        this.numberOfLaunches = numberOfLaunches;
        this.currentNumberScheduled = currentNumberScheduled;

        eventHandler = new EventHandlerVO();
        eventHandler.setId(123);
        eventHandler.setAlias("event-handler-alias-test");
        eventHandler.setHandlerType(channelType.getEventHandlerType());

        inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");

        List<AddressEntry> addressEntries1 = createAddressEntry("test@test.com", "111111111");
        mailingList = MailingListTestUtils.createMailingList(1, inactiveIntervalTime, addressEntries1.toArray(new AddressEntry[]{}));
        mailingList.setCollectInactiveEmails(true);
        mailingList.setDailyLimitSentEmails(dailyLimitSentEmails);
        mailingList.setDailyLimitSentEmailsNumber(dailyLimitSentEmailsNumber);

        mailingListServiceMock = mock(MailingListService.class);
        Mockito.when(mailingListServiceMock.getMailingLists(any())).thenReturn(Collections.emptyList());
        Mockito.when(mailingListServiceMock.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingList));

        systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getSMSDomain()).thenReturn("domain.com");
    }

    @Before
    public void init() {

        this.channelTypeMock = mock(CommunicationChannelTypable.class);
        when(channelTypeMock.sendMsg(any(), anySet(), anyString())).thenReturn(true);
        when(channelTypeMock.getEventHandlerType()).thenReturn(channelType.getEventHandlerType());
        when(channelTypeMock.validateAddress(anyString())).thenAnswer(a -> {
            Object[] args = a.getArguments();
            return channelType.validateAddress((String)args[0]);
        });
        this.channel = CommunicationChannel.newChannel(mailingList, channelTypeMock, systemSettingsServiceMock);

        ScheduledExecuteInactiveEventDAO dao = new ScheduledExecuteInactiveEventDAOMock();

        this.service = ScheduledExecuteInactiveEventService.newInstance(dao, mailingListServiceMock);

        List<EventInstance> eventInstances = new ArrayList<>();
        for(int i = 0 ; i<scheduledEventsNumber;i++) {
            EventInstance eventInstance = EventTestUtils
                    .createScheduledEventWithMock(eventHandler.getId(), eventHandler.getAlias(),
                            channelTypeMock, inactiveIntervalTime).getEvent();
            eventInstances.add(eventInstance);
            this.service.scheduleEvent(eventHandler, eventInstance);
        }
        EventDAOMock eventDAOMock = new EventDAOMock(eventInstances, Arrays.asList(eventHandler));
        EventDAO eventDAO = mock(EventDAO.class);
        when(eventDAO.getAllStatusEvents(anySet())).thenAnswer(a ->
                eventDAOMock.getAllStatusEvents((Set<Integer>)a.getArguments()[0]));
        when(eventDAO.getEventHandlers(anySet())).thenAnswer(a ->
                eventDAOMock.getEventHandlers((Set<Integer>)a.getArguments()[0]));

        DataPointService dataPointServiceMock = mock(DataPointService.class);
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointServiceMock.getDataPoint(anyInt())).thenReturn(dataPointVO);

        DataSourceService dataSourceServiceMock = mock(DataSourceService.class);
        DataSourceVO dataSourceVO = mock(DataSourceVO.class);
        when(dataSourceServiceMock.getDataSource(anyInt())).thenReturn(dataSourceVO);

        inactiveEventsProvider = InactiveEventsProvider.newInstance(eventDAO, dao, channel);

        this.testSubject = new ScheduledExecuteInactiveEventRT(service, inactiveEventsProvider,
                dataPointServiceMock, dataSourceServiceMock);
    }

    @Test
    public void when_scheduleTimeout_then_verify_times_sendMsg_limit() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(true);

        //when:
        TestConcurrentUtils.biConsumer(numberOfLaunches, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        //then:
        assertEquals(scheduledEventsNumber - invokeSendMsgTimes, result.size());
        assertEquals(currentNumberScheduled, testSubject.getCurrentNumberScheduled());
        assertEquals(testSubject.getCurrentNumberScheduled(), testSubject.getCurrentNumberExecuted());

        //and then:
        verify(channelTypeMock, times(communicateLimitTimes)).sendMsg(any(EventInstance.class), anySet(), eq("Limit"));
        verify(channelTypeMock, times(communicateLimitTimes)).sendMsg(any(EventInstance.class), eq(addresses),
                eq("Limit"));
    }

    @Test
    public void when_scheduleTimeout_then_verify_times_sendMsg() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        String alias = eventHandler.getAlias();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(false);

        //when:
        TestConcurrentUtils.biConsumer(numberOfLaunches, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        System.out.println("when_scheduleTimeout_then_verify_times_sendMsg:" + testSubject.getCurrentNumberExecuted());

        //then:
        assertEquals(scheduledEventsNumber, result.size());
        assertTrue(testSubject.getCurrentNumberScheduled() <= invokeSendMsgTimes * numberOfLaunches);
        assertEquals(testSubject.getCurrentNumberScheduled(), testSubject.getCurrentNumberExecuted());

        //and then:
        verify(channelTypeMock, times(testSubject.getCurrentNumberExecuted())).sendMsg(any(EventInstance.class),
                anySet(), eq(alias));
        verify(channelTypeMock, times(testSubject.getCurrentNumberExecuted())).sendMsg(any(EventInstance.class),
                eq(addresses), eq(alias));

        verify(channelTypeMock, times(0)).sendMsg(any(EventInstance.class), anySet(), eq("Limit"));
    }

    @Test
    public void when_scheduleTimeout_then_verify_times_sendMsg_() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        String alias = eventHandler.getAlias();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(true);

        //when:
        TestConcurrentUtils.biConsumer(numberOfLaunches, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = inactiveEventsProvider.getScheduledEvents(Integer.MAX_VALUE);

        //then:
        assertEquals(scheduledEventsNumber - invokeSendMsgTimes, result.size());
        assertEquals(currentNumberScheduled, testSubject.getCurrentNumberScheduled());
        assertEquals(testSubject.getCurrentNumberScheduled(), testSubject.getCurrentNumberExecuted());

        //and then:
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), anySet(), eq(alias));
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), eq(addresses), eq(alias));
    }
}
