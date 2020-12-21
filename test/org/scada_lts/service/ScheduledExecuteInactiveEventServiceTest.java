package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.ScheduledEvent;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import com.serotonin.web.i18n.LocalizableMessage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.ScheduledExecuteInactiveEventDAO;
import org.scada_lts.mango.service.MailingListService;
import utils.EventTestUtils;
import utils.MailingListTestUtils;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static utils.MailingListTestUtils.createUserEntry;

public class ScheduledExecuteInactiveEventServiceTest {

    private ScheduledExecuteInactiveEventService testSubject;
    private MailingList mailingListWithInactiveInterval1;

    private MailingList mailingListWithInactiveInterval2;
    private ScheduledExecuteInactiveEventDAO dao;
    private DateTime inactiveIntervalTime;
    private EventHandlerVO eventHandler;
    private LocalizableMessage message;
    private CommunicationChannel channelSms;
    private CommunicationChannel channelEmail;

    @Before
    public void config() {

        eventHandler = new EventHandlerVO();
        eventHandler.setId(1);

        String tel1 = "111111111";
        String email1 = "test1@test.com";
        UserEntry user1 = createUserEntry("Ada", tel1, email1);

        String tel2 = "111111112";
        String email2 = "test2@test.com";
        UserEntry user2 = createUserEntry("Ewa", tel2, email2);

        inactiveIntervalTime = MailingListTestUtils.newDateTime("2020-12-13 20:30:00");
        mailingListWithInactiveInterval1 = MailingListTestUtils
                .createMailingListWithInactiveInterval(inactiveIntervalTime, true, user1);

        mailingListWithInactiveInterval2 = MailingListTestUtils
                .createMailingListWithInactiveInterval(inactiveIntervalTime, true, user2);


        MailingListService mailingListService = mock(MailingListService.class);
        when(mailingListService.getMailingLists(any())).thenReturn(Collections.emptyList());
        when(mailingListService.convertToMailingLists(any())).thenReturn(Arrays.asList(mailingListWithInactiveInterval1,
                mailingListWithInactiveInterval2));

        EventDAO eventDAO = mock(EventDAO.class);
        when(eventDAO.getAllStatusEvents(any())).thenReturn(Collections.emptyList());

        dao = mock(ScheduledExecuteInactiveEventDAO.class);
        when(dao.select()).thenReturn(Collections.emptyList());

        testSubject = ScheduledExecuteInactiveEventService.newInstance(eventDAO, dao, mailingListService);
        message = new LocalizableMessage("com.test");

        channelEmail = CommunicationChannel.newChannel(mailingListWithInactiveInterval1,CommunicationChannelType.EMAIL);
        channelSms = CommunicationChannel.newChannel(mailingListWithInactiveInterval2, CommunicationChannelType.SMS);


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
    public void test_scheduleEvent() {

        //given:
        EventHandlerVO eventHandler2 = new EventHandlerVO();
        eventHandler2.setId(2);
        eventHandler2.setHandlerType(EventHandlerVO.TYPE_SMS);

        EventHandlerVO eventHandler3 = new EventHandlerVO();
        eventHandler3.setId(3);
        eventHandler3.setHandlerType(EventHandlerVO.TYPE_SMS);

        EventInstance eventAsEmail1 = EventTestUtils.createEventWithActiveTime(1, inactiveIntervalTime, message);

        eventHandler.setHandlerType(EventHandlerVO.TYPE_EMAIL);
        ScheduledEvent scheduledEvent1 = new ScheduledEvent(eventAsEmail1,eventHandler);
        ScheduledEvent scheduledEvent2 = new ScheduledEvent(eventAsEmail1,eventHandler2);
        ScheduledEvent scheduledEvent3 = new ScheduledEvent(eventAsEmail1,eventHandler3);

        //when:
        testSubject.scheduleEvent(scheduledEvent1);
        testSubject.scheduleEvent(scheduledEvent2);

        //and:
        testSubject.scheduleEvent(scheduledEvent3);

        //and:
        List<ScheduledEvent> events = testSubject.getScheduledEvents(channelSms);

        //then:
        assertTrue(events.contains(scheduledEvent2));
        assertTrue(events.contains(scheduledEvent3));
    }

}