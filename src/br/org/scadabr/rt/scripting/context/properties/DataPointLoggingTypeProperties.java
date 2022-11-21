package br.org.scadabr.rt.scripting.context.properties;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.IntervalLoggingPeriodType;
import com.serotonin.mango.vo.IntervalLoggingType;
import com.serotonin.mango.vo.LoggingType;
import org.mozilla.javascript.NativeObject;

import java.util.Objects;

public class DataPointLoggingTypeProperties implements DataPointUpdate {

    private final LoggingType loggingType;
    private final IntervalLoggingType intervalLoggingType;
    private final IntervalLoggingPeriodType intervalPeriodType;
    private final int intervalPeriod;
    private final double tolerance;

    public DataPointLoggingTypeProperties(LoggingType loggingType, IntervalLoggingPeriodType intervalPeriodType,
                                          IntervalLoggingType intervalLoggingType, int intervalPeriod, double tolerance) {
        this.loggingType = loggingType;
        this.intervalPeriodType = intervalPeriodType;
        this.intervalLoggingType = intervalLoggingType;
        this.intervalPeriod = intervalPeriod;
        this.tolerance = tolerance;
    }

    public static DataPointLoggingTypeProperties defaultProperties() {
        return new DataPointLoggingTypeProperties(LoggingType.ON_CHANGE, IntervalLoggingPeriodType.MINUTES,
                IntervalLoggingType.INSTANT, 15, 0.0);
    }

    public static DataPointLoggingTypeProperties byNativeObject(NativeObject nativeObject) {
        DataPointLoggingTypeProperties defaultProperties = DataPointLoggingTypeProperties.defaultProperties();

        String loggingType = (String)nativeObject.get("loggingType");
        String intervalLoggingPeriodType = (String)nativeObject.get("intervalPeriodType");
        String intervalLoggingType = (String)nativeObject.get("intervalLoggingType");
        Double intervalLoggingPeriod = (Double)nativeObject.get("intervalPeriod");
        Double tolerance = (Double)nativeObject.get("tolerance");

        if(tolerance != null && tolerance < 0) {
            throw new IllegalArgumentException("tolerance must be >= 0");
        }

        if(intervalLoggingPeriod != null && intervalLoggingPeriod <= 0) {
            throw new IllegalArgumentException("intervalLoggingPeriod must be > 0");
        }

        return new DataPointLoggingTypeProperties(loggingType == null ? defaultProperties.getLoggingType() : LoggingType.valueOf(loggingType.toUpperCase()),
                intervalLoggingPeriodType == null ? defaultProperties.getIntervalPeriodType() : IntervalLoggingPeriodType.valueOf(intervalLoggingPeriodType.toUpperCase()),
                intervalLoggingType == null ? defaultProperties.getIntervalLoggingType() : IntervalLoggingType.valueOf(intervalLoggingType.toUpperCase()),
                intervalLoggingPeriod == null ? defaultProperties.getIntervalPeriod() : intervalLoggingPeriod.intValue(),
                tolerance == null ? defaultProperties.getTolerance() : tolerance);
    }

    public static DataPointLoggingTypeProperties.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LoggingType loggingType;
        private IntervalLoggingPeriodType intervalLoggingPeriodType;
        private IntervalLoggingType intervalLoggingType;
        private int intervalLoggingPeriod;
        private double tolerance;

        public Builder loggingType(LoggingType loggingType) {
            this.loggingType = loggingType;
            return this;
        }

        public Builder intervalLoggingPeriodType(IntervalLoggingPeriodType intervalLoggingPeriodType) {
            this.intervalLoggingPeriodType = intervalLoggingPeriodType;
            return this;
        }

        public Builder intervalLoggingType(IntervalLoggingType intervalLoggingType) {
            this.intervalLoggingType = intervalLoggingType;
            return this;
        }

        public Builder intervalLoggingPeriod(int intervalLoggingPeriod) {
            this.intervalLoggingPeriod = intervalLoggingPeriod;
            return this;
        }

