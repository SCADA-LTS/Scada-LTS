package com.serotonin.mango.web.dwr;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.web.ContextWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doReturn;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataPointEditDwr.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class DataPointEditDwrTest {

    RuntimeManager runtimeManager;
    DataPointEditDwr dataPointEditDwr;

    @Before
    public void config() throws Exception {
        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        runtimeManager = mock(RuntimeManager.class);
        dataPointEditDwr = PowerMockito.spy(new DataPointEditDwr());
        Common.ctx = contextWrapper;
        when(contextWrapper.getRuntimeManager()).thenReturn(runtimeManager);
        doReturn(new DataPointVO(1)).when(dataPointEditDwr, "getDataPoint");
    }

    @Test
    public void when_purgeNowValuesLimit_for_limit_lower_than_2_then_return_0() {
        long result = dataPointEditDwr.purgeNowValuesLimit(1);

        assertEquals(0, result);
        verify(runtimeManager, never()).purgeDataPointValuesWithLimit(anyInt(), anyInt());
    }

    @Test
    public void when_purgeNowValuesLimit_for_limit_grater_than_2_then_return_10() {
        when(runtimeManager.purgeDataPointValuesWithLimit(anyInt(), anyInt())).thenReturn(10L);

        long result = dataPointEditDwr.purgeNowValuesLimit(100);

        assertEquals(10, result);
    }

    @Test
    public void when_purgeNowAll_then_purgeDataPointValues_method_called() {
        when(runtimeManager.purgeDataPointValues(anyInt())).thenReturn(10L);

        dataPointEditDwr.purgeNowAll();

        verify(runtimeManager).purgeDataPointValues(-1);
    }

    @Test
    public void when_purgeNowPeriod_for_valid_period_then_return_100() {
        when(runtimeManager.purgeDataPointValues(anyInt(), anyInt(), anyInt())).thenReturn(100L);

        long result = dataPointEditDwr.purgeNowPeriod(1, 1);

        assertEquals(100, result);
    }

    @Test
    public void when_purgeNowPeriod_for_invalid_purge_period_then_return_0() {
        when(runtimeManager.purgeDataPointValues(anyInt(), anyInt(), anyInt())).thenReturn(100L);

        long result = dataPointEditDwr.purgeNowPeriod(1, -1);

        assertEquals(0, result);
        verify(runtimeManager, never()).purgeDataPointValues(anyInt(), anyInt(), anyInt());
    }
}
