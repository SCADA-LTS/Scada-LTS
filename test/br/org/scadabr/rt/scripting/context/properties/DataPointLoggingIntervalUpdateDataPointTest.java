package br.org.scadabr.rt.scripting.context.properties;


import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.IntervalLoggingPeriodType;
import com.serotonin.mango.vo.IntervalLoggingType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;
import static utils.DataPointLoggingTestUtils.msg;
import static utils.DataPointLoggingTestUtils.msgLoggingType;

@RunWith(Parameterized.class)
public class DataPointLoggingIntervalUpdateDataPointTest {

    @Parameterized.Parameters(name= "{index}: intervalPeriodType: {0}, intervalPeriod: {1}, IntervalLoggingType: {2}")
    public static Object[] data() {
        return new Object[][] {
                {IntervalLoggingPeriodType.SECONDS, 2, IntervalLoggingType.AVERAGE},
                {IntervalLoggingPeriodType.SECONDS, 2, IntervalLoggingType.MINIMUM},
                {IntervalLoggingPeriodType.SECONDS, 2, IntervalLoggingType.MAXIMUM},
                {IntervalLoggingPeriodType.SECONDS, 2, IntervalLoggingType.INSTANT},
                {IntervalLoggingPeriodType.MINUTES, 2, IntervalLoggingType.AVERAGE},
                {IntervalLoggingPeriodType.MINUTES, 2, IntervalLoggingType.MINIMUM},
                {IntervalLoggingPeriodType.MINUTES, 2, IntervalLoggingType.MAXIMUM},
                {IntervalLoggingPeriodType.MINUTES, 2, IntervalLoggingType.INSTANT},
                {IntervalLoggingPeriodType.HOURS, 2, IntervalLoggingType.AVERAGE},
                {IntervalLoggingPeriodType.HOURS, 2, IntervalLoggingType.MINIMUM},
                {IntervalLoggingPeriodType.HOURS, 2, IntervalLoggingType.MAXIMUM},
                {IntervalLoggingPeriodType.HOURS, 2, IntervalLoggingType.INSTANT},
                {IntervalLoggingPeriodType.DAYS, 2, IntervalLoggingType.AVERAGE},
                {IntervalLoggingPeriodType.DAYS, 2, IntervalLoggingType.MINIMUM},
                {IntervalLoggingPeriodType.DAYS, 2, IntervalLoggingType.MAXIMUM},
                {IntervalLoggingPeriodType.DAYS, 2, IntervalLoggingType.INSTANT},
                {IntervalLoggingPeriodType.WEEKS, 2, IntervalLoggingType.AVERAGE},
                {IntervalLoggingPeriodType.WEEKS, 2, IntervalLoggingType.MINIMUM},
                {IntervalLoggingPeriodType.WEEKS, 2, IntervalLoggingType.MAXIMUM},
                {IntervalLoggingPeriodType.WEEKS, 2, IntervalLoggingType.INSTANT},
                {IntervalLoggingPeriodType.MONTHS, 2, IntervalLoggingType.AVERAGE},
                {IntervalLoggingPeriodType.MONTHS, 2, IntervalLoggingType.MINIMUM},
                {IntervalLoggingPeriodType.MONTHS, 2, IntervalLoggingType.MAXIMUM},
                {IntervalLoggingPeriodType.MONTHS, 2, IntervalLoggingType.INSTANT},
                {IntervalLoggingPeriodType.YEARS, 2, IntervalLoggingType.AVERAGE},
                {IntervalLoggingPeriodType.YEARS, 2, IntervalLoggingType.MINIMUM},
                {IntervalLoggingPeriodType.YEARS, 2, IntervalLoggingType.MAXIMUM},
                {IntervalLoggingPeriodType.YEARS, 2, IntervalLoggingType.INSTANT},
                {IntervalLoggingPeriodType.YEARS, 33, IntervalLoggingType.AVERAGE},
                {IntervalLoggingPeriodType.YEARS, 33, IntervalLoggingType.MINIMUM},
                {IntervalLoggingPeriodType.YEARS, 33, IntervalLoggingType.MAXIMUM},
                {IntervalLoggingPeriodType.YEARS, 33, IntervalLoggingType.INSTANT},
        };
    }

    private IntervalLoggingPeriodType intervalLoggingPeriodType;
    private int intervalLoggingPeriod;
    private IntervalLoggingType intervalLoggingType;

    public DataPointLoggingIntervalUpdateDataPointTest(IntervalLoggingPeriodType intervalLoggingPeriodType,
                                                       int intervalLoggingPeriod, IntervalLoggingType intervalLoggingType) {
        this.intervalLoggingPeriodType = intervalLoggingPeriodType;
        this.intervalLoggingPeriod = intervalLoggingPeriod;
        this.intervalLoggingType = intervalLoggingType;
    }

    @Test
    public void when_updateDataPoint_for_LoggingType_Interval() {
        //given:
        DataPointLoggingTypeProperties dataPointUpdate = DataPointLoggingTypeProperties.interval(intervalLoggingPeriodType, intervalLoggingPeriod, intervalLoggingType);
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.INTERVAL);
        Predicate<DataPointVO> predicate = dataPoint ->
                dataPoint.getLoggingType() == dataPointUpdate.getLoggingType().getCode()
                        && dataPoint.getIntervalLoggingPeriod() == dataPointUpdate.getIntervalPeriod()
                        && dataPoint.getIntervalLoggingType() == dataPointUpdate.getIntervalLoggingType().getCode()
                        && dataPoint.getIntervalLoggingPeriodType() == dataPointUpdate.getIntervalPeriodType().getCode();

        //when:
        dataPointUpdate.updateDataPoint(dataPointVO);

        //then:
        assertTrue(msg(dataPointUpdate, msgLoggingType(dataPointVO)), predicate.test(dataPointVO));
    }
}
