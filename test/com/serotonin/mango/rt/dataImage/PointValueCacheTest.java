package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.MangoContextListener;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.event.handlers.SetPointHandlerRT;
import com.serotonin.mango.rt.link.PointLinkRT;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.rt.maint.work.AbstractBeforeAfterWorkItem;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import com.serotonin.mango.web.ContextWrapper;
import com.serotonin.web.taglib.DateFunctions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.login.ILoggedUsers;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.PointValueDAOMemory;

import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DAO.class, PointValueDAO.class, PointValueService.class, ApplicationBeans.class,
        AbstractBeforeAfterWorkItem.class, MangoContextListener.class, Common.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class PointValueCacheTest {

    private PointValueCache pointValueCache;
    private PointValueTime pvt;
    private User sourceUser;
    private String annotatedPointValueTimeToStringExpected;
    private String pointValueTimeToStringExpected;

    @Before
    public void config() throws Exception {
        sourceUser = new User();
        sourceUser.setId(12);
        sourceUser.setLang("pl");
        String username = "testName";
        sourceUser.setUsername(username);

        IUserDAO userDAO = mock(IUserDAO.class);
        when(userDAO.getUser(eq(12))).thenReturn(sourceUser);

        mockStatic(PointValueDAO.class);
        when(PointValueDAO.getInstance()).thenReturn(new PointValueDAOMemory(userDAO));

        ILoggedUsers loggedUsers = mock(ILoggedUsers.class);
        when(loggedUsers.getUser(eq(12))).thenReturn(sourceUser);

        mockStatic(ApplicationBeans.class);
        when(ApplicationBeans.getLoggedUsersBean()).thenReturn(loggedUsers);

        SystemSettingsService systemSettingsService = mock(SystemSettingsService.class);
        whenNew(SystemSettingsService.class).withAnyArguments().thenReturn(systemSettingsService);

        BackgroundProcessing backgroundProcessing = mock(BackgroundProcessing.class);
        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        when(contextWrapper.getBackgroundProcessing()).thenReturn(backgroundProcessing);
        Common.ctx = contextWrapper;

        mockStatic(Common.class);
        when(Common.getUser()).thenReturn(sourceUser);
        when(Common.getBundle()).thenReturn(ResourceBundle.getBundle("messages"));
        long time = 1708165754243L;

        pvt = new PointValueTime(MangoValue.stringToValue("1", DataTypes.BINARY), time);
        pointValueCache = new PointValueCache(-1, 1);

        annotatedPointValueTimeToStringExpected = "AnnotatedPointValueTime(" + sourceUser.getUsername() + " -- true@" + DateFunctions.getTime(time) + ")";
        pointValueTimeToStringExpected = "PointValueTime(true@" + DateFunctions.getTime(time) + ")";
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_logValue_false_for_User_then_annotated_true() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, sourceUser, false, false);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_logValue_false_for_User_then_toString() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, sourceUser, false, false);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(annotatedPointValueTimeToStringExpected, pvt.toString());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_logValue_false_for_PointLinkRT_then_annotated_true() {

        //given:
        PointLinkRT source = new PointLinkRT(new PointLinkVO());
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, source, false, false);

        //when:
        PointValueTime pvt =  pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_logValue_false_for_SetPointHandlerRT_then_annotated_true() {

        //given:
        SetPointHandlerRT source = new SetPointHandlerRT(new EventHandlerVO());
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, source, false, false);

        //when:
        PointValueTime pvt =  pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_sync_for_User_then_annotated_true() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, sourceUser, true, false);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_sync_for_User_then_toString() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, sourceUser, true, false);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(annotatedPointValueTimeToStringExpected, pvt.toString());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_sync_for_PointLinkRT_then_annotated_true() {

        //given:
        PointLinkRT source = new PointLinkRT(new PointLinkVO());
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, source, true, false);

        //when:
        PointValueTime pvt =  pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_sync_for_SetPointHandlerRT_then_annotated_true() {

        //given:
        SetPointHandlerRT source = new SetPointHandlerRT(new EventHandlerVO());
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, source, true, false);

        //when:
        PointValueTime pvt =  pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_async_for_User_then_annotated_true() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, sourceUser, true, true);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_async_for_User_then_toString() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, sourceUser, true, true);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(annotatedPointValueTimeToStringExpected, pvt.toString());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_async_for_PointLinkRT_then_annotated_true() {

        //given:
        PointLinkRT source = new PointLinkRT(new PointLinkVO());
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, source, true, true);

        //when:
        PointValueTime pvt =  pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_async_for_SetPointHandlerRT_then_annotated_true() {

        //given:
        SetPointHandlerRT source = new SetPointHandlerRT(new EventHandlerVO());
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, source, true, true);

        //when:
        PointValueTime pvt =  pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(true, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_logValue_false_source_null_then_annotated_false() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, null, false, false);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(false, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_logValue_false_source_null_then_toString() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, null, false, false);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(pointValueTimeToStringExpected, pvt.toString());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_sync_source_null_then_annotated_false() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, null, true, false);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(false, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_sync_source_null_then_toString() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, null, true, false);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(pointValueTimeToStringExpected, pvt.toString());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_async_source_null_then_annotated_false() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, null, true, true);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(false, pvt.isAnnotated());
    }

    @Test
    public void when_getLatestPointValue_after_savePointValueIntoDaoAndCacheUpdate_async_source_null_then_toString() {

        //given:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt, null, true, true);

        //when:
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertEquals(pointValueTimeToStringExpected, pvt.toString());
    }
}
