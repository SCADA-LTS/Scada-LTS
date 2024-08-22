package org.scada_lts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.maint.work.WorkItemPriority;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;

import org.scada_lts.web.mvc.api.AggregateSettings;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.scada_lts.config.ThreadPoolExecutorConfig.getKey;
import static org.scada_lts.utils.CreateObjectUtils.parseObjects;

import org.scada_lts.config.ThreadPoolExecutorConfig;

public final class SystemSettingsUtils {

    private SystemSettingsUtils() {}

    private static final String DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED_KEY = "datapoint.runtime.value.synchronized";
    private static final String HTTP_RESPONSE_HEADERS_KEY = "systemsettings.http.response.headers";
    private static final String EMAIL_TIMEOUT_KEY = "systemsettings.email.timeout";
    private static final String PROCESSING_WORK_ITEMS_LIMIT_KEY = "processing.workitems.limit";
    private static final String PROCESSING_RUNNING_WORK_ITEMS_LIMIT_KEY = "processing.workitems.running.limit";
    private static final String PROCESSING_REPEAT_RUNNING_WORK_ITEMS_KEY = "processing.workitems.running.repeat";
    private static final String PROCESSING_HISTORY_WORK_ITEMS_LIMIT_KEY = "processing.workitems.history.limit";
    private static final String PROCESSING_HISTORY_FAILED_WORK_ITEMS_LIMIT_KEY = "processing.workitems.history.failed.limit";
    private static final String PROCESSING_HISTORY_PROCESS_WORK_ITEMS_LIMIT_KEY = "processing.workitems.history.process.limit";
    private static final String PROCESSING_HISTORY_HIGH_WORK_ITEMS_LIMIT_KEY = "processing.workitems.history.priority.high.limit";
    private static final String PROCESSING_HISTORY_MEDIUM_WORK_ITEMS_LIMIT_KEY = "processing.workitems.history.priority.medium.limit";
    private static final String PROCESSING_HISTORY_LOW_WORK_ITEMS_LIMIT_KEY = "processing.workitems.history.priority.low.limit";
    private static final String PROCESSING_HISTORY_EXECUTED_LONGER_WORK_ITEMS_THAN_MS_KEY = "processing.workitems.history.longer.thanMs";
    private static final String PROCESSING_HISTORY_EXECUTED_LONGER_WORK_ITEMS_LIMIT_KEY = "processing.workitems.history.longer.limit";
    private static final String SECURITY_JS_ACCESS_DENIED_METHOD_REGEXES_KEY = "scadalts.security.js.access.denied.method.regexes";
    private static final String SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES_KEY = "scadalts.security.js.access.denied.class.regexes";
    private static final String SECURITY_JS_ACCESS_GRANTED_METHOD_REGEXES_KEY = "scadalts.security.js.access.granted.method.regexes";
    private static final String SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES_KEY = "scadalts.security.js.access.granted.class.regexes";
    public static final String VIEW_FORCE_FULL_SCREEN_MODE_KEY = "view.forceFullScreen";
    public static final String VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN_KEY = "view.hideShortcutDisableFullScreen";
    public static final String EVENT_PENDING_LIMIT_KEY = "event.pending.limit";
    public static final String EVENT_PENDING_UPDATE_LIMIT_KEY = "event.pending.update.limit";
    public static final String EVENT_PENDING_CACHE_ENABLED_KEY = "abilit.cacheEnable";
    public static final String WORK_ITEMS_REPORTING_ENABLED_KEY = "workitems.reporting.enabled";
    public static final String WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED_KEY = "workitems.reporting.itemspersecond.enabled";
    public static final String WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT_KEY = "workitems.reporting.itemspersecond.limit";
    public static final String THREADS_NAME_ADDITIONAL_LENGTH_KEY = "threads.name.additional.length";
    public static final String WEB_RESOURCE_GRAPHICS_PATH_KEY = "webresource.graphics.path";
    public static final String WEB_RESOURCE_UPLOADS_PATH_KEY = "webresource.uploads.path";
    public static final String WEBSOCKET_CLIENT_SOCKJS_URL_KEY = "websocket.client.sockjs.url";

    public static final String WORK_ITEMS_CONFIG_BATCH_WRITE_BEHIND_MAX_ROWS_KEY = "workitems.config.BatchWriteBehind.maxRows";
    public static final String WORK_ITEMS_CONFIG_BATCH_WRITE_BEHIND_MAX_INSTANCES_KEY = "workitems.config.BatchWriteBehind.maxInstances";
    public static final String WORK_ITEMS_CONFIG_BATCH_WRITE_BEHIND_SPAWN_THRESHOLD_KEY = "workitems.config.BatchWriteBehind.spawnThreshold";

