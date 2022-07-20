package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonSettingsMisc implements Serializable {

    private int uiPerformance;
    private String dataPointRuntimeValueSynchronized;

    public JsonSettingsMisc() {}

    public JsonSettingsMisc(int uiPerformance, String dataPointRuntimeValueSynchronized) {
        this.uiPerformance = uiPerformance;
        this.dataPointRuntimeValueSynchronized = dataPointRuntimeValueSynchronized;
    }

    public int getUiPerformance() {
        return uiPerformance;
    }

    public void setUiPerformance(int uiPerformance) {
        this.uiPerformance = uiPerformance;
    }

    public String getDataPointRuntimeValueSynchronized() {
        return dataPointRuntimeValueSynchronized;
    }

    public void setDataPointRuntimeValueSynchronized(String dataPointRuntimeValueSynchronized) {
        this.dataPointRuntimeValueSynchronized = dataPointRuntimeValueSynchronized;
    }
}
