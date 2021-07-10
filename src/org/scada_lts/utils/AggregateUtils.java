package org.scada_lts.utils;

import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;

public final class AggregateUtils {

    private static final String API_AMCHARTS_AGGREGATION_ENABLED_KEY = "api.amcharts.aggregation.enabled";
    private static final String API_AMCHARTS_AGGREGATION_VALUES_LIMIT_KEY = "api.amcharts.aggregation.valuesLimit";
    private static final String API_AMCHARTS_AGGREGATION_LIMIT_FACTOR_KEY = "api.amcharts.aggregation.limitFactor";
    private static final int NUMBER_OF_VALUES_LIMIT_DEFAULT = 10000;
    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(AggregateUtils.class);


    private AggregateUtils() {}

    public static long calculateIntervalMs(long startTs, long endTs, int numberOfPoints) {
        if(numberOfPoints == 0)
            return calculate(startTs, endTs, 1);
        return calculate(startTs, endTs, numberOfPoints);
    }

    public static boolean withAggregation() {
        try {
            return ScadaConfig.getInstance().getBoolean(API_AMCHARTS_AGGREGATION_ENABLED_KEY, false);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    private static long calculate(long startTs, long endTs, int numberOfPoints) {
        long result = Math.round((endTs - startTs)/(getNumberOfValuesLimit() * getLimitFactor()/numberOfPoints));
        return result == 0 ? 1 : result;
    }

    public static int getNumberOfValuesLimit() {
        try {
            return ScadaConfig.getInstance().getInt(API_AMCHARTS_AGGREGATION_VALUES_LIMIT_KEY, NUMBER_OF_VALUES_LIMIT_DEFAULT);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return NUMBER_OF_VALUES_LIMIT_DEFAULT;
        }
    }

    public static double getLimitFactor() {
        try {
            return Double.parseDouble(ScadaConfig.getInstance().getProperty(API_AMCHARTS_AGGREGATION_LIMIT_FACTOR_KEY));
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 1;
        }
    }
}
