package com.serotonin.mango.rt.dataSource;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueCache;
import com.serotonin.mango.rt.dataSource.meta.MetaPointLocatorRT;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.TimePeriodType;
import com.serotonin.mango.vo.dataSource.meta.MetaDataSourceVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.web.dwr.MiscDwr;
import com.serotonin.web.content.ContentGenerator;
import org.directwebremoting.WebContextFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.utils.ValidationUtils;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static utils.mock.PowerMockUtils.mockBackgroundProcessing;
import static utils.mock.RuntimeMockUtils.mockingServices;
import static utils.mock.RuntimeMockUtils.runtimeManagerMock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WebContextFactory.class, Common.class, MiscDwr.class, SystemSettingsDAO.class,
        ContentGenerator.class, PointValueCache.class, DataPointRT.class, RuntimeManager.class, DataPointDao.class,
        PointValueDao.class, ApplicationBeans.class, PollingDataSource.class, ValidationUtils.class, MetaPointLocatorRT.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public abstract class AbstractDataPointUnreliableUtilsTest {

    protected RuntimeManager runtimeManager;
    protected DataPointRT metaDataPoint111;
    protected DataPointRT metaDataPoint112With_M111_V121;

    protected DataPointRT metaDataPoint116With_M117_V121;
    protected DataPointRT metaDataPoint117With_M118_V121_V123;
    protected DataPointRT metaDataPoint118With_M119;
    protected DataPointRT metaDataPoint119With_M120_V122;
    protected DataPointRT metaDataPoint120With_V121;

    protected DataPointRT virtualDataPoint121;
    protected DataPointRT virtualDataPoint122;
    protected DataPointRT virtualDataPoint123;

    protected DataSourceRT metaDataSource123;
    protected DataSourceRT metaDataSource345;
    protected DataSourceRT virtualDataSource567;

    protected final List<DataPointRT> metaDataPointsWith_111 = new ArrayList<>();
    protected final List<DataPointRT> metaDataPointsWithContextWith_112_116_117_118_119_120 = new ArrayList<>();
    protected final List<DataPointRT> virtualDataPointsWith_121_122_123 = new ArrayList<>();

    protected final List<DataPointRT> allDataPoints = new ArrayList<>();

    private static BackgroundProcessing backgroundProcessing;

    @AfterClass
    public static void clean() {
        backgroundProcessing.terminate();
    }

    @After
    public void cleanTimer() {
        Common.timer.cancel();
    }

    @BeforeClass
    public static void config() {
        backgroundProcessing = mockBackgroundProcessing();
    }

    @Before
    public void configMock() throws Exception {

        MetaDataSourceVO metaDataSource123 = new MetaDataSourceVO();
        metaDataSource123.setId(123);
        metaDataSource123.setEnabled(true);
        metaDataSource123.setXid("TEST_DS_XID1");
        metaDataSource123.setName("Meta_Test1");

        MetaDataSourceVO metaDataSource345 = new MetaDataSourceVO();
        metaDataSource345.setId(345);
        metaDataSource345.setEnabled(true);
        metaDataSource345.setXid("TEST_DS_XID2");
        metaDataSource345.setName("Meta_Test2");

        VirtualDataSourceVO virtualDataSource567 = new VirtualDataSourceVO();
        virtualDataSource567.setEnabled(true);
        virtualDataSource567.setId(567);
        virtualDataSource567.setXid("TEST_DS_XID3");
        virtualDataSource567.setName("Virtual_Test");
        virtualDataSource567.setUpdatePeriods(1);
        virtualDataSource567.setUpdatePeriodType(TimePeriodType.SECONDS.getCode());

        MetaPointLocatorVO metaPointLocator112 = new MetaPointLocatorVO();
        metaPointLocator112.setContext(Arrays.asList(new IntValuePair(111,""), new IntValuePair(121,"")));
        MetaPointLocatorVO metaPointLocator116 = new MetaPointLocatorVO();
        metaPointLocator116.setContext(Arrays.asList(new IntValuePair(117,""), new IntValuePair(121,"")));
        MetaPointLocatorVO metaPointLocator117 = new MetaPointLocatorVO();
        metaPointLocator117.setContext(Arrays.asList(new IntValuePair(118,""), new IntValuePair(121,""), new IntValuePair(123,"")));
        MetaPointLocatorVO metaPointLocator118 = new MetaPointLocatorVO();
        metaPointLocator118.setContext(Arrays.asList(new IntValuePair(119,"")));
        MetaPointLocatorVO metaPointLocator119 = new MetaPointLocatorVO();
        metaPointLocator119.setContext(Arrays.asList(new IntValuePair(120,""), new IntValuePair(122,"")));
        MetaPointLocatorVO metaPointLocator120 = new MetaPointLocatorVO();
        metaPointLocator120.setContext(Arrays.asList(new IntValuePair(121,"")));

        DataPointVO metaDataPoint111 = TestUtils.newPointSettable(111, metaDataSource123, -1, new MetaPointLocatorVO());
        metaDataPoint111.setEnabled(true);
        DataPointVO metaDataPoint112_M111_V121 = TestUtils.newPointSettable(112, metaDataSource123, -1, metaPointLocator112);
        metaDataPoint112_M111_V121.setEnabled(true);
        DataPointVO metaDataPoint116With_M117_V121 = TestUtils.newPointSettable(116, metaDataSource345, -1, metaPointLocator116);
        metaDataPoint116With_M117_V121.setEnabled(true);
        DataPointVO metaDataPoint117With_M118_V121_V123 = TestUtils.newPointSettable(117, metaDataSource345, -1, metaPointLocator117);
        metaDataPoint117With_M118_V121_V123.setEnabled(true);
        DataPointVO metaDataPoint118With_M119 = TestUtils.newPointSettable(118, metaDataSource345, -1, metaPointLocator118);
        metaDataPoint118With_M119.setEnabled(true);
        DataPointVO metaDataPoint119With_M120_V122 = TestUtils.newPointSettable(119, metaDataSource345, -1, metaPointLocator119);
        metaDataPoint119With_M120_V122.setEnabled(true);
        DataPointVO metaDataPoint120With_V121 = TestUtils.newPointSettable(120, metaDataSource345, -1, metaPointLocator120);
        metaDataPoint120With_V121.setEnabled(true);
        DataPointVO virtualDataPoint121 = TestUtils.newPointSettable(121, virtualDataSource567, -1, new VirtualPointLocatorVO());
        virtualDataPoint121.setEnabled(true);
        DataPointVO virtualDataPoint122 = TestUtils.newPointSettable(122, virtualDataSource567, -1, new VirtualPointLocatorVO());
        virtualDataPoint122.setEnabled(true);
        DataPointVO virtualDataPoint123 = TestUtils.newPointSettable(123, virtualDataSource567, -1, new VirtualPointLocatorVO());
        virtualDataPoint123.setEnabled(true);

        mockingServices(Arrays.asList(metaDataPoint111, metaDataPoint112_M111_V121, metaDataPoint116With_M117_V121,
                metaDataPoint117With_M118_V121_V123, metaDataPoint118With_M119, metaDataPoint119With_M120_V122, metaDataPoint120With_V121,
                virtualDataPoint121, virtualDataPoint122, virtualDataPoint123), Arrays.asList(metaDataSource123, metaDataSource345, virtualDataSource567));
        runtimeManagerMock(this.runtimeManager = new RuntimeManager(), mock(EventManager.class), backgroundProcessing);



        runtimeManager.saveDataSource(virtualDataSource567);
        runtimeManager.saveDataPoint(virtualDataPoint121);
        runtimeManager.saveDataPoint(virtualDataPoint122);
        runtimeManager.saveDataPoint(virtualDataPoint123);

        runtimeManager.saveDataSource(metaDataSource123);
        runtimeManager.saveDataPoint(metaDataPoint111);
        runtimeManager.saveDataPoint(metaDataPoint112_M111_V121);

        runtimeManager.saveDataSource(metaDataSource345);
        runtimeManager.saveDataPoint(metaDataPoint120With_V121);
        runtimeManager.saveDataPoint(metaDataPoint119With_M120_V122);
        runtimeManager.saveDataPoint(metaDataPoint118With_M119);
        runtimeManager.saveDataPoint(metaDataPoint117With_M118_V121_V123);
        runtimeManager.saveDataPoint(metaDataPoint116With_M117_V121);


        this.metaDataPoint111 = runtimeManager.getDataPoint(metaDataPoint111.getId());

        this.metaDataPoint112With_M111_V121 = runtimeManager.getDataPoint(metaDataPoint112_M111_V121.getId());
        this.metaDataPoint116With_M117_V121 = runtimeManager.getDataPoint(metaDataPoint116With_M117_V121.getId());
        this.metaDataPoint117With_M118_V121_V123 = runtimeManager.getDataPoint(metaDataPoint117With_M118_V121_V123.getId());
        this.metaDataPoint118With_M119 = runtimeManager.getDataPoint(metaDataPoint118With_M119.getId());
        this.metaDataPoint119With_M120_V122 = runtimeManager.getDataPoint(metaDataPoint119With_M120_V122.getId());
        this.metaDataPoint120With_V121 = runtimeManager.getDataPoint(metaDataPoint120With_V121.getId());

        this.virtualDataPoint121 = runtimeManager.getDataPoint(virtualDataPoint121.getId());
        this.virtualDataPoint122 = runtimeManager.getDataPoint(virtualDataPoint122.getId());
        this.virtualDataPoint123 = runtimeManager.getDataPoint(virtualDataPoint123.getId());

        this.metaDataSource123 = runtimeManager.getRunningDataSource(metaDataSource123.getId());
        this.metaDataSource345 = runtimeManager.getRunningDataSource(metaDataSource345.getId());
        this.virtualDataSource567 = runtimeManager.getRunningDataSource(virtualDataSource567.getId());


        metaDataPointsWith_111.add(this.metaDataPoint111);

        metaDataPointsWithContextWith_112_116_117_118_119_120.add(this.metaDataPoint112With_M111_V121);
        metaDataPointsWithContextWith_112_116_117_118_119_120.add(this.metaDataPoint116With_M117_V121);
        metaDataPointsWithContextWith_112_116_117_118_119_120.add(this.metaDataPoint117With_M118_V121_V123);
        metaDataPointsWithContextWith_112_116_117_118_119_120.add(this.metaDataPoint118With_M119);
        metaDataPointsWithContextWith_112_116_117_118_119_120.add(this.metaDataPoint119With_M120_V122);
        metaDataPointsWithContextWith_112_116_117_118_119_120.add(this.metaDataPoint120With_V121);


        virtualDataPointsWith_121_122_123.add(this.virtualDataPoint121);
        virtualDataPointsWith_121_122_123.add(this.virtualDataPoint122);
        virtualDataPointsWith_121_122_123.add(this.virtualDataPoint123);

        allDataPoints.addAll(virtualDataPointsWith_121_122_123);
        allDataPoints.addAll(metaDataPointsWith_111);
        allDataPoints.addAll(metaDataPointsWithContextWith_112_116_117_118_119_120);

        afterConfig();
    }

    @After
    public void after() {
        afterTest();
    }

    protected void afterConfig() {};
    protected void afterTest() {};
}