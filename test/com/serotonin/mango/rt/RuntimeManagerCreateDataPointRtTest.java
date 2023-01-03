package com.serotonin.mango.rt;

import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.ChangeTypeVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.mango.service.SystemSettingsService;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DAO.class, Common.class, PointValueDAO.class, RuntimeManager.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class RuntimeManagerCreateDataPointRtTest {

    private DataPointVO dataPointVO;
    public SystemSettingsService systemSettingsServiceMock;

    @Before
    public void config() throws Exception {
        DAO dao = mock(DAO.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(dao.getJdbcTemp()).thenReturn(jdbcTemplate);
        whenNew(DAO.class)
                .withNoArguments()
                .thenReturn(dao);

        DataSourceVO dataSourceVO = new VirtualDataSourceVO();
        dataSourceVO.setEnabled(true);
        dataSourceVO.setId(567);
        dataSourceVO.setName("test_ds");
        dataSourceVO.setXid("test_xid");

        VirtualPointLocatorVO virtualPointLocatorVO = new VirtualPointLocatorVO();
        virtualPointLocatorVO.setDataTypeId(DataTypes.NUMERIC);
        virtualPointLocatorVO.setChangeTypeId(ChangeTypeVO.Types.NO_CHANGE);
        virtualPointLocatorVO.getNoChange().setStartValue("0.123");

        dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPointVO.setId(321);
        dataPointVO.setDefaultCacheSize(10);
        dataPointVO.setTolerance(0.0);
        dataPointVO.setPointLocator(virtualPointLocatorVO);
        dataPointVO.setEventDetectors(new ArrayList<>());
        dataPointVO.setEnabled(true);
        dataPointVO.setDataSourceId(dataSourceVO.getId());
        dataPointVO.setDataSourceName(dataSourceVO.getName());
        dataPointVO.setDeviceName(dataSourceVO.getName());
        systemSettingsServiceMock = mock(SystemSettingsService.class);
    }

    @Test
    public void when_createDataPointRT_for_DataPointSyncMode_HIGH_then_equals_type() throws Exception {

        //given:
        DataPointSyncMode dataPointSyncMode = DataPointSyncMode.HIGH;
        when(systemSettingsServiceMock.getDataPointRtValueSynchronized()).thenReturn(dataPointSyncMode);
        whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsServiceMock);

        //when:
        DataPointRT dataPointRT = RuntimeManager.createDataPointRT(dataPointVO);

        //then:
        assertEquals(dataPointSyncMode.getType(), dataPointRT.getClass());
    }


    @Test
    public void when_createDataPointRT_for_DataPointSyncMode_MEDIUM_then_equals_type() throws Exception {

        //given:
        DataPointSyncMode dataPointSyncMode = DataPointSyncMode.MEDIUM;
        when(systemSettingsServiceMock.getDataPointRtValueSynchronized()).thenReturn(dataPointSyncMode);
        whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsServiceMock);

        //when:
        DataPointRT dataPointRT = RuntimeManager.createDataPointRT(dataPointVO);

        //then:
        assertEquals(dataPointSyncMode.getType(), dataPointRT.getClass());
    }

    @Test
    public void when_createDataPointRT_for_DataPointSyncMode_LOW_then_equals_type() throws Exception {

        //given:
        DataPointSyncMode dataPointSyncMode = DataPointSyncMode.LOW;
        when(systemSettingsServiceMock.getDataPointRtValueSynchronized()).thenReturn(dataPointSyncMode);
        whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsServiceMock);

        //when:
        DataPointRT dataPointRT = RuntimeManager.createDataPointRT(dataPointVO);

        //then:
        assertEquals(dataPointSyncMode.getType(), dataPointRT.getClass());
    }

    @Test
    public void when_createDataPointRT_for_DataPointSyncMode_HIGH_then_not_equals_other_type() throws Exception {

        //given:
        DataPointSyncMode dataPointSyncMode = DataPointSyncMode.HIGH;
        when(systemSettingsServiceMock.getDataPointRtValueSynchronized()).thenReturn(dataPointSyncMode);
        whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsServiceMock);

        //when:
        DataPointRT dataPointRT = RuntimeManager.createDataPointRT(dataPointVO);

        //then:
        assertNotEquals(DataPointSyncMode.LOW.getType(), dataPointRT.getClass());
    }


    @Test
    public void when_createDataPointRT_for_DataPointSyncMode_MEDIUM_then_not_equals_other_type() throws Exception {

        //given:
        DataPointSyncMode dataPointSyncMode = DataPointSyncMode.MEDIUM;
        when(systemSettingsServiceMock.getDataPointRtValueSynchronized()).thenReturn(dataPointSyncMode);
        whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsServiceMock);

        //when:
        DataPointRT dataPointRT = RuntimeManager.createDataPointRT(dataPointVO);

        //then:
        assertNotEquals(DataPointSyncMode.LOW.getType(), dataPointRT.getClass());
    }

    @Test
    public void when_createDataPointRT_for_DataPointSyncMode_LOW_then_not_equals_other_type() throws Exception {

        //given:
        DataPointSyncMode dataPointSyncMode = DataPointSyncMode.LOW;
        when(systemSettingsServiceMock.getDataPointRtValueSynchronized()).thenReturn(dataPointSyncMode);
        whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsServiceMock);

        //when:
        DataPointRT dataPointRT = RuntimeManager.createDataPointRT(dataPointVO);

        //then:
        assertNotEquals(DataPointSyncMode.HIGH.getType(), dataPointRT.getClass());
    }
}