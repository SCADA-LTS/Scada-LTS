package utils;

import br.org.scadabr.rt.scripting.context.properties.DataPointUpdate;
import com.serotonin.mango.vo.*;

public class DataPointLoggingTestUtils {

    public static String msg(DataPointUpdate dataPointUpdate, String dataPointInfo) {
        return "expected: " + dataPointUpdate + " but was: " + dataPointInfo;
    }

    public static String msgLoggingType(DataPointVO dataPoint) {
        return "loggingType: " + LoggingType.getType(dataPoint.getLoggingType()) + ", " +
                "intervalLoggingType: " + IntervalLoggingType.getType(dataPoint.getIntervalLoggingType()) + ", " +
                "intervalPeriodType: " + IntervalLoggingPeriodType.getType(dataPoint.getIntervalLoggingPeriodType()) + ", " +
                "intervalPeriod: " + dataPoint.getIntervalLoggingPeriod() + ", " +
                "tolerance: " + dataPoint.getTolerance();
    }

    public static String msgPurgeType(DataPointVO dataPoint) {
        return "purgeType: " + PurgeType.getType(dataPoint.getPurgeType()) + ", " +
                "purgePeriod: " + dataPoint.getPurgePeriod();
    }

    public static String msgDiscardValues(DataPointVO dataPoint) {
        return "discardExtremeValues: " + dataPoint.isDiscardExtremeValues() + ", " +
                "discardHighLimit: " + dataPoint.getDiscardHighLimit() + ", " +
                "discardLowLimit: " + dataPoint.getDiscardLowLimit();
    }
}
