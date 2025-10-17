package com.serotonin.mango.rt.maint.work;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.mango.service.SystemSettingsService;

import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemSettingsService.class, AbstractBeforeAfterWorkItem.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class CreateWorkItemToStringTest {

    @Before
    public void config() throws Exception {
        SystemSettingsService systemSettingsService = mock(SystemSettingsService.class);
        PowerMockito.whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsService);
    }

    @Test
    public void when_EmailFinallyWorkItem_newInstance() {
        //when:
        WorkItem workItem = EmailFinallyWorkItem.newInstance(null, null, null, null, null);
        //then:
        System.out.println(workItem);
    }

    @Test
    public void when_EmailAfterWorkItem_newInstance() {
        //when:
        WorkItem workItem = EmailAfterWorkItem.newInstance(null, null, null, null);
        //then:
        System.out.println(workItem);
    }

    @Test
    public void when_new_ReportWorkItem() {
        //when:
        WorkItem workItem = new ReportWorkItem();
        //then:
        System.out.println(workItem);
    }

    @Test
    public void when_new_ProcessWorkItem() {
        //when:
        WorkItem workItem = new ProcessWorkItem(null, null, null);
        //then:
        System.out.println(workItem);
    }

    @Test
    public void when_new_SetPointWorkItem() {
        //when:
        WorkItem workItem = new SetPointWorkItem(-1, null, null, null);
        //then:
        System.out.println(workItem);
    }

    @Test
    public void when_new_PointLinkSetPointWorkItem() {
        //when:
        WorkItem workItem = new PointLinkSetPointWorkItem(-1, null, null, null);
        //then:
        System.out.println(workItem);
    }
}