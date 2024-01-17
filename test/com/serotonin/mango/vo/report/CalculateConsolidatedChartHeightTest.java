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
        String _0CharactersPointName = "";
        String _1CharacterPointName = "1";
        String _2CharacterPointName = "2c";
        String _3CharacterPointName = "3ch";
        String _4CharacterPointName = "4cha";
        String _5CharacterPointName = "5char";
        String _6CharacterPointName = "6chara";
        String _7CharacterPointName = "7charac";
        String _8CharacterPointName = "8charact";
        String _9CharacterPointName = "9characte";
        String _10CharactersPointName = "10characte";
        String _15CharactersPointName = "15characters15c";
        String _20CharactersPointName = "20characters - 20cha";
        String _25CharactersPointName = "25characters - 25characte";
        String _30CharactersPointName = "30characters - 30characters30c";
        String _135CharactersPointName = "135characters - 135characters135characters135characters135characters135characters135characters135characters135characters135characters13";
        String _138CharactersPointName = "138characters - 138characters138characters138characters138characters138characters138characters138characters138characters138characters138ch";

        return Arrays.asList(new Object[][]{
            {List.of(), 400, 20, 400, 138},
            {createPointStatisticsList(Arrays.asList(
                    _0CharactersPointName, _0CharactersPointName, _0CharactersPointName, _0CharactersPointName, _0CharactersPointName, _0CharactersPointName, _0CharactersPointName, _0CharactersPointName, _0CharactersPointName, _0CharactersPointName
            )), 420, 20, 400, 15},
            {createPointStatisticsList(List.of(_135CharactersPointName)), 400, 20, 400, 138},
            {createPointStatisticsList(List.of(_138CharactersPointName)), 400, 20, 400, 138},
            {createPointStatisticsList(Arrays.asList(_138CharactersPointName, _138CharactersPointName, _138CharactersPointName)), 440, 20, 400, 138},
            {createPointStatisticsList(Arrays.asList(_135CharactersPointName, _135CharactersPointName, _135CharactersPointName)), 440, 20, 400, 138},
            {createPointStatisticsList(Arrays.asList(
                    _4CharacterPointName, _2CharacterPointName, _1CharacterPointName, _3CharacterPointName, _4CharacterPointName
            )), 420, 20, 400, 16},
            {createPointStatisticsList(Arrays.asList(
                    _4CharacterPointName, _2CharacterPointName, _3CharacterPointName, _1CharacterPointName, _4CharacterPointName
            )), 440, 20, 400, 16},
            {createPointStatisticsList(Arrays.asList(
                    _5CharacterPointName, _6CharacterPointName, _7CharacterPointName, _8CharacterPointName, _9CharacterPointName
            )), 460, 20, 400, 18},
            {createPointStatisticsList(Arrays.asList(
                    _5CharacterPointName, _8CharacterPointName, _6CharacterPointName, _9CharacterPointName, _7CharacterPointName
            )), 480, 20, 400, 18},
            {createPointStatisticsList(Arrays.asList(
                    _10CharactersPointName, _15CharactersPointName, _20CharactersPointName, _30CharactersPointName, _25CharactersPointName
            )), 460, 20, 400, 35},
            {createPointStatisticsList(Arrays.asList(
                    _10CharactersPointName, _20CharactersPointName, _15CharactersPointName, _25CharactersPointName, _30CharactersPointName
            )), 480, 20, 400, 35},


            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10"
                )), 440, 20, 400, 149},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 60characters60characters60characters60characters60cha",
                "ds_1 - 50characters50characters50characters50chara"
                )), 480, 20, 400, 149},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 10c",
                "ds_1 - 30characters30character",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character"
                )), 460, 20, 400, 149},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 10c",
                "ds_1 - 30characters30character",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 30characters30character",
                "ds_1 - 20characters2",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10"
            )),520,20,400,138},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 10c",
                "ds_1 - 30characters30character",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 30characters30character",
                "ds_1 - 20characters2",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 30characters30character",
                "ds_1 - 20characters2",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 10c",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character"
            )),580,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 30characters30character",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 20characters2",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 10c"
            )),580,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 30characters30character",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 20characters2",
                "ds_1 - 30characters30character",
                "ds_1 - 60characters60characters60characters60characters60cha",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 30characters30character",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 30characters30character"
            )),480,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 30characters30character",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 30characters30character",
                "ds_1 - 60characters60characters60characters60characters60cha",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 10c",
                "ds_1 - 30characters30character",
                "ds_1 - 30characters30character",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 20characters2",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 20characters2",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 20characters2",
                "ds_1 - 10c"
            )),540,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 30characters30character",
                "ds_1 - 20characters2",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 30characters30character",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 30characters30character",
                "ds_1 - 60characters60characters60characters60characters60cha"
            )),520,20,400,149},
            {createPointStatisticsList(Arrays.asList(
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 30characters30character",
                "ds_1 - 20characters2",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 30characters30character",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 20characters2",
                "ds_1 - 10c",
                "ds_1 - 30characters30character",
                "ds_1 - 60characters60characters60characters60characters60cha",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 90characters90characters90characters90characters90characters90characters90character",
                "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 30characters30character",
                "ds_1 - 80characters80characters80characters80characters80characters80characters8",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 70characters70characters70characters70characters70characters70c",
                "ds_1 - 50characters50characters50characters50chara",
                "ds_1 - 60characters60characters60characters60characters60cha",
                "ds_1 - 60characters60characters60characters60characters60cha"
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