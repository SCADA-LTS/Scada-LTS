package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.timer.RealTimeTimer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import utils.mock.MockUtils;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({MetaPointLocatorRT.ScheduledUpdateTimeout.class, MetaPointLocatorRT.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class MetaPointLocatorRtInitializeTest {

    @Parameterized.Parameters(name= "{index}: Update event type: {0}, Expected times of invocation: {1}")
    public static Object[] data() {
        return new Object[] {
                new Object[] {MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE, 1},
                new Object[] {MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE, 1},
                new Object[] {MetaPointLocatorVO.UPDATE_EVENT_CRON, 0},
                new Object[] {Common.TimePeriods.MINUTES, 0},
                new Object[] {Common.TimePeriods.HOURS, 0},
                new Object[] {Common.TimePeriods.DAYS, 0},
                new Object[] {Common.TimePeriods.WEEKS, 0},
                new Object[] {Common.TimePeriods.MONTHS, 0},
                new Object[] {Common.TimePeriods.YEARS, 0},
        };
    }

    private final DataPointRT dataPoint;
    private MetaDataSourceRT dataSource;
    private final int expectedInvocationTimes;
    private final MetaPointLocatorRT metaPointLocatorRT;
    private RealTimeTimer timer;

    public MetaPointLocatorRtInitializeTest(int updateEvent, int expectedInvocationTimes){
        this.expectedInvocationTimes = expectedInvocationTimes;
        dataPoint = mock(DataPointRT.class);

        MetaPointLocatorVO metaPointLocatorVO = new MetaPointLocatorVO();
        metaPointLocatorVO.setUpdateEvent(updateEvent);
        metaPointLocatorVO.setDataTypeId(DataTypes.NUMERIC);
        metaPointLocatorVO.setScript("return 1;");

        if(metaPointLocatorVO.getUpdateEvent() == MetaPointLocatorVO.UPDATE_EVENT_CRON) {
            metaPointLocatorVO.setUpdateCronPattern("30 30 12 ? * * *");
        }

        metaPointLocatorRT = new MetaPointLocatorRT(metaPointLocatorVO);
    }

    @Before
    public void config() throws Exception {
        dataSource = mock(MetaDataSourceRT.class);
        timer = mock(RealTimeTimer.class);
        RuntimeManager runtimeManagerMock = mock(RuntimeManager.class);
        MockUtils.configMockContextWrapper(runtimeManagerMock);
        when(dataPoint.isInitialized()).thenReturn(true);
        when(dataPoint.getPointValue()).thenReturn(mock(PointValueTime.class));

        MetaPointLocatorRT.ScheduledUpdateTimeout scheduledUpdateTimeout = mock(MetaPointLocatorRT.ScheduledUpdateTimeout.class);
        whenNew(MetaPointLocatorRT.ScheduledUpdateTimeout.class).withArguments(anyLong()).thenReturn(scheduledUpdateTimeout);
    }

    @Test
    public void when_initialize_with_different_update_events_then_updatePointValue(){
        //when:
        metaPointLocatorRT.initialize(timer, dataSource, dataPoint);

        //then:
        verify(dataPoint, times(expectedInvocationTimes)).updatePointValue(any(PointValueTime.class));
    }
}