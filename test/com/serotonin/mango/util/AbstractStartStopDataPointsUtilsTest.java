package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.TestUtils;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.junit.Before;
import org.scada_lts.mango.service.DataPointService;
import utils.mock.PowerMockUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static utils.mock.MockitoUtils.mockBackgroundProcessing;
import static utils.mock.MockitoUtils.mockContextWrapper;

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

        DataSource mockDataSource = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);
        DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");

        mockStatic(ApplicationBeans.class);
        when(ApplicationBeans.getBean(eq("databaseSource"), eq(DataSource.class)))
                .thenReturn(mockDataSource);

        java.lang.reflect.Field instanceField = DAO.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        PointValueDAO pointValueDAOMock = mock(PointValueDAO.class);
        when(pointValueDAOMock.getLatestPointValue(anyInt())).thenReturn(System.currentTimeMillis());

        mockStatic(PointValueDAO.class);
        when(PointValueDAO.getInstance()).thenReturn(pointValueDAOMock);

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
