package com.serotonin.mango.vo.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static br.org.scadabr.db.utils.TestUtils.*;

@RunWith(Parameterized.class)
public class CalculateLinesImageChartUtilsExceptionTest {

    @Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeight(points: {0}, lineLengthInLegendLimit: {1}, dataPointExtendedNameLengthLimit: {2})")
    public static Object[][] data() {

        return new Object[][] {
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), -1, -1},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 0, 0},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 0, -1},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), -1, 0},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), -1, 1},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 0, 1},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 1, -1},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 1, 0},
        };
    }

    private final List<String> pointStatisticsNames;
    private final int lineLengthInLegendLimit;
    private final int dataPointExtendedNameLengthLimit;

    public CalculateLinesImageChartUtilsExceptionTest(List<String> pointStatisticsNames, int lineLengthInLegendLimit, int dataPointExtendedNameLengthLimit) {
        this.pointStatisticsNames = pointStatisticsNames;
        this.lineLengthInLegendLimit = lineLengthInLegendLimit;
        this.dataPointExtendedNameLengthLimit = dataPointExtendedNameLengthLimit;
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_calculateConsolidatedChartHeight() {

        //when:
        ImageChartUtils.calculateLinesNumber(pointStatisticsNames, lineLengthInLegendLimit, dataPointExtendedNameLengthLimit);
    }

}