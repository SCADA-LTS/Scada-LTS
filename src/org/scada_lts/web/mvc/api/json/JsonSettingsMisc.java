package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonSettingsMisc implements Serializable {

    private int uiPerformance;
    private String dataPointRuntimeValueSynchronized;
    public boolean enableFullScreen;
    public boolean hideShortcutDisableFullScreen;

    public JsonSettingsMisc() {}

    public JsonSettingsMisc(int uiPerformance, String dataPointRuntimeValueSynchronized,
                            boolean enableFullScreen, boolean hideShortcutDisableFullScreen) {
        this.uiPerformance = uiPerformance;
        this.dataPointRuntimeValueSynchronized = dataPointRuntimeValueSynchronized;
        this.enableFullScreen = enableFullScreen;
        this.hideShortcutDisableFullScreen = hideShortcutDisableFullScreen;
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

    public boolean isEnableFullScreen() {
        return enableFullScreen;
    }

    public void setEnableFullScreen(boolean enableFullScreen) {
        this.enableFullScreen = enableFullScreen;
    }

    public boolean isHideShortcutDisableFullScreen() {
        return hideShortcutDisableFullScreen;
    }

    public void setHideShortcutDisableFullScreen(boolean hideShortcutDisableFullScreen) {
        this.hideShortcutDisableFullScreen = hideShortcutDisableFullScreen;
    }
}
