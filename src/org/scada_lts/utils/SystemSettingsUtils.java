package org.scada_lts.utils;

import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.web.mvc.api.AggregateSettings;

public final class SystemSettingsUtils {

    private SystemSettingsUtils() {}

    private static final String DATAPOINT_RUNTIME_VALUE_SYNCHRONIZED_KEY = "datapoint.runtime.value.synchronized";

    private static final String BACKGROUND_PROCESSING_WORK_ITEMS_LIMIT_KEY = "bp.workitems.limit";
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
        String msg = ValidationUtils.msgIfNullOrInvalid("valuesLimit must be >= 0; ", body.getValuesLimit(), a -> a <= 0);
        msg += ValidationUtils.msgIfNullOrInvalid("limitFactor must be > 0; ", body.getLimitFactor(), a -> a <= 0.0);
        msg += ValidationUtils.msgIfNull("Correct property enabled; ", body.isEnabled());
        return msg;
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
}
