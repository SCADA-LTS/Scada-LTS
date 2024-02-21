package com.serotonin.mango.rt.dataSource.meta;

import br.org.scadabr.rt.scripting.context.DPCommandsScriptContextObject;
import br.org.scadabr.rt.scripting.context.ScriptContextObject;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;

import com.serotonin.mango.vo.permission.Permissions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.ScriptTestUtils;

import static com.serotonin.mango.Common.timer;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetaPointLocatorRT.class, Common.class, Permissions.class, ScriptContextObject.class, ApplicationBeans.class})
@PowerMockIgnore({ "javax.xml.*", "org.xml.*", "javax.management.*", "javax.script.*"})
public class MetaPointLocatorRTTest {

    private final MetaPointLocatorVO vo = mock(MetaPointLocatorVO.class);
    private final DataPointRT dataPoint = mock(DataPointRT.class);
    private final MetaDataSourceRT dataSource = mock(MetaDataSourceRT.class);
    private MetaPointLocatorRT locatorRT = mock(MetaPointLocatorRT.class);
    private final RuntimeManager runtimeManagerMock = mock(RuntimeManager.class);
    private final DPCommandsScriptContextObject scriptContextObject = mock(DPCommandsScriptContextObject.class);
    private final MetaPointLocatorRT.ExecutionDelayTimeout executionDelayTimeout = mock(MetaPointLocatorRT.ExecutionDelayTimeout.class);
    private final MetaPointLocatorRT.ScheduledUpdateTimeout scheduledUpdateTimeout = mock(MetaPointLocatorRT.ScheduledUpdateTimeout.class);

    @Before
    public void config() throws Exception {
        ScriptTestUtils.configMock(runtimeManagerMock, scriptContextObject);
        locatorRT = new MetaPointLocatorRT(vo);
        locatorRT.dataPoint = dataPoint;

        locatorRT = Mockito.spy(new MetaPointLocatorRT(vo));

        Mockito.doNothing().when(locatorRT).initializeTimerTask();
    }

    @Test
    public void testUpdatePointCalledForContextChangeEvent(){
        when(vo.getUpdateEvent()).thenReturn(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE);
        when(dataPoint.getPointValue()).thenReturn(mock(PointValueTime.class));
        when(vo.getDataTypeId()).thenReturn(DataTypes.NUMERIC);
        when(vo.getScript()).thenReturn("return 1;");

        locatorRT.initialize(timer, dataSource, dataPoint);
        locatorRT.pointChanged(mock(PointValueTime.class), mock(PointValueTime.class));
        locatorRT.pointUpdated(mock(PointValueTime.class));
        scheduledUpdateTimeout.run(anyLong());
        executionDelayTimeout.run(anyLong());

        verify(dataPoint, times(2)).updatePointValue(any(PointValueTime.class));
    }

    @Test
    public void testUpdatePointNotCalledForOtherEvents() {

        when(vo.getUpdateEvent()).thenReturn(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE);
        locatorRT.initialize(timer, dataSource, dataPoint);
        locatorRT.pointUpdated(mock(PointValueTime.class));
        scheduledUpdateTimeout.run(anyLong());
        executionDelayTimeout.run(anyLong());

        when(vo.getUpdateEvent()).thenReturn(MetaPointLocatorVO.UPDATE_EVENT_CRON);
        locatorRT.initialize(timer, dataSource, dataPoint);
        locatorRT.pointUpdated(mock(PointValueTime.class));
        scheduledUpdateTimeout.run(anyLong());
        executionDelayTimeout.run(anyLong());

        verify(dataPoint, never()).updatePointValue(any(PointValueTime.class));
    }
}