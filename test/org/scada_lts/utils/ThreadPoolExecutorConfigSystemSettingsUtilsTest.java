package org.scada_lts.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.AbstractBeforeAfterWorkItem;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import com.serotonin.mango.web.ContextWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.quartz.ItemsPerSecond;
import org.scada_lts.web.beans.ApplicationBeans;

import javax.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({ApplicationBeans.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class ThreadPoolExecutorConfigSystemSettingsUtilsTest {


    @Parameterized.Parameters(name = "{index}: priority: {0}, blockingQueueInterfaceImpl: {1}, corePoolSize: {2}, maximumPoolSize: {3}, keepAliveTime: {4}, timeUnitEnumValue: {5}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();
        datas.add(new Object[] {WorkItemPriority.LOW, "java.util.concurrent.LinkedBlockingQueue", 1, 1, 0L, "MILLISECONDS", new Object[]{3, 4L}});
        datas.add(new Object[] {WorkItemPriority.MEDIUM, "java.util.concurrent.LinkedBlockingQueue", 3, 100, 60L, "SECONDS", new Object[]{"abc", true}});
        datas.add(new Object[] {WorkItemPriority.HIGH, "java.util.concurrent.SynchronousQueue", 0, 1000, 30L, "SECONDS", new Object[]{}});
        return datas;
    }

    private final WorkItemPriority priority;
    private final String blockingQueueInterfaceImpl;
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final  long keepAliveTime;
    private final String timeUnitEnumValue;
    private final Object[] args;

    public ThreadPoolExecutorConfigSystemSettingsUtilsTest(WorkItemPriority priority, String blockingQueueInterfaceImpl, int corePoolSize, int maximumPoolSize, long keepAliveTime, String timeUnitEnumValue, Object[] args) {
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
        ObjectMapper objectMapper = new ObjectMapper();
        PowerMockito.mockStatic(ApplicationBeans.class);
        when(ApplicationBeans.getObjectMapper()).thenReturn(objectMapper);
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