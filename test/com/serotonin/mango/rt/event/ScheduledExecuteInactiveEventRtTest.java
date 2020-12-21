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
import utils.EventTestUtils;
import utils.MailingListTestUtils;

import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static utils.MailingListTestUtils.createAddressEntry;
import static utils.MailingListTestUtils.createUserEntry;

@RunWith(Parameterized.class)
public class ScheduledExecuteInactiveEventRtTest {

    @Parameterized.Parameters(name= "{index}: dailyLimitSentEmailsNumber: {0}, " +
            "isDailyLimitSentEmails: {1}, CommunicationChannelType: {2}, times: {3}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                { 1, true, CommunicationChannelType.EMAIL, 1},
                { 2, true, CommunicationChannelType.EMAIL, 2},
                { 3, true, CommunicationChannelType.EMAIL, 2},
                { 1, true, CommunicationChannelType.SMS, 1},
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
    private MailingList mailingList;
    private EventHandlerVO eventHandler;

    public ScheduledExecuteInactiveEventRtTest(int dailyLimitSentEmailsNumber,
                                               boolean dailyLimitSentEmails,
                                               CommunicationChannelType type,
                                               int times) {

        this.times = times;
        EventHandlerVO eventHandler = new EventHandlerVO();
        eventHandler.setHandlerType(type.getEventHandlerType());

        UserEntry user1 = createUserEntry("Ewa","111111111", "test1@test.com");
        List<AddressEntry> addressEntries1 = createAddressEntry("test2@test.com");

        mailingList = MailingListTestUtils.createMailingList(addressEntries1, user1);
        mailingList.setDailyLimitSentEmails(dailyLimitSentEmails);
        mailingList.setDailyLimitSentEmailsNumber(dailyLimitSentEmailsNumber);

        channel = CommunicationChannel.newChannel(mailingList, type);

        message = new LocalizableMessage("com.test");

        DataPointEventType type1 = mock(DataPointEventType.class);
        when(type1.getDataSourceId()).thenReturn(1);
        when(type1.getDataPointId()).thenReturn(2);
        when(type1.getReferenceId1()).thenReturn(2);
        when(type1.getReferenceId2()).thenReturn(3);

        EventInstance event1 = EventTestUtils.createEventWithActiveTime(1, DateTime.now(),type1);


        ScheduledEvent scheduledEvent1 = new ScheduledEvent(event1, eventHandler);

        DataPointEventType type2 = mock(DataPointEventType.class);
        when(type2.getDataPointId()).thenReturn(5);
        when(type2.getReferenceId1()).thenReturn(5);
        when(type2.getReferenceId2()).thenReturn(6);

        EventInstance event2 = EventTestUtils.createEventWithActiveTime(2, DateTime.now(),type2);

        ScheduledEvent scheduledEvent2 = new ScheduledEvent(event2, eventHandler);

        List<ScheduledEvent> events = new ArrayList<>();
        events.add(scheduledEvent1);
        events.add(scheduledEvent2);

        service = mock(ScheduledExecuteInactiveEventService.class);
        when(service.getScheduledEvents(mailingList)).thenReturn(events);

        eventManager = mock(EventManager.class);

        DataPointService dataPointService = mock(DataPointService.class);
        DataPointVO dataPointVO = mock(DataPointVO.class);
        when(dataPointService.getDataPoint(anyInt())).thenReturn(dataPointVO);

        DataSourceService dataSourceService = mock(DataSourceService.class);
        DataSourceVO dataSourceVO = mock(DataSourceVO.class);
        when(dataSourceService.getDataSource(anyInt())).thenReturn(dataSourceVO);

        testSubject = new ScheduledExecuteInactiveEventRT(mailingList,service,eventManager,
                dataPointService, dataSourceService);
    }


    @Test
    public void test_scheduleTimeout_then_verify_times_raiseEvent() {

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(eventManager, times(times)).raiseEvent(any(), anyLong(), anyBoolean(),
                anyInt(), eq(message), anyMap());
    }

    @Test
    public void test_scheduleTimeout_then_verify_times_unscheduleEvent() {

        //when:
        testSubject.scheduleTimeout(false, DateTime.now().getMillis());

        //then:
        verify(service, times(times)).unscheduleEvent(any(ScheduledEvent.class), eq(mailingList));
    }
}