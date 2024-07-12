package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonSettingsMisc implements Serializable {

    private int uiPerformance;
    private String dataPointRuntimeValueSynchronized;
    public boolean enableFullScreen;
    public boolean hideShortcutDisableFullScreen;
    public int eventPendingLimit;
    public boolean eventPendingCacheEnabled;
    public boolean workItemsReportingEnabled;
    public boolean workItemsReportingItemsPerSecondEnabled;
    public int workItemsReportingItemsPerSecondLimit;
    public int threadsNameAdditionalLength;
    public String webResourceGraphicsPath;
    public String webResourceUploadsPath;

    public JsonSettingsMisc() {}

    @Deprecated(since = "2.7.7.1")
    public JsonSettingsMisc(int uiPerformance, String dataPointRuntimeValueSynchronized, boolean enableFullScreen, boolean hideShortcutDisableFullScreen, int eventPendingLimit, boolean eventPendingCacheEnabled, String webResourceGraphicsPath, String webResourceUploadsPath) {
        this.uiPerformance = uiPerformance;
        this.dataPointRuntimeValueSynchronized = dataPointRuntimeValueSynchronized;
        this.enableFullScreen = enableFullScreen;
        this.hideShortcutDisableFullScreen = hideShortcutDisableFullScreen;
        this.eventPendingLimit = eventPendingLimit;
        this.eventPendingCacheEnabled = eventPendingCacheEnabled;
        this.webResourceGraphicsPath = webResourceGraphicsPath;
        this.webResourceUploadsPath = webResourceUploadsPath;
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

    public int getEventPendingLimit() {
        return eventPendingLimit;
    }

    public void setEventPendingLimit(int eventPendingLimit) {
        this.eventPendingLimit = eventPendingLimit;
    }

    public boolean isEventPendingCacheEnabled() {
        return eventPendingCacheEnabled;
    }

    public void setEventPendingCacheEnabled(boolean eventPendingCacheEnabled) {
        this.eventPendingCacheEnabled = eventPendingCacheEnabled;
    }

    public boolean isWorkItemsReportingEnabled() {
        return workItemsReportingEnabled;
    }

    public void setWorkItemsReportingEnabled(boolean workItemsReportingEnabled) {
        this.workItemsReportingEnabled = workItemsReportingEnabled;
    }

    public boolean isWorkItemsReportingItemsPerSecondEnabled() {
        return workItemsReportingItemsPerSecondEnabled;
    }

    public void setWorkItemsReportingItemsPerSecondEnabled(boolean workItemsReportingItemsPerSecondEnabled) {
        this.workItemsReportingItemsPerSecondEnabled = workItemsReportingItemsPerSecondEnabled;
    }

    public int getWorkItemsReportingItemsPerSecondLimit() {
        return workItemsReportingItemsPerSecondLimit;
    }

    public void setWorkItemsReportingItemsPerSecondLimit(int workItemsReportingItemsPerSecondLimit) {
        this.workItemsReportingItemsPerSecondLimit = workItemsReportingItemsPerSecondLimit;
    }

    public int getThreadsNameAdditionalLength() {
        return threadsNameAdditionalLength;
    }

    public void setThreadsNameAdditionalLength(int threadsNameAdditionalLength) {
        this.threadsNameAdditionalLength = threadsNameAdditionalLength;
    }

    public String getWebResourceGraphicsPath() {
        return webResourceGraphicsPath;
    }

    public void setWebResourceGraphicsPath(String webResourceGraphicsPath) {
        this.webResourceGraphicsPath = webResourceGraphicsPath;
    }

    public String getWebResourceUploadsPath() {
        return webResourceUploadsPath;
    }

    public void setWebResourceUploadsPath(String webResourceUploadsPath) {
        this.webResourceUploadsPath = webResourceUploadsPath;
    }
}
