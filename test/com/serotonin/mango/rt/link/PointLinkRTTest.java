package com.serotonin.mango.rt.link;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PointLinkRTTest {

    private PointLinkRT pointLinkRT;

    @Before
    public void init() {
        PointLinkVO pointLinkVO = new PointLinkVO();
        pointLinkVO.setEvent(PointLinkVO.EVENT_CHANGE);
        pointLinkRT = new PointLinkRT(pointLinkVO);
    }

    @After
    public void finalized() {
        pointLinkRT = null;
    }
    private void invodePointChanged(){
        pointLinkRT.pointChanged(
                new PointValueTime("0",new Long(0)),
                new PointValueTime("0",new Long(0)));
    }
    @Test
    public void setUpReadyFlagResponsibleForExecutePartOfMethodToRunScriptDefinedForPointLinkTest() {
        invodePointChanged();
        Assert.assertEquals(Boolean.TRUE,pointLinkRT.getReady());
    }
    @Test
    public void setDownReadyFlagResponsibleForExecutePartOfMethodToRunScriptDefinedForPointLinkTest() {
        pointLinkRT.setReady(Boolean.FALSE);
        invodePointChanged();
        Assert.assertEquals(Boolean.FALSE,pointLinkRT.getReady());
    }
}
