package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.rt.maint.work.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.quartz.ItemsPerSecond;

import java.util.stream.Collectors;
import static org.scada_lts.utils.TimeUtils.toMs;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemSettingsService.class, AbstractBeforeAfterWorkItem.class, ItemsPerSecond.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class WorkItemInfoListTest {

    private static WorkItemInfoList workItemInfoList;

    @BeforeClass
    public static void config() throws Exception {
        SystemSettingsService systemSettingsServiceMock = PowerMockito.mock(SystemSettingsService.class);
        PowerMockito.when(systemSettingsServiceMock.getThreadsNameAdditionalLength()).thenReturn(255);
        PowerMockito.when(systemSettingsServiceMock.isWorkItemsReportingEnabled()).thenReturn(true);
        PowerMockito.when(systemSettingsServiceMock.isWorkItemsReportingItemsPerSecondEnabled()).thenReturn(false);
        PowerMockito.when(systemSettingsServiceMock.getWorkItemsReportingItemsPerSecondLimit()).thenReturn(0);
        PowerMockito.whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsServiceMock);

        //given:
        for(int i=0; i < 50; i++) {
            WorkItem workItem = new AbstractBeforeAfterWorkItem() {
                @Override
                public void work() {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public WorkItemPriority getPriorityType() {
                    return WorkItemPriority.LOW;
                }

                @Override
                public String getDetails() {
                    return "";
                }
            };
            workItem.execute();
        }
        //when:
        ReadWorkItems workItems = WorkItemsUtils.getCurrentAll();
        workItemInfoList = new WorkItemInfoList(workItems.get().stream().map(WorkItemInfo::new).collect(Collectors.toList()), workItems.getItemsPerSecond());
    }

    @Test
    public void when_getSize_then_50() {
        //then:
        Assert.assertEquals(50, workItemInfoList.getSize());
    }

    @Test
    public void when_getAvgExecutedMs_and_getAvgExecutedNanos_and_convert_to_ms_then_equals() {

        //when:
        long ms = workItemInfoList.getAvgExecutedMs();
        long nanos = workItemInfoList.getAvgExecutedNanos();

        //then:
        Assert.assertEquals(ms, toMs(nanos));
    }

    @Test
    public void when_getMinExecutedMs_and_getMinExecutedNanos_and_convert_to_ms_then_equals() {

        //when:
        long ms = workItemInfoList.getMinExecutedMs();
        long nanos = workItemInfoList.getMinExecutedNanos();

        //then:
        Assert.assertEquals(ms, toMs(nanos));
    }

    @Test
    public void when_getMaxExecutedMs_and_getMaxExecutedNanos_and_convert_to_ms_then_equals() {

        //when:
        long ms = workItemInfoList.getMaxExecutedMs();
        long nanos = workItemInfoList.getMaxExecutedNanos();

        //then:
        Assert.assertEquals(ms, toMs(nanos));
    }

    @Test
    public void when_getAvgTimeInitMs_and_getAvgTimeInitNanos_and_convert_to_ms_then_equals() {

        //when:
        long ms = workItemInfoList.getAvgTimeInitMs();
        long nanos = workItemInfoList.getAvgTimeInitNanos();

        //then:
        Assert.assertEquals(ms, toMs(nanos));
    }
}