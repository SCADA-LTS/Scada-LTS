package com.serotonin.mango.rt.event.detectors;

import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.event.BinaryEventTextRenderer;
import com.serotonin.mango.view.event.EventTextRenderer;
import com.serotonin.mango.view.event.NoneEventRenderer;
import com.serotonin.mango.view.text.BinaryTextRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Collections;

import static com.serotonin.mango.vo.event.PointEventDetectorVO.*;
import static org.junit.Assert.assertEquals;

public class EventDetectorMessageTest {

    private static DataPointVO createDataPointVO(int pointFromContextId, MangoValue mangoValue) {
        PointLocatorVO locatorFromContext = new VirtualPointLocatorVO();
        ((VirtualPointLocatorVO) locatorFromContext).setDataTypeId(mangoValue.getDataType());

        DataPointVO pointFromContextVO = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        pointFromContextVO.setPointLocator(locatorFromContext);
        pointFromContextVO.setEventDetectors(Collections.emptyList());
        pointFromContextVO.setId(pointFromContextId);
        pointFromContextVO.setName("point test");
        return pointFromContextVO;
    }


    @Test
    public void test_point_event_detector_message_with_event_text_renderer(){
        //given
        DataPointVO dataPointVO = createDataPointVO(0, new BinaryValue(true));
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(TYPE_BINARY_STATE);
        ped.njbSetDataPoint(dataPointVO);
        TextRenderer textRenderer = new BinaryTextRenderer();
        dataPointVO.setTextRenderer(textRenderer);
        EventTextRenderer eventTextRenderer = new BinaryEventTextRenderer("zero", "one");
        dataPointVO.setEventTextRenderer(eventTextRenderer);

        //when
        BinaryStateDetectorRT pointEventDetectorRT = new BinaryStateDetectorRT(ped);

        //then
        assertEquals( "event.detector.state", pointEventDetectorRT.getMessage().getKey());
        assertEquals( "event.detector.shortMessage", pointEventDetectorRT.getShortMessage().getKey());
    }

    @Test
    public void test_point_event_detector_message_with_event_text_renderer_empty(){
        //given
        DataPointVO dataPointVO = createDataPointVO(0, new BinaryValue(true));
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(TYPE_BINARY_STATE);
        ped.njbSetDataPoint(dataPointVO);
        TextRenderer textRenderer = new BinaryTextRenderer();
        dataPointVO.setTextRenderer(textRenderer);
        EventTextRenderer eventTextRenderer = new BinaryEventTextRenderer("", "");
        dataPointVO.setEventTextRenderer(eventTextRenderer);

        //when
        BinaryStateDetectorRT pointEventDetectorRT = new BinaryStateDetectorRT(ped);

        //then
        assertEquals( "event.detector.state", pointEventDetectorRT.getMessage().getKey());
        assertEquals( "event.detector.state", pointEventDetectorRT.getShortMessage().getKey());
    }

    @Test
    public void test_point_event_detector_message_with_event_text_renderer_none(){
        //given
        DataPointVO dataPointVO = createDataPointVO(0, new BinaryValue(true));
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(TYPE_BINARY_STATE);
        ped.njbSetDataPoint(dataPointVO);
        TextRenderer textRenderer = new BinaryTextRenderer();
        dataPointVO.setTextRenderer(textRenderer);
        EventTextRenderer eventTextRenderer = new NoneEventRenderer();
        dataPointVO.setEventTextRenderer(eventTextRenderer);

        //when
        BinaryStateDetectorRT pointEventDetectorRT = new BinaryStateDetectorRT(ped);

        //then
        assertEquals( "event.detector.state", pointEventDetectorRT.getMessage().getKey());
        assertEquals( "event.detector.state", pointEventDetectorRT.getShortMessage().getKey());
    }

    @Test
    public void test_point_event_detector_message_with_description(){
        //given
        DataPointVO dataPointVO = createDataPointVO(0, new BinaryValue(true));
        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setDetectorType(TYPE_BINARY_STATE);
        ped.njbSetDataPoint(dataPointVO);
        TextRenderer textRenderer = new BinaryTextRenderer();
        dataPointVO.setTextRenderer(textRenderer);
        dataPointVO.setDescription("test description");

        //when
        BinaryStateDetectorRT pointEventDetectorRT = new BinaryStateDetectorRT(ped);

        //then
        assertEquals( " (test description)", Array.get(pointEventDetectorRT.getMessage().getArgs(), 2));
    }
}
