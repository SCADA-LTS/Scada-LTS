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
import org.scada_lts.service.CommunicationChannel;
import org.scada_lts.service.CommunicationChannelTypable;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;
import utils.EventTestUtils;
import utils.MailingListTestUtils;
import utils.TestConcurrentUtils;

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
            " communicateLimitTimes: {4}, scheduledEventsNumber: {5}," +
            " numberOfLaunches: {6}")
    public static Collection data() {
        return Arrays.asList(new Object[][] {
                { 3, true, CommunicationChannelType.EMAIL, 3, 1, 100, 150},
                { 3, true, CommunicationChannelType.SMS, 3, 1, 100, 150},
                { 20, true, CommunicationChannelType.EMAIL, 10, 0, 10, 150},
                { 3, false, CommunicationChannelType.EMAIL, 100, 0, 100, 150},
                { 3, false, CommunicationChannelType.SMS, 100, 0, 100, 150},
                { 20, false, CommunicationChannelType.EMAIL, 10, 0, 10, 150},
        });
    }


    private ScheduledExecuteInactiveEventRT testSubject;

    private int invokeSendMsgTimes;
    private int communicateLimitTimes;
    private int scheduledEventsNumber;
    private int numberOfLaunches;

    private CommunicationChannelTypable channelType;
    private CommunicationChannel channel;
    private EventHandlerVO eventHandler;
    private MailingList mailingList;
    private DateTime inactiveIntervalTime;
    private ScheduledExecuteInactiveEventService service;

    private CommunicationChannelTypable channelTypeMock;
    private MailingListService mailingListServiceMock;


    public ScheduledExecuteInactiveEventRtMultiThreadTest(int dailyLimitSentEmailsNumber,
                                                          boolean dailyLimitSentEmails,
                                                          CommunicationChannelType type,
                                                          int invokeSendMsgTimes,
                                                          int communicateLimitTimes,
                                                          int scheduledEventsNumber,
                                                          int numberOfLaunches) {

        this.invokeSendMsgTimes = invokeSendMsgTimes;
        this.channelType = type;
        this.communicateLimitTimes = communicateLimitTimes;
        this.scheduledEventsNumber = scheduledEventsNumber;
        this.numberOfLaunches = numberOfLaunches;

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
        this.channel = CommunicationChannel.newChannel(mailingList, channelTypeMock);

        EventDAO eventDAO = mock(EventDAO.class);
        Mockito.when(eventDAO.getAllStatusEvents(any())).thenReturn(Collections.emptyList());

        ScheduledExecuteInactiveEventDAO dao = mock(ScheduledExecuteInactiveEventDAO.class);
        Mockito.when(dao.select()).thenReturn(Collections.emptyList());

        this.service = ScheduledExecuteInactiveEventService.newInstance(eventDAO, dao, mailingListServiceMock);

        for(int i = 0 ; i<scheduledEventsNumber;i++) {
            this.service.scheduleEvent(eventHandler, EventTestUtils
                    .createScheduledEventWithMock(eventHandler.getId(), eventHandler.getAlias(),
                            channelTypeMock, inactiveIntervalTime).getEvent());
        }
        DataPointService dataPointServiceMock = mock(DataPointService.class);
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointServiceMock.getDataPoint(anyInt())).thenReturn(dataPointVO);

        DataSourceService dataSourceServiceMock = mock(DataSourceService.class);
        DataSourceVO dataSourceVO = mock(DataSourceVO.class);
        when(dataSourceServiceMock.getDataSource(anyInt())).thenReturn(dataSourceVO);

        this.testSubject = new ScheduledExecuteInactiveEventRT(channel, service, dataPointServiceMock,
                dataSourceServiceMock);
    }

    @Test
    public void when_scheduleTimeout_then_verify_times_sendMsg_limit() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(true);

        //when:
        TestConcurrentUtils.biConsumer(numberOfLaunches, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = service.getScheduledEvents(channel, Integer.MAX_VALUE);

        //then:
        assertEquals(scheduledEventsNumber - invokeSendMsgTimes, result.size());
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
        List<ScheduledEvent> result = service.getScheduledEvents(channel, Integer.MAX_VALUE);

        System.out.println("" + testSubject.getCurrentNumberExecuted());

        //then:
        assertTrue(testSubject.getCurrentNumberExecuted() <= invokeSendMsgTimes * numberOfLaunches);
        assertEquals(scheduledEventsNumber, result.size());
        verify(channelTypeMock, times(testSubject.getCurrentNumberExecuted())).sendMsg(any(EventInstance.class),
                anySet(), eq(alias));
        verify(channelTypeMock, times(testSubject.getCurrentNumberExecuted())).sendMsg(any(EventInstance.class),
                eq(addresses), eq(alias));

    }

    @Test
    public void when_scheduleTimeout_then_verify_times_sendMsg_() {

        //given:
        Set<String> addresses = channel.getAllAdresses();
        String alias = eventHandler.getAlias();
        when(channelTypeMock.sendMsg(any(EventInstance.class), anySet(), anyString())).thenReturn(true);

        //when:
        TestConcurrentUtils.biConsumer(numberOfLaunches, testSubject::scheduleTimeout, false, DateTime.now().getMillis());
        List<ScheduledEvent> result = service.getScheduledEvents(channel, Integer.MAX_VALUE);

        //then:
        assertEquals(scheduledEventsNumber - invokeSendMsgTimes, result.size());
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), anySet(), eq(alias));
        verify(channelTypeMock, times(invokeSendMsgTimes)).sendMsg(any(EventInstance.class), eq(addresses), eq(alias));
    }
}
