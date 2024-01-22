package com.serotonin.mango.vo.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CalculateConsolidatedChartHeightTest {

    private final List<ReportChartCreator.PointStatistics> pointStatistics;
    private final int expectedHeight;
    private final int imageHeightForDataPointNameInLegend;
    private final int imageHeight;
    private final int charAmountPerLine;
    public CalculateConsolidatedChartHeightTest(List<ReportChartCreator.PointStatistics> pointStatistics, int expectedHeight, int imageHeightForDataPointNameInLegend, int imageHeight, int charAmountPerLine) {
        this.pointStatistics = pointStatistics;
        this.expectedHeight = expectedHeight;
        this.imageHeightForDataPointNameInLegend = imageHeightForDataPointNameInLegend;
        this.imageHeight = imageHeight;
        this.charAmountPerLine = charAmountPerLine;
    }

    @Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeight(points: {0}, expected height: {1},pixels per line: {2} base height of chart: {3}, characters per line: {4})")
    public static Collection<Object[]> data() {
        String chars0PointName = "";
        String chars1PointName = "1";
        String chars2PointName = "2c";
        String chars3PointName = "3ch";
        String chars4PointName = "4cha";
        String chars5PointName = "5char";
        String chars6PointName = "6chara";
        String chars7PointName = "7charac";
        String chars8PointName = "8charact";
        String chars9PointName = "9characte";
        String chars10PointName = "ds_1 - 10c";
        String chars15PointName = "ds_1 - 15charac";
        String chars20PointName = "ds_1 - 20characters2";
        String chars25PointName = "ds_1 - 25characters25char";
        String chars30PointName = "ds_1 - 30characters30character";
        String chars50PointName = "ds_1 - 50characters50characters50characters50chara";
        String chars60PointName = "ds_1 - 60characters60characters60characters60characters60cha";
        String chars70PointName = "ds_1 - 70characters70characters70characters70characters70characters70c";
        String chars80PointName = "ds_1 - 80characters80characters80characters80characters80characters80characters8";
        String chars90PointName = "ds_1 - 90characters90characters90characters90characters90characters90characters90character";
        String chars100PointName = "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10";
        String chars135PointName = "135characters - 135characters135characters135characters135characters135characters135characters135characters135characters135characters13";
        String chars138PointName = "138characters - 138characters138characters138characters138characters138characters138characters138characters138characters138characters138ch";

        return Arrays.asList(new Object[][]{
            {List.of(), 400, 20, 400, 138},
            {createPointStatisticsList(Arrays.asList(
                    chars0PointName, chars0PointName, chars0PointName, chars0PointName, chars0PointName, chars0PointName, chars0PointName, chars0PointName, chars0PointName, chars0PointName
            )), 420, 20, 400, 15},
            {createPointStatisticsList(List.of(chars135PointName)), 400, 20, 400, 138},
            {createPointStatisticsList(List.of(chars138PointName)), 400, 20, 400, 138},
            {createPointStatisticsList(Arrays.asList(chars138PointName, chars138PointName, chars138PointName)), 440, 20, 400, 138},
            {createPointStatisticsList(Arrays.asList(chars135PointName, chars135PointName, chars135PointName)), 440, 20, 400, 138},
            {createPointStatisticsList(Arrays.asList(
                    chars4PointName, chars2PointName, chars1PointName, chars3PointName, chars4PointName
            )), 420, 20, 400, 16},
            {createPointStatisticsList(Arrays.asList(
                    chars4PointName, chars2PointName, chars3PointName, chars1PointName, chars4PointName
            )), 440, 20, 400, 16},
            {createPointStatisticsList(Arrays.asList(
                    chars5PointName, chars6PointName, chars7PointName, chars8PointName, chars9PointName
            )), 460, 20, 400, 18},
            {createPointStatisticsList(Arrays.asList(
                    chars5PointName, chars8PointName, chars6PointName, chars9PointName, chars7PointName
            )), 480, 20, 400, 18},
            {createPointStatisticsList(Arrays.asList(
                    chars10PointName, chars15PointName, chars20PointName, chars30PointName, chars25PointName
            )), 460, 20, 400, 35},
            {createPointStatisticsList(Arrays.asList(
                    chars10PointName, chars20PointName, chars15PointName, chars25PointName, chars30PointName
            )), 480, 20, 400, 35},


            {createPointStatisticsList(Arrays.asList(
                chars100PointName,
                chars50PointName,
                chars100PointName
                )), 440, 20, 400, 149},
            {createPointStatisticsList(Arrays.asList(
                chars50PointName,
                chars100PointName,
                chars80PointName,
                chars90PointName,
                chars60PointName,
                chars50PointName
                )), 480, 20, 400, 149},
            {createPointStatisticsList(Arrays.asList(
                chars100PointName,
                chars80PointName,
                chars10PointName,
                chars30PointName,
                chars50PointName,
                chars20PointName,
                chars10PointName,
                chars90PointName
                )), 460, 20, 400, 149},
            {createPointStatisticsList(Arrays.asList(
                chars100PointName,
                chars80PointName,
                chars10PointName,
                chars30PointName,
                chars50PointName,
                chars20PointName,
                chars10PointName,
                chars90PointName,
                chars70PointName,
                chars30PointName,
                chars20PointName,
                chars90PointName,
                chars100PointName
            )),520,20,400,138},
            {createPointStatisticsList(Arrays.asList(
                chars100PointName,
                chars80PointName,
                chars10PointName,
                chars30PointName,
                chars50PointName,
                chars20PointName,
                chars10PointName,
                chars90PointName,
                chars70PointName,
                chars30PointName,
                chars20PointName,
                chars90PointName,
                chars100PointName,
                chars30PointName,
                chars20PointName,
                chars100PointName,
                chars50PointName,
                chars10PointName,
                chars90PointName
            )),580,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                chars100PointName,
                chars80PointName,
                chars90PointName,
                chars70PointName,
                chars30PointName,
                chars90PointName,
                chars100PointName,
                chars100PointName,
                chars50PointName,
                chars90PointName,
                chars20PointName,
                chars80PointName,
                chars80PointName,
                chars10PointName
            )),580,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                chars70PointName,
                chars30PointName,
                chars50PointName,
                chars20PointName,
                chars10PointName,
                chars20PointName,
                chars30PointName,
                chars60PointName,
                chars70PointName,
                chars20PointName,
                chars10PointName,
                chars50PointName,
                chars30PointName,
                chars80PointName,
                chars30PointName
            )),480,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                chars70PointName,
                chars30PointName,
                chars50PointName,
                chars30PointName,
                chars60PointName,
                chars70PointName,
                chars10PointName,
                chars30PointName,
                chars30PointName,
                chars100PointName,
                chars20PointName,
                chars70PointName,
                chars90PointName,
                chars100PointName,
                chars20PointName,
                chars50PointName,
                chars20PointName,
                chars10PointName
            )),540,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                chars70PointName,
                chars30PointName,
                chars20PointName,
                chars70PointName,
                chars20PointName,
                chars10PointName,
                chars100PointName,
                chars90PointName,
                chars30PointName,
                chars90PointName,
                chars20PointName,
                chars10PointName,
                chars50PointName,
                chars20PointName,
                chars10PointName,
                chars30PointName,
                chars60PointName
            )),520,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                chars70PointName,
                chars30PointName,
                chars20PointName,
                chars70PointName,
                chars20PointName,
                chars10PointName,
                chars100PointName,
                chars90PointName,
                chars30PointName,
                chars90PointName,
                chars20PointName,
                chars10PointName,
                chars50PointName,
                chars20PointName,
                chars10PointName,
                chars30PointName,
                chars60PointName,
                chars100PointName,
                chars80PointName,
                chars90PointName,
                chars100PointName,
                chars80PointName,
                chars30PointName,
                chars80PointName,
                chars50PointName,
                chars70PointName,
                chars50PointName,
                chars60PointName,
                chars60PointName
            )),680,20,400,149},
        });
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

    @Test
    public void when_calculateConsolidatedChartHeight() {
        //when:
        int actualHeight = ImageChartUtils.calculateHeightConsolidatedChart(pointStatistics, imageHeight, imageHeightForDataPointNameInLegend, charAmountPerLine);
        //than:
        assertEquals(expectedHeight, actualHeight);
    }

}