    public static final String HTTP_PROTOCOL_REJECT_RELATIVE_REDIRECT_KEY = "http.protocol.reject-relative-redirect";
    public static final String HTTP_PROTOCOL_METHOD_FOLLOW_REDIRECTS_KEY = "http.protocol.method.follow-redirects";
    public static final String HTTP_PROTOCOL_MAX_REDIRECTS_KEY = "http.protocol.max-redirects";
    public static final String HTTP_PROTOCOL_ALLOW_CIRCULAR_REDIRECTS_KEY = "http.protocol.allow-circular-redirects";
    public static final String HTTP_PROTOCOL_TIMEOUT_MS_KEY = "http.protocol.timeout-ms";

    public static final String EVENT_ASSIGN_ENABLED_KEY = "event.assign.enabled";

    private static final String SECURITY_HTTP_QUERY_ACCESS_DENIED_REGEX_KEY = "scadalts.security.http.query.access.denied.regex";
    private static final String SECURITY_HTTP_QUERY_ACCESS_GRANTED_REGEX_KEY = "scadalts.security.http.query.access.granted.regex";
    private static final String SECURITY_HTTP_QUERY_LIMIT_KEY = "scadalts.security.http.query.limit";
    private static final String SECURITY_HTTP_QUERY_XSS_ENABLED_KEY = "scadalts.security.http.query.xss.enabled";

    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(SystemSettingsUtils.class);

    public static DataPointSyncMode getDataPointSynchronizedMode() {
        try {
            String mode = ScadaConfig.getInstance().getConf().getProperty(DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED_KEY, "NONE");
            return DataPointSyncMode.typeOf(mode.toUpperCase());
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return DataPointSyncMode.LOW;
        }
    }

    public static String validateAggregateSettings(AggregateSettings body) {
        String msg = ValidationUtils.msgIfNull("Correct property enabled; ", body.isEnabled());
        if(msg.isEmpty() && body.isEnabled()) {
            msg += ValidationUtils.msgIfNullOrInvalid("valuesLimit must be > 0; ", body.getValuesLimit(), a -> a <= 0);
            msg += ValidationUtils.msgIfNullOrInvalid("limitFactor must be > 0; ", body.getLimitFactor(), a -> a <= 0.0);
        }
        return msg;
    }

