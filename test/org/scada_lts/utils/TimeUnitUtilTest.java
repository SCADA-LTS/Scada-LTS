package org.scada_lts.utils;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TimeUnitUtilTest {

    @Test
    public void invoke_enumValueByName_for_TimeUnitEnum_and_SECONDS_then_TimeUnit_SECONDS() {
        //when:
        TimeUnit result = TimeUnitUtil
                .timeUnitByValueName("SECONDS")
                .orElse(null);
        //then:
        assertEquals(TimeUnit.SECONDS, result);
    }

    @Test
    public void invoke_enumValueByName_for_TimeUnitEnum_and_MILLISECONDS_then_TimeUnit_MILLISECONDS() {
        //when:
        TimeUnit result = TimeUnitUtil
                .timeUnitByValueName("MILLISECONDS")
                .orElse(null);
        //then:
        assertEquals(TimeUnit.MILLISECONDS, result);
    }

    @Test
    public void invoke_enumValueByName_for_TimeUnitEnum_and_MINUTES_then_TimeUnit_MINUTES() {
        //when:
        TimeUnit result = TimeUnitUtil
                .timeUnitByValueName("MINUTES")
                .orElse(null);
        //then:
        assertEquals(TimeUnit.MINUTES, result);
    }

    @Test
    public void invoke_enumValueByName_for_TimeUnitEnum_and_ANY_then_Optional_empty() {
        //when:
        Optional<TimeUnit> result = TimeUnitUtil
                .timeUnitByValueName("ANY");
        //then:
        assertEquals(Optional.empty(), result);
    }
}