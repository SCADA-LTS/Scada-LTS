package org.scada_lts.mango.service;

import br.org.scadabr.db.configuration.ConfigurationDB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.DataPurge;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.bean.PointHistoryCount;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.web.email.IMsgSubjectContent;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.mango.adapter.MangoEvent;
import org.scada_lts.serorepl.utils.DirectoryInfo;
import org.scada_lts.serorepl.utils.DirectoryUtils;
import org.scada_lts.utils.SystemSettingsUtils;
import org.scada_lts.web.mvc.api.AggregateSettings;
import org.scada_lts.web.mvc.api.json.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.serotonin.mango.util.LoggingUtils.userInfo;
import static com.serotonin.mango.util.SendUtils.sendMsgTestSync;
import static org.scada_lts.utils.SystemSettingsUtils.serializeMap;

/**
 * Based on the WatchListService created by Grzegorz Bylica
 *
 * @author Radoslaw Jajko
 */
public class SystemSettingsService {

    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(SystemSettingsService.class);

    private SystemSettingsDAO systemSettingsDAO;

    public SystemSettingsService() {
        systemSettingsDAO = new SystemSettingsDAO();
    }

    public Map<String, Object> getSettings() {
        Map<String, Object> settings = new HashMap<>();

        settings.put("emailSettings", this.getEmailSettings());
        settings.put("httpSettings", this.getHttpSettings());
        settings.put("miscSettings", this.getMiscSettings());
        settings.put("systemInfoSettings", this.getSystemInfoSettings());
        settings.put("systemEventTypes", this.getSystemEventAlarmLevels());
        settings.put("auditEventTypes", this.getAuditEventAlarmLevels());
        settings.put("databaseType", this.getDatabaseType());
        settings.put("scadaConfig", this.getScadaConfig());
        settings.put("aggregateSettings", this.getAggregateSettings());

        return settings;
    }

    public Map<String, Object> getDefaultLoggingType() {
        Map<String, Object> response = new HashMap<>();
        response.put("defaultLoggingType", SystemSettingsDAO.getIntValue(SystemSettingsDAO.DEFAULT_LOGGING_TYPE));
        return response;
    }

    public void setDefaultLoggingType(String defaultLoggingType) {
        systemSettingsDAO.setValue(SystemSettingsDAO.DEFAULT_LOGGING_TYPE, defaultLoggingType);
    }

