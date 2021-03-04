package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.view.chart.ChartRenderer;
import com.serotonin.mango.view.event.EventTextRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

import java.io.Serializable;
import java.util.List;

public class JsonPointProperties implements Serializable {

    private String name;
    private String description;
    private boolean enabled;
    private int loggingType;
    private int intervalLoggingPeriodType;
    private int intervalLoggingPeriod;
    private int intervalLoggingType;
    private double tolerance;
    private int purgeType;
    private int purgePeriod;
    private EventTextRenderer eventTextRenderer;
    private TextRenderer textRenderer;
    private ChartRenderer chartRenderer;
    private int defaultCacheSize;
    private boolean discardExtremeValues;
    private double discardLowLimit;
    private double discardHighLimit;
    private int engineeringUnits;
    private String chartColour;

    public JsonPointProperties() {
        DataPointVO defaultValues = new DataPointVO();
        this.loggingType = defaultValues.getLoggingType();
        this.intervalLoggingPeriodType = defaultValues.getIntervalLoggingPeriodType();
        this.intervalLoggingPeriod = defaultValues.getIntervalLoggingPeriod();
        this.intervalLoggingType = defaultValues.getIntervalLoggingType();
        this.tolerance = defaultValues.getTolerance();
        this.purgeType = defaultValues.getPurgeType();
        this.purgePeriod = defaultValues.getPurgePeriod();
        this.defaultCacheSize = defaultValues.getDefaultCacheSize();
        this.discardExtremeValues = defaultValues.isDiscardExtremeValues();
        this.discardLowLimit = defaultValues.getDiscardLowLimit();
        this.discardHighLimit = defaultValues.getDiscardHighLimit();
        this.engineeringUnits = defaultValues.getEngineeringUnits();
        this.eventTextRenderer = defaultValues.getEventTextRenderer();
    }

    public JsonPointProperties(String name, String description, boolean enabled, int loggingType, int intervalLoggingPeriodType, int intervalLoggingPeriod, int intervalLoggingType, double tolerance, int purgeType, int purgePeriod, EventTextRenderer eventTextRenderer, TextRenderer textRenderer, ChartRenderer chartRenderer, List<PointEventDetectorVO> eventDetectors, int defaultCacheSize, boolean discardExtremeValues, double discardLowLimit, double discardHighLimit, int engineeringUnits, String chartColour) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.loggingType = loggingType;
        this.intervalLoggingPeriodType = intervalLoggingPeriodType;
        this.intervalLoggingPeriod = intervalLoggingPeriod;
        this.intervalLoggingType = intervalLoggingType;
        this.tolerance = tolerance;
        this.purgeType = purgeType;
        this.purgePeriod = purgePeriod;
        this.eventTextRenderer = eventTextRenderer;
        this.textRenderer = textRenderer;
        this.chartRenderer = chartRenderer;
        this.defaultCacheSize = defaultCacheSize;
        this.discardExtremeValues = discardExtremeValues;
        this.discardLowLimit = discardLowLimit;
        this.discardHighLimit = discardHighLimit;
        this.engineeringUnits = engineeringUnits;
        this.chartColour = chartColour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getLoggingType() {
        return loggingType;
    }

    public void setLoggingType(int loggingType) {
        this.loggingType = loggingType;
    }

    public int getIntervalLoggingPeriodType() {
        return intervalLoggingPeriodType;
    }

    public void setIntervalLoggingPeriodType(int intervalLoggingPeriodType) {
        this.intervalLoggingPeriodType = intervalLoggingPeriodType;
    }

    public int getIntervalLoggingPeriod() {
        return intervalLoggingPeriod;
    }

    public void setIntervalLoggingPeriod(int intervalLoggingPeriod) {
        this.intervalLoggingPeriod = intervalLoggingPeriod;
    }

    public int getIntervalLoggingType() {
        return intervalLoggingType;
    }

    public void setIntervalLoggingType(int intervalLoggingType) {
        this.intervalLoggingType = intervalLoggingType;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public int getPurgeType() {
        return purgeType;
    }

    public void setPurgeType(int purgeType) {
        this.purgeType = purgeType;
    }

    public int getPurgePeriod() {
        return purgePeriod;
    }

    public void setPurgePeriod(int purgePeriod) {
        this.purgePeriod = purgePeriod;
    }

    public EventTextRenderer getEventTextRenderer() {
        return eventTextRenderer;
    }

    public void setEventTextRenderer(EventTextRenderer eventTextRenderer) {
        this.eventTextRenderer = eventTextRenderer;
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public ChartRenderer getChartRenderer() {
        return chartRenderer;
    }

    public void setChartRenderer(ChartRenderer chartRenderer) {
        this.chartRenderer = chartRenderer;
    }

    public int getDefaultCacheSize() {
        return defaultCacheSize;
    }

    public void setDefaultCacheSize(int defaultCacheSize) {
        this.defaultCacheSize = defaultCacheSize;
    }

    public boolean isDiscardExtremeValues() {
        return discardExtremeValues;
    }

    public void setDiscardExtremeValues(boolean discardExtremeValues) {
        this.discardExtremeValues = discardExtremeValues;
    }

    public double getDiscardLowLimit() {
        return discardLowLimit;
    }

    public void setDiscardLowLimit(double discardLowLimit) {
        this.discardLowLimit = discardLowLimit;
    }

    public double getDiscardHighLimit() {
        return discardHighLimit;
    }

    public void setDiscardHighLimit(double discardHighLimit) {
        this.discardHighLimit = discardHighLimit;
    }

    public int getEngineeringUnits() {
        return engineeringUnits;
    }

    public void setEngineeringUnits(int engineeringUnits) {
        this.engineeringUnits = engineeringUnits;
    }

    public String getChartColour() {
        return chartColour;
    }

    public void setChartColour(String chartColour) {
        this.chartColour = chartColour;
    }
}