    public static String getHttpResponseHeaders() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(HTTP_RESPONSE_HEADERS_KEY, "");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return "";
        }
    }

    public static String serializeMap(Map<String, String> map, ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
    }

    public static Map<String, String> deserializeMap(String value, ObjectMapper objectMapper) throws IOException {
        if(value == null || "null".equals(value))
            return Collections.emptyMap();
        return objectMapper.readValue(value, new TypeReference<HashMap<String, String>>() {});
    }

    public static int getEmailTimeout() {
        try {
            return Integer.parseInt(ScadaConfig.getInstance().getConf().getProperty(EMAIL_TIMEOUT_KEY, "10001"));
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return -1;
        }
    }

    public static int getWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getFailedWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_HISTORY_FAILED_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getHistoryExecutedLongerWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_HISTORY_EXECUTED_LONGER_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getHistoryExecutedLongerWorkItemsThan() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_HISTORY_EXECUTED_LONGER_WORK_ITEMS_THAN_MS_KEY, "1500");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 1500;
        }
    }

    public static int getRunningWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_RUNNING_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getHistoryProcessWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_HISTORY_PROCESS_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getHistoryHighPriorityWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_HISTORY_HIGH_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getHistoryMediumPriorityWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_HISTORY_MEDIUM_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getHistoryLowPriorityWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_HISTORY_LOW_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getRepeatRunningWorkItems() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_REPEAT_RUNNING_WORK_ITEMS_KEY, "0");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 0;
        }
    }

    public static int getHistoryWorkItemsLimit() {
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(PROCESSING_HISTORY_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static String[] getSecurityJsAccessGrantedClassRegexes() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES_KEY, "");
            return config.split(";");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new String[]{};
        }
    }

    public static String[] getSecurityJsAccessDeniedClassRegexes() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES_KEY, "");
            return config.split(";");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new String[]{};
        }
    }

    public static String[] getSecurityJsAccessDeniedMethodRegexes() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(SECURITY_JS_ACCESS_DENIED_METHOD_REGEXES_KEY, "");
            return config.split(";");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new String[]{};
        }
    }

    public static String[] getSecurityJsAccessGrantedMethodRegexes() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(SECURITY_JS_ACCESS_GRANTED_METHOD_REGEXES_KEY, "");
            return config.split(";");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new String[]{};
        }
    }

    public static boolean isHideShortcutDisableFullScreen() {
        try {
            String hideShortcutDisableFullScreen = ScadaConfig.getInstance().getConf().getProperty(VIEW_HIDE_SHORTCUT_DISABLE_FULL_SCREEN_KEY, "false");
            return Boolean.parseBoolean(hideShortcutDisableFullScreen);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static boolean isForceFullScreenMode() {
        try {
            String hideShortcutDisableFullScreen = ScadaConfig.getInstance().getConf().getProperty(VIEW_FORCE_FULL_SCREEN_MODE_KEY, "false");
            return Boolean.parseBoolean(hideShortcutDisableFullScreen);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static int getEventPendingLimit() {
        try {
            String eventPendingLimit = ScadaConfig.getInstance().getConf().getProperty(EVENT_PENDING_LIMIT_KEY, "100");
            return Integer.parseInt(eventPendingLimit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static int getEventPendingUpdateLimit() {
        try {
            String eventPendingLimit = ScadaConfig.getInstance().getConf().getProperty(EVENT_PENDING_UPDATE_LIMIT_KEY, "1000");
            return Integer.parseInt(eventPendingLimit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 1000;
        }
    }

    public static boolean isEventPendingCacheEnabled() {
        try {
            String eventPendingCache = ScadaConfig.getInstance().getConf().getProperty(EVENT_PENDING_CACHE_ENABLED_KEY, "true");
            return Boolean.parseBoolean(eventPendingCache);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return true;
        }
    }

    public static boolean isWorkItemsReportingEnabled() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(WORK_ITEMS_REPORTING_ENABLED_KEY, "true");
            return Boolean.parseBoolean(config);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return true;
        }
    }

    public static boolean isWorkItemsReportingItemsPerSecondEnabled() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_ENABLED_KEY, "true");
            return Boolean.parseBoolean(config);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return true;
        }
    }

    public static int getWorkItemsReportingItemsPerSecondLimit() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(WORK_ITEMS_REPORTING_ITEMS_PER_SECOND_LIMIT_KEY, "20000");
            return Integer.parseInt(config);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 20000;
        }
    }

    public static int getThreadsNameAdditionalLength() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(THREADS_NAME_ADDITIONAL_LENGTH_KEY, "255");
            return Integer.parseInt(config);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 255;
        }
    }

    public static String getWebResourceUploadsPath() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(WEB_RESOURCE_UPLOADS_PATH_KEY, "");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return "";
        }
    }

    public static String getWebResourceGraphicsPath() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(WEB_RESOURCE_GRAPHICS_PATH_KEY, "");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return "";
        }
    }

    public static String getWebsocketClientSockjsUrl() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(WEBSOCKET_CLIENT_SOCKJS_URL_KEY, "");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return "";
        }
    }
  
    public static String getThreadExecutorBlockingQueueInterfaceImpl(WorkItemPriority priority) {
        String defaultValue = "java.util.concurrent.LinkedBlockingQueue";
        try {
            return ScadaConfig.getInstance().getConf().getProperty(getKey(priority, ThreadPoolExecutorConfig.BLOCKING_QUEUE_INTERFACE_IMPL), defaultValue);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public static Object[] getThreadExecutorBlockingQueueInterfaceImplArgs(WorkItemPriority priority) {
        Object[] defaultValue = new Object[0];
        String defaultValue1 = "";
        try {
            String args = ScadaConfig.getInstance().getConf().getProperty(getKey(priority, ThreadPoolExecutorConfig.BLOCKING_QUEUE_INTERFACE_IMPL_ARGS), defaultValue1);
            String argsTypes = ScadaConfig.getInstance().getConf().getProperty(getKey(priority, ThreadPoolExecutorConfig.BLOCKING_QUEUE_INTERFACE_IMPL_ARGS_TYPES), defaultValue1);
            return parseObjects(args, argsTypes);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public static int getThreadExecutorCorePoolSize(WorkItemPriority priority) {
        int defaultValue = 1;
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(getKey(priority, ThreadPoolExecutorConfig.CORE_POOL_SIZE), String.valueOf(defaultValue));
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public static int getThreadExecutorMaximumPoolSize(WorkItemPriority priority) {
        int defaultValue = 1;
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(getKey(priority, ThreadPoolExecutorConfig.MAXIMUM_POOL_SIZE), String.valueOf(defaultValue));
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public static long getThreadExecutorKeepAliveTime(WorkItemPriority priority) {
        long defaultValue = 0;
        try {
            String limit = ScadaConfig.getInstance().getConf().getProperty(getKey(priority, ThreadPoolExecutorConfig.KEEP_ALIVE_TIME), String.valueOf(defaultValue));
            return Long.parseLong(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public static String getThreadExecutorTimeUnitEnumValue(WorkItemPriority priority) {
        String defaultValue = "MILLISECONDS";
        try {
            return ScadaConfig.getInstance().getConf().getProperty(getKey(priority, ThreadPoolExecutorConfig.TIME_UNIT_ENUM_VALUE), defaultValue);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultValue;
        }
    }

    public static int getWorkItemsConfigBatchWriteBehindMaxRows() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(WORK_ITEMS_CONFIG_BATCH_WRITE_BEHIND_MAX_ROWS_KEY, "255");
            return Integer.parseInt(config);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 255;
        }
    }

    public static int getWorkItemsConfigBatchWriteBehindMaxInstances() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(WORK_ITEMS_CONFIG_BATCH_WRITE_BEHIND_MAX_INSTANCES_KEY, "255");
            return Integer.parseInt(config);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 255;
        }
    }

    public static int getWorkItemsConfigBatchWriteBehindSpawnThreshold() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(WORK_ITEMS_CONFIG_BATCH_WRITE_BEHIND_SPAWN_THRESHOLD_KEY, "255");
            return Integer.parseInt(config);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 255;
        }
    }

    public static int getHttpMaxRedirects() {
        try {
            String eventPendingLimit = ScadaConfig.getInstance().getConf().getProperty(HTTP_PROTOCOL_MAX_REDIRECTS_KEY, "100");
            return Integer.parseInt(eventPendingLimit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static boolean isHttpAllowCircularRedirects() {
        try {
            String eventPendingCache = ScadaConfig.getInstance().getConf().getProperty(HTTP_PROTOCOL_ALLOW_CIRCULAR_REDIRECTS_KEY, "false");
            return Boolean.parseBoolean(eventPendingCache);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static boolean isHttpRejectRelativeRedirect() {
        try {
            String eventPendingCache = ScadaConfig.getInstance().getConf().getProperty(HTTP_PROTOCOL_REJECT_RELATIVE_REDIRECT_KEY, "true");
            return Boolean.parseBoolean(eventPendingCache);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return true;
        }
    }

    public static boolean isHttpFollowRedirects() {
        try {
            String eventPendingCache = ScadaConfig.getInstance().getConf().getProperty(HTTP_PROTOCOL_METHOD_FOLLOW_REDIRECTS_KEY, "false");
            return Boolean.parseBoolean(eventPendingCache);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static int getHttpTimeoutMs() {
        try {
            String eventPendingLimit = ScadaConfig.getInstance().getConf().getProperty(HTTP_PROTOCOL_TIMEOUT_MS_KEY, "15001");
            return Integer.parseInt(eventPendingLimit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 15001;
        }
    }

    public static boolean isEventAssignEnabled() {
        try {
            String eventAssignEnabled = ScadaConfig.getInstance().getConf().getProperty(EVENT_ASSIGN_ENABLED_KEY, "true");
            return Boolean.parseBoolean(eventAssignEnabled);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static String getSecurityHttpQueryAccessDeniedRegex() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(SECURITY_HTTP_QUERY_ACCESS_DENIED_REGEX_KEY, "");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return "";
        }
    }

    public static String getSecurityHttpQueryAccessGrantedRegex() {
        try {
            return ScadaConfig.getInstance().getConf().getProperty(SECURITY_HTTP_QUERY_ACCESS_GRANTED_REGEX_KEY, "");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return "";
        }
    }

    public static int getSecurityHttpQueryLimit() {
        try {
            String securityHttpQueryXssLimit = ScadaConfig.getInstance().getConf().getProperty(SECURITY_HTTP_QUERY_LIMIT_KEY, "4001");
            return Integer.parseInt(securityHttpQueryXssLimit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 4002;
        }
    }

    public static boolean isSecurityHttpQueryXssEnabled() {
        try {
            String securityHttpQueryXssEnabled = ScadaConfig.getInstance().getConf().getProperty(SECURITY_HTTP_QUERY_XSS_ENABLED_KEY, "false");
            return Boolean.parseBoolean(securityHttpQueryXssEnabled);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }
}
