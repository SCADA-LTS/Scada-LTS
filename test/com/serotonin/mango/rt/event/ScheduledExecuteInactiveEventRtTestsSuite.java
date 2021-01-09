package com.serotonin.mango.rt.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ScheduledExecuteInactiveEventRtUnscheduleEventTest.class,
        ScheduledExecuteInactiveEventRtTest.class,
        ScheduledExecuteInactiveEventRtOneThreadTest.class,
        ScheduledExecuteInactiveEventRtMultiThreadTest.class
})
public class ScheduledExecuteInactiveEventRtTestsSuite {
}
