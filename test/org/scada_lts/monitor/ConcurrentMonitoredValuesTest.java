package org.scada_lts.monitor;

import org.junit.Test;
import org.scada_lts.monitor.type.ObjectMonitor;
import utils.TestConcurrentUtils;


public class ConcurrentMonitoredValuesTest {

    @Test
    public void when_addIfMissingStatMonitor_in_ConcurrentMonitoredValues_then_non_exception() throws Throwable {
        //given:
        IMonitoredValues concurrentMonitoredValues = new ConcurrentMonitoredValues();

        //when
        TestConcurrentUtils.functionCheck(16, concurrentMonitoredValues::addIfMissingStatMonitor,
                new ObjectMonitor<>("id", "name"));
    }

}