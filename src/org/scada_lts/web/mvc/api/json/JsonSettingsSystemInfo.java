package org.scada_lts.web.mvc.api.json;

import java.io.Serializable;

public class JsonSettingsSystemInfo implements Serializable {

    private String newVersionNotificationLevel;
    private String instanceDescription;
    private String language;
    private String topDescription;
    private String topDescriptionPrefix;

    public JsonSettingsSystemInfo() {}

    public JsonSettingsSystemInfo(String newVersionNotificationLevel, String instanceDescription, String language, String topDescriptionPrefix, String topDescription) {
        this.newVersionNotificationLevel = newVersionNotificationLevel;
        this.instanceDescription = instanceDescription;
        this.language = language;
        this.topDescriptionPrefix = topDescriptionPrefix;
        this.topDescription = topDescription;
    }

    public String getNewVersionNotificationLevel() {
        return newVersionNotificationLevel;
    }

    public void setNewVersionNotificationLevel(String newVersionNotificationLevel) {
        this.newVersionNotificationLevel = newVersionNotificationLevel;
    }

    public String getInstanceDescription() {
        return instanceDescription;
    }

    public void setInstanceDescription(String instanceDescription) {
        this.instanceDescription = instanceDescription;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTopDescription(){
        return topDescription;
    }

    public void setTopDescription(String topDescription){
        this.topDescription = topDescription;
    }

    public String getTopDescriptionPrefix(){
        return topDescriptionPrefix;
    }

    public void setTopDescriptionPrefix(String topDescriptionPrefix){
        this.topDescriptionPrefix = topDescriptionPrefix;
    }
}
