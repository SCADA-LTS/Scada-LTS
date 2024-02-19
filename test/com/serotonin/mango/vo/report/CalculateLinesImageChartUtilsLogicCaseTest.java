package com.serotonin.mango.vo.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static br.org.scadabr.db.utils.TestUtils.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CalculateLinesImageChartUtilsLogicCaseTest {

    @Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeight(points: {0}, lineLengthInLegendLimit: {1}, dataPointExtendedNameLengthLimit: {2}, expectedLinesNumber: {3})")
    public static Object[][] data() {

        List<String> withNulls = new ArrayList<>();
        withNulls.add(null);
        withNulls.add(null);
        return new Object[][] {

                {List.of(), 1, 1, 1},
                {List.of(STRING_LENGTH_0, STRING_LENGTH_0), 1, 1, 1},
                {withNulls, 1, 1, 1},


                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 27, 1, 1},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 27, 2, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 27, 3, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 27, 4, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 27, 5, 2},

                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 27, 1, 1},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 27, 2, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 27, 3, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 27, 4, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 27, 5, 2},


                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 18, 1, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 18, 2, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 18, 3, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 18, 4, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 18, 5, 2},

                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 18, 1, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 18, 2, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 18, 3, 2},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 18, 4, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 18, 5, 3},


                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 14, 1, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 14, 2, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 14, 3, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 14, 4, 4},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 14, 5, 4},

                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 14, 1, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 14, 2, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 14, 3, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 14, 4, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 14, 5, 3},


                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 12, 1, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 12, 2, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 12, 3, 4},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 12, 4, 4},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 12, 5, 4},

                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 12, 1, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 12, 2, 3},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 12, 3, 4},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 12, 4, 4},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 12, 5, 4},


                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 9, 1, 5},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 9, 2, 5},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 9, 3, 5},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 9, 4, 5},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_4, STRING_LENGTH_3, STRING_LENGTH_2, STRING_LENGTH_1), 9, 5, 5},

                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 9, 1, 5},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 9, 2, 5},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 9, 3, 5},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 9, 4, 5},
                {List.of(STRING_LENGTH_4, STRING_LENGTH_2, STRING_LENGTH_1, STRING_LENGTH_3, STRING_LENGTH_4), 9, 5, 5},

        };
    }

    private final List<String> pointStatisticsNames;
    private final int expectedLinesNumber;
    private final int lineLengthInLegendLimit;
    private final int dataPointExtendedNameLengthLimit;

    public CalculateLinesImageChartUtilsLogicCaseTest(List<String> pointStatisticsNames, int lineLengthInLegendLimit, int dataPointExtendedNameLengthLimit, int expectedLinesNumber) {
        this.pointStatisticsNames = pointStatisticsNames;
        this.expectedLinesNumber = expectedLinesNumber;
        this.lineLengthInLegendLimit = lineLengthInLegendLimit;
        this.dataPointExtendedNameLengthLimit = dataPointExtendedNameLengthLimit;
    }

    @Test
    public void when_calculateConsolidatedChartHeight() {

        //when:
        int resultLinesNumber = ImageChartUtils.calculateLinesNumber(pointStatisticsNames, lineLengthInLegendLimit, dataPointExtendedNameLengthLimit);

        //then:
        assertEquals(expectedLinesNumber, resultLinesNumber);
    }

}