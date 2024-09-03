package org.scada_lts.utils;

import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.chart.ChartRenderer;
import com.serotonin.mango.view.chart.StatisticsChartRenderer;
import com.serotonin.mango.view.chart.TableChartRenderer;
import com.serotonin.mango.view.event.BinaryEventTextRenderer;
import com.serotonin.mango.view.event.EventTextRenderer;
import com.serotonin.mango.view.text.BinaryTextRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.Test;
import org.scada_lts.web.mvc.api.json.JsonPointProperties;

import static org.junit.Assert.*;

public class UpdateValuePointPropertiesTest {

    private static final String ID = "id";
    private static final String TEXT_RENDERER = "textRenderer";
    private static final String EVENT_TEXT_RENDERER = "eventTextRenderer";
    private static final String NAME = "name";
    private static final String ENABLED = "enabled";
    private static final String CHART_COLOUR = "chartColour";
    private static final String CHART_RENDERER = "chartRenderer";
    private static final String DISCARD_EXTREME_VALUES = "discardExtremeValues";
    private static final String DISCARD_HIGH_LIMIT = "discardHighLimit";
    private static final String DISCARD_LOW_LIMIT = "discardLowLimit";
    private static final String DEFAULT_CACHE_SIZE = "defaultCacheSize";
    private static final String PURGE_TYPE = "purgeType";
    private static final String PURGE_PERIOD = "purgePeriod";
    private static final String TOLERANCE = "tolerance";
    private static final String INTERVAL_LOGGING_TYPE = "intervalLoggingType";
    private static final String INTERVAL_LOGGING_PERIOD_TYPE = "intervalLoggingPeriodType";
    private static final String INTERVAL_LOGGING_PERIOD = "intervalLoggingPeriod";
    private static final String ENGEERING_UNIT = "engeeringUnit";
    private static final String DESCRIPTION = "description";
    private static int dataPointId = 123;
    private static String dataPointXid = "DP_12121_TEST";

    private static int loggintTypeInterval = DataPointVO.LoggingTypes.INTERVAL;
    private static int intervalLoggingTypeMaximum = DataPointVO.IntervalLoggingTypes.MAXIMUM;
    private static int intervalLoggingPeriodType = Common.TimePeriods.DAYS;
    private static int purgeTypeWeeks =  DataPointVO.PurgeTypes.WEEKS;
    private static int engeeringUnitBar = EngineeringUnits.bars.intValue();

    private static TextRenderer textRenderer = new BinaryTextRenderer();
    private static EventTextRenderer eventTextRenderer = new BinaryEventTextRenderer();
    private static String dataPointNameValue = "dataPointNameValue";
    private static boolean settable = true;
    private static boolean enabled = true;
    private static String chartColour = "red";
    private static String description = "description_test";
    private static ChartRenderer tableChartRenderer = new TableChartRenderer();
    private static boolean discardExtremeValues = true;
    private static double discardHighLimit = 1000.0;
    private static double discardLowLimit = -1000.0;
    private static int defaultCacheSize = 321;
    private static int purgePeriod = 3;
    private static double tolerance = 0.98;
    private static int intervalLoggingPeriod = 4;

    @Test
    public void when_source_empty_then_data_point_no_change() {

        //given:
        DataPointVO result = new DataPointVO(loggintTypeInterval);
        result.setXid(dataPointXid);
        result.setId(dataPointId);
        result.setTextRenderer(textRenderer);
        result.setEventTextRenderer(eventTextRenderer);
        result.setName(dataPointNameValue);
        result.setSettable(settable);
        result.setEnabled(enabled);
        result.setChartColour(chartColour);
        result.setChartRenderer(tableChartRenderer);
        result.setDiscardExtremeValues(discardExtremeValues);
        result.setDiscardHighLimit(discardHighLimit);
        result.setDiscardLowLimit(discardLowLimit);
        result.setDefaultCacheSize(defaultCacheSize);
        result.setPurgeType(purgeTypeWeeks);
        result.setPurgePeriod(purgePeriod);
        result.setTolerance(tolerance);
        result.setIntervalLoggingType(intervalLoggingTypeMaximum);
        result.setIntervalLoggingPeriodType(intervalLoggingPeriodType);
        result.setIntervalLoggingPeriod(intervalLoggingPeriod);
        result.setEngineeringUnits(engeeringUnitBar);
        result.setDescription(description);

        JsonPointProperties source = new JsonPointProperties();

        //when:
        PointPropertiesApiUtils.updateValuePointProperties(result, source);

        //then:
        assertEquals(ID, dataPointId, result.getId());
        assertEquals(TEXT_RENDERER, textRenderer.getTypeName(), result.getTextRenderer().getTypeName());
        assertEquals(EVENT_TEXT_RENDERER, eventTextRenderer.getTypeName(), result.getEventTextRenderer().getTypeName());
        assertEquals(NAME, dataPointNameValue, result.getName());
        assertEquals(ENABLED, enabled, result.isEnabled());
        assertEquals(CHART_COLOUR, chartColour, result.getChartColour());
        assertEquals(CHART_RENDERER, tableChartRenderer.getTypeName(), result.getChartRenderer().getTypeName());
        assertEquals(DISCARD_EXTREME_VALUES, discardExtremeValues, result.isDiscardExtremeValues());
        assertEquals(DISCARD_HIGH_LIMIT, discardHighLimit, result.getDiscardHighLimit(), 0.01);
        assertEquals(DISCARD_LOW_LIMIT, discardLowLimit, result.getDiscardLowLimit(), 0.01);
        assertEquals(DEFAULT_CACHE_SIZE, defaultCacheSize, result.getDefaultCacheSize());
        assertEquals(PURGE_TYPE, purgeTypeWeeks, result.getPurgeType());
        assertEquals(PURGE_PERIOD, purgePeriod, result.getPurgePeriod());
        assertEquals(TOLERANCE, tolerance, result.getTolerance(), 0.01);
        assertEquals(INTERVAL_LOGGING_TYPE, intervalLoggingTypeMaximum, result.getIntervalLoggingType());
        assertEquals(INTERVAL_LOGGING_PERIOD_TYPE, intervalLoggingPeriodType, result.getIntervalLoggingPeriodType());
        assertEquals(INTERVAL_LOGGING_PERIOD, intervalLoggingPeriod, result.getIntervalLoggingPeriod());
        assertEquals(ENGEERING_UNIT, engeeringUnitBar, result.getEngineeringUnits());
        assertEquals(DESCRIPTION, description, result.getDescription());
    }

