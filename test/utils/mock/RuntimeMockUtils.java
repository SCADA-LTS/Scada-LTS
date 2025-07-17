package utils.mock;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.util.timeout.TimeoutTask;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.web.ContextWrapper;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.ws.services.DataPointServiceWebSocket;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public final class RuntimeMockUtils {

    private RuntimeMockUtils() {}

    public static void runtimeManagerMock(RuntimeManager runtimeManager, EventManager eventManager) throws Exception {
        BackgroundProcessing backgroundProcessing = PowerMockito.mock(BackgroundProcessing.class);
        runtimeManagerMock(runtimeManager, eventManager, backgroundProcessing);
    }

    public static void runtimeManagerMock(RuntimeManager runtimeManager, EventManager eventManager,
                                          BackgroundProcessing backgroundProcessing) throws Exception {
        ContextWrapper contextWrapper = mock(ContextWrapper.class);

        when(contextWrapper.getRuntimeManager()).thenReturn(runtimeManager);
        when(contextWrapper.getEventManager()).thenReturn(eventManager);
        when(contextWrapper.getBackgroundProcessing()).thenReturn(backgroundProcessing);

        TimeoutTask timeoutTaskMock = mock(TimeoutTask.class);
        whenNew(TimeoutTask.class).withAnyArguments()
                .thenReturn(timeoutTaskMock);

        Common.ctx = contextWrapper;
        Common.timer.init();
    }

    public static void mockingServices(List<DataPointVO> dataPoints, List<DataSourceVO<?>> dataSources) throws Exception {
        DataSourceService dataSourceService = PowerMockito.mock(DataSourceService.class);
        when(dataSourceService.getDataSources()).thenReturn(dataSources);
        whenNew(DataSourceService.class).withNoArguments().thenReturn(dataSourceService);

        DataPointService dataPointService = PowerMockito.mock(DataPointService.class);
        when(dataPointService.getDataPoints(ArgumentMatchers.isNull(), anyBoolean())).thenReturn(dataPoints);
        whenNew(DataPointService.class).withNoArguments().thenReturn(dataPointService);

        SystemSettingsService systemSettingsService = PowerMockito.mock(SystemSettingsService.class);
        whenNew(SystemSettingsService.class).withNoArguments().thenReturn(systemSettingsService);

        SystemSettingsDAO systemSettingsDAO = PowerMockito.mock(SystemSettingsDAO.class);
        whenNew(SystemSettingsDAO.class).withNoArguments().thenReturn(systemSettingsDAO);

        mockStatic(SystemSettingsDAO.class);
        when(SystemSettingsDAO.getValue(anyString())).thenReturn("");
        when(SystemSettingsDAO.getFutureDateLimit()).thenReturn(1L);

        PointValueService mangoPointValues = PowerMockito.mock(PointValueService.class);
        whenNew(PointValueService.class).withNoArguments().thenReturn(mangoPointValues);

        mockStatic(ApplicationBeans.class);
        DataPointServiceWebSocket dataPointServiceWebSocket = mock(DataPointServiceWebSocket.class);
        when(ApplicationBeans.getDataPointServiceWebSocketBean()).thenReturn(dataPointServiceWebSocket);
    }
}
