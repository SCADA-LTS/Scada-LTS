package com.serotonin.mango.rt.dataSource.meta;

import br.org.scadabr.rt.scripting.context.ScriptContextObject;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;

import com.serotonin.mango.vo.permission.Permissions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.mock.MockUtils;

import java.util.ArrayList;

import static com.serotonin.mango.Common.timer;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Common.class, Permissions.class, ScriptContextObject.class, ApplicationBeans.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class MetaPointLocatorRTPointChangedPointUpdatedTest {

    private final MetaPointLocatorVO vo = new MetaPointLocatorVO();
    private DataPointRT dataPoint;
    private MetaPointLocatorRT locatorRT;

    @Before
    public void config() throws Exception {
        dataPoint = mock(DataPointRT.class);
        MetaDataSourceRT dataSource = mock(MetaDataSourceRT.class);
        RuntimeManager runtimeManagerMock = mock(RuntimeManager.class);
        MockUtils.configMockContextWrapper(runtimeManagerMock);
        vo.setContext(new ArrayList<>());
        vo.setDataTypeId(DataTypes.NUMERIC);
        vo.setScript("return 1;");
        locatorRT = new MetaPointLocatorRT(vo);
        locatorRT.initialize(timer, dataSource, dataPoint);
    }

    @After
    public void resetMock() {
        reset(dataPoint);
    }



    @Test
    public void test_updatePointValue_In_pointChanged_For_ContextChange_Happens_once() throws DataPointStateException {
        //given:
        vo.setUpdateEvent(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE);
        PointValueTime pointValueTime = new PointValueTime(MangoValue.objectToValue(1),534534L);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.objectToValue(2),53453L);

        //when:
        locatorRT.pointChanged(pointValueTime, pointValueTime2);

        //than:
        verify(dataPoint, times(1)).updatePointValue(any(PointValueTime.class));
    }

    @Test
    public void test_updatePointValue_In_pointUpdated_For_ContextChange_Happens_never(){
        //given:
        vo.setUpdateEvent(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE);
        PointValueTime pointValueTime = new PointValueTime(MangoValue.objectToValue(1),534534L);

        //when:
        locatorRT.pointUpdated(pointValueTime);

        //than:
        verify(dataPoint, never()).updatePointValue(any(PointValueTime.class));
    }

    @Test
    public void test_updatePointValue_In_pointChanged_For_ContextUpdate_Happens_never(){
        //given:
        vo.setUpdateEvent(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE);
        PointValueTime pointValueTime = new PointValueTime(MangoValue.objectToValue(1),534534L);
        PointValueTime pointValueTime2 = new PointValueTime(MangoValue.objectToValue(2),53453L);

        //when:
        locatorRT.pointChanged(pointValueTime, pointValueTime2);

        //than:
        verify(dataPoint, never()).updatePointValue(any(PointValueTime.class));
    }

    @Test
    public void test_updatePointValue_In_pointUpdated_For_ContextUpdate_Happens_never(){
        //given:
        vo.setUpdateEvent(MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE);
        PointValueTime pointValueTime = new PointValueTime(MangoValue.objectToValue(1),534534L);

        //when:
        locatorRT.pointUpdated(pointValueTime);

        //than:
        verify(dataPoint, times(1)).updatePointValue(any(PointValueTime.class));
    }

}