package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.view.chart.ChartRenderer;
import com.serotonin.mango.view.event.EventTextRenderer;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;

import java.io.Serializable;

public class JsonPointProperties implements Serializable {

    private String name;
    private String description;
    private Boolean enabled;
    private Integer loggingType;
    private Integer intervalLoggingPeriodType;
    private Integer intervalLoggingPeriod;
    private Integer intervalLoggingType;
    private Double tolerance;
    private Integer purgeType;
    private Integer purgePeriod;
    private EventTextRenderer eventTextRenderer;
    private TextRenderer textRenderer;
    private ChartRenderer chartRenderer;
    private Integer defaultCacheSize;
    private Boolean discardExtremeValues;
    private Double discardLowLimit;
    private Double discardHighLimit;
    private Integer engineeringUnits;
    private String chartColour;

    public JsonPointProperties() {
    }

    public JsonPointProperties(String name, String description, Boolean enabled, Integer loggingType, Integer intervalLoggingPeriodType, Integer intervalLoggingPeriod, Integer intervalLoggingType, Double tolerance, Integer purgeType, Integer purgePeriod, EventTextRenderer eventTextRenderer, TextRenderer textRenderer, ChartRenderer chartRenderer, Integer defaultCacheSize, Boolean discardExtremeValues, Double discardLowLimit, Double discardHighLimit, Integer engineeringUnits, String chartColour) {
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

    public static void defaultValues(JsonPointProperties body) {
        DataPointVO defaultValues = new DataPointVO();
        if (body.getLoggingType() == null)
            body.setLoggingType(defaultValues.getLoggingType());
        if (body.getIntervalLoggingPeriodType() == null)
            body.setIntervalLoggingPeriodType(defaultValues.getIntervalLoggingPeriodType());
        if (body.getIntervalLoggingPeriod() == null)
            body.setIntervalLoggingPeriod(defaultValues.getIntervalLoggingPeriod());
        if (body.getIntervalLoggingType() == null)
            body.setIntervalLoggingType(defaultValues.getIntervalLoggingType());
        if (body.getTolerance() == null)
            body.setTolerance(defaultValues.getTolerance());
        if (body.getPurgeType() == null)
            body.setPurgeType(defaultValues.getPurgeType());
        if (body.getPurgePeriod() == null)
            body.setPurgePeriod(defaultValues.getPurgePeriod());
        if (body.getDefaultCacheSize() == null)
            body.setDefaultCacheSize(defaultValues.getDefaultCacheSize());
        if (body.getDiscardExtremeValues() == null)
            body.setDiscardExtremeValues(defaultValues.isDiscardExtremeValues());
        if (body.getDiscardLowLimit() == null)
            body.setDiscardLowLimit(defaultValues.getDiscardLowLimit());
        if (body.getDiscardHighLimit() == null)
            body.setDiscardHighLimit(defaultValues.getDiscardHighLimit());
        if (body.getEngineeringUnits() == null)
            body.setEngineeringUnits(defaultValues.getEngineeringUnits());
        if (body.getEventTextRenderer() == null)
            body.setEventTextRenderer(defaultValues.getEventTextRenderer());
        if (body.getEnabled() == null)
            body.setEnabled(defaultValues.isEnabled());
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getLoggingType() {
        return loggingType;
    }

    public void setLoggingType(Integer loggingType) {
        this.loggingType = loggingType;
    }

    public Integer getIntervalLoggingPeriodType() {
        return intervalLoggingPeriodType;
    }

    public void setIntervalLoggingPeriodType(Integer intervalLoggingPeriodType) {
        this.intervalLoggingPeriodType = intervalLoggingPeriodType;
    }

    public Integer getIntervalLoggingPeriod() {
        return intervalLoggingPeriod;
    }

    public void setIntervalLoggingPeriod(Integer intervalLoggingPeriod) {
        this.intervalLoggingPeriod = intervalLoggingPeriod;
    }

    public Integer getIntervalLoggingType() {
        return intervalLoggingType;
    }

    public void setIntervalLoggingType(Integer intervalLoggingType) {
        this.intervalLoggingType = intervalLoggingType;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public void setTolerance(Double tolerance) {
        this.tolerance = tolerance;
    }

    public Integer getPurgeType() {
        return purgeType;
    }

    public void setPurgeType(Integer purgeType) {
        this.purgeType = purgeType;
    }

    public Integer getPurgePeriod() {
        return purgePeriod;
    }

    public void setPurgePeriod(Integer purgePeriod) {
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

    public Integer getDefaultCacheSize() {
        return defaultCacheSize;
    }

    public void setDefaultCacheSize(Integer defaultCacheSize) {
        this.defaultCacheSize = defaultCacheSize;
    }

    public Boolean getDiscardExtremeValues() {
        return discardExtremeValues;
    }

    public void setDiscardExtremeValues(Boolean discardExtremeValues) {
        this.discardExtremeValues = discardExtremeValues;
    }

    public Double getDiscardLowLimit() {
        return discardLowLimit;
    }

    public void setDiscardLowLimit(Double discardLowLimit) {
        this.discardLowLimit = discardLowLimit;
    }

    public Double getDiscardHighLimit() {
        return discardHighLimit;
    }

    public void setDiscardHighLimit(Double discardHighLimit) {
        this.discardHighLimit = discardHighLimit;
    }

    public Integer getEngineeringUnits() {
        return engineeringUnits;
    }

    public void setEngineeringUnits(Integer engineeringUnits) {
        this.engineeringUnits = engineeringUnits;
    }

    public String getChartColour() {
        return chartColour;
    }

    public void setChartColour(String chartColour) {
        this.chartColour = chartColour;
    }
}
