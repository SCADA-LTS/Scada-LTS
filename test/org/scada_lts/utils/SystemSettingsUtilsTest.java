package org.scada_lts.utils;

import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import org.junit.Test;

import static org.junit.Assert.*;

public class SystemSettingsUtilsTest {

    @Test
    public void when_getDataPointSynchronizedMode_then_medium() {

        //when:
        DataPointSyncMode result = SystemSettingsUtils.getDataPointSynchronizedMode();

        //then:
        assertEquals(DataPointSyncMode.MEDIUM, result);
    }
}