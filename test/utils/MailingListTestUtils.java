package utils;

import com.serotonin.mango.util.IntervalUtil;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MailingListTestUtils {


    public static DateTime newDateTime(String date) {
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(date);
    }

    public static UserEntry createUserEntry(String userName, String tel1, String email1) {
        Random random = new Random();
        User user = new User();
        user.setId(random.nextInt());
        user.setPhone(tel1);
        user.setEmail(email1);
        user.setUsername("user_test");

        return new UserEntry(user.getId(), user);
    }

    public static List<AddressEntry> createAddressEntry(String... addresses) {

        List<AddressEntry> result = new ArrayList<>();
        for (String address: addresses) {
            AddressEntry addressEntry = new AddressEntry();
            addressEntry.setAddress(address);
            result.add(addressEntry);
        }
        return result;
    }

    public static MailingList createMailingList(EmailRecipient... emailRecipients) {
        MailingList mailingList = new MailingList();
        mailingList.setId(new Random().nextInt());
        mailingList.setEntries(Stream.of(emailRecipients).collect(Collectors.toList()));
        return mailingList;
    }

    public static MailingList createMailingListWithInactiveInterval(DateTime inactiveIntervalTime, boolean collectInactiveEmails, EmailRecipient... emailRecipients) {
        MailingList mailingList = new MailingList();
        mailingList.setId(new Random().nextInt());
        mailingList.setEntries(Stream.of(emailRecipients).collect(Collectors.toList()));
        mailingList.setCollectInactiveEmails(collectInactiveEmails);
        Integer inactiveInterval = IntervalUtil.getIntervalIdAt(inactiveIntervalTime);
        Set<Integer> inactiveIntervals = new HashSet<>();
        inactiveIntervals.add(inactiveInterval);
        mailingList.setInactiveIntervals(inactiveIntervals);
        return mailingList;
    }

    public static MailingList createMailingList(List<AddressEntry> addressEntries, EmailRecipient... emailRecipients) {
        MailingList mailingList = new MailingList();
        mailingList.setId(new Random().nextInt());
        List<EmailRecipient> entries = Stream.of(emailRecipients).collect(Collectors.toList());
        entries.addAll(addressEntries);
        mailingList.setEntries(entries);
        return mailingList;
    }

}
