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

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({MetaPointLocatorRT.ScheduledUpdateTimeout.class, MetaPointLocatorRT.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class MetaPointLocatorRtUpdateEventCronAndStartOfTest {

    @Parameterized.Parameters(name= "{index}: Update event type: {0}, Expected times of invocation: {1}")
    public static Object[] data() {
        return new Object[] {
                new Object[] {MetaPointLocatorVO.UPDATE_EVENT_CRON, 1},
                new Object[] {Common.TimePeriods.MINUTES, 1},
                new Object[] {Common.TimePeriods.HOURS, 1},
                new Object[] {Common.TimePeriods.DAYS, 1},
                new Object[] {Common.TimePeriods.WEEKS, 1},
                new Object[] {Common.TimePeriods.MONTHS, 1},
                new Object[] {Common.TimePeriods.YEARS, 1},
        };
    }

    private final DataPointRT dataPoint;
    private final int expectedInvocationTimes;
    private final MetaPointLocatorRT metaPointLocatorRT;

    public MetaPointLocatorRtUpdateEventCronAndStartOfTest(int updateEvent, int expectedInvocationTimes){
        this.expectedInvocationTimes = expectedInvocationTimes;
        dataPoint = mock(DataPointRT.class);

        MetaPointLocatorVO metaPointLocatorVO = new MetaPointLocatorVO();
        metaPointLocatorVO.setDataTypeId(DataTypes.NUMERIC);
        metaPointLocatorVO.setScript("return 1;");
        metaPointLocatorVO.setUpdateEvent(updateEvent);

        if(metaPointLocatorVO.getUpdateEvent() == MetaPointLocatorVO.UPDATE_EVENT_CRON) {
            metaPointLocatorVO.setUpdateCronPattern("30 30 12 ? * * *");
        }

        metaPointLocatorRT = new MetaPointLocatorRT(metaPointLocatorVO);
    }

    @Before
    public void config() throws Exception {
        MetaDataSourceRT dataSource = mock(MetaDataSourceRT.class);
        RealTimeTimer timer = mock(RealTimeTimer.class);
        RuntimeManager runtimeManagerMock = mock(RuntimeManager.class);
        MockUtils.configMockContextWrapper(runtimeManagerMock);
        metaPointLocatorRT.initialize(timer, dataSource, dataPoint);
    }

    @Test
    public void when_scheduledUpdateTimeout_run_with_different_update_events_then_updatePointValue(){
        //given:
        MetaPointLocatorRT.ScheduledUpdateTimeout scheduledUpdateTimeout = metaPointLocatorRT.new ScheduledUpdateTimeout(123456L);

        //when:
        scheduledUpdateTimeout.run(123456L);

        //then:
        verify(dataPoint, times(expectedInvocationTimes)).updatePointValue(any(PointValueTime.class));
    }
}