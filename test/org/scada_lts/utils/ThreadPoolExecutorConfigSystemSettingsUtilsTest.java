package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.web.ContextWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.config.ThreadPoolExecutorConfig;

import javax.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ThreadPoolExecutorConfigSystemSettingsUtilsTest {


    @Parameterized.Parameters(name = "{index}: priority: {0}, blockingQueueInterfaceImpl: {1}, corePoolSize: {2}, maximumPoolSize: {3}, keepAliveTime: {4}, timeUnitEnumValue: {5}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();
        datas.add(new Object[] {ThreadPoolExecutorConfig.Priority.LOW, "java.util.concurrent.LinkedBlockingQueue", 1, 1, 0L, "MILLISECONDS", new Object[]{3, 4L}});
        datas.add(new Object[] {ThreadPoolExecutorConfig.Priority.MEDIUM, "java.util.concurrent.LinkedBlockingQueue", 3, 100, 60L, "SECONDS", new Object[]{"abc", true}});
        datas.add(new Object[] {ThreadPoolExecutorConfig.Priority.HIGH, "java.util.concurrent.SynchronousQueue", 0, 1000, 30L, "SECONDS", new Object[]{}});
        return datas;
    }

    private final ThreadPoolExecutorConfig.Priority priority;
    private final String blockingQueueInterfaceImpl;
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final  long keepAliveTime;
    private final String timeUnitEnumValue;
    private final Object[] args;

    public ThreadPoolExecutorConfigSystemSettingsUtilsTest(ThreadPoolExecutorConfig.Priority priority, String blockingQueueInterfaceImpl, int corePoolSize, int maximumPoolSize, long keepAliveTime, String timeUnitEnumValue, Object[] args) {
        this.priority = priority;
        this.blockingQueueInterfaceImpl = blockingQueueInterfaceImpl;
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnitEnumValue = timeUnitEnumValue;
        this.args = args;
    }

    @Before
    public void config() {
        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getRealPath("")).thenReturn("test/");
        when(contextWrapper.getServletContext()).thenReturn(servletContext);
        Common.ctx = contextWrapper;
    }

    @Test
    public void when_getThreadExecutorCorePoolSize_then_() {

        //when:
        int result = SystemSettingsUtils.getThreadExecutorCorePoolSize(priority);

        //then:
        assertEquals(corePoolSize, result);
    }

    @Test
    public void when_getThreadExecutorBlockingQueueInterfaceImpl_then_() {

        //when:
        String result = SystemSettingsUtils.getThreadExecutorBlockingQueueInterfaceImpl(priority);

        //then:
        assertEquals(blockingQueueInterfaceImpl, result);
    }

    @Test
    public void when_getThreadExecutorMaximumPoolSize_then_() {

        //when:
        int result = SystemSettingsUtils.getThreadExecutorMaximumPoolSize(priority);

        //then:
        assertEquals(maximumPoolSize, result);
    }

    @Test
    public void when_getThreadExecutorKeepAliveTime_then_() {

        //when:
        long result = SystemSettingsUtils.getThreadExecutorKeepAliveTime(priority);

        //then:
        assertEquals(keepAliveTime, result);
    }

    @Test
    public void when_getThreadExecutorTimeUnitEnumValue_then_() {

        //when:
        String result = SystemSettingsUtils.getThreadExecutorTimeUnitEnumValue(priority);

        //then:
        assertEquals(timeUnitEnumValue, result);
    }

    @Test
    public void when_getThreadExecutorBlockingQueueInterfaceImplArgs_then_() {

        //when:
        Object[] result = SystemSettingsUtils.getThreadExecutorBlockingQueueInterfaceImplArgs(priority);

        //then:
        assertArrayEquals(args, result);
    }

}