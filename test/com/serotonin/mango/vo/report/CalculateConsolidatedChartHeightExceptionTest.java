package com.serotonin.mango.vo.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class CalculateConsolidatedChartHeightExceptionTest {

    @Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeightException(points: {0}, expected height: {1},pixels per line: {2} base height of chart: {3}, characters per line: {4})")
    public static Collection<Object[]> data() {
        String _4CharacterPointName = "4cha";
        String _8CharacterPointName = "8charact";
        String _25CharactersPointName = "25characters - 25characte";
        String _50CharactersPointName = "50characters - 50characters50characters50character";
        String _140CharactersPointName = "140characters - 140characters140characters140characters140characters140characters140characters140characters140characters140characters140char";
        return Arrays.asList(new Object[][]{
                {createPointStatisticsList(List.of(_4CharacterPointName)), 20, 400, 3},
                {createPointStatisticsList(List.of(_8CharacterPointName)), 20, 400, 7},
                {createPointStatisticsList(List.of(_25CharactersPointName)), 20, 400, 20},
                {createPointStatisticsList(List.of(_50CharactersPointName)), 20, 400, 40},
                {createPointStatisticsList(List.of(_140CharactersPointName)), 20, 400, 138},
        });
    }

    private final List<ReportChartCreator.PointStatistics> pointStatistics;
    private final int imageHeightForDataPointNameInLegend;
    private final int imageHeight;
    private final int charAmountPerLine;
    public CalculateConsolidatedChartHeightExceptionTest(List<ReportChartCreator.PointStatistics> pointStatistics, int imageHeightForDataPointNameInLegend, int imageHeight, int charAmountPerLine) {
        this.pointStatistics = pointStatistics;
        this.imageHeightForDataPointNameInLegend = imageHeightForDataPointNameInLegend;
        this.imageHeight = imageHeight;
        this.charAmountPerLine = charAmountPerLine;
    }

    private static List<ReportChartCreator.PointStatistics> createPointStatisticsList(List<String> pointName) {
        List<ReportChartCreator.PointStatistics> pointStatisticsList = new ArrayList<>();
        for (String pName : pointName) {
            ReportChartCreator.PointStatistics pointStatistics = new ReportChartCreator.PointStatistics(1, "");
            pointStatistics.setName(pName);
            pointStatisticsList.add(pointStatistics);
        }
        return pointStatisticsList;
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_calculateConsolidatedChartHeightExpectException() {
        ImageChartUtils.calculateHeightConsolidatedChart(pointStatistics, imageHeight, imageHeightForDataPointNameInLegend, charAmountPerLine);
    }

}