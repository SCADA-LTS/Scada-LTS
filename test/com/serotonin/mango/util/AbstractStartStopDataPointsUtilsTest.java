package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.pointvalues.IPointValueDAO;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.TestUtils;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.junit.Before;
import org.scada_lts.mango.service.DataPointService;
import utils.mock.PowerMockUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static utils.mock.MockitoUtils.mockBackgroundProcessing;
import static utils.mock.MockitoUtils.mockContextWrapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationBeans.class, PointValueService.class})
public class AbstractStartStopDataPointsUtilsTest {

    private DataPointVO dataPoint1;
    private DataPointVO dataPoint2;
    private DataPointVO dataPoint3;
    private DataPointVO dataPoint4;
    private DataPointVO dataPoint5;

    private MetaPointLocatorVO metaPointLocator1;
    private MetaPointLocatorVO metaPointLocator2;
    private MetaPointLocatorVO metaPointLocator3;
    private MetaPointLocatorVO metaPointLocator4;
    private MetaPointLocatorVO metaPointLocator5;

    private DataPointService dataPointServiceMock;
    private DataSourceRT dataSourceRtMock;
    private List<DataPointVO> dataPoints;


    private static BackgroundProcessing backgroundProcessing;

    @AfterClass
    public static void terminateBackgroundProcessing() {
        backgroundProcessing.terminate();
    }

    @BeforeClass
    public static void configBackgroundProcessing() {

        backgroundProcessing = mockBackgroundProcessing();
        Common.ctx = mockContextWrapper(backgroundProcessing);
    }

    @Before
    public void config() throws Exception {

        PowerMockUtils.configDaoMock();

        IPointValueDAO dao = mock(IPointValueDAO.class);
        PowerMockito.when(ApplicationBeans.getPointValueDAOBean())
                .thenReturn(dao);
        when(dao.getLatestPointValue(anyInt())).thenReturn(null);
        when(dao.findByIdAndTs(anyInt(), anyLong())).thenReturn(Collections.emptyList());
        when(dao.applyBounds(anyDouble())).thenAnswer(inv -> inv.getArgument(0));
        when(dao.create(anyInt(), anyInt(), anyDouble(), anyLong())).thenReturn(new Object[]{1L});

        PointValueService pvs = mock(PointValueService.class);
        when(pvs.getLatestPointValue(anyInt())).thenReturn(null);
        when(pvs.getLatestPointValues(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        PowerMockito.whenNew(PointValueService.class).withNoArguments().thenReturn(pvs);


        dataPoint1 = TestUtils.newPointSettable(1, -1);
        dataPoint2 = TestUtils.newPointSettable(2, -1);
        dataPoint3 = TestUtils.newPointSettable(3, -1);
        dataPoint4 = TestUtils.newPointSettable(4, -1);
        dataPoint5 = TestUtils.newPointSettable(5, -1);

        dataPoint1.setEnabled(true);
        dataPoint2.setEnabled(true);
        dataPoint3.setEnabled(true);
        dataPoint4.setEnabled(false);
        dataPoint5.setEnabled(false);

        metaPointLocator1 = new MetaPointLocatorVO();
        metaPointLocator2 = new MetaPointLocatorVO();
        metaPointLocator3 = new MetaPointLocatorVO();
        metaPointLocator4 = new MetaPointLocatorVO();
        metaPointLocator5 = new MetaPointLocatorVO();

        dataPoints = new ArrayList<>();
        dataPoints.add(dataPoint1);
        dataPoints.add(dataPoint2);
        dataPoints.add(dataPoint3);
        dataPoints.add(dataPoint4);
        dataPoints.add(dataPoint5);

        dataPointServiceMock = mock(DataPointService.class);
        when(dataPointServiceMock.getDataPoints(eq(null), eq(true)))
                .thenReturn(dataPoints);

        dataSourceRtMock = mock(DataSourceRT.class);

    }

    public DataPointVO getDataPoint1() {
        return dataPoint1;
    }

    public DataPointVO getDataPoint2() {
        return dataPoint2;
    }

    public DataPointVO getDataPoint3() {
        return dataPoint3;
    }

    public DataPointVO getDataPoint4() {
        return dataPoint4;
    }

    public DataPointVO getDataPoint5() {
        return dataPoint5;
    }

    public MetaPointLocatorVO getMetaPointLocator1() {
        return metaPointLocator1;
    }

    public MetaPointLocatorVO getMetaPointLocator2() {
        return metaPointLocator2;
    }

    public MetaPointLocatorVO getMetaPointLocator3() {
        return metaPointLocator3;
    }

    public MetaPointLocatorVO getMetaPointLocator4() {
        return metaPointLocator4;
    }

    public MetaPointLocatorVO getMetaPointLocator5() {
        return metaPointLocator5;
    }

    public DataPointService getDataPointServiceMock() {
        return dataPointServiceMock;
    }

    public DataSourceRT getDataSourceRtMock() {
        return dataSourceRtMock;
    }

    public List<DataPointVO> getDataPoints() {
        return dataPoints;
    }
}
