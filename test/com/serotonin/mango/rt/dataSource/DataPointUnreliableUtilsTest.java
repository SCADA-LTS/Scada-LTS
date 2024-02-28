package com.serotonin.mango.rt.dataSource;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueCache;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.TimePeriodType;
import com.serotonin.mango.vo.dataSource.meta.MetaDataSourceVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.web.dwr.MiscDwr;
import com.serotonin.web.content.ContentGenerator;
import org.directwebremoting.WebContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;
import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.resetUnreliableDataPoints;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.resetUnreliable;
import static com.serotonin.mango.util.InitializeDataSourceRtMockUtils.setUnreliable;
import static org.mockito.Mockito.mock;
import static utils.mock.RuntimeMockUtils.runtimeManagerMock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WebContextFactory.class, Common.class, MiscDwr.class, SystemSettingsDAO.class,
        ContentGenerator.class, PointValueCache.class, DataPointRT.class, RuntimeManager.class, DataPointDao.class,
        PointValueDao.class, ApplicationBeans.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class DataPointUnreliableUtilsTest {

    private RuntimeManager runtimeManager;

    private DataPointVO metaDataPoint1;
    private DataPointVO metaDataPoint2;

    private DataPointVO metaDataPoint6WithVirtualDataPoint1;
    private DataPointVO metaDataPoint7WithVirtualDataPoint1;
    private DataPointVO metaDataPoint8;
    private DataPointVO metaDataPoint9WithVirtualDataPoint2;
    private DataPointVO metaDataPoint10WithVirtualDataPoint1;

    private DataPointVO virtualDataPoint1;
    private DataPointVO virtualDataPoint2;

    private MetaDataSourceVO metaDataSourceVO1;
    private MetaDataSourceVO metaDataSourceVO2;
    private VirtualDataSourceVO virtualDataSourceVO;

    private final List<DataPointRT> metaDataPoints = new ArrayList<>();
    private final List<DataPointRT> metaDataPointsWithContext = new ArrayList<>();
    private final List<DataPointRT> virtualDataPoints = new ArrayList<>();


    @Before
    public void configMock() throws Exception {

        runtimeManagerMock(this.runtimeManager = new RuntimeManager(), mock(EventManager.class));

        configData();
    }

    private void configData() {

        metaDataSourceVO1 = new MetaDataSourceVO();
        metaDataSourceVO1.setId(123);
        metaDataSourceVO1.setEnabled(true);
        metaDataSourceVO1.setXid("TEST_DS_XID1");
        metaDataSourceVO1.setName("Meta_Test1");

        metaDataSourceVO2 = new MetaDataSourceVO();
        metaDataSourceVO2.setId(345);
        metaDataSourceVO2.setEnabled(true);
        metaDataSourceVO2.setXid("TEST_DS_XID2");
        metaDataSourceVO2.setName("Meta_Test2");

        virtualDataSourceVO = new VirtualDataSourceVO();
        virtualDataSourceVO.setEnabled(true);
        virtualDataSourceVO.setId(567);
        virtualDataSourceVO.setXid("TEST_DS_XID3");
        virtualDataSourceVO.setName("Virtual_Test");
        virtualDataSourceVO.setUpdatePeriods(1);
        virtualDataSourceVO.setUpdatePeriodType(TimePeriodType.SECONDS.getCode());


        metaDataPoint1 = TestUtils.newPointSettable(111, metaDataSourceVO1, -1, new MetaPointLocatorVO());
        metaDataPoint1.setEnabled(true);

        metaDataPoint2 = TestUtils.newPointSettable(112, metaDataSourceVO1, -1, new MetaPointLocatorVO());
        metaDataPoint2.setEnabled(true);

        MetaPointLocatorVO metaPointLocatorVO1 = new MetaPointLocatorVO();
        metaPointLocatorVO1.setContext(Arrays.asList(new IntValuePair(117,""), new IntValuePair(121,"")));
        metaDataPoint6WithVirtualDataPoint1 = TestUtils.newPointSettable(116, metaDataSourceVO2, -1, metaPointLocatorVO1);
        metaDataPoint6WithVirtualDataPoint1.setEnabled(true);

        MetaPointLocatorVO metaPointLocatorVO2 = new MetaPointLocatorVO();
        metaPointLocatorVO2.setContext(Arrays.asList(new IntValuePair(118,""), new IntValuePair(121,"")));
        metaDataPoint7WithVirtualDataPoint1 = TestUtils.newPointSettable(117, metaDataSourceVO2, -1, metaPointLocatorVO2);
        metaDataPoint7WithVirtualDataPoint1.setEnabled(true);

        MetaPointLocatorVO metaPointLocatorVO3 = new MetaPointLocatorVO();
        metaPointLocatorVO3.setContext(Arrays.asList(new IntValuePair(119,"")));
        metaDataPoint8 = TestUtils.newPointSettable(118, metaDataSourceVO2, -1, metaPointLocatorVO3);
        metaDataPoint8.setEnabled(true);

        MetaPointLocatorVO metaPointLocatorVO4 = new MetaPointLocatorVO();
        metaPointLocatorVO4.setContext(Arrays.asList(new IntValuePair(120,""), new IntValuePair(122,"")));
        metaDataPoint9WithVirtualDataPoint2 = TestUtils.newPointSettable(119, metaDataSourceVO2, -1, metaPointLocatorVO4);
        metaDataPoint9WithVirtualDataPoint2.setEnabled(true);

        MetaPointLocatorVO metaPointLocatorVO5 = new MetaPointLocatorVO();
        metaPointLocatorVO5.setContext(Arrays.asList(new IntValuePair(118,""), new IntValuePair(121,"")));
        metaDataPoint10WithVirtualDataPoint1 = TestUtils.newPointSettable(120, metaDataSourceVO2, -1, metaPointLocatorVO5);
        metaDataPoint10WithVirtualDataPoint1.setEnabled(true);

        virtualDataPoint1 = TestUtils.newPointSettable(121, virtualDataSourceVO, -1, new VirtualPointLocatorVO());
        virtualDataPoint1.setEnabled(true);

        virtualDataPoint2 = TestUtils.newPointSettable(122, virtualDataSourceVO, -1, new VirtualPointLocatorVO());
        virtualDataPoint2.setEnabled(true);


        runtimeManager.saveDataSource(metaDataSourceVO1);
        runtimeManager.saveDataPoint(metaDataPoint1);
        runtimeManager.saveDataPoint(metaDataPoint2);

        runtimeManager.saveDataSource(metaDataSourceVO2);
        runtimeManager.saveDataPoint(metaDataPoint8);
        runtimeManager.saveDataPoint(metaDataPoint6WithVirtualDataPoint1);
        runtimeManager.saveDataPoint(metaDataPoint7WithVirtualDataPoint1);
        runtimeManager.saveDataPoint(metaDataPoint9WithVirtualDataPoint2);
        runtimeManager.saveDataPoint(metaDataPoint10WithVirtualDataPoint1);

        runtimeManager.saveDataSource(virtualDataSourceVO);
        runtimeManager.saveDataPoint(virtualDataPoint1);
        runtimeManager.saveDataPoint(virtualDataPoint2);


        virtualDataPoints.add(runtimeManager.getDataPoint(virtualDataPoint1.getId()));
        virtualDataPoints.add(runtimeManager.getDataPoint(virtualDataPoint2.getId()));

        metaDataPoints.add(runtimeManager.getDataPoint(metaDataPoint1.getId()));
        metaDataPoints.add(runtimeManager.getDataPoint(metaDataPoint2.getId()));

        metaDataPointsWithContext.add(runtimeManager.getDataPoint(metaDataPoint6WithVirtualDataPoint1.getId()));
        metaDataPointsWithContext.add(runtimeManager.getDataPoint(metaDataPoint7WithVirtualDataPoint1.getId()));
        metaDataPointsWithContext.add(runtimeManager.getDataPoint(metaDataPoint8.getId()));
        metaDataPointsWithContext.add(runtimeManager.getDataPoint(metaDataPoint9WithVirtualDataPoint2.getId()));
        metaDataPointsWithContext.add(runtimeManager.getDataPoint(metaDataPoint10WithVirtualDataPoint1.getId()));

    }

    @Test
    public void when_setUnreliableDataPoints_with_virtual_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);

        //when:
        setUnreliableDataPoints(virtualDataPoints);

        //then:
        for(DataPointRT dataPoint: virtualDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_meta_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);

        //when:
        setUnreliableDataPoints(metaDataPoints);

        //then:
        for(DataPointRT dataPoint: metaDataPoints) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_meta_points_with_context_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);

        //when:
        setUnreliableDataPoints(metaDataPointsWithContext);

        //then:
        for(DataPointRT dataPoint: metaDataPointsWithContext) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_getRunningDataSource_virtual_data_source_then_initialized_true() {

        //when:
        DataSourceRT dataSourceRT = runtimeManager.getRunningDataSource(virtualDataSourceVO.getId());

        //then:
        Assert.assertEquals(true, dataSourceRT.isInitialized());

    }

    @Test
    public void when_getRunningDataSource_meta_data_source_then_initialized_true() {

        //when:
        DataSourceRT dataSourceRT = runtimeManager.getRunningDataSource(metaDataSourceVO1.getId());

        //then:
        Assert.assertEquals(true, dataSourceRT.isInitialized());

    }

    @Test
    public void when_getRunningDataSource_meta_data_source_with_context_then_initialized_true() {

        //when:
        DataSourceRT dataSourceRT = runtimeManager.getRunningDataSource(metaDataSourceVO2.getId());

        //then:
        Assert.assertEquals(true, dataSourceRT.isInitialized());

    }

    @Test
    public void when_setUnreliableDataPoint_with_virtual_point_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(virtualDataPoint1.getId());

        //when:
        setUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(true, dataPointRT.isUnreliable());

    }

    @Test
    public void when_setUnreliableDataPoint_with_meta_point_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint1.getId());

        //when:
        setUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(true, dataPointRT.isUnreliable());

    }

    @Test
    public void when_setUnreliableDataPoint_with_meta_point_with_context_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint6WithVirtualDataPoint1.getId());

        //when:
        setUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(true, dataPointRT.isUnreliable());

    }

    @Test
    public void when_setUnreliableDataPoints_with_virtual_points_then_meta_points_unreliable_false() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);

        //when:
        setUnreliableDataPoints(virtualDataPoints);

        //then:
        for(DataPointRT dataPoint: metaDataPoints) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_virtual_points_from_meta_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);

        //when:
        setUnreliableDataPoints(virtualDataPoints);

        //then:
        for(DataPointRT dataPoint: metaDataPointsWithContext) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoint_with_meta_point_from_context_meta_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint10WithVirtualDataPoint1.getId());

        //when:
        setUnreliableDataPoint(dataPointRT);

        //then:
        for(DataPointRT dataPoint: metaDataPointsWithContext) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoint_with_meta_point_from_context_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint10WithVirtualDataPoint1.getId());

        //when:
        setUnreliableDataPoint(dataPointRT);

        //then:
        for(DataPointRT dataPoint: runtimeManager.getRunningMetaDataPoints(dataPointRT.getId())) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoints_with_virtual_points_from_context_meta_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);

        //when:
        setUnreliableDataPoints(virtualDataPoints);

        //then:
        for(DataPointRT dataPoint: metaDataPointsWithContext) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoint_with_virtual_point_from_context_meta_points_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(virtualDataPoint1.getId());

        //when:
        setUnreliableDataPoint(dataPointRT);

        //then:
        for(DataPointRT dataPoint: runtimeManager.getRunningMetaDataPoints(dataPointRT.getId())) {
            Assert.assertEquals(true, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_setUnreliableDataPoint_with_virtual_point_from_context_meta_point_then_unreliable_true() {

        //given:
        resetUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(virtualDataPoint2.getId());
        DataPointRT metaDataPointRT = runtimeManager.getDataPoint(metaDataPoint9WithVirtualDataPoint2.getId());

        //when:
        setUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(true, metaDataPointRT.isUnreliable());

    }

    @Test
    public void when_resetUnreliableDataPoint_with_virtual_point_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(virtualDataPoint1.getId());

        //when:
        resetUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(false, dataPointRT.isUnreliable());

    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint1.getId());

        //when:
        resetUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(false, dataPointRT.isUnreliable());

    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_with_context_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint6WithVirtualDataPoint1.getId());

        //when:
        resetUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(false, dataPointRT.isUnreliable());

    }

    @Test
    public void when_resetUnreliableDataPoint_with_meta_point_from_context_meta_point_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint10WithVirtualDataPoint1.getId());

        //when:
        resetUnreliableDataPoint(dataPointRT);

        //then:
        for(DataPointRT dataPoint: metaDataPointsWithContext) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_meta_point_then_unreliable_false() {
        
        //given:
        setUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint1.getId());

        //when:
        resetUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(false, dataPointRT.isUnreliable());

    }

    @Test
    public void when_resetUnreliableDataPoints_with_meta_points_then_unreliable_false() {

        //given:
        setUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(metaDataPoint1.getId());

        //when:
        resetUnreliableDataPoints(metaDataPoints);

        //then:
        Assert.assertEquals(false, dataPointRT.isUnreliable());

    }

    @Test
    public void when_resetUnreliableDataPoints_with_virtual_point_from_context_meta_point_then_meta_points_unreliable_false() {

        //given:
        setUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(virtualDataPoint1.getId());

        //when:
        resetUnreliableDataPoint(dataPointRT);

        //then:
        for(DataPointRT dataPoint: runtimeManager.getRunningMetaDataPoints(dataPointRT.getId())) {
            Assert.assertEquals(false, dataPoint.isUnreliable());
        }
    }

    @Test
    public void when_resetUnreliableDataPoints_with_virtual_point_from_context_meta_point_then_meta_point_unreliable_false() {

        //given:
        setUnreliable(virtualDataPoints, metaDataPoints, metaDataPointsWithContext);
        DataPointRT dataPointRT = runtimeManager.getDataPoint(virtualDataPoint2.getId());
        DataPointRT metaDataPointRT = runtimeManager.getDataPoint(metaDataPoint9WithVirtualDataPoint2.getId());

        //when:
        resetUnreliableDataPoint(dataPointRT);

        //then:
        Assert.assertEquals(false, metaDataPointRT.isUnreliable());

    }
}