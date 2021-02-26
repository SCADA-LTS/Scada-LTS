package com.serotonin.mango.vo;

import com.fasterxml.jackson.databind.*;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.chart.ChartRenderer;
import com.serotonin.mango.view.chart.TableChartRenderer;
import com.serotonin.mango.view.text.AnalogRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.*;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.ds.state.ApiChangeEnableStateDs;
import utils.SerializeDataPointTestUtils;

import java.util.*;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static utils.SerializeDataPointTestUtils.createObjectMapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemSettingsDAO.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class SerializeDataPointTest {

    private static DataPointVO expected;
    private static DataPointVO result;
    private static String json;

    @BeforeClass
    public static void config() throws Exception {

        //given:
        int loggintTypeInterval = DataPointVO.LoggingTypes.INTERVAL;
        int intervalLoggingTypeMaximum = DataPointVO.IntervalLoggingTypes.MAXIMUM;
        int intervalLoggingPeriodType = Common.TimePeriods.DAYS;
        int purgeTypeWeeks =  DataPointVO.PurgeTypes.WEEKS;
        int dataTypeNumeric = DataTypes.NUMERIC;
        int changeTypeIncrementAnalog = ChangeTypeVO.Types.INCREMENT_ANALOG;
        int engeeringUnitBar = EngineeringUnits.bars.intValue();
        int alarmLevelUrgent = AlarmLevels.URGENT;
        int updatePeriodTypeMinutes = Common.TimePeriods.MINUTES;
        int durationTypeSeconds = Common.TimePeriods.SECONDS;
        int eventDetectorTypePointChange = PointEventDetectorVO.TYPE_POINT_CHANGE;
        int alarmLevelCritical = AlarmLevels.CRITICAL;

        Map<String, Integer> types = new HashMap<>();
        types.put("INTERVAL", loggintTypeInterval);
        types.put("MAXIMUM", intervalLoggingTypeMaximum);
        types.put("DAYS", intervalLoggingPeriodType);
        types.put("WEEKS", purgeTypeWeeks);
        types.put("NUMERIC", dataTypeNumeric);
        types.put("INCREMENT_ANALOG", changeTypeIncrementAnalog);
        types.put("BARS", engeeringUnitBar);
        types.put("SECONDS", durationTypeSeconds);
        types.put("TYPE_POINT_CHANGE", eventDetectorTypePointChange);
        types.put("CRITICAL", alarmLevelCritical);
        types.put("POINT_CHANGE", eventDetectorTypePointChange);

        VirtualDataSourceVO dataSource = new VirtualDataSourceVO();
        dataSource.setUpdatePeriods(12);
        dataSource.setUpdatePeriodType(updatePeriodTypeMinutes);
        dataSource.setId(21);
        dataSource.setName("dataSourceNameValue");
        dataSource.setXid("dataSourceXidValue");
        dataSource.setAlarmLevel(4321, alarmLevelUrgent);
        dataSource.setEnabled(true);
        dataSource.setState(new ApiChangeEnableStateDs());

        expected = new DataPointVO(loggintTypeInterval);
        expected.setXid("dataPointXidValue");

        PointEventDetectorVO eventDetectorVO = new PointEventDetectorVO();
        eventDetectorVO.setXid("eventDetectorXidValue");
        eventDetectorVO.setAlias("aliasValue");
        eventDetectorVO.setAlarmLevel(alarmLevelCritical);
        eventDetectorVO.setDuration(2);
        eventDetectorVO.setId(999);
        eventDetectorVO.setLimit(112233);
        eventDetectorVO.setChangeCount(44);
        eventDetectorVO.setBinaryState(true);
        eventDetectorVO.setDurationType(durationTypeSeconds);
        eventDetectorVO.setDetectorType(eventDetectorTypePointChange);
        eventDetectorVO.setWeight(1234.5);
        eventDetectorVO.setAlphanumericState("aphanumericState");
        eventDetectorVO.setMultistateState(123);

        List<PointEventDetectorVO> eventDetectors = new ArrayList<>();
        eventDetectors.add(eventDetectorVO);

        expected.setEventDetectors(eventDetectors);
        expected.setId(123);
        expected.setComments(new ArrayList<>());

        TextRenderer textRenderer = new AnalogRenderer();
        ((AnalogRenderer) textRenderer).setSuffix("suffixValue");
        ((AnalogRenderer) textRenderer).setFormat("formatValue");

        expected.setTextRenderer(textRenderer);
        expected.setName("dataPointNameValue");
        expected.setSettable(true);
        expected.setEnabled(true);
        expected.setDataSourceName(dataSource.getName());
        expected.setDataSourceId(dataSource.getId());
        expected.setDataSourceXid(dataSource.getXid());
        expected.setDataSourceTypeId(dataSource.getType().getId());
        expected.setPointFolderId(12345);
        expected.setChartColour("red");

        TableChartRenderer tableChartRenderer = new TableChartRenderer();
        tableChartRenderer.setLimit(1122);

        expected.setChartRenderer(tableChartRenderer);
        expected.setDiscardExtremeValues(true);
        expected.setDiscardHighLimit(1000);
        expected.setDiscardLowLimit(-1000);
        expected.setDeviceName(dataSource.getName());
        expected.setDefaultCacheSize(321);
        expected.setPurgeType(purgeTypeWeeks);
        expected.setPurgePeriod(3);
        expected.setTolerance(0.98);
        expected.setIntervalLoggingType(intervalLoggingTypeMaximum);
        expected.setIntervalLoggingPeriodType(intervalLoggingPeriodType);
        expected.setIntervalLoggingPeriod(4);
        expected.setEngineeringUnits(engeeringUnitBar);

        PointLocatorVO pointLocator = dataSource.createPointLocator();
        ((VirtualPointLocatorVO) pointLocator).setDataTypeId(dataTypeNumeric);
        ((VirtualPointLocatorVO) pointLocator).setSettable(true);
        ((VirtualPointLocatorVO) pointLocator).setChangeTypeId(changeTypeIncrementAnalog);

        IncrementAnalogChangeVO analogChangeVO = new IncrementAnalogChangeVO();
        analogChangeVO.setStartValue("12.3");
        analogChangeVO.setMax(1234567);
        analogChangeVO.setMin(-1234567);
        analogChangeVO.setRoll(true);
        analogChangeVO.setChange(4321);
        ((VirtualPointLocatorVO) pointLocator).setIncrementAnalogChange(analogChangeVO);

        expected.setPointLocator(pointLocator);

        mockStatic(SystemSettingsDAO.class);
        when(SystemSettingsDAO.getIntValue(SystemSettingsDAO.DEFAULT_LOGGING_TYPE))
                .thenReturn(loggintTypeInterval);

        SerializeDataPointTestUtils.Deserializers deserializers = new SerializeDataPointTestUtils.Deserializers();
        deserializers.put(PointLocatorVO.class, new SerializeDataPointTestUtils.Deserializer<>(pointLocator.getClass()));
        deserializers.put(ChartRenderer.class, new SerializeDataPointTestUtils.Deserializer<>(tableChartRenderer.getClass()));
        deserializers.put(TextRenderer.class, new SerializeDataPointTestUtils.Deserializer<>(textRenderer.getClass()));

        //when:
        json = new JsonWriter().write(expected);
        System.out.println(json);
        ObjectMapper mapper = createObjectMapper(types, deserializers);
        result = mapper.readValue(json, DataPointVO.class);

        String analogChangeJson = new JsonWriter().write(analogChangeVO);
        IncrementAnalogChangeVO incrementAnalogChange = mapper.readValue(analogChangeJson, IncrementAnalogChangeVO.class);
        ((VirtualPointLocatorVO) result.getPointLocator()).setIncrementAnalogChange(incrementAnalogChange);

        String detectorTypeJson = new JsonWriter().write(eventDetectorVO);
        DetectorType detectorType = mapper.readValue(detectorTypeJson, DetectorType.class);
        System.out.println(detectorType);
        result.getEventDetectors().get(0).setDetectorType(detectorType.getType());
    }

    private static class DetectorType {
        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    @Test
    @Ignore("The json structure not contains dataTypeMessage.")
    public void test_getDataTypeMessage() {

        //then:
        assertThat(json, containsString("dataTypeMessage"));
        assertEquals(expected.getDataTypeMessage(), result.getDataTypeMessage());
    }

    @Test
    @Ignore("The json structure not contains configurationDescription.")
    public void test_getConfigurationDescription() {

        //then:
        assertThat(json, containsString("configurationDescription"));
        assertEquals(expected.getConfigurationDescription(), result.getConfigurationDescription());
    }

    @Test
    @Ignore("The json structure not contains extendedName.")
    public void test_getExtendedName() {

        //then:
        assertThat(json, containsString("extendedName"));
        assertEquals(expected.getExtendedName(), result.getExtendedName());
    }

    @Test
    @Ignore("The field settable from DataPointVO not serailized.")
    public void test_isSettable() {
        
        //then:
        assertThat(json, containsString("settable"));
        assertEquals(expected.isSettable(), result.isSettable());
    }

    @Test
    @Ignore("The json structure contains dataSourceXid, not dataSourceId.")
    public void test_getDataSourceId() {

        //then:
        assertThat(json, containsString("dataSourceId"));
        assertEquals(expected.getDataSourceId(), result.getDataSourceId());
    }

    @Test
    public void test_getDeviceName() {

        //then:
        assertThat(json, containsString("deviceName"));
        assertEquals(expected.getDeviceName(), result.getDeviceName());
    }

    @Test
    public void test_isEnabled() {

        //then:
        assertThat(json, containsString("enabled"));
        assertEquals(expected.isEnabled(), result.isEnabled());
    }

    @Test
    @Ignore("The json structure not contains pointFolderId.")
    public void test_getPointFolderId() {
        
        //then:
        assertThat(json, containsString("pointFolderId"));
        assertEquals(expected.getPointFolderId(), result.getPointFolderId());
    }

    @Test
    @Ignore("The json structure contains xid, not id.")
    public void test_getId() {
        
        //then:
        assertThat(json, containsString("id"));
        assertEquals(expected.getId(), result.getId());
    }

    @Test
    public void test_getXid() {

        //then:
        assertThat(json, containsString("xid"));
        assertEquals(expected.getXid(), result.getXid());
    }

    @Test
    public void test_getName() {

        //then:
        assertThat(json, containsString("name"));
        assertEquals(expected.getName(), result.getName());
    }

    @Test
    @Ignore("The json structure contains dataType, not dataTypeId.")
    public void test_getPointLocator() {

        //then:
        assertThat(json, containsString("pointLocator"));
        VirtualPointLocatorVO pointLocatorExpects = expected.getPointLocator();
        VirtualPointLocatorVO pointLocatorResult = result.getPointLocator();

        assertThat(json, containsString("dataType"));
        assertEquals("settable", pointLocatorExpects.isSettable(), pointLocatorResult.isSettable());
        assertEquals("dataTypeId", pointLocatorExpects.getDataTypeId(), pointLocatorResult.getDataTypeId());
        assertEquals("changeTypeId", pointLocatorExpects.getChangeTypeId(), pointLocatorResult.getChangeTypeId());
    }


    @Test
    public void test_getPointLocatorChangeType() {

        //then:
        assertThat(json, containsString("pointLocator"));
        VirtualPointLocatorVO pointLocatorExpects = expected.getPointLocator();
        VirtualPointLocatorVO pointLocatorResult = result.getPointLocator();

        IncrementAnalogChangeVO incrementAnalogChangeExpects = pointLocatorExpects.getIncrementAnalogChange();
        IncrementAnalogChangeVO incrementAnalogChangeResult = pointLocatorResult.getIncrementAnalogChange();

        assertEquals("typeId", incrementAnalogChangeExpects.typeId(), incrementAnalogChangeResult.typeId());
        assertEquals("startValue", incrementAnalogChangeExpects.getStartValue(), incrementAnalogChangeResult.getStartValue());
        assertEquals("description", incrementAnalogChangeExpects.getDescription(), incrementAnalogChangeResult.getDescription());
        assertEquals("roll", incrementAnalogChangeExpects.isRoll(), incrementAnalogChangeResult.isRoll());
        assertEquals("max", incrementAnalogChangeExpects.getMax(), incrementAnalogChangeResult.getMax(), 0.01);
        assertEquals("min", incrementAnalogChangeExpects.getMin(), incrementAnalogChangeResult.getMin(), 0.01);
        assertEquals("change", incrementAnalogChangeExpects.getChange(), incrementAnalogChangeResult.getChange(), 0.01);

    }

    @Test
    @Ignore("The json structure not contains dataSourceName.")
    public void test_getDataSourceName() {

        //then:
        assertThat(json, containsString("dataSourceName"));
        assertEquals(expected.getDataSourceName(), result.getDataSourceName());
    }

    @Test
    public void test_getDataSourceXid() {

        //then:
        assertThat(json, containsString("dataSourceXid"));
        assertEquals(expected.getDataSourceXid(), result.getDataSourceXid());
    }

    @Test
    @Ignore("The json structure not contains getDataSourceTypeId.")
    public void test_getDataSourceTypeId() {
        
        //then:
        assertThat(json, containsString("dataSourceTypeId"));
        assertEquals(expected.getDataSourceTypeId(), result.getDataSourceTypeId());
    }

    @Test
    public void test_getLoggingType() {
        
        //then:
        assertThat(json, containsString("loggingType"));
        assertEquals(expected.getLoggingType(), result.getLoggingType());
    }

    @Test
    public void test_getPurgePeriod() {

        //then:
        assertThat(json, containsString("purgePeriod"));
        assertEquals(expected.getPurgePeriod(), result.getPurgePeriod());
    }

    @Test
    public void test_getPurgeType() {

        //then:
        assertThat(json, containsString("purgeType"));
        assertEquals(expected.getPurgeType(), result.getPurgeType());
    }

    @Test
    public void test_getTolerance() {

        //then:
        assertThat(json, containsString("tolerance"));
        assertEquals(expected.getTolerance(), result.getTolerance(), 0.01);
    }

    @Test
    public void test_getTextRenderer() {

        //then:
        assertThat(json, containsString("textRenderer"));
        AnalogRenderer rendererExpects = (AnalogRenderer) expected.getTextRenderer();
        AnalogRenderer rendererResult = (AnalogRenderer) result.getTextRenderer();

        assertEquals("colour", rendererExpects.getColour(), rendererResult.getColour());
        assertEquals("metaText", rendererExpects.getMetaText(), rendererResult.getMetaText());
        assertEquals("typeName", rendererExpects.getTypeName(), rendererResult.getTypeName());
        assertEquals("suffix", rendererExpects.getSuffix(), rendererResult.getSuffix());
        assertEquals("format", rendererExpects.getFormat(), rendererResult.getFormat());
    }

    @Test
    public void test_getTextRendererDef() {

        //then:
        assertThat(json, containsString("textRenderer"));
        ImplDefinition rendererExpects = expected.getTextRenderer().getDef();
        ImplDefinition rendererResult = result.getTextRenderer().getDef();


        assertEquals("name", rendererExpects.getName(), rendererResult.getName());
        assertEquals("exportName", rendererExpects.getExportName(), rendererResult.getExportName());
        assertEquals("nameKey", rendererExpects.getNameKey(), rendererResult.getNameKey());
        assertEquals("id", rendererExpects.getId(), rendererResult.getId());
        assertEquals("supportedDataTypes", rendererExpects.getSupportedDataTypes(), rendererResult.getSupportedDataTypes());
    }

    @Test
    public void test_getChartRenderer() {

        //then:
        assertThat(json, containsString("chartRenderer"));
        TableChartRenderer rendererExpects = (TableChartRenderer) expected.getChartRenderer();
        TableChartRenderer rendererResult = (TableChartRenderer) result.getChartRenderer();

        assertEquals("typeName", rendererExpects.getTypeName(), rendererResult.getTypeName());
        assertEquals("limit", rendererExpects.getLimit(), rendererResult.getLimit());
    }

    @Test
    public void test_getChartRendererDef() {

        //then:
        assertThat(json, containsString("chartRenderer"));

        ImplDefinition rendererExpects = expected.getChartRenderer().getDef();
        ImplDefinition rendererResult = result.getChartRenderer().getDef();

        assertEquals("name", rendererExpects.getName(), rendererResult.getName());
        assertEquals("exportName", rendererExpects.getExportName(), rendererResult.getExportName());
        assertEquals("nameKey", rendererExpects.getNameKey(), rendererResult.getNameKey());
        assertEquals("id", rendererExpects.getId(), rendererResult.getId());
        assertEquals("supportedDataTypes", rendererExpects.getSupportedDataTypes(), rendererResult.getSupportedDataTypes());
    }

    @Test
    public void test_getEventDetectors() {

        //then:
        assertThat(json, containsString("eventDetectors"));

        List<PointEventDetectorVO> eventDetectorsExpected = expected.getEventDetectors();
        List<PointEventDetectorVO> eventDetectorsResult = result.getEventDetectors();

        assertFalse("eventDetectors is empty", eventDetectorsResult.isEmpty());

        PointEventDetectorVO eventDetectorExpected = eventDetectorsExpected.get(0);
        PointEventDetectorVO eventDetectorResult = eventDetectorsResult.get(0);

        assertEquals("detectorType", eventDetectorExpected.getDetectorType(), eventDetectorResult.getDetectorType());
        assertEquals("alarmLevel", eventDetectorExpected.getAlarmLevel(), eventDetectorResult.getAlarmLevel());
        assertEquals("xid", eventDetectorExpected.getXid(), eventDetectorResult.getXid());
        assertEquals("alias", eventDetectorExpected.getAlias(), eventDetectorResult.getAlias());
    }

    @Test
    @Ignore("No serialized from PointEventDetectorVO: id, limit, alphanumericState, eventDetectorKey, typeKey, duration," +
            "changeCount, weight, description, eventType, durationDescription.")
    public void test_getEventDetectorsNotSerialized() {

        //then:
        assertThat(json, containsString("eventDetectors"));

        List<PointEventDetectorVO> eventDetectorsExpected = expected.getEventDetectors();
        List<PointEventDetectorVO> eventDetectorsResult = result.getEventDetectors();

        assertFalse("eventDetectors is empty", eventDetectorsResult.isEmpty());

        PointEventDetectorVO eventDetectorExpected = eventDetectorsExpected.get(0);
        PointEventDetectorVO eventDetectorResult = eventDetectorsResult.get(0);

        assertEquals("id", eventDetectorExpected.getId(), eventDetectorResult.getId());
        assertEquals("limit", eventDetectorExpected.getLimit(), eventDetectorResult.getLimit(), 0.01);
        assertEquals("alphanumericState", eventDetectorExpected.getAlphanumericState(), eventDetectorResult.getAlphanumericState());
        assertEquals("eventDetectorKey", eventDetectorExpected.getEventDetectorKey(), eventDetectorResult.getEventDetectorKey());
        assertEquals("typeKey", eventDetectorExpected.getTypeKey(), eventDetectorResult.getTypeKey());
        assertEquals("duration", eventDetectorExpected.getDuration(), eventDetectorResult.getDuration());
        assertEquals("changeCount", eventDetectorExpected.getChangeCount(), eventDetectorResult.getChangeCount());
        assertEquals("weight", eventDetectorExpected.getWeight(), eventDetectorResult.getWeight(), 0.01);
        assertEquals("description", eventDetectorExpected.getDescription(), eventDetectorResult.getDescription());
        assertEquals("eventType", eventDetectorExpected.getEventType(), eventDetectorResult.getEventType());
        assertEquals("durationDescription", eventDetectorExpected.getDurationDescription(), eventDetectorResult.getDurationDescription());
    }

    @Test
    @Ignore("No serialized from PointEventDetectorVO: def.")
    public void test_getEventDetectorsDef() {
        //then:
        assertThat(json, containsString("eventDetectors"));

        List<PointEventDetectorVO> eventDetectorsExpected = expected.getEventDetectors();
        List<PointEventDetectorVO> eventDetectorsResult = result.getEventDetectors();

        assertFalse("eventDetectors is empty", eventDetectorsResult.isEmpty());

        PointEventDetectorVO eventDetectorExpected = eventDetectorsExpected.get(0);
        PointEventDetectorVO eventDetectorResult = eventDetectorsResult.get(0);
        ImplDefinition definitionExpected = eventDetectorExpected.getDef();
        ImplDefinition definitionResult = eventDetectorResult.getDef();

        assertNotNull("definitionExpected is null", definitionExpected);
        assertNotNull("definitionResult is null", definitionResult);
        assertEquals("name", definitionExpected.getName(), definitionResult.getName());
        assertEquals("exportName", definitionExpected.getExportName(), definitionResult.getExportName());
        assertEquals("nameKey", definitionExpected.getNameKey(), definitionResult.getNameKey());
        assertEquals("id", definitionExpected.getId(), definitionResult.getId());
        assertEquals("supportedDataTypes", definitionExpected.getSupportedDataTypes(), definitionResult.getSupportedDataTypes());
    }

    @Test
    @Ignore("The json structure not contains comments.")
    public void test_getComments() {

        //then:
        assertThat(json, containsString("comments"));
        assertEquals(expected.getComments(), result.getComments());
    }

    @Test
    public void test_getDefaultCacheSize() {

        //then:
        assertThat(json, containsString("defaultCacheSize"));
        assertEquals(expected.getDefaultCacheSize(), result.getDefaultCacheSize());
    }

    @Test
    public void test_getIntervalLoggingPeriodType() {

        //then:
        assertThat(json, containsString("intervalLoggingPeriodType"));
        assertEquals(expected.getIntervalLoggingPeriodType(), result.getIntervalLoggingPeriodType());
    }

    @Test
    public void test_getIntervalLoggingPeriod() {

        //then:
        assertThat(json, containsString("intervalLoggingPeriod"));
        assertEquals(expected.getIntervalLoggingPeriod(), result.getIntervalLoggingPeriod());
    }

    @Test
    public void test_getIntervalLoggingType() {

        //then:
        assertThat(json, containsString("intervalLoggingType"));
        assertEquals(expected.getIntervalLoggingType(), result.getIntervalLoggingType());
    }

    @Test
    public void test_isDiscardExtremeValues() {

        //then:
        assertThat(json, containsString("discardExtremeValues"));
        assertEquals(expected.isDiscardExtremeValues(), result.isDiscardExtremeValues());
    }

    @Test
    public void test_getDiscardLowLimit() {

        //then:
        assertThat(json, containsString("discardLowLimit"));
        assertEquals(expected.getDiscardLowLimit(), result.getDiscardLowLimit(), 0.01);
    }

    @Test
    public void test_getDiscardHighLimit() {

        //then:
        assertThat(json, containsString("discardHighLimit"));
        assertEquals(expected.getDiscardHighLimit(), result.getDiscardHighLimit(), 0.01);
    }

    @Test
    public void test_getEngineeringUnits() {

        //then:
        assertThat(json, containsString("engineeringUnits"));
        assertEquals(expected.getEngineeringUnits(), result.getEngineeringUnits());
    }

    @Test
    public void test_getChartColour() {

        //then:
        assertThat(json, containsString("chartColour"));
        assertEquals(expected.getChartColour(), result.getChartColour());
    }
}