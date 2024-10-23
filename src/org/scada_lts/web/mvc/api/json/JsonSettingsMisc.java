package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonSettingsMisc implements Serializable {

    private int uiPerformance;
    private String dataPointRuntimeValueSynchronized;
    public boolean viewForceFullScreenEnabled;
    public boolean viewHideShortcutDisableFullScreenEnabled;
    public int eventPendingLimit;
    public boolean eventPendingCacheEnabled;
    public boolean workItemsReportingEnabled;
    public boolean workItemsReportingItemsPerSecondEnabled;
    public int workItemsReportingItemsPerSecondLimit;
    public int threadsNameAdditionalLength;
    public String webResourceGraphicsPath;
    public String webResourceUploadsPath;
    public boolean eventAssignEnabled;
    public int pointExtendedNameLengthInReportsLimit;

    public JsonSettingsMisc() {}

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

    public boolean isViewForceFullScreenEnabled() {
        return viewForceFullScreenEnabled;
    }

    public void setViewForceFullScreenEnabled(boolean viewForceFullScreenEnabled) {
        this.viewForceFullScreenEnabled = viewForceFullScreenEnabled;
    }

    public boolean isViewHideShortcutDisableFullScreenEnabled() {
        return viewHideShortcutDisableFullScreenEnabled;
    }

    public void setViewHideShortcutDisableFullScreenEnabled(boolean viewHideShortcutDisableFullScreenEnabled) {
        this.viewHideShortcutDisableFullScreenEnabled = viewHideShortcutDisableFullScreenEnabled;
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

    public boolean isEventAssignEnabled() {
        return eventAssignEnabled;
    }

    public void setEventAssignEnabled(boolean eventAssignEnabled) {
        this.eventAssignEnabled = eventAssignEnabled;
    }

    public int getDataPointExtendedNameLengthInReportsLimit() {
        return pointExtendedNameLengthInReportsLimit;
    }

    public void setDataPointExtendedNameLengthInReportsLimit(int pointExtendedNameLengthInReportsLimit) {
        this.pointExtendedNameLengthInReportsLimit = pointExtendedNameLengthInReportsLimit;
    }

}
