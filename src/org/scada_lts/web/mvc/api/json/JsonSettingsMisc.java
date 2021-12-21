package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonSettingsMisc implements Serializable {

    private int uiPerformance;

    public JsonSettingsMisc() {}

    public JsonSettingsMisc(int uiPerformance) {
        this.uiPerformance = uiPerformance;
    }

    public int getUiPerformance() {
        return uiPerformance;
    }

    public void setUiPerformance(int uiPerformance) {
        this.uiPerformance = uiPerformance;
    }


}
