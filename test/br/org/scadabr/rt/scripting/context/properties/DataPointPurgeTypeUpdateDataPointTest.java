package br.org.scadabr.rt.scripting.context.properties;


import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.PurgeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;
import static utils.DataPointLoggingTestUtils.*;

@RunWith(Parameterized.class)
public class DataPointPurgeTypeUpdateDataPointTest {

    @Parameterized.Parameters(name= "{index}: purgeType: {0}, purgePeriod: {1}")
    public static Object[] data() {
        return new Object[][] {
                {PurgeType.YEARS, 2},
                {PurgeType.MONTHS, 2},
                {PurgeType.WEEKS, 2},
                {PurgeType.DAYS, 2},
                {PurgeType.YEARS, 33},
                {PurgeType.MONTHS, 33},
                {PurgeType.WEEKS, 33},
                {PurgeType.DAYS, 33}
        };
    }

    private PurgeType purgeType;
    private int purgePeriod;

    public DataPointPurgeTypeUpdateDataPointTest(PurgeType purgeType, int purgePeriod) {
        this.purgeType = purgeType;
        this.purgePeriod = purgePeriod;
    }

    @Test
    public void when_updateDataPoint_for_LoggingType_Interval() {
        //given:
        DataPointPurgeTypeProperties dataPointUpdate = new DataPointPurgeTypeProperties(purgeType, purgePeriod);
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.INTERVAL);
        Predicate<DataPointVO> predicate = dataPoint ->
                dataPoint.getPurgeType() == dataPointUpdate.getPurgeType().getCode()
                        && dataPoint.getPurgePeriod() == dataPointUpdate.getPurgePeriod();

        //when:
        dataPointUpdate.updateDataPoint(dataPointVO);

        //then:
        assertTrue(msg(dataPointUpdate, msgPurgeType(dataPointVO)), predicate.test(dataPointVO));
    }
}
