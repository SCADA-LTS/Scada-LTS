package br.org.scadabr.rt.scripting.context.properties;


import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.PurgeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Objects;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;
import static utils.DataPointLoggingTestUtils.*;

@RunWith(Parameterized.class)
public class DataPointDiscardValuesUpdateDataPointTest {

    @Parameterized.Parameters(name= "{index}: discardExtremeValues: {0}, discardHighLimit: {1}, discardLowLimit: {2}")
    public static Object[] data() {
        return new Object[][] {
                {true, 12, -12},
                {true, 0.5, -0.5},
                {true, 1, -2},
                {true, 12, 0},
                {true, 123, 23},
                {true, -23, -123},
                {false, 12, -12},
                {false, 0.5, -0.5},
                {false, 1, -2},
                {false, 12, 0},
                {false, 123, 23},
                {false, -23, -123},

        };
    }

    private final boolean discardExtremeValues;
    private final double discardHighLimit;
    private final double discardLowLimit;

    public DataPointDiscardValuesUpdateDataPointTest(boolean discardExtremeValues, double discardHighLimit, double discardLowLimit) {
        this.discardExtremeValues = discardExtremeValues;
        this.discardHighLimit = discardHighLimit;
        this.discardLowLimit = discardLowLimit;
    }

    @Test
    public void when_updateDataPoint_for_DiscardValues() {
        //given:
        DataPointDiscardValuesProperties dataPointUpdate = new DataPointDiscardValuesProperties(discardExtremeValues, discardHighLimit, discardLowLimit);
        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.INTERVAL);
        Predicate<DataPointVO> predicate = dataPoint -> dataPoint.isDiscardExtremeValues() == dataPointUpdate.isDiscardExtremeValues()
                        && Objects.equals(dataPoint.getDiscardHighLimit(), dataPointUpdate.getDiscardHighLimit())
                        && Objects.equals(dataPoint.getDiscardLowLimit(), dataPointUpdate.getDiscardLowLimit());

        //when:
        dataPointUpdate.updateDataPoint(dataPointVO);

        //then:
        assertTrue(msg(dataPointUpdate, msgDiscardValues(dataPointVO)), predicate.test(dataPointVO));
    }
}
