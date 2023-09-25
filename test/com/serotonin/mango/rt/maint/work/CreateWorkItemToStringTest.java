package com.serotonin.mango.rt.maint.work;

import org.junit.Test;

public class CreateWorkItemToStringTest {

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
        WorkItem workItem = new ProcessWorkItem(null, null);
        //then:
        System.out.println(workItem);
    }

    @Test
    public void when_new_SetPointWorkItem() {
        //when:
        WorkItem workItem = new SetPointWorkItem(-1, null, null);
        //then:
        System.out.println(workItem);
    }

    @Test
    public void when_new_PointLinkSetPointWorkItem() {
        //when:
        WorkItem workItem = new PointLinkSetPointWorkItem(-1, null, null);
        //then:
        System.out.println(workItem);
    }
}