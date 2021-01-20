package com.serotonin.mango.vo.mailingList;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MailingListInactive15MinuteIntervalTest.class,
        MailingListInactive15MinuteIntervalWithEmailTest.class,
        MailingListInactive15MinuteIntervalWithSmsTest.class
})
public class MailingListInactive15MinuteIntervalTestsSuite {
}
