package org.scada_lts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.web.mvc.api.AggregateSettings;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SystemSettingsUtils {

    private SystemSettingsUtils() {}

    private static final String DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED_KEY = "datapoint.runtime.value.synchronized";
    private static final String HTTP_RESPONSE_HEADERS_KEY = "systemsettings.http.response.headers";
    private static final String EMAIL_TIMEOUT_KEY = "systemsettings.email.timeout";
    private static final String BACKGROUND_PROCESSING_WORK_ITEMS_LIMIT_KEY = "bp.workitems.limit";

    private static final String SECURITY_JS_ACCESS_DENIED_METHOD_REGEXES = "scadalts.security.js.access.denied.method.regexes";
    private static final String SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES = "scadalts.security.js.access.denied.class.regexes";
    private static final String SECURITY_JS_ACCESS_GRANTED_METHOD_REGEXES = "scadalts.security.js.access.granted.method.regexes";
    private static final String SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES = "scadalts.security.js.access.granted.class.regexes";

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
            String limit = ScadaConfig.getInstance().getConf().getProperty(BACKGROUND_PROCESSING_WORK_ITEMS_LIMIT_KEY, "100");
            return Integer.parseInt(limit);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 100;
        }
    }

    public static String[] getSecurityJsAccessGrantedClassRegexes() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES, "");
            return config.split(";");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new String[]{};
        }
    }

    public static String[] getSecurityJsAccessDeniedClassRegexes() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES, "");
            return config.split(";");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new String[]{};
        }
    }

    public static String[] getSecurityJsAccessDeniedMethodRegexes() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(SECURITY_JS_ACCESS_DENIED_METHOD_REGEXES, "");
            return config.split(";");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new String[]{};
        }
    }

    public static String[] getSecurityJsAccessGrantedMethodRegexes() {
        try {
            String config = ScadaConfig.getInstance().getConf().getProperty(SECURITY_JS_ACCESS_GRANTED_METHOD_REGEXES, "");
            return config.split(";");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new String[]{};
        }
    }
}
