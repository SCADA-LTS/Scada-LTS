package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonSettingsMisc implements Serializable {

    private int uiPerformance;
    private boolean dataPointRuntimeValueSynchronized;

    public JsonSettingsMisc() {}

    public JsonSettingsMisc(int uiPerformance, boolean dataPointRuntimeValueSynchronized) {
        this.uiPerformance = uiPerformance;
        this.dataPointRuntimeValueSynchronized = dataPointRuntimeValueSynchronized;
    }

    public int getUiPerformance() {
        return uiPerformance;
    }

    public void setUiPerformance(int uiPerformance) {
        this.uiPerformance = uiPerformance;
    }

    public boolean isDataPointRuntimeValueSynchronized() {
        return dataPointRuntimeValueSynchronized;
    }

    public void setDataPointRuntimeValueSynchronized(boolean dataPointRuntimeValueSynchronized) {
        this.dataPointRuntimeValueSynchronized = dataPointRuntimeValueSynchronized;
    }
}
