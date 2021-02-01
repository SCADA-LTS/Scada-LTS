package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.web.ContextWrapper;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.mvc.api.dto.eventDetector.EventDetectorChangeDTO;
import org.scada_lts.web.mvc.api.json.JsonPointEventDetector;
import org.springframework.http.ResponseEntity;
import utils.TestConcurrentUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class EventDetectorAPIConcurrentTest {

    private EventDetectorAPI eventDetectorAPISubject;
    private EventDetectorChangeDTO detectorChangeDTO;
    private int dataPointId;

    private DataPointService dataPointServiceMock;
    private RuntimeManager runtimeManagerMock;
    private HttpServletRequest requestMock;

    @Before
    public void config() {
        dataPointId = 123;
        dataPointServiceMock = mock(DataPointService.class);
        runtimeManagerMock = mock(RuntimeManager.class);

        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute(eq(Common.ContextKeys.RUNTIME_MANAGER)))
                .thenReturn(runtimeManagerMock);

        Common.ctx = new ContextWrapper(context);

        detectorChangeDTO = new EventDetectorChangeDTO();
        detectorChangeDTO.setAlarmLevel(AlarmLevels.INFORMATION);
        detectorChangeDTO.setAlias("alias-tst");
        detectorChangeDTO.setXid("xid-test");

        requestMock = mock(HttpServletRequest.class);
        when(requestMock.getAttribute(eq(Common.SESSION_USER))).thenReturn(new User());

        eventDetectorAPISubject = new EventDetectorAPI(dataPointServiceMock);
    }

    @Test
    public void when_createChangeEventDetector_concurrent_with_same_dataPoint_then_saveDataPoint_one_times() {

        //given:
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPointVO.setXid("ABC_" + dataPointId);
        dataPointVO.setId(dataPointId);
        dataPointVO.setEventDetectors(new ArrayList<>());
        when(dataPointServiceMock.getDataPoint(eq(dataPointId))).thenReturn(dataPointVO);

        //when:
        TestConcurrentUtils.biFunction(4, this::createChangeEventDetector, detectorChangeDTO, dataPointId);

        //then:
        verify(runtimeManagerMock, times(1)).saveDataPoint(eq(dataPointVO));
    }

    @Test
    public void when_createChangeEventDetector_concurrent_with_same_dataPoint_then_eventDetecotrs_one_size() {

        //given:
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPointVO.setXid("ABC_" + dataPointId);
        dataPointVO.setId(dataPointId);
        dataPointVO.setEventDetectors(new ArrayList<>());
        when(dataPointServiceMock.getDataPoint(eq(dataPointId))).thenReturn(dataPointVO);

        //when:
        TestConcurrentUtils.biFunction(4, this::createChangeEventDetector, detectorChangeDTO, dataPointId);
        List<PointEventDetectorVO> eventDetectors = dataPointVO.getEventDetectors();

        //then:
        assertEquals(1, eventDetectors.size());
    }

    @Test
    public void when_createChangeEventDetector_concurrent_with_two_dataPoint_then_saveDataPoint_one_times_per_dataPoint() {

        //given:
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPointVO.setXid("ABC_" + dataPointId);
        dataPointVO.setId(dataPointId);
        dataPointVO.setEventDetectors(new ArrayList<>());
        when(dataPointServiceMock.getDataPoint(eq(dataPointId))).thenReturn(dataPointVO);

        int dataPointId2 = 12345;
        DataPointVO dataPointVO2 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);

        dataPointVO2.setXid("ABC_" + dataPointId2);
        dataPointVO2.setId(dataPointId2);
        dataPointVO2.setEventDetectors(new ArrayList<>());
        when(dataPointServiceMock.getDataPoint(eq(dataPointId2))).thenReturn(dataPointVO2);

        doAnswer((a) -> {Thread.sleep(1000); return null;}).when(dataPointServiceMock)
                .saveDataPoint(any(DataPointVO.class));
        doAnswer((a) -> {Thread.sleep(1000); return null;}).when(runtimeManagerMock)
                .saveDataPoint(any(DataPointVO.class));


        //when:
        TestConcurrentUtils.biFunction(2, this::createChangeEventDetector, detectorChangeDTO, new Supplier<Integer>() {
            private AtomicInteger counter = new AtomicInteger();
            @Override
            public Integer get() {
                return counter.getAndIncrement() % 2 == 0 ? dataPointId : dataPointId2;
            }
        });

        //then:
        verify(runtimeManagerMock, times(1)).saveDataPoint(eq(dataPointVO));
        verify(runtimeManagerMock, times(1)).saveDataPoint(eq(dataPointVO2));
    }

    @Test
    public void when_createChangeEventDetector_concurrent_with_two_dataPoint_then_eventDetecotrs_one_size_per_dataPoint() {

        //given:
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPointVO.setXid("ABC_" + dataPointId);
        dataPointVO.setId(dataPointId);
        dataPointVO.setEventDetectors(new ArrayList<>());
        when(dataPointServiceMock.getDataPoint(eq(dataPointId))).thenReturn(dataPointVO);

        int dataPointId2 = 12345;
        DataPointVO dataPointVO2 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);

        dataPointVO2.setXid("ABC_" + dataPointId2);
        dataPointVO2.setId(dataPointId2);
        dataPointVO2.setEventDetectors(new ArrayList<>());
        when(dataPointServiceMock.getDataPoint(eq(dataPointId2))).thenReturn(dataPointVO2);

        doAnswer((a) -> {Thread.sleep(1000); return null;}).when(dataPointServiceMock)
                .saveDataPoint(any(DataPointVO.class));
        doAnswer((a) -> {Thread.sleep(1000); return null;}).when(runtimeManagerMock)
                .saveDataPoint(any(DataPointVO.class));

        //when:
        TestConcurrentUtils.biFunction(2, this::createChangeEventDetector, detectorChangeDTO, new Supplier<Integer>() {
            private AtomicInteger counter = new AtomicInteger();
            @Override
            public Integer get() {
                return counter.getAndIncrement() % 2 == 0 ? dataPointId : dataPointId2;
            }
        });
        List<PointEventDetectorVO> eventDetectors = dataPointVO.getEventDetectors();
        List<PointEventDetectorVO> eventDetectors2 = dataPointVO2.getEventDetectors();

        //then:
        assertEquals(1, eventDetectors.size());
        assertEquals(1, eventDetectors2.size());
    }

    private ResponseEntity<JsonPointEventDetector> createChangeEventDetector(EventDetectorChangeDTO body, int datapointId) {
        return eventDetectorAPISubject.createChangeEventDetector(datapointId, requestMock, body);
    }
}