    public JsonSettingsEmail getEmailSettings() {
        JsonSettingsEmail json = new JsonSettingsEmail();
        json.setAuth(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EMAIL_AUTHORIZATION));
        json.setTls(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EMAIL_TLS));
        json.setContentType(SystemSettingsDAO.getIntValue(SystemSettingsDAO.EMAIL_CONTENT_TYPE));
        json.setPort(SystemSettingsDAO.getIntValue(SystemSettingsDAO.EMAIL_SMTP_PORT));
        json.setFrom(SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_FROM_ADDRESS));
        json.setHost(SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_HOST));
        json.setName(SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_FROM_NAME));
        json.setUsername(SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_USERNAME));
        json.setPassword(SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_PASSWORD));
        return json;
    }

    public void saveEmailSettings(JsonSettingsEmail json) {
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.EMAIL_AUTHORIZATION, json.isAuth());
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.EMAIL_TLS, json.isTls());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.EMAIL_CONTENT_TYPE, json.getContentType());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.EMAIL_SMTP_PORT, json.getPort());
        systemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_SMTP_HOST, json.getHost());
        systemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_SMTP_USERNAME, json.getUsername());
        systemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_SMTP_PASSWORD, json.getPassword());
        systemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_FROM_ADDRESS, json.getFrom());
        systemSettingsDAO.setValue(SystemSettingsDAO.EMAIL_FROM_NAME, json.getName());
    }

    public String getSMSDomain() {
        return SystemSettingsDAO.getValue(SystemSettingsDAO.SMS_DOMAIN);
    }

    public void saveSMSDomain(String smsDomain) {
        systemSettingsDAO.setValue(SystemSettingsDAO.SMS_DOMAIN, smsDomain);
    }

    public JsonSettingsHttp getHttpSettings() {
        JsonSettingsHttp json = new JsonSettingsHttp();
        json.setUseProxy(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.HTTP_CLIENT_USE_PROXY));
        json.setPort(SystemSettingsDAO.getIntValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT));
        json.setHost(SystemSettingsDAO.getValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER));
        json.setUsername(SystemSettingsDAO.getValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME));
        json.setPassword(SystemSettingsDAO.getValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD));
        json.setHttpResponseHeaders(SystemSettingsDAO.getValue(SystemSettingsDAO.HTTP_RESPONSE_HEADERS));
        return json;
    }

    public void saveHttpSettings(JsonSettingsHttp json) {
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.HTTP_CLIENT_USE_PROXY, json.isUseProxy());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_PORT, json.getPort());
        systemSettingsDAO.setValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_SERVER, json.getHost());
        systemSettingsDAO.setValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_USERNAME, json.getUsername());
        systemSettingsDAO.setValue(SystemSettingsDAO.HTTP_CLIENT_PROXY_PASSWORD, json.getPassword());
        systemSettingsDAO.setValue(SystemSettingsDAO.HTTP_RESPONSE_HEADERS, getHttpResponseHeaders(json));
    }

    public JsonSettingsMisc getMiscSettings() {
        JsonSettingsMisc json = new JsonSettingsMisc();
        json.setUiPerformance(SystemSettingsDAO.getIntValue(SystemSettingsDAO.UI_PERFORMANCE));
        json.setDataPointRuntimeValueSynchronized(SystemSettingsDAO.getValue(SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED));
        json.setHideShortcutDisableFullScreen(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN, false));
        json.setEnableFullScreen(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE, false));
        json.setEventPendingLimit(SystemSettingsDAO.getIntValue(SystemSettingsDAO.EVENT_PENDING_LIMIT, 100));
        json.setEventPendingCacheEnabled(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED, false));
        json.setThreadsNameAdditionalLength(SystemSettingsDAO.getIntValue(SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH, 255));
        json.setWorkItemsReportingEnabled(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED, true));
        json.setWorkItemsReportingItemsPerSecondEnabled(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED, true));
        json.setWorkItemsReportingItemsPerSecondLimit(SystemSettingsDAO.getIntValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT, 20000));
        json.setWebResourceGraphicsPath(SystemSettingsDAO.getValue(SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH));
        json.setWebResourceUploadsPath(SystemSettingsDAO.getValue(SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH));
        return json;
    }

    public void saveMiscSettings(JsonSettingsMisc json) {
        systemSettingsDAO.setIntValue(SystemSettingsDAO.UI_PERFORMANCE, json.getUiPerformance());
        systemSettingsDAO.setValue(SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED, DataPointSyncMode.getName(json.getDataPointRuntimeValueSynchronized()));
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN, json.isHideShortcutDisableFullScreen());
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.VIEW_FORCE_FULL_SCREEN_MODE, json.isEnableFullScreen());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.EVENT_PENDING_LIMIT, json.getEventPendingLimit());
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.EVENT_PENDING_CACHE_ENABLED, json.isEventPendingCacheEnabled());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH, json.getThreadsNameAdditionalLength());
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED, json.isWorkItemsReportingEnabled());
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED, json.isWorkItemsReportingItemsPerSecondEnabled());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT, json.getWorkItemsReportingItemsPerSecondLimit());
        systemSettingsDAO.setValue(SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH, json.getWebResourceGraphicsPath());
        systemSettingsDAO.setValue(SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH, json.getWebResourceUploadsPath());
    }

    public SettingsDataRetention getDataRetentionSettings() {
        SettingsDataRetention settings = new SettingsDataRetention();
        settings.setGroveLogging(SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.GROVE_LOGGING));
        settings.setEventPurgePeriodType(SystemSettingsDAO.getIntValue(SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE));
        settings.setEventPurgePeriods(SystemSettingsDAO.getIntValue(SystemSettingsDAO.EVENT_PURGE_PERIODS));
        settings.setReportPurgePeriodType(SystemSettingsDAO.getIntValue(SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE));
        settings.setReportPurgePeriods(SystemSettingsDAO.getIntValue(SystemSettingsDAO.REPORT_PURGE_PERIODS));
        settings.setFutureDateLimitPeriodType(SystemSettingsDAO.getIntValue(SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE));
        settings.setFutureDateLimitPeriods(SystemSettingsDAO.getIntValue(SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS));
        settings.setValuesLimitForPurge(SystemSettingsDAO.getIntValue(SystemSettingsDAO.VALUES_LIMIT_FOR_PURGE));
        return settings;
    }

    public void saveDataRetentionSettings(SettingsDataRetention settings) {
        systemSettingsDAO.setBooleanValue(SystemSettingsDAO.GROVE_LOGGING, settings.isGroveLogging());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.EVENT_PURGE_PERIOD_TYPE, settings.getEventPurgePeriodType());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.EVENT_PURGE_PERIODS, settings.getEventPurgePeriods());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.REPORT_PURGE_PERIOD_TYPE, settings.getReportPurgePeriodType());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.REPORT_PURGE_PERIODS, settings.getReportPurgePeriods());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIOD_TYPE, settings.getFutureDateLimitPeriodType());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.FUTURE_DATE_LIMIT_PERIODS, settings.getFutureDateLimitPeriods());
        systemSettingsDAO.setIntValue(SystemSettingsDAO.VALUES_LIMIT_FOR_PURGE, settings.getValuesLimitForPurge());
    }

    public List<JsonSettingsEventLevels> getAuditEventAlarmLevels() {
        List<EventTypeVO> auditTypeVoList = AuditEventType.getAuditEventTypes();
        List<JsonSettingsEventLevels> json = new ArrayList<JsonSettingsEventLevels>();
        for (EventTypeVO eventTypeVO : auditTypeVoList) {
            JsonSettingsEventLevels ip = new JsonSettingsEventLevels();
            ip.setI1(eventTypeVO.getTypeRef1());
            ip.setI2(eventTypeVO.getAlarmLevel());
            ip.setTranslation(eventTypeVO.getDescription().getKey());
            json.add(ip);
        }
        return json;
    }

    public void saveAuditEventAlarmLevels(List<JsonSettingsEventLevels> eventAlarmLevels) {
        for (JsonSettingsEventLevels eventAlarmLevel : eventAlarmLevels) {
            AuditEventType.setEventTypeAlarmLevel(eventAlarmLevel.getI1(), eventAlarmLevel.getI2());
        }
    }

    public List<JsonSettingsEventLevels> getSystemEventAlarmLevels() {
        List<EventTypeVO> eventTypeVoList = SystemEventType.getSystemEventTypes();
        List<JsonSettingsEventLevels> json = new ArrayList<JsonSettingsEventLevels>();
        for (EventTypeVO eventTypeVO : eventTypeVoList) {
            JsonSettingsEventLevels ip = new JsonSettingsEventLevels();
            ip.setI1(eventTypeVO.getTypeRef1());
            ip.setI2(eventTypeVO.getAlarmLevel());
            ip.setTranslation(eventTypeVO.getDescription().getKey());
            json.add(ip);
        }
        return json;
    }

    public void saveSystemEventAlarmLevels(List<JsonSettingsEventLevels> eventAlarmLevels) {
        for (JsonSettingsEventLevels eventAlarmLevel : eventAlarmLevels) {
            SystemEventType.setEventTypeAlarmLevel(eventAlarmLevel.getI1(), eventAlarmLevel.getI2());
        }
    }

    public JsonSettingsSystemInfo getSystemInfoSettings() {
        JsonSettingsSystemInfo json = new JsonSettingsSystemInfo();
        json.setNewVersionNotificationLevel(SystemSettingsDAO.getValue(SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL));
        json.setInstanceDescription(SystemSettingsDAO.getValue(SystemSettingsDAO.INSTANCE_DESCRIPTION));
        json.setLanguage(SystemSettingsDAO.getValue(SystemSettingsDAO.LANGUAGE));
        return json;
    }

    public void saveSystemInfoSettings(JsonSettingsSystemInfo json) {
        systemSettingsDAO.setValue(SystemSettingsDAO.NEW_VERSION_NOTIFICATION_LEVEL, json.getNewVersionNotificationLevel());
        systemSettingsDAO.setValue(SystemSettingsDAO.INSTANCE_DESCRIPTION, json.getInstanceDescription());
        systemSettingsDAO.setValue(SystemSettingsDAO.LANGUAGE, json.getLanguage());
    }

    public String getDatabaseType() {
        return Common.getEnvironmentProfile().getString("db.type", "derby");
    }

    public void setDatabaseType(String databaseType) {
        if (databaseType.equalsIgnoreCase("mysql")) {
            ConfigurationDB.useMysqlDB();
        } else if (databaseType.equalsIgnoreCase("mssql")) {
            ConfigurationDB.useMssqlDB();
        } else if (databaseType.equalsIgnoreCase("oracle11g")) {
            ConfigurationDB.useOracle11gDB();
        } else {
            ConfigurationDB.useDerbyDB();
        }
    }

    public Map<String, Object> getDatabaseSize() {
        Map<String, Object> data = new HashMap<>();

        File dataDirecotry = Common.ctx.getDatabaseAccess().getDataDirectory();
        long dbSize = 0;
        if (dataDirecotry != null) {
            DirectoryInfo dbInfo = DirectoryUtils.getDirectorySize(dataDirecotry);
            dbSize = dbInfo.getSize();
            data.put("databaseSize", com.serotonin.util.DirectoryUtils.bytesDescription(dbSize));
        } else {
            data.put("databaseSize", "common.unknown");
        }

        DirectoryInfo fileDatainfo = DirectoryUtils.getDirectorySize(new File(Common.getFiledataPath()));
        long filedataSize = fileDatainfo.getSize();
        data.put("filedataCount", fileDatainfo.getCount());
        data.put("filedataSize", com.serotonin.util.DirectoryUtils.bytesDescription(filedataSize));
        data.put("totalSize", com.serotonin.util.DirectoryUtils.bytesDescription(dbSize + filedataSize));

        if (getDatabaseType().equalsIgnoreCase("mysql")) {
            double size = systemSettingsDAO.getDataBaseSize();
            data.put("databaseSize", size + "MB");
            data.put("filedataCount", 0);
            data.put("filedataSize", 0);
            data.put("totalSize", size + "MB");
        }

        List<PointHistoryCount> counts = new DataPointDao().getTopPointHistoryCounts();
        int sum = 0;
        for (PointHistoryCount c : counts) {
            sum += c.getCount();
        }

        data.put("historyCount", sum);
        data.put("topPoints", counts);

        MangoEvent eventService = new EventService();
        data.put("eventCount", eventService.getEventCount());

        return data;
    }

    public String sendTestEmail(User user) throws Exception {

        ResourceBundle bundle = Common.getBundle();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("message", new LocalizableMessage("systemSettings.testEmail"));
        IMsgSubjectContent cnt = IMsgSubjectContent.newInstance(
                "testEmail", model, bundle, I18NUtils.getMessage(bundle, "ftl.testEmail"), Common.UTF8);
        sendMsgTestSync(user.getEmail(), cnt, model, () -> "sendTestEmail from: " + this.getClass().getName()
                + ", " + userInfo(user));

        return "{\"recipient\":\""+user.getEmail()+ "\"}";
    }

    public void purgeAllData() {
        Common.ctx.getRuntimeManager().purgeDataPointValues();
    }

    public void purgeNow() {
        DataPurge dataPurge = new DataPurge();
        dataPurge.execute(System.currentTimeMillis());
    }

    public long getStartupTime() {
        return Common.getStartupTime();
    }

    public String getSchemaVersion() {
        return systemSettingsDAO.getDatabaseSchemaVersion(SystemSettingsDAO.DATABASE_INFO_SCHEMA_VERSION, "1");
    }

    public JsonSettingsScadaConfig getScadaConfig() {
        try {
            JsonSettingsScadaConfig json = new JsonSettingsScadaConfig();

            json.setApiReplaceAlertOnView(ScadaConfig.getInstance().getBoolean(ScadaConfig.REPLACE_ALERT_ON_VIEW, true));

            json.setCacheEnable(ScadaConfig.getInstance().getBoolean(ScadaConfig.ENABLE_CACHE, true));
            json.setStartUpdateUnsilencedAlarmLevel(ScadaConfig.getInstance().getInt(ScadaConfig.START_UPDATE_UNSILENCED_ALARM_LEVEL, 100000));
            json.setStartUpdateEventDetectors(ScadaConfig.getInstance().getInt(ScadaConfig.START_UPDATE_EVENT_DETECTORS, 100000));
            json.setStartUpdatePendingEvents(ScadaConfig.getInstance().getInt(ScadaConfig.START_UPDATE_PENDING_EVENTS, 100000));
            json.setMillisSecondsPeriodUpdateUnsilencedAlarmLevel(ScadaConfig.getInstance().getInt(ScadaConfig.MILLIS_SECONDS_PERIOD_UPDATE_UNSILENCED_ALARM_LEVEL, 1000));
            json.setMillisSecondsPeriodUpdateEventDetectors(ScadaConfig.getInstance().getInt(ScadaConfig.MILLIS_SECONDS_PERIOD_UPDATE_EVENT_DETECTORS, 1000));
            json.setMillisSecondsPeriodUpdatePendingEvents(ScadaConfig.getInstance().getInt(ScadaConfig.MILLIS_SECONDS_PERIOD_UPDATE_PENDING_EVENTS, 1000));
            json.setCroneUpdateCachePointHierarchy(ScadaConfig.getInstance().getProperty(ScadaConfig.CRONE_UPDATE_CACHE_POINT_HIERARCHY));

            json.setCroneUpdateDataSourcesPoints(ScadaConfig.getInstance().getProperty(ScadaConfig.CRONE_UPDATE_CACHE_DATA_SOURCES_POINTS));
            json.setUseCacheDataSourcePointsWhenSystemIsReady(ScadaConfig.getInstance().getBoolean(ScadaConfig.USE_CACHE_DATA_SOURCES_POINTS_WHEN_THE_SYSTEM_IS_READY, true));

            json.setUseAcl(ScadaConfig.getInstance().getBoolean(ScadaConfig.ACL_SERVER, false));
            json.setAclServer(ScadaConfig.getInstance().getProperty(ScadaConfig.ACL_SERVER));

            json.setDoNotCreateEventsForEmailError(ScadaConfig.getInstance().getBoolean(ScadaConfig.DO_NOT_CREATE_EVENTS_FOR_EMAIL_ERROR, true));

            json.setHttpRetriverSleepCheckToReactivationWhenStart(ScadaConfig.getInstance().getBoolean(ScadaConfig.HTTP_RETRIVER_SLEEP_CHECK_TO_REACTIVATION_WHEN_START, false));
            json.setHttpRetriverDoNotAllowEnableReactivation(ScadaConfig.getInstance().getBoolean(ScadaConfig.HTTP_RETRIVER_DO_NOT_ALLOW_ENABLE_REACTIVATION, false));

            return json;

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }


    }

    public AggregateSettings getAggregateSettings() {
        AggregateSettings aggregateSettings = new AggregateSettings();
        aggregateSettings.setEnabled(SystemSettingsDAO.getBooleanValueOrDefault(SystemSettingsDAO.AGGREGATION_ENABLED));
        aggregateSettings.setValuesLimit(SystemSettingsDAO.getIntValue(SystemSettingsDAO.AGGREGATION_VALUES_LIMIT));

        try {
            double var = Double.parseDouble(SystemSettingsDAO.getValue(SystemSettingsDAO.AGGREGATION_LIMIT_FACTOR));
            aggregateSettings.setLimitFactor(var);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            AggregateSettings defaultValue = AggregateSettings.fromEnvProperties();
            aggregateSettings.setLimitFactor(defaultValue.getLimitFactor());
        }
        return aggregateSettings;
    }

    public void saveAggregateSettings(AggregateSettings aggregateSettings) {
        systemSettingsDAO.setValue(SystemSettingsDAO.AGGREGATION_VALUES_LIMIT, String.valueOf(aggregateSettings.getValuesLimit()));
        systemSettingsDAO.setValue(SystemSettingsDAO.AGGREGATION_LIMIT_FACTOR, String.valueOf(aggregateSettings.getLimitFactor()));
        systemSettingsDAO.setValue(SystemSettingsDAO.AGGREGATION_ENABLED, String.valueOf(aggregateSettings.isEnabled()));
    }

    public DataPointSyncMode getDataPointRtValueSynchronized() {
        return SystemSettingsDAO.getObject(SystemSettingsDAO.DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED, DataPointSyncMode::typeOf);
    }

    public Map<String, String> getHttpResponseHeaders() {
        try {
            return SystemSettingsDAO.getObject(SystemSettingsDAO.HTTP_RESPONSE_HEADERS, SystemSettingsService::deserializeMap);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    public boolean isWorkItemsReportingEnabled() {
        boolean defaultValue = SystemSettingsUtils.isWorkItemsReportingEnabled();
        try {
            return SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ENABLED, defaultValue);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public boolean isWorkItemsReportingItemsPerSecondEnabled() {
        boolean defaultValue = SystemSettingsUtils.isWorkItemsReportingItemsPerSecondEnabled();
        try {
            return SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED, defaultValue);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public int getWorkItemsReportingItemsPerSecondLimit() {
        int defaultValue = SystemSettingsUtils.getWorkItemsReportingItemsPerSecondLimit();
        try {
            return SystemSettingsDAO.getIntValue(SystemSettingsDAO.WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT, defaultValue);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public String getWebResourceGraphicsPath(){
        String defaultValue = SystemSettingsUtils.getWebResourceGraphicsPath();
        try {
            return SystemSettingsDAO.getValue(SystemSettingsDAO.WEB_RESOURCE_GRAPHICS_PATH, defaultValue);
        } catch (Exception e){
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public String getWebResourceUploadsPath(){
        String defaultValue = SystemSettingsUtils.getWebResourceUploadsPath();
        try {
            return SystemSettingsDAO.getValue(SystemSettingsDAO.WEB_RESOURCE_UPLOADS_PATH, defaultValue);
        } catch (Exception e){
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public int getThreadsNameAdditionalLength() {
        int defaultValue = SystemSettingsUtils.getThreadsNameAdditionalLength();
        try {
            return SystemSettingsDAO.getIntValue(SystemSettingsDAO.THREADS_NAME_ADDITIONAL_LENGTH, defaultValue);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    private static String getHttpResponseHeaders(JsonSettingsHttp json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> headers = SystemSettingsUtils.deserializeMap(json.getHttpResponseHeaders(), objectMapper);
            return serializeMap(headers, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> deserializeMap(String json) {
        try {
            return SystemSettingsUtils.deserializeMap(json, new ObjectMapper());
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return Collections.emptyMap();
        }
    }
}
