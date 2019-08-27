package org.scada_lts.config;

public enum SystemConfigKeys implements ScadaConfigKey {
    /**
     * Replace alert enabled (=true) or disabled (=false)
     */
    REPLACE_ALERT_ON_VIEW("abilit.api.replace.alert.onview"),

    /**
     * Event cache enabled (=true) or disabled (=false)
     */
    ENABLE_CACHE("abilit.cacheEnable"),

    /**
     * Period update unsilenced alarm level but can be delayed when system heavy loaded.
     */
    MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL("abilit.MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL"),

    /**
     * Start update unsilenced alarm level.
     */
    START_UPDATE_UNSILENCED_ALARM_LEVEL("abilit.START_UPDATE_UNSILENCED_ALARM_LEVEL"),

    /**
     * Period update event detectors but can be delayed when system heavy loaded.
     */
    MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS("abilit.MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS"),

    /**
     * Start update event detectors.
     */
    START_UPDATE_EVENT_DETECTORS("abilit.START_UPDATE_EVENT_DETECTORS"),

    /**
     * Period update pending events but can be delayed when system heavy loaded.
     */
    MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS("abilit.MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS"),

    /**
     * Start update pending events.
     */
    START_UPDATE_PENDING_EVENTS("abilit.START_UPDATE_PENDING_EVENTS"),

    /**
     * Period update point hierarchy but can be delayed when system heavy loaded. For example cron 0 15 1 ? * *.
     */
    CRONE_UPDATE_CACHE_POINT_HIERARCHY("abilit.CRONE_UPDATE_CACHE_POINT_HIERARCHY"),

    /**
     * Period update data sources points. For example cron 0 15 1 ? * * the after start
     */
    CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS("abilit.CRONE_UPDATE_DATA_SOURCES_POINTS"),

    /**
     * Use Cache data sources points when the system is ready
     */
    USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY("abilit.USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY"),
    USE_ACL("abilit.USE_ACL"),
    ACL_SERVER("abilit.ACL_SERVER"),
    HTTP_RETRIVER_SLEEP_CHECK_TO_REACTIVATION_WHEN_START("abilit.HTTP_RETRIVER_SLEEP_CHECK_TO_REACTIVATION_WHEN_START"),
    HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION("abilit.HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION");


    private String key;

    SystemConfigKeys(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
