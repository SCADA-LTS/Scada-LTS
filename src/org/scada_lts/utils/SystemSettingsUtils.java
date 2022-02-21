package org.scada_lts.utils;

import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.web.mvc.api.AggregateSettings;

public class SystemSettingsUtils {

    private static final String DATAPOINT_RUNTIME_SYNCHRONIZED_KEY = "datapoint.runtime.synchronized";
    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(SystemSettingsUtils.class);

    public static boolean isDataPointRtSynchronized() {
        try {
            return ScadaConfig.getInstance().getBoolean(DATAPOINT_RUNTIME_SYNCHRONIZED_KEY, false);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static String validateAggregateSettings(AggregateSettings body) {
        String msg = ValidationUtils.msgIfNullOrInvalid("valuesLimit must be >= 0; ", body.getValuesLimit(), a -> a <= 0);
        msg += ValidationUtils.msgIfNullOrInvalid("limitFactor must be > 0; ", body.getLimitFactor(), a -> a <= 0.0);
        msg += ValidationUtils.msgIfNull("Correct property enabled; ", body.isEnabled());
        return msg;
    }
}
