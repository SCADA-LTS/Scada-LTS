package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonBinaryEventTextRenderer implements Serializable {
    private String eventText;

    public JsonBinaryEventTextRenderer() {
    }

    public JsonBinaryEventTextRenderer(String eventText) {
        this.eventText = eventText;
    }

    public String getEventText() {
        return eventText;
    }

    public void setEventText(String label) {
        this.eventText = label;
    }
}