    @Test
    public void when_source_fields_set_then_data_point_change() {

        //given:
        DataPointVO result = new DataPointVO(loggintTypeInterval);
        result.setXid(dataPointXid);
        result.setId(dataPointId);
        result.setTextRenderer(textRenderer);
        result.setName(dataPointNameValue);
        result.setSettable(settable);
        result.setEnabled(enabled);
        result.setChartColour(chartColour);
        result.setChartRenderer(tableChartRenderer);
        result.setDiscardExtremeValues(discardExtremeValues);
        result.setDiscardHighLimit(discardHighLimit);
        result.setDiscardLowLimit(discardLowLimit);
        result.setDefaultCacheSize(defaultCacheSize);
        result.setPurgeType(purgeTypeWeeks);
        result.setPurgePeriod(purgePeriod);
        result.setTolerance(tolerance);
        result.setIntervalLoggingType(intervalLoggingTypeMaximum);
        result.setIntervalLoggingPeriodType(intervalLoggingPeriodType);
        result.setIntervalLoggingPeriod(intervalLoggingPeriod);
        result.setEngineeringUnits(engeeringUnitBar);
        result.setDescription(description);

        JsonPointProperties source = new JsonPointProperties();
        source.setTextRenderer(new BinaryTextRenderer());
        source.setEventTextRenderer(new BinaryEventTextRenderer());
        source.setName("data_point_source_name");
        source.setEnabled(Boolean.FALSE);
        source.setChartColour("green");
        source.setChartRenderer(new StatisticsChartRenderer());
        source.setDiscardExtremeValues(Boolean.FALSE);
        source.setDiscardHighLimit(1234.0);
        source.setDiscardLowLimit(-12.0);
        source.setDefaultCacheSize(8888);
        source.setPurgeType(DataPointVO.PurgeTypes.MONTHS);
        source.setPurgePeriod(7);
        source.setTolerance(0.5);
        source.setIntervalLoggingType(DataPointVO.IntervalLoggingTypes.INSTANT);
        source.setIntervalLoggingPeriodType(Common.TimePeriods.MINUTES);
        source.setIntervalLoggingPeriod(11);
        source.setEngineeringUnits(EngineeringUnits.amperes.intValue());
        source.setDescription("desc");

        //when:
        PointPropertiesApiUtils.updateValuePointProperties(result, source);

        //then:
        assertEquals(TEXT_RENDERER,source.getTextRenderer().getTypeName(), result.getTextRenderer().getTypeName());
        assertEquals(EVENT_TEXT_RENDERER,source.getEventTextRenderer().getTypeName(), result.getEventTextRenderer().getTypeName());
        assertEquals(NAME,source.getName(), result.getName());
        assertEquals(ENABLED,source.getEnabled(), result.isEnabled());
        assertEquals(CHART_COLOUR,source.getChartColour(), result.getChartColour());
        assertEquals(CHART_RENDERER,source.getChartRenderer().getTypeName(), result.getChartRenderer().getTypeName());
        assertEquals(DISCARD_EXTREME_VALUES,source.getDiscardExtremeValues(), result.isDiscardExtremeValues());
        assertEquals(DISCARD_HIGH_LIMIT,source.getDiscardHighLimit(), result.getDiscardHighLimit(), 0.01);
        assertEquals(DISCARD_LOW_LIMIT,source.getDiscardLowLimit(), result.getDiscardLowLimit(), 0.01);
        assertEquals(DEFAULT_CACHE_SIZE,(int)source.getDefaultCacheSize(), result.getDefaultCacheSize());
        assertEquals(PURGE_TYPE,(int)source.getPurgeType(), result.getPurgeType());
        assertEquals(PURGE_PERIOD,(int)source.getPurgePeriod(), result.getPurgePeriod());
        assertEquals(TOLERANCE,source.getTolerance(), result.getTolerance(), 0.01);
        assertEquals(INTERVAL_LOGGING_TYPE,(int)source.getIntervalLoggingType(), result.getIntervalLoggingType());
        assertEquals(INTERVAL_LOGGING_PERIOD_TYPE,(int)source.getIntervalLoggingPeriodType(), result.getIntervalLoggingPeriodType());
        assertEquals(INTERVAL_LOGGING_PERIOD,(int)source.getIntervalLoggingPeriod(), result.getIntervalLoggingPeriod());
        assertEquals(ENGEERING_UNIT,(int)source.getEngineeringUnits(), result.getEngineeringUnits());
        assertEquals(DESCRIPTION,source.getDescription(), result.getDescription());
    }
}