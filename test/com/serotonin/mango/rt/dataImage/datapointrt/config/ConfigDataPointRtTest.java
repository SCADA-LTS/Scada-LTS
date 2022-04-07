package com.serotonin.mango.rt.dataImage.datapointrt.config;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.virtual.VirtualDataSourceRT;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.util.timeout.TimeoutTask;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.ChangeTypeVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.web.ContextWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.model.point.PointValueAdnnotation;
import org.scada_lts.dao.pointvalues.PointValueAdnnotationsDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.springframework.jdbc.core.JdbcTemplate;
import utils.PointValueDAOMemory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.clearInvocations;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({DAO.class, Common.class, PointValueDAO.class, PointValueAdnnotationsDAO.class,
        DataPointDao.class, DataSourceDao.class, VirtualDataSourceRT.class, RuntimeManager.class,
        PointValueService.class, PointValueDAO.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class ConfigDataPointRtTest {

    private static final Log LOG = LogFactory.getLog(ConfigDataPointRtTest.class);

    private static final PointValueDAOMemory pointValueDAOMemory = new PointValueDAOMemory();
    private static final int NUMBER_OF_TESTS = 20;

    private PointValueTime oldValue;
    private PointValueTime oldValueWithUser;
    private PointValueTime newValue;
    private PointValueTime newValueWithUser;
    private PointValueTime newValue2;
    private PointValueTime newValueWithUser2;

    private final int dataTypeId;
    private final String dataType;
    private final String startValue;
    private final double tolerance;

    private final int defaultCacheSize = 30;
    private final int numberOfLaunches = 10;
    private final User user;
    private final DataPointSyncMode sync;

    private RuntimeManager runtimeManagerMock;
    private PointValueDAO pointValueDAOMock;
    private DataSourceVO dataSourceVO;
    private DataPointVO dataPointVO;

    public ConfigDataPointRtTest(DataPointSyncMode sync, Object oldValue, Object newValue, Object newValue2,
                                 int dataTypeId, String dataType, String startValue) {
        setPointValueTime(oldValue, newValue, newValue2, dataTypeId);
        this.dataTypeId = dataTypeId;
        this.dataType = dataType;
        this.startValue = startValue;
        this.tolerance = 0.0;
        this.user = TestUtils.newUser(123);
        setPointValueTimeWithUser(oldValue, newValue, newValue2, dataTypeId);
        this.sync = sync;
        init();
    }

    public ConfigDataPointRtTest(DataPointSyncMode sync, Object oldValue, Object newValue, Object newValue2,
                                 int dataTypeId, String dataType, String startValue, double tolerance) {
        setPointValueTime(oldValue, newValue, newValue2, dataTypeId);
        this.dataTypeId = dataTypeId;
        this.dataType = dataType;
        this.startValue = startValue;
        this.tolerance = tolerance;
        this.user = TestUtils.newUser(123);
        setPointValueTimeWithUser(oldValue, newValue, newValue2, dataTypeId);
        this.sync = sync;
        init();
    }

    private void setPointValueTime(Object oldValue, Object newValue, Object newValue2, int dataTypeId) {
        if(dataTypeId == DataTypes.ALPHANUMERIC) {
            this.oldValue = new AnnotatedPointValueTime(MangoValue.objectToValue(oldValue), System.currentTimeMillis(), 1, 123);
            this.newValue = new AnnotatedPointValueTime(MangoValue.objectToValue(newValue), System.currentTimeMillis() + 10, 1, 123);
            this.newValue2 = new AnnotatedPointValueTime(MangoValue.objectToValue(newValue2), System.currentTimeMillis() + 15, 1, 123);

        } else {
            this.oldValue = new PointValueTime(MangoValue.objectToValue(oldValue), System.currentTimeMillis());
            this.newValue = new PointValueTime(MangoValue.objectToValue(newValue), System.currentTimeMillis() + 10);
            this.newValue2 = new PointValueTime(MangoValue.objectToValue(newValue2), System.currentTimeMillis() + 15);

        }
    }

    private void setPointValueTimeWithUser(Object oldValue, Object newValue, Object newValue2, int dataTypeId) {
        if(dataTypeId == DataTypes.ALPHANUMERIC) {
            this.oldValueWithUser = new AnnotatedPointValueTime(MangoValue.objectToValue(oldValue), this.oldValue.getTime(), 1, 123);
            this.oldValueWithUser.setWhoChangedValue(user.getUsername());

            this.newValueWithUser =  new AnnotatedPointValueTime(MangoValue.objectToValue(newValue), this.newValue.getTime(), 1, 123);
            this.newValueWithUser.setWhoChangedValue(user.getUsername());

            this.newValueWithUser2 =  new AnnotatedPointValueTime(MangoValue.objectToValue(newValue2), this.newValue2.getTime(), 1, 123);
            this.newValueWithUser2.setWhoChangedValue(user.getUsername());

        } else {
            this.oldValueWithUser = new PointValueTime(MangoValue.objectToValue(oldValue), this.oldValue.getTime());
            this.oldValueWithUser.setWhoChangedValue(user.getUsername());

            this.newValueWithUser = new PointValueTime(MangoValue.objectToValue(newValue), this.newValue.getTime());
            this.newValueWithUser.setWhoChangedValue(user.getUsername());

            this.newValueWithUser2 = new PointValueTime(MangoValue.objectToValue(newValue2), this.newValue2.getTime());
            this.newValueWithUser2.setWhoChangedValue(user.getUsername());

        }
    }

    private void init() {
        try {
            preconfig();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    private void preconfig() throws Exception {
        DAO dao = mock(DAO.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(dao.getJdbcTemp()).thenReturn(jdbcTemplate);
        whenNew(DAO.class)
                .withNoArguments()
                .thenReturn(dao);

        dataSourceVO = new VirtualDataSourceVO();
        dataSourceVO.setEnabled(true);
        dataSourceVO.setId(567);
        dataSourceVO.setName("test_ds");
        dataSourceVO.setXid("test_xid");

        VirtualPointLocatorVO virtualPointLocatorVO = new VirtualPointLocatorVO();
        virtualPointLocatorVO.setDataTypeId(dataTypeId);
        virtualPointLocatorVO.setChangeTypeId(ChangeTypeVO.Types.NO_CHANGE);
        virtualPointLocatorVO.getNoChange().setStartValue(startValue);

        dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPointVO.setId(321);
        dataPointVO.setDefaultCacheSize(defaultCacheSize);
        dataPointVO.setTolerance(tolerance);
        dataPointVO.setPointLocator(virtualPointLocatorVO);
        dataPointVO.setEventDetectors(new ArrayList<>());
        dataPointVO.setEnabled(true);
        dataPointVO.setDataSourceId(dataSourceVO.getId());
        dataPointVO.setDataSourceName(dataSourceVO.getName());
        dataPointVO.setDeviceName(dataSourceVO.getName());

        PointValueAdnnotationsDAO pointValueAdnnotationsDAOMock = mock(PointValueAdnnotationsDAO.class);
        when(pointValueAdnnotationsDAOMock.create(any(PointValueAdnnotation.class))).thenAnswer(a -> {
            Object[] args = a.getArguments();
            return pointValueDAOMemory.create((PointValueAdnnotation)args[0]);
        });
        whenNew(PointValueAdnnotationsDAO.class)
                .withNoArguments()
                .thenReturn(pointValueAdnnotationsDAOMock);

        pointValueDAOMock = mock(PointValueDAO.class);
        when(pointValueDAOMock.create(anyInt(), anyInt(), anyDouble(), anyLong()))
                .thenAnswer(a -> {
                    Object[] args = a.getArguments();
                    return pointValueDAOMemory.create((int)args[0], (int)args[1], (double)args[2], (long)args[3]);
                });

        when(pointValueDAOMock.getPointValue(anyLong())).thenAnswer(a -> {
            Object[] args = a.getArguments();
            return pointValueDAOMemory.getPointValue((long)args[0]);
        });
        when(pointValueDAOMock.applyBounds(anyDouble())).thenCallRealMethod();

        mockStatic(PointValueDAO.class);
        when(PointValueDAO.getInstance()).thenReturn(pointValueDAOMock);

        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        Common.ctx = contextWrapper;

        RuntimeManager runtimeManager = new RuntimeManager();
        runtimeManagerMock = mock(RuntimeManager.class);
        doAnswer(a -> {
            runtimeManager.saveDataPoint((DataPointVO)a.getArguments()[0]);
            return null;
        }).when(runtimeManagerMock).saveDataPoint(any(DataPointVO.class));

        doAnswer(a -> {
            runtimeManager.saveDataSource((DataSourceVO)a.getArguments()[0]);
            return null;
        }).when(runtimeManagerMock).saveDataSource(any(DataSourceVO.class));

        when(runtimeManagerMock.getDataPoint(anyInt()))
                .thenAnswer(a -> runtimeManager.getDataPoint((int)a.getArguments()[0]));
        when(contextWrapper.getRuntimeManager()).thenReturn(runtimeManagerMock);

        BackgroundProcessing backgroundProcessingMock = mock(BackgroundProcessing.class);
        when(contextWrapper.getBackgroundProcessing()).thenReturn(backgroundProcessingMock);

        EventManager eventManagerMock = mock(EventManager.class);
        when(contextWrapper.getEventManager()).thenReturn(eventManagerMock);

        DataPointService dataPointServiceMock = mock(DataPointService.class);
        whenNew(DataPointService.class)
                .withNoArguments()
                .thenReturn(dataPointServiceMock);
        when(dataPointServiceMock.getDataPoint(anyInt())).thenReturn(dataPointVO);

        DataSourceService dataSourceServiceMock = mock(DataSourceService.class);
        whenNew(DataSourceService.class)
                .withNoArguments()
                .thenReturn(dataSourceServiceMock);
        when(dataSourceServiceMock.getDataSource(anyInt())).thenReturn(dataSourceVO);

        TimeoutTask timeoutTaskMock = mock(TimeoutTask.class);
        whenNew(TimeoutTask.class)
                .withAnyArguments()
                .thenReturn(timeoutTaskMock);

        SystemSettingsService systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getDataPointRtValueSynchronized()).thenReturn(sync);
        whenNew(SystemSettingsService.class)
                .withNoArguments()
                .thenReturn(systemSettingsServiceMock);
    }

    public void clear() {
        pointValueDAOMemory.clear();
    }

    protected static int getNumberOfTests() {
        return NUMBER_OF_TESTS;
    }

    protected DataPointRT start() {
        runtimeManagerMock.saveDataSource(dataSourceVO);
        runtimeManagerMock.saveDataPoint(dataPointVO);
        return runtimeManagerMock.getDataPoint(dataPointVO.getId());
    }

    protected List<PointValueTime> getPointValuesWithUserExpected(List<Double> exepected) {
        PointValueTime oldValue = getOldValueWithUser();
        PointValueTime newValue = getNewValueWithUser();
        PointValueTime newValue2 = getNewValueWithUser2();

        List<PointValueTime> pointValuesExpected = new ArrayList<>();
        pointValuesExpected.add(oldValue);
        for(double i: exepected) {
            if(oldValue.getValue().equals(MangoValue.objectToValue(i))) {
                continue;
            }
            if(newValue.getValue().equals(MangoValue.objectToValue(i))) {
                pointValuesExpected.add(newValue);
            } else if(newValue2.getValue().equals(MangoValue.objectToValue(i))) {
                pointValuesExpected.add(newValue2);
            }
        }
        pointValuesExpected.sort(Comparator.comparing(PointValueTime::getTime).reversed());
        return pointValuesExpected;
    }

    protected List<PointValueTime> getPointValuesExpected(List<Double> exepected) {
        PointValueTime oldValue = getOldValue();
        PointValueTime newValue = getNewValue();
        PointValueTime newValue2 = getNewValue2();

        List<PointValueTime> pointValuesExpected = new ArrayList<>();
        pointValuesExpected.add(oldValue);
        for(double i: exepected) {
            if(oldValue.getValue().equals(MangoValue.objectToValue(i))) {
                continue;
            }
            if(newValue.getValue().equals(MangoValue.objectToValue(i))) {
                pointValuesExpected.add(newValue);
            } else if(newValue2.getValue().equals(MangoValue.objectToValue(i))) {
                pointValuesExpected.add(newValue2);
            }
        }
        pointValuesExpected.sort(Comparator.comparing(PointValueTime::getTime).reversed());
        return pointValuesExpected;
    }

    protected List<PointValueTime> getPointValuesExpected() {
        PointValueTime oldValue = getOldValue();
        PointValueTime newValue = getNewValue();
        PointValueTime newValue2 = getNewValue2();
        List<PointValueTime> pointValuesExpected = new ArrayList<>();
        pointValuesExpected.add(oldValue);
        pointValuesExpected.add(newValue);
        pointValuesExpected.add(newValue2);
        pointValuesExpected.sort(Comparator.comparing(PointValueTime::getTime).reversed());
        return pointValuesExpected;
    }

    protected List<PointValueTime> getPointValuesWithUserExpected() {
        PointValueTime oldValue = getOldValueWithUser();
        PointValueTime newValue = getNewValueWithUser();
        PointValueTime newValue2 = getNewValueWithUser2();
        List<PointValueTime> pointValuesExpected = new ArrayList<>();
        pointValuesExpected.add(oldValue);
        pointValuesExpected.add(newValue);
        pointValuesExpected.add(newValue2);
        pointValuesExpected.sort(Comparator.comparing(PointValueTime::getTime).reversed());
        return pointValuesExpected;
    }

    protected void initValueByUser(DataPointRT dataPointRT) {
        dataPointRT.setPointValue(getOldValue(), getUser());
    }

    protected void initValue(DataPointRT dataPointRT) {
        dataPointRT.setPointValue(getOldValue(), null);
    }

    protected PointValueTime getOldValue() {
        return oldValue;
    }

    protected PointValueTime getOldValueWithUser() {
        return oldValueWithUser;
    }

    protected PointValueTime getNewValue() {
        return newValue;
    }

    protected PointValueTime getNewValueWithUser() {
        return newValueWithUser;
    }

    protected PointValueTime getNewValue2() {
        return newValue2;
    }

    protected PointValueTime getNewValueWithUser2() {
        return newValueWithUser2;
    }

    protected int getDataTypeId() {
        return dataTypeId;
    }

    protected String getDataType() {
        return dataType;
    }

    protected String getStartValue() {
        return startValue;
    }

    protected User getUser() {
        return user;
    }

    protected int getDefaultCacheSize() {
        return defaultCacheSize;
    }

    protected int getNumberOfLaunches() {
        return numberOfLaunches;
    }

    public PointValueDAO getPointValueDAOMock() {
        return pointValueDAOMock;
    }
}
