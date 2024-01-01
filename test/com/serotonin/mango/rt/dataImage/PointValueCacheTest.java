package com.serotonin.mango.rt.dataImage;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.virtual.VirtualDataSourceRT;
import com.serotonin.mango.rt.link.PointLinkRT;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.login.ILoggedUsers;
import org.scada_lts.login.LoggedUsers;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import utils.mock.MockUtils;

import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DAO.class, PointValueDAO.class, PointValueService.class, ApplicationBeans.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class PointValueCacheTest {

    private PointValueCache pointValueCache;
    private PointValueTime pvt = new PointValueTime(MangoValue.stringToValue("1",1), 1);

    @Before
    public void config() throws Exception {
        MockUtils.configDaoMock();
        pointValueCache = new PointValueCache(-1, 1);
    }

    @Test
    public void sourceIsNotEmptyFillNeededPropertyInPointValueTime_Test() {

        //given:
        User source = new User();
        source.setId(12);
        String username = "testName";
        source.setUsername(username);

        //when:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt,source,Boolean.FALSE,Boolean.FALSE);
        PointValueTime pvt = pointValueCache.getLatestPointValue();

        //then:
        Assert.assertTrue(pvt.isAnnotated());
        AnnotatedPointValueTime annotatedPointValueTime = (AnnotatedPointValueTime) pvt;
        Assert.assertEquals(username, annotatedPointValueTime.getSourceDescriptionArgument());
    }

    @Test
    public void sourceIsEmptyNotFillNeededPropertyInPointValueTime_Test() {
        //given:
        PointLinkRT source = new PointLinkRT(new PointLinkVO());


        //when:
        pointValueCache.savePointValueIntoDaoAndCacheUpdate(pvt,source,Boolean.FALSE,Boolean.FALSE);
        PointValueTime pvt =  pointValueCache.getLatestPointValue();

        //then:
        Assert.assertFalse(pvt.isAnnotated());
    }

}