        public Builder tolerance(double tolerance) {
            this.tolerance = tolerance;
            return this;
        }

        public DataPointLoggingTypeProperties build() {
            DataPointLoggingTypeProperties defaultProperties = DataPointLoggingTypeProperties.defaultProperties();
            return new DataPointLoggingTypeProperties( loggingType == null ? defaultProperties.getLoggingType() : loggingType,
                    intervalLoggingPeriodType == null ? defaultProperties.getIntervalPeriodType() : intervalLoggingPeriodType,
                    intervalLoggingType == null ? defaultProperties.getIntervalLoggingType() : intervalLoggingType,
                    intervalLoggingPeriod == 0 ? defaultProperties.getIntervalPeriod() : intervalLoggingPeriod,
                    tolerance < 0.0 ? defaultProperties.getTolerance() : tolerance);
        }
    }

    public static DataPointLoggingTypeProperties onChange(double tolerance) {
        return DataPointLoggingTypeProperties.builder()
                .loggingType(LoggingType.ON_CHANGE)
                .tolerance(tolerance)
                .build();
    }

    public static DataPointLoggingTypeProperties all() {
        return DataPointLoggingTypeProperties.builder()
                .loggingType(LoggingType.ALL)
                .build();
    }


    public static DataPointLoggingTypeProperties onTsChange() {
        return DataPointLoggingTypeProperties.builder()
                .loggingType(LoggingType.ON_TS_CHANGE)
                .build();
    }

    public static DataPointLoggingTypeProperties none() {
        return DataPointLoggingTypeProperties.builder()
                .loggingType(LoggingType.NONE)
                .build();
    }

    public static DataPointLoggingTypeProperties interval(IntervalLoggingPeriodType intervalLoggingPeriodType,
                                                          int intervalLoggingPeriod, IntervalLoggingType intervalLoggingType) {
        return DataPointLoggingTypeProperties.builder()
                .loggingType(LoggingType.INTERVAL)
                .intervalLoggingPeriod(intervalLoggingPeriod)
                .intervalLoggingPeriodType(intervalLoggingPeriodType)
                .intervalLoggingType(intervalLoggingType)
                .build();
    }

    @Override
    public void updateDataPoint(DataPointVO dataPoint) {
        dataPoint.setLoggingType(loggingType.getCode());
        dataPoint.setIntervalLoggingPeriod(intervalPeriod);
        dataPoint.setIntervalLoggingPeriodType(intervalPeriodType.getCode());
        dataPoint.setIntervalLoggingType(intervalLoggingType.getCode());
        dataPoint.setTolerance(tolerance);
    }

    public LoggingType getLoggingType() {
        return loggingType;
    }

    public int getIntervalPeriod() {
        return intervalPeriod;
    }

    public double getTolerance() {
        return tolerance;
    }

    public IntervalLoggingPeriodType getIntervalPeriodType() {
        return intervalPeriodType;
    }

    public IntervalLoggingType getIntervalLoggingType() {
        return intervalLoggingType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPointLoggingTypeProperties)) return false;
        DataPointLoggingTypeProperties that = (DataPointLoggingTypeProperties) o;
        return getIntervalPeriod() == that.getIntervalPeriod() && Double.compare(that.getTolerance(), getTolerance()) == 0 && getLoggingType() == that.getLoggingType() && getIntervalLoggingType() == that.getIntervalLoggingType() && getIntervalPeriodType() == that.getIntervalPeriodType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLoggingType(), getIntervalLoggingType(), getIntervalPeriodType(), getIntervalPeriod(), getTolerance());
    }

    @Override
    public String toString() {
        return "DataPointLoggingTypeProperties{" +
                "loggingType=" + loggingType +
                ", intervalLoggingType=" + intervalLoggingType +
                ", intervalPeriodType=" + intervalPeriodType +
                ", intervalPeriod=" + intervalPeriod +
                ", tolerance=" + tolerance +
                '}';
    }
}