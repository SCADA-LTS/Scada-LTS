package br.org.scadabr.rt.scripting.context.properties;


import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.IntervalLoggingPeriodType;
import com.serotonin.mango.vo.IntervalLoggingType;
import com.serotonin.mango.vo.LoggingType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Objects;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;
import static utils.DataPointLoggingTestUtils.msg;
import static utils.DataPointLoggingTestUtils.msgLoggingType;

@RunWith(Parameterized.class)
public class DataPointLoggingTypeUpdateDataPointTest {

    @Parameterized.Parameters(name= "{index}: properties: {0}")
    public static Object[] data() {
        return new Object[][] {
                {
                        DataPointLoggingTypeProperties.all(),
                        (Predicate<DataPointVO>) dataPointVO -> dataPointVO.getLoggingType() == LoggingType.ALL.getCode()},
                {
                        DataPointLoggingTypeProperties.none(),
                        (Predicate<DataPointVO>) dataPointVO -> dataPointVO.getLoggingType() == LoggingType.NONE.getCode()},
                {
                        DataPointLoggingTypeProperties.onTsChange(),
                        (Predicate<DataPointVO>) dataPointVO -> dataPointVO.getLoggingType() == LoggingType.ON_TS_CHANGE.getCode()},
                {
                        DataPointLoggingTypeProperties.onChange(0.45),
                        (Predicate<DataPointVO>) dataPointVO ->
                                dataPointVO.getLoggingType() == LoggingType.ON_CHANGE.getCode()
                                        && Objects.equals(dataPointVO.getTolerance(), 0.45)},
                {
                        DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS, 2, IntervalLoggingType.AVERAGE),
                        (Predicate<DataPointVO>) dataPointVO ->
                                dataPointVO.getLoggingType() == LoggingType.INTERVAL.getCode()
                                        && dataPointVO.getIntervalLoggingPeriod() == 2
                                        && dataPointVO.getIntervalLoggingType() == IntervalLoggingType.AVERAGE.getCode()
                                        && dataPointVO.getIntervalLoggingPeriodType() == IntervalLoggingPeriodType.DAYS.getCode()
                },
                {
                        DataPointLoggingTypeProperties.onChange(0.111),
                        (Predicate<DataPointVO>) dataPointVO ->
                                dataPointVO.getLoggingType() == LoggingType.ON_CHANGE.getCode()
                                        && Objects.equals(dataPointVO.getTolerance(), 0.111)},
                {
                        DataPointLoggingTypeProperties.interval(IntervalLoggingPeriodType.DAYS, 77, IntervalLoggingType.AVERAGE),
                        (Predicate<DataPointVO>) dataPointVO ->
                                dataPointVO.getLoggingType() == LoggingType.INTERVAL.getCode()
                                        && dataPointVO.getIntervalLoggingPeriod() == 77
                                        && dataPointVO.getIntervalLoggingType() == IntervalLoggingType.AVERAGE.getCode()
                                        && dataPointVO.getIntervalLoggingPeriodType() == IntervalLoggingPeriodType.DAYS.getCode()
                }
        };
    }

    private DataPointUpdate dataPointUpdate;
    private Predicate<DataPointVO> predicate;
    private DataPointVO dataPoint;

    public DataPointLoggingTypeUpdateDataPointTest(DataPointUpdate dataPointUpdate, Predicate<DataPointVO> predicate) {
        this.dataPointUpdate = dataPointUpdate;
        this.predicate = predicate;
        this.dataPoint = new DataPointVO(DataPointVO.LoggingTypes.INTERVAL);
    }

    @Test
    public void when_updateDataPoint() {
        //when:
        dataPointUpdate.updateDataPoint(dataPoint);

        //then:
        assertTrue(msg(dataPointUpdate, msgLoggingType(dataPoint)), predicate.test(dataPoint));
    }
}
