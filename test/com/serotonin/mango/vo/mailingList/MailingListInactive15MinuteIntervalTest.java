package com.serotonin.mango.vo.mailingList;

import com.serotonin.mango.util.IntervalUtil;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static utils.MailingListTestUtils.createAddressEntry;
import static utils.MailingListTestUtils.createMailingList;
import static utils.MailingListTestUtils.createUserEntry;

public class MailingListInactive15MinuteIntervalTest {

    private MailingList testSuject;
    private Set<String> addressesExpected;

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

        testSuject = createMailingList(1, addressEntries, user1, user2, user3, user4);

        addressesExpected = new HashSet<>();
        addressesExpected.add(email1);
        addressesExpected.add(email2);
        addressesExpected.add(email3);
        addressesExpected.add(email4);
        addressesExpected.add(badEmail1);
        addressesExpected.add(badEmail2);
        addressesExpected.add(badEmail3);
        addressesExpected.add(tel3);
        addressesExpected.add(tel4);
        addressesExpected.add(badTel2);
        addressesExpected.add(badTel3);
    }

    @Test
    public void test_appendAddresses_without_inactive_intervals_then_not_empty_list() {

        //given:
        DateTime activeDate = DateTime.parse("2020-12-13T21:40:00.618-08:00");
        Set<String> addresses = new HashSet<>();

        //when
        testSuject.appendAddresses(addresses, activeDate);

        //then:
        assertEquals(addressesExpected, addresses);
    }

    @Test
    public void test_appendAddresses_with_inactive_interval_and_time_in_interval_then_empty_list() {

        //given:
        DateTime timeForInactiveInterval = DateTime.parse("2020-12-13T21:44:59.618-08:00");

        DateTime timeForInactiveInterval1 = DateTime.parse("2020-12-13T21:30:00.618-08:00");
        Integer inactiveInterval = IntervalUtil.getIntervalIdAt(timeForInactiveInterval1);

        Set<Integer> inactiveIntervals = new HashSet<>();
        inactiveIntervals.add(inactiveInterval);
        testSuject.setInactiveIntervals(inactiveIntervals);

        Set<String> addresses = new HashSet<>();

        //when
        testSuject.appendAddresses(addresses, timeForInactiveInterval);

        //then:
        assertEquals(Collections.emptySet(), addresses);
    }

    @Test
    public void test_appendAddresses_with_inactive_interval_and_active_time_after_interval_then_not_empty_list() {

        //given:
        Set<Integer> inactiveIntervals = new HashSet<>();

        DateTime timeForActiveInterval = DateTime.parse("2020-12-13T21:45:00.618-08:00");
        DateTime timeForInactiveInterval = DateTime.parse("2020-12-13T21:30:00.618-08:00");
        Integer inactiveInterval = IntervalUtil.getIntervalIdAt(timeForInactiveInterval);
        inactiveIntervals.add(inactiveInterval);
        testSuject.setInactiveIntervals(inactiveIntervals);

        Set<String> addresses = new HashSet<>();

        //when
        testSuject.appendAddresses(addresses, timeForActiveInterval);

        //then:
        assertEquals(addressesExpected, addresses);
    }

    @Test
    public void test_appendAddresses_with_inactive_interval_and_active_time_before_interval_then_not_empty_list() {

        //given:
        Set<Integer> inactiveIntervals = new HashSet<>();

        DateTime timeForActiveInterval = DateTime.parse("2020-12-13T21:15:00.618-08:00");
        DateTime timeForInactiveInterval = DateTime.parse("2020-12-13T21:30:00.618-08:00");
        Integer inactiveInterval = IntervalUtil.getIntervalIdAt(timeForInactiveInterval);
        inactiveIntervals.add(inactiveInterval);
        testSuject.setInactiveIntervals(inactiveIntervals);

        Set<String> addresses = new HashSet<>();

        //when
        testSuject.appendAddresses(addresses, timeForActiveInterval);

        //then:
        assertEquals(addressesExpected, addresses);
    }

    @Test
    public void test_appendAllAddresses_without_inactive_intervals_then_not_empty_list() {

        //given:
        Set<String> addresses = new HashSet<>();

        //when
        testSuject.appendAllAddresses(addresses);

        //then:
        assertEquals(addressesExpected, addresses);
    }

    @Test
    public void test_appendAllAddresses_with_inactive_intervals_then_not_empty_list() {

        //given:
        DateTime date1 = DateTime.parse("2020-12-13T21:30:00.618-08:00");
        DateTime date2 = DateTime.parse("2020-12-13T21:40:00.618-08:00");
        Integer inactiveInterval1 = IntervalUtil.getIntervalIdAt(date1);
        Integer inactiveInterval2 = IntervalUtil.getIntervalIdAt(date2);

        Set<Integer> inactiveIntervals = new HashSet<>();
        inactiveIntervals.add(inactiveInterval1);
        inactiveIntervals.add(inactiveInterval2);

        testSuject.setInactiveIntervals(inactiveIntervals);

        Set<String> addresses = new HashSet<>();

        //when
        testSuject.appendAllAddresses(addresses);

        //then:
        assertEquals(addressesExpected, addresses);
    }
}