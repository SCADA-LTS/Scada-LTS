package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonBinaryEventTextRenderer implements Serializable {
    private String shortLabel;
    private String longLabel;
    private String colour;

    public JsonBinaryEventTextRenderer() {
    }

    public JsonBinaryEventTextRenderer(String shortLabel, String longLabel, String colour) {
        this.shortLabel = shortLabel;
        this.longLabel = longLabel;
        this.colour = colour;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    public String getLongLabel() {
        return longLabel;
    }

    public void setLongLabel(String longLabel) {
        this.longLabel = longLabel;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
