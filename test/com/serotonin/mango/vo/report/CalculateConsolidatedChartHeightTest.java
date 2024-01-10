package com.serotonin.mango.vo.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
                        "Copy of Copy of Copy of Copy of virtual_ - dp_1",
                        "Copy of Copy of Copy of Copy of virtual_ - dp_2",
                        "Copy of Copy of Copy of virtual_1 - dp_1",
                        "Copy of Copy of Copy of virtual_1 - dp_2",
                        "Copy of Copy of virtual_1 - dp_1",
                        "Copy of Copy of virtual_1 - dp_2",
                        "Copy of virtual_1 - dp_1",
                        "Copy of virtual_1 - dp_2",
                        "virtual_1 - dp_1",
                        "virtual_1 - dp_2",
                        "Copy of Copy of Copy of Copy of Copy of - dp_9",
                        "Copy of Copy of Copy of Copy of ds_12 - DataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlacehol",
                        "Copy of Copy of Copy of Copy of ds_12 - DataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlacehol",
                        "Copy of Copy of Copy of Copy of ds_12 - DataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlacehol",
                        "Copy of Copy of Copy of Copy of ds_12 - DataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlacehol",
                        "Copy of Copy of Copy of ds_12 - dp_1",
                        "Copy of Copy of Copy of ds_12 - dp_10",
                        "Copy of Copy of Copy of ds_12 - dp_2",
                        "Copy of Copy of Copy of ds_12 - dp_3",
                        "Copy of Copy of Copy of ds_12 - dp_4",
                        "Copy of Copy of Copy of ds_12 - dp_5",
                        "Copy of Copy of Copy of ds_12 - dp_6",
                        "Copy of Copy of Copy of ds_12 - dp_7",
                        "Copy of Copy of Copy of ds_12 - dp_8",
                        "Copy of Copy of Copy of ds_12 - dp_9",
                        "Copy of Copy of ds_12 - dp_1",
                        "Copy of Copy of ds_12 - dp_10",
                        "Copy of Copy of ds_12 - dp_2",
                        "Copy of Copy of ds_12 - dp_3",
                        "Copy of Copy of ds_12 - dp_4",
                        "Copy of Copy of ds_12 - dp_5",
                        "Copy of Copy of ds_12 - dp_6",
                        "Copy of Copy of ds_12 - dp_7",
                        "Copy of Copy of ds_12 - dp_8",
                        "Copy of Copy of ds_12 - dp_9",
                        "Copy of ds_12 - dp_1",
                        "Copy of ds_12 - dp_10",
                        "Copy of ds_12 - dp_2",
                        "Copy of ds_12 - dp_3",
                        "Copy of ds_12 - dp_4",
                        "Copy of ds_12 - dp_5",
                        "Copy of ds_12 - dp_6",
                        "Copy of ds_12 - dp_7",
                        "Copy of ds_12 - dp_8",
                        "Copy of ds_12 - dp_9",
                        "ds_12 - dp_1",
                        "ds_12 - dp_10",
                        "ds_12 - dp_2",
                        "ds_12 - dp_3",
                        "ds_12 - dp_4",
                        "ds_12 - dp_5",
                        "ds_12 - dp_6",
                        "ds_12 - dp_7",
                        "ds_12 - dp_8",
                        "ds_12 - dp_9")),
                        720, 20, 400, 138
                },
                {createPointStatisticsList(Arrays.asList(
                        "ds_1_virtual - dp-1-binary",
                        "ds_1_virtual - dp_flag",
                        "ds_1_virtual - dp_numeric",
                        "ds_2-MetaData - dp_2-binary2",
                        "ds_2-MetaData - dp_meta-test-numeric",
                        "meta_ds - meta_dp",
                        "mqtt_test - dp_1-mqtt",
                        "mqtt_test - dp_2-mqtt",
                        "mqtt_test - dp_3-mqtt",
                        "mqtt_test - dp_4-mqtt - ustawiacz",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd3",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd3",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpddd5",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdppdpdpdpdpdpdpdpddd6",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpddd2",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd78",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpddpdpdpdpdpdpdpdpdpdpddd1",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpddpdpdpdpdpdpdpdpdpd5",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdppdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd6",
                        "test_virtual_ds - virtual_dp",
                        "ds - dp_1",
                        "meta_ds1 - meta_dp",
                        "modbus_test - modbus_dp",
                        "test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd5",
                        "test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd4",
                        "test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpd7",
                        "test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd1",
                        "test_virtual_ds - pdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpd2",
                        "test_virtual_ds - pdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd3",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd8",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd6",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpddddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd1",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdddpdpddpdpdpdpdpdpdpdpdpdpd12",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpddpdpdpd144",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpd	Numeric",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpd111",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdppdpdpdpdpdpdpd134",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd12",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd17",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd15",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpd13",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdppdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd18")),
                        1020, 20, 400, 138},
                {createPointStatisticsList(Arrays.asList(
                        "test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd5",
                        "ds - dp_1",
                        "ds_1_virtual - dp-1-binary",
                        "test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpd7",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd6",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpddpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd11",
                        "test_virtual_ds - pdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd3",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpddpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpddd10",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd3",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpddpdpdpd144",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpddd9",
                        "test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpd4")),
                        600, 20, 400, 138}
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
        assertEquals(actualHeight, expectedHeight);
    }

}