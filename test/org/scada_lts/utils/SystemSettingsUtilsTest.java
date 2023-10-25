package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.web.ContextWrapper;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SystemSettingsUtilsTest {

    @Before
    public void config() {
        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getRealPath("")).thenReturn("test/");
        when(contextWrapper.getServletContext()).thenReturn(servletContext);
        Common.ctx = contextWrapper;
    }

    @Test
    public void when_getDataPointSynchronizedMode_then_low() {

        //when:
        DataPointSyncMode result = SystemSettingsUtils.getDataPointSynchronizedMode();

        //then:
        assertEquals(DataPointSyncMode.LOW, result);
    }
}