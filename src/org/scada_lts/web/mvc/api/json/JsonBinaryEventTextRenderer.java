package org.scada_lts.web.mvc.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonBinaryEventTextRenderer implements Serializable {
    private String zeroShortLabel;
    private String zeroLongLabel;
    private String zeroColour;
    private String oneShortLabel;
    private String oneLongLabel;
    private String oneColour;

    public JsonBinaryEventTextRenderer(String zeroShortLabel, String zeroLongLabel, String zeroColour, String oneShortLabel, String oneLongLabel, String oneColour) {
        this.zeroShortLabel = zeroShortLabel;
        this.zeroLongLabel = zeroLongLabel;
        this.zeroColour = zeroColour;
        this.oneShortLabel = oneShortLabel;
        this.oneLongLabel = oneLongLabel;
        this.oneColour = oneColour;
    }

    public String getZeroShortLabel() {
        return zeroShortLabel;
    }

    public void setZeroShortLabel(String zeroShortLabel) {
        this.zeroShortLabel = zeroShortLabel;
    }

    public String getZeroLongLabel() {
        return zeroLongLabel;
    }

    public void setZeroLongLabel(String zeroLongLabel) {
        this.zeroLongLabel = zeroLongLabel;
    }

    public String getZeroColour() {
        return zeroColour;
    }

    public void setZeroColour(String zeroColour) {
        this.zeroColour = zeroColour;
    }

    public String getOneShortLabel() {
        return oneShortLabel;
    }

    public void setOneShortLabel(String oneShortLabel) {
        this.oneShortLabel = oneShortLabel;
    }

    public String getOneLongLabel() {
        return oneLongLabel;
    }

    public void setOneLongLabel(String oneLongLabel) {
        this.oneLongLabel = oneLongLabel;
    }

    public String getOneColour() {
        return oneColour;
    }

    public void setOneColour(String oneColour) {
        this.oneColour = oneColour;
    }
}
