package org.scada_lts.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.web.mvc.api.AggregateSettings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SystemSettingsUtils {

    private SystemSettingsUtils() {}

    private static final String DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED_KEY = "datapoint.runtime.value.synchronized";
    private static final String HTTP_RESPONSE_HEADERS_KEY = "systemsettings.http.response.headers";
    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(SystemSettingsUtils.class);

    public static DataPointSyncMode getDataPointSynchronizedMode() {
        try {
            String mode = ScadaConfig.getInstance().getConf().getProperty(DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED_KEY, "LOW");
            return DataPointSyncMode.valueOf(mode.toUpperCase());
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

    public static String serializeMap(Map<String, String> map, ObjectMapper objectMapper) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Map<String, String> deserializeMap(String value, ObjectMapper objectMapper) {
        try {
            if(value == null || "null".equals(value))
                return Collections.emptyMap();
            return objectMapper.readValue(value, new TypeReference<HashMap<String, String>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
