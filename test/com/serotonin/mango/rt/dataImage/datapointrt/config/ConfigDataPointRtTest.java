package com.serotonin.mango.rt.dataImage.datapointrt.config;

import com.serotonin.mango.db.DatabaseAccess;
import org.springframework.jdbc.core.JdbcTemplate;
import utils.TestUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.*;
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
import com.serotonin.timer.RealTimeTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.dao.pointvalues.IPointValueDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.login.ILoggedUsers;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.beans.GetApplicationBeans;
import org.scada_lts.web.ws.services.DataPointServiceWebSocket;
import utils.PointValueDAOMemory;
import org.springframework.context.ApplicationContext;
import utils.UsersDAOMemory;
import utils.mock.PowerMockUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({DAO.class, Common.class, PointValueDAO.class, DataPointDao.class, DataSourceDao.class,
        VirtualDataSourceRT.class, RuntimeManager.class, PointValueService.class, IPointValueDAO.class, ApplicationBeans.class, PointValueDao.class,
        SystemSettingsDAO.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class ConfigDataPointRtTest {

    private static final Log LOG = LogFactory.getLog(ConfigDataPointRtTest.class);

    private static final IPointValueDAO pointValueDAOMemory;
    private static final IUserDAO usersDAOMemory = new UsersDAOMemory();
    private static final User user;

    static {
        User testUser = TestUtils.newUser(123);
        usersDAOMemory.insert(testUser);
        user = testUser;
        pointValueDAOMemory = new PointValueDAOMemory(usersDAOMemory);
    }
    private static final int NUMBER_OF_TESTS = 2;

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
    private final int numberOfLaunches = 2;

    private final DataPointSyncMode sync;

    private RuntimeManager runtimeManagerMock;
    private IPointValueDAO pointValueDAOMock;
    private DataSourceVO dataSourceVO;
    private DataPointVO dataPointVO;

    public ConfigDataPointRtTest(DataPointSyncMode sync, Object oldValue, Object newValue, Object newValue2,
                                 int dataTypeId, String dataType, String startValue) {
        setPointValueTime(oldValue, newValue, newValue2);
        this.dataTypeId = dataTypeId;
        this.dataType = dataType;
        this.startValue = startValue;
        this.tolerance = 0.0;
        setPointValueTime(this.oldValue, this.newValue, this.newValue2, user);
        this.sync = sync;
        init();
    }

    public ConfigDataPointRtTest(DataPointSyncMode sync, Object oldValue, Object newValue, Object newValue2,
                                 int dataTypeId, String dataType, String startValue, double tolerance) {
        setPointValueTime(oldValue, newValue, newValue2);
        this.dataTypeId = dataTypeId;
        this.dataType = dataType;
        this.startValue = startValue;
        this.tolerance = tolerance;
        setPointValueTime(this.oldValue, this.newValue, this.newValue2, user);
        this.sync = sync;
        init();
    }

    private void setPointValueTime(Object oldValue, Object newValue, Object newValue2) {
        this.oldValue = new PointValueTime(MangoValue.objectToValue(oldValue), System.currentTimeMillis());
        this.newValue = new PointValueTime(MangoValue.objectToValue(newValue), System.currentTimeMillis() + 10);
        this.newValue2 = new PointValueTime(MangoValue.objectToValue(newValue2), System.currentTimeMillis() + 15);
    }

    private void setPointValueTime(PointValueTime oldValue, PointValueTime newValue, PointValueTime newValue2, User user) {
        AnnotatedPointValueTime oldValueWithUser = new AnnotatedPointValueTime(oldValue.getValue(), oldValue.getTime(), SetPointSource.Types.USER, user.getId());
        oldValueWithUser.setSourceDescriptionArgument(user.getUsername());
        this.oldValueWithUser = oldValueWithUser;

        AnnotatedPointValueTime newValueWithUser = new AnnotatedPointValueTime(newValue.getValue(), newValue.getTime(), SetPointSource.Types.USER, user.getId());
        newValueWithUser.setSourceDescriptionArgument(user.getUsername());
        this.newValueWithUser = newValueWithUser;

        AnnotatedPointValueTime newValueWithUser2 =  new AnnotatedPointValueTime(newValue2.getValue(), newValue2.getTime(), SetPointSource.Types.USER, user.getId());
        newValueWithUser2.setSourceDescriptionArgument(user.getUsername());
        this.newValueWithUser2 = newValueWithUser2;
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
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        GetApplicationBeans getApplicationBeans = new GetApplicationBeans();
        getApplicationBeans.setApplicationContext(applicationContext);

        PowerMockUtils.configDaoMock();

        ILoggedUsers loggedUsers = mock(ILoggedUsers.class);
        when(ApplicationBeans.getLoggedUsersBean()).thenReturn(loggedUsers);
        when(loggedUsers.getUser(eq(user.getId()))).thenReturn(user);

        DataSource dsMock = mock(DataSource.class);
        when(ApplicationBeans.getBean("databaseSource", DataSource.class)).thenReturn(dsMock);

        dataSourceVO = createDataSource();
        dataPointVO = createDataPoint(defaultCacheSize, tolerance, startValue, dataTypeId, dataSourceVO);

        pointValueDAOMock = mock(PointValueDAOMemory.class);

        when(pointValueDAOMock.applyBounds(anyDouble())).thenAnswer(inv -> inv.getArgument(0));

        when(pointValueDAOMock.createAnnotation(anyLong(), anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(a -> pointValueDAOMemory.createAnnotation((long)a.getArgument(0), a.getArgument(1), a.getArgument(2), a.getArgument(3), a.getArgument(4)));
        when(pointValueDAOMock.createAnnotation(anyLong(), isNull(), anyString(), anyInt(), anyInt()))
                .thenAnswer(a -> pointValueDAOMemory.createAnnotation((long)a.getArgument(0), a.getArgument(1), a.getArgument(2), a.getArgument(3), a.getArgument(4)));
        when(pointValueDAOMock.createAnnotation(anyLong(), anyString(), isNull(), anyInt(), anyInt()))
                .thenAnswer(a -> pointValueDAOMemory.createAnnotation((long)a.getArgument(0), a.getArgument(1), a.getArgument(2), a.getArgument(3), a.getArgument(4)));
        when(pointValueDAOMock.createAnnotation(anyLong(), isNull(), isNull(), anyInt(), anyInt()))
                .thenAnswer(a -> pointValueDAOMemory.createAnnotation((long)a.getArgument(0), a.getArgument(1), a.getArgument(2), a.getArgument(3), a.getArgument(4)));

        when(pointValueDAOMock.create(anyInt(), anyInt(), anyDouble(), anyLong()))
                .thenAnswer(a -> pointValueDAOMemory.create((int)a.getArgument(0), (int)a.getArgument(1), (double)a.getArgument(2), (long)a.getArgument(3)));

        when(pointValueDAOMock.getPointValue(anyLong()))
                .thenAnswer(a -> pointValueDAOMemory.getPointValue((long)a.getArgument(0)));

        mockStatic(ApplicationBeans.class);
        when(ApplicationBeans.getPointValueDAOBean()).thenReturn(pointValueDAOMock);

        DatabaseAccess dbAccessMock = mock(DatabaseAccess.class);
        when(ApplicationBeans.getBean("databaseAccess", DatabaseAccess.class)).thenReturn(dbAccessMock);
        when(dbAccessMock.getTypeKey()).thenReturn("mysql");

        JdbcTemplate jdbcTemplateMock = mock(JdbcTemplate.class);
        DAO daoMock = mock(DAO.class);
        when(daoMock.getJdbcTemp()).thenReturn(jdbcTemplateMock);
        mockStatic(DAO.class);
        when(DAO.getInstance()).thenReturn(daoMock);
        when(jdbcTemplateMock.queryForObject(anyString(), any(), eq(Long.class))).thenReturn(1L);

        RealTimeTimer realTimeTimerMock = mock(RealTimeTimer.class);
        whenNew(RealTimeTimer.class).withNoArguments().thenReturn(realTimeTimerMock);

        ContextWrapper contextWrapper = mock(ContextWrapper.class);
        Common.ctx = contextWrapper;

        RuntimeManager runtimeManager = new RuntimeManager();
        runtimeManagerMock = mock(RuntimeManager.class);

        doAnswer(a -> {
            runtimeManager.saveDataPoint(a.getArgument(0));
            return null;
        }).when(runtimeManagerMock).saveDataPoint(any(DataPointVO.class));

        doAnswer(a -> {
            runtimeManager.saveDataSource(a.getArgument(0));
            return null;
        }).when(runtimeManagerMock).saveDataSource(any(DataSourceVO.class));

        when(runtimeManagerMock.getDataPoint(anyInt()))
                .thenAnswer(a -> runtimeManager.getDataPoint(a.getArgument(0)));
        when(contextWrapper.getRuntimeManager()).thenReturn(runtimeManagerMock);

        BackgroundProcessing backgroundProcessingMock = mock(BackgroundProcessing.class);
        when(contextWrapper.getBackgroundProcessing()).thenReturn(backgroundProcessingMock);

        EventManager eventManagerMock = mock(EventManager.class);
        when(contextWrapper.getEventManager()).thenReturn(eventManagerMock);

        DataPointService dataPointServiceMock = mock(DataPointService.class);
        whenNew(DataPointService.class).withNoArguments().thenReturn(dataPointServiceMock);
        when(dataPointServiceMock.getDataPoint(anyInt())).thenReturn(dataPointVO);

        DataSourceService dataSourceServiceMock = mock(DataSourceService.class);
        whenNew(DataSourceService.class).withNoArguments().thenReturn(dataSourceServiceMock);
        when(dataSourceServiceMock.getDataSource(anyInt())).thenReturn(dataSourceVO);

        TimeoutTask timeoutTaskMock = mock(TimeoutTask.class);
        whenNew(TimeoutTask.class).withAnyArguments().thenReturn(timeoutTaskMock);

        SystemSettingsService systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getDataPointRtValueSynchronized()).thenReturn(sync);
        whenNew(SystemSettingsService.class).withNoArguments().thenReturn(systemSettingsServiceMock);

        DataPointServiceWebSocket dataPointServiceWebSocket = mock(DataPointServiceWebSocket.class);
        when(ApplicationBeans.getDataPointServiceWebSocketBean()).thenReturn(dataPointServiceWebSocket);

        IUserDAO userDAO = mock(IUserDAO.class);
        when(ApplicationBeans.getUserDaoBean()).thenReturn(userDAO);
    }


    protected DataPointVO createDataPoint(int defaultCacheSize,
                                          double tolerance, String startValue,
                                          int dataTypeId, DataSourceVO<?> dataSourceVO) {
        VirtualPointLocatorVO virtualPointLocatorVO = new VirtualPointLocatorVO();
        virtualPointLocatorVO.setDataTypeId(dataTypeId);
        virtualPointLocatorVO.setChangeTypeId(ChangeTypeVO.Types.NO_CHANGE);
        virtualPointLocatorVO.getNoChange().setStartValue(startValue);

        DataPointVO dataPointVO = TestUtils.newDefaultEmptyDataPointVO();
        dataPointVO.setId(321);
        dataPointVO.setName("test_dp");
        dataPointVO.setXid("test_dp_xid");
        dataPointVO.setDefaultCacheSize(defaultCacheSize);
        dataPointVO.setTolerance(tolerance);
        dataPointVO.setPointLocator(virtualPointLocatorVO);
        dataPointVO.setEventDetectors(new ArrayList<>());
        dataPointVO.setEnabled(true);
        dataPointVO.setDataSourceId(dataSourceVO.getId());
        dataPointVO.setDataSourceName(dataSourceVO.getName());
        dataPointVO.setDeviceName(dataSourceVO.getName());
        return dataPointVO;
    }

    protected DataSourceVO<?> createDataSource() {
        DataSourceVO<?> dataSourceVO = new VirtualDataSourceVO();
        dataSourceVO.setEnabled(true);
        dataSourceVO.setId(567);
        dataSourceVO.setName("test_ds");
        dataSourceVO.setXid("test_ds_xid");
        return dataSourceVO;
    }

    public void clear() {
        ((PointValueDAOMemory)pointValueDAOMemory).clear();
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

    protected List<PointValueTime> getPointValuesWithUserDaoTestExpected() {
        return getPointValuesWithUserExpected();
    }

    protected List<PointValueTime> getPointValuesDaoTestExpected() {
        return getPointValuesExpected();
    }

    protected List<PointValueTime> getPointValuesDaoTestExpected(List<Double> exepected) {
        return getPointValuesExpected(exepected);
    }

    protected List<PointValueTime> getPointValuesWithUserDaoTestExpected(List<Double> exepected) {
        return getPointValuesDaoTestExpected(exepected);
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

    public IPointValueDAO getPointValueDAOMock() {
        return pointValueDAOMock;
    }

    public DataSourceVO getDataSourceVO() {
        return dataSourceVO;
    }

    public DataPointVO getDataPointVO() {
        return dataPointVO;
    }
}
