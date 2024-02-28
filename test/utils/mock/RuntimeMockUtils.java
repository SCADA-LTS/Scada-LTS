package utils.mock;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.mango.web.ContextWrapper;
import org.powermock.api.mockito.PowerMockito;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.ws.services.DataPointServiceWebSocket;

import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public final class RuntimeMockUtils {

    private RuntimeMockUtils() {}

    public static void runtimeManagerMock(RuntimeManager runtimeManager, EventManager eventManager) throws Exception {

        DataSourceService dataSourceService = PowerMockito.mock(DataSourceService.class);
        whenNew(DataSourceService.class).withNoArguments().thenReturn(dataSourceService);

        DataPointService dataPointService = PowerMockito.mock(DataPointService.class);
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
        DataPointServiceWebSocket dataPointServiceWebSocket = PowerMockito.mock(DataPointServiceWebSocket.class);
        when(ApplicationBeans.getDataPointServiceWebSocketBean()).thenReturn(dataPointServiceWebSocket);

        ContextWrapper contextWrapper = PowerMockito.mock(ContextWrapper.class);
        BackgroundProcessing backgroundProcessing = PowerMockito.mock(BackgroundProcessing.class);

        when(contextWrapper.getRuntimeManager()).thenReturn(runtimeManager);
        when(contextWrapper.getEventManager()).thenReturn(eventManager);
        when(contextWrapper.getBackgroundProcessing()).thenReturn(backgroundProcessing);

        Common.ctx = contextWrapper;
        Common.timer.init();

    }
}
