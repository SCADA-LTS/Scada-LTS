package com.serotonin.mango.vo.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.utils.SystemSettingsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.org.scadabr.db.utils.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class CalculateLinesImageChartUtilsAppCaseTest {

    @Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeight(points: {0}, lineLengthInLegendLimit: {1}, dataPointExtendedNameLengthLimit: {2}, expectedLinesNumber: {3})")
    public static Object[][] data() {

        List<String> withNulls = new ArrayList<>();
        withNulls.add(null);
        withNulls.add(null);

        int lineLengthInLegendLimit = ReportChartCreator.getLineLengthInLegendLimit();
        int dataPointExtendedNameLengthLimit = SystemSettingsUtils.getDataPointExtendedNameLengthInReportsLimit();

        return new Object[][] {

                {List.of(STRING_LENGTH_135, STRING_LENGTH_138, STRING_LENGTH_16),
                        lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 1},

                {List.of(STRING_LENGTH_135, STRING_LENGTH_138, STRING_LENGTH_16, STRING_LENGTH_8),
                        lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 2},

                {List.of(STRING_LENGTH_100, STRING_LENGTH_138, STRING_LENGTH_8, STRING_LENGTH_9, STRING_LENGTH_17),
                        lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 2},

                {List.of(STRING_LENGTH_100, STRING_LENGTH_10, STRING_LENGTH_11, STRING_LENGTH_12, STRING_LENGTH_135,
                        STRING_LENGTH_138, STRING_LENGTH_13, STRING_LENGTH_140, STRING_LENGTH_14, STRING_LENGTH_15
                ), lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 3},

                {List.of(STRING_LENGTH_12, STRING_LENGTH_135, STRING_LENGTH_13, STRING_LENGTH_140, STRING_LENGTH_14,
                        STRING_LENGTH_15, STRING_LENGTH_16, STRING_LENGTH_17, STRING_LENGTH_100, STRING_LENGTH_10
                ), lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 3},

                {List.of(STRING_LENGTH_100, STRING_LENGTH_10, STRING_LENGTH_138, STRING_LENGTH_20, STRING_LENGTH_25,
                        STRING_LENGTH_30, STRING_LENGTH_40, STRING_LENGTH_50, STRING_LENGTH_60, STRING_LENGTH_70
                ), lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 4},

                {List.of(STRING_LENGTH_100, STRING_LENGTH_10, STRING_LENGTH_138, STRING_LENGTH_20, STRING_LENGTH_25,
                        STRING_LENGTH_30, STRING_LENGTH_40, STRING_LENGTH_50, STRING_LENGTH_60, STRING_LENGTH_70,
                        STRING_LENGTH_8, STRING_LENGTH_80, STRING_LENGTH_9, STRING_LENGTH_11,
                        STRING_LENGTH_12, STRING_LENGTH_135, STRING_LENGTH_13, STRING_LENGTH_140, STRING_LENGTH_14,
                        STRING_LENGTH_15, STRING_LENGTH_16, STRING_LENGTH_17
                ), lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 6},

                {List.of(STRING_LENGTH_100, STRING_LENGTH_10, STRING_LENGTH_11, STRING_LENGTH_12, STRING_LENGTH_135,
                        STRING_LENGTH_138, STRING_LENGTH_13, STRING_LENGTH_140, STRING_LENGTH_14, STRING_LENGTH_15,
                        STRING_LENGTH_16, STRING_LENGTH_17, STRING_LENGTH_20, STRING_LENGTH_25, STRING_LENGTH_30,
                        STRING_LENGTH_40, STRING_LENGTH_50, STRING_LENGTH_60, STRING_LENGTH_70, STRING_LENGTH_8,
                        STRING_LENGTH_80, STRING_LENGTH_9
                ), lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 6},

                {List.of(STRING_LENGTH_12, STRING_LENGTH_135, STRING_LENGTH_13, STRING_LENGTH_140, STRING_LENGTH_14,
                        STRING_LENGTH_15, STRING_LENGTH_16, STRING_LENGTH_17, STRING_LENGTH_100, STRING_LENGTH_10,
                        STRING_LENGTH_138, STRING_LENGTH_20, STRING_LENGTH_25, STRING_LENGTH_30, STRING_LENGTH_40,
                        STRING_LENGTH_50, STRING_LENGTH_60, STRING_LENGTH_70, STRING_LENGTH_8, STRING_LENGTH_80,
                        STRING_LENGTH_9, STRING_LENGTH_11
                ), lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 7},

                {List.of(STRING_LENGTH_100, STRING_LENGTH_10, STRING_LENGTH_11, STRING_LENGTH_12, STRING_LENGTH_135,
                        STRING_LENGTH_138, STRING_LENGTH_13, STRING_LENGTH_140, STRING_LENGTH_14, STRING_LENGTH_15,
                        STRING_LENGTH_16, STRING_LENGTH_17, STRING_LENGTH_20, STRING_LENGTH_25, STRING_LENGTH_30,
                        STRING_LENGTH_40, STRING_LENGTH_50, STRING_LENGTH_60, STRING_LENGTH_70,
                        STRING_LENGTH_80, STRING_LENGTH_8, STRING_LENGTH_9, STRING_LENGTH_90
                ), lineLengthInLegendLimit, dataPointExtendedNameLengthLimit, 7},

        };
    }

    private final List<String> pointStatisticsNames;
    private final int expectedLinesNumber;
    private final int lineLengthInLegendLimit;
    private final int dataPointExtendedNameLengthLimit;

    public CalculateLinesImageChartUtilsAppCaseTest(List<String> pointStatisticsNames, int lineLengthInLegendLimit, int dataPointExtendedNameLengthLimit, int expectedLinesNumber) {
        SystemSettingsService systemSettingsServiceMock = mock(SystemSettingsService.class);
        when(systemSettingsServiceMock.getDataPointExtendedNameLengthInReportsLimit()).thenReturn(64);
        this.pointStatisticsNames = pointStatisticsNames.stream().map(a -> {
            if(a == null)
                return null;
            return ImageChartUtils.truncatePointNameForReport(a, systemSettingsServiceMock);
        }).collect(Collectors.toList());
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