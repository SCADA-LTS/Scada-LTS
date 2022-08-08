package org.scada_lts.utils;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.web.mvc.api.AggregateSettings;

import static org.scada_lts.dao.pointvalues.PointValueAmChartDAO.dataPointInfo;

public final class AggregateUtils {

    private static final String API_AMCHARTS_AGGREGATION_ENABLED_KEY = "api.amcharts.aggregation.enabled";
    private static final String API_AMCHARTS_AGGREGATION_VALUES_LIMIT_KEY = "api.amcharts.aggregation.valuesLimit";
    private static final String API_AMCHARTS_AGGREGATION_LIMIT_FACTOR_KEY = "api.amcharts.aggregation.limitFactor";
    private static final int NUMBER_OF_VALUES_LIMIT_DEFAULT = 10000;
    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(AggregateUtils.class);


    private AggregateUtils() {}

    public static long calculateIntervalMs(long startTs, long endTs, int numberOfPoints, AggregateSettings aggregateSettings) {
        if(startTs > endTs)
            throw new IllegalArgumentException("startTs > endTs");
        if(numberOfPoints == 0)
            return calculate(startTs, endTs, 1, aggregateSettings);
        return calculate(startTs, endTs, numberOfPoints, aggregateSettings);
    }

    public static boolean isEnabled() {
        try {
            return ScadaConfig.getInstance().getBoolean(API_AMCHARTS_AGGREGATION_ENABLED_KEY, false);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static int getValuesLimit() {
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

    public static int limitByDataType(DataPointVO dataPoint, int limit) {
        int dataType = dataType(dataPoint);
        return dataType == DataTypes.NUMERIC || dataType == DataTypes.BINARY ? limit : 0;
    }

    public static int calculateLimit(AggregateSettings aggregateSettings) {
        return aggregateSettings.getLimitFactor() > 1.0 ? (int)Math.ceil(aggregateSettings.getValuesLimit() * aggregateSettings.getLimitFactor()) + 1 : aggregateSettings.getValuesLimit() + 1;
    }

    public static long calculateStartTs(long startTs, long intervalMs) {
        return  startTs > intervalMs ? startTs - intervalMs : startTs;
    }

    private static long calculate(long startTs, long endTs, int numberOfPoints, AggregateSettings aggregateSettings) {
        long result = Math.round((endTs - startTs)/(aggregateSettings.getValuesLimit() * aggregateSettings.getLimitFactor()/numberOfPoints));
        return result == 0 ? 1 : result;
    }

    private static int dataType(DataPointVO dataPoint) {
        if(dataPoint == null) {
            LOG.warn("dataPoint is null!");
            return -1;
        }
        if(dataPoint.getPointLocator() == null) {
            LOG.warn(dataPointInfo(dataPoint));
            return -1;
        }
        return dataPoint.getPointLocator().getDataTypeId();
    }
}
