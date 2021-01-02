package org.scada_lts.service;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.util.IntervalUtil;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.EventTestUtils;

import java.util.*;

import static org.junit.Assert.*;
import static utils.MailingListTestUtils.createAddressEntry;
import static utils.MailingListTestUtils.createMailingList;
import static utils.MailingListTestUtils.createUserEntry;

@RunWith(Parameterized.class)
public class CommunicationChannelTest {

    @Parameterized.Parameters(name= "{index}: CommunicationChannelType: {0}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                { CommunicationChannelType.EMAIL},
                { CommunicationChannelType.SMS},
        });
    }

    private CommunicationChannel channelSubject;
    private MailingList compare;
    private CommunicationChannelType type;

    public CommunicationChannelTest(CommunicationChannelType type) {
        this.type = type;
    }

    @Before
    public void config() {

        String tel1 = "111111111";
        String tel2 = "222222222";
        String tel3 = "333333333";
        String tel4 = "444444444";
        String badTel1 = "555";
        String badTel2 = "666666666666";
        String badTel3 = "7777";

        String email1 = "test1@test.com";
        String email2 = "test2@test.com";
        String email3 = "test3@test.com";
        String email4 = "test4@test.com";
        String badEmail1 = "test1test.com";
        String badEmail2 = "test2test.com";
        String badEmail3 = "test3@testcom";

        UserEntry user1 = createUserEntry("Sam", tel1, email1);
        UserEntry user2 = createUserEntry("Eryk", tel2, email2);
        UserEntry user3 = createUserEntry("Josh", badTel1, email2);
        UserEntry user4 = createUserEntry("John", badTel1, badEmail1);

        List<AddressEntry> addressEntries = createAddressEntry(tel3, email4,
                email3, tel4, badTel2, badTel3, badEmail2, badEmail3);

        compare = createMailingList(1, addressEntries, user1, user2, user3, user4);
        channelSubject = CommunicationChannel.newChannel(compare, type);
    }

    @Test
    public void when_getActiveAdresses_without_inactive_intervals_then_not_empty_list() {

        //given:
        DateTime activeDate = DateTime.parse("2020-12-13T21:40:00.618-08:00");
        Set<String> addressesExpected = new HashSet<>();
        compare.appendAddresses(addressesExpected, activeDate, type);

        //when
        Set<String> result = channelSubject.getActiveAdresses(activeDate);

        //then:
        assertEquals(addressesExpected, result);
    }

    @Test
    public void when_getActiveAdresses_with_inactive_interval_and_time_in_interval_then_empty_list() {

        //given:
        DateTime timeInInactiveInterval = DateTime.parse("2020-12-13T21:44:59.618-08:00");

        DateTime timeForInactiveInverval = DateTime.parse("2020-12-13T21:30:00.618-08:00");
        Integer inactiveInterval = IntervalUtil.getIntervalIdAt(timeForInactiveInverval);

        Set<Integer> inactiveIntervals = new HashSet<>();
        inactiveIntervals.add(inactiveInterval);
        compare.setInactiveIntervals(inactiveIntervals);
        
        Set<String> addressesExpected = new HashSet<>();
        compare.appendAddresses(addressesExpected, timeInInactiveInterval, type);
        channelSubject = CommunicationChannel.newChannel(compare, type);
        
        //when:
        Set<String> result = channelSubject.getActiveAdresses(timeInInactiveInterval);

        //then:
        assertEquals(addressesExpected, result);
    }

    @Test
    public void when_getActiveAdresses_with_inactive_interval_and_active_time_after_interval_then_not_empty_list() {

        //given:
        Set<Integer> inactiveIntervals = new HashSet<>();

        DateTime activeDate = DateTime.parse("2020-12-13T21:45:01.618-08:00");
        DateTime inactiveDate = DateTime.parse("2020-12-13T21:30:00.618-08:00");
        Integer inactiveInterval = IntervalUtil.getIntervalIdAt(inactiveDate);
        inactiveIntervals.add(inactiveInterval);
        compare.setInactiveIntervals(inactiveIntervals);

        Set<String> addressesExpected = new HashSet<>();
        compare.appendAddresses(addressesExpected, activeDate, type);
        channelSubject = CommunicationChannel.newChannel(compare, type);

        //when:
        Set<String> result = channelSubject.getActiveAdresses(activeDate);

        //then:
        assertEquals(addressesExpected, result);
    }

    @Test
    public void when_getActiveAdresses_with_inactive_interval_and_active_time_before_interval_then_not_empty_list() {

        //given:
        Set<Integer> inactiveIntervals = new HashSet<>();

        DateTime activeDate = DateTime.parse("2020-12-13T21:15:00.618-08:00");
        DateTime inactiveDate = DateTime.parse("2020-12-13T21:30:00.618-08:00");
        Integer inactiveInterval = IntervalUtil.getIntervalIdAt(inactiveDate);
        inactiveIntervals.add(inactiveInterval);
        compare.setInactiveIntervals(inactiveIntervals);

        Set<String> addressesExpected = new HashSet<>();
        compare.appendAddresses(addressesExpected, activeDate, type);
        channelSubject = CommunicationChannel.newChannel(compare, type);

        //when:
        Set<String> result = channelSubject.getActiveAdresses(activeDate);

        //then:
        assertEquals(addressesExpected, result);
    }

    @Test
    public void when_getAllAdresses_without_inactive_intervals_then_not_empty_list() {

        //given:
        Set<String> addressesExpected = new HashSet<>();
        compare.appendAllAddresses(addressesExpected, type);
        
        //when:
        Set<String> result = channelSubject.getAllAdresses();

        //then:
        assertEquals(addressesExpected, result);
    }

    @Test
    public void when_getAllAdresses_with_inactive_intervals_then_not_empty_list() {

        //given:
        DateTime date1 = DateTime.parse("2020-12-13T21:30:00.618-08:00");
        DateTime date2 = DateTime.parse("2020-12-13T21:40:00.618-08:00");
        Integer inactiveInterval1 = IntervalUtil.getIntervalIdAt(date1);
        Integer inactiveInterval2 = IntervalUtil.getIntervalIdAt(date2);

        Set<Integer> inactiveIntervals = new HashSet<>();
        inactiveIntervals.add(inactiveInterval1);
        inactiveIntervals.add(inactiveInterval2);

        compare.setInactiveIntervals(inactiveIntervals);

        Set<String> addressesExpected = new HashSet<>();
        compare.appendAllAddresses(addressesExpected, type);
        channelSubject = CommunicationChannel.newChannel(compare, type);

        //when:
        Set<String> result = channelSubject.getAllAdresses();

        //then:
        assertEquals(addressesExpected, result);
    }

    @Test
    public void when_getChannelId_then_equals() {

        //then:
        assertEquals(compare.getId(), channelSubject.getChannelId());
    }

    @Test
    public void when_getType_then_equals() {

        //then:
        assertEquals(type, channelSubject.getType());
    }

    @Test
    public void when_getDailyLimitSentNumber_then_equals() {

        //then:
        assertEquals(compare.getDailyLimitSentEmailsNumber(), channelSubject.getDailyLimitSentNumber());
    }

    @Test
    public void when_isDailyLimitSent_then_equals() {

        //then:
        assertEquals(compare.isDailyLimitSentEmails(), channelSubject.isDailyLimitSent());
    }

    @Test
    public void when_getSendingActivationCron_then_equals() {

        //then:
        assertEquals(compare.getCronPattern(), channelSubject.getSendingActivationCron());
    }

    @Test
    public void when_isActiveFor_DateTime_then_equals() {

        DateTime dateTime = DateTime.now();

        //then:
        assertEquals(compare.isActive(dateTime), channelSubject.isActiveFor(dateTime));
    }

    @Test
    public void when_isActiveFor_Event_then_equals() {

        DateTime dateTime = DateTime.now();
        EventInstance event = EventTestUtils.createEventCriticalWithActiveTimeAndDataPointEventType(1, dateTime);

        //then:
        assertEquals(compare.isActive(event), channelSubject.isActiveFor(event));
    }

    @Test
    public void when_isCollectInactiveEvents_then_equals() {

        //then:
        assertEquals(compare.isCollectInactiveEmails(), channelSubject.isCollectInactiveEvents());
    }

    @Test
    public void when_getData_then_equals() {

        //then:
        assertEquals(compare, channelSubject.getData());
    }
}