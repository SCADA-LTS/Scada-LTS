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
                        "40CharactersDataSourceName40CharactersDa - dp_1",
                        "40CharactersDataSourceName40CharactersDa - dp_2",
                        "33CharactersDataSourceName33Chara - dp_1",
                        "25CharactersDataSourceNam - dp_2",
                        "25CharactersDataSourceNam - dp_1",
                        "25CharactersDataSourceNam - dp_2",
                        "Copy of virtual_1 - dp_1",
                        "Copy of virtual_1 - dp_2",
                        "virtual_1 - dp_1",
                        "virtual_1 - dp_2",
                        "39CharactersDataSourceName39CharactersD - dp_9",
                        "37CharactersDataSourceName37Character - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "37CharactersDataSourceName37Character - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "37CharactersDataSourceName37Character - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "37CharactersDataSourceName37Character - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "29CharactersDataSourceName29C - dp_1",
                        "29CharactersDataSourceName29C - dp_10",
                        "29CharactersDataSourceName29C - dp_2",
                        "29CharactersDataSourceName29C - dp_3",
                        "29CharactersDataSourceName29C - dp_4",
                        "29CharactersDataSourceName29C - dp_5",
                        "29CharactersDataSourceName29C - dp_6",
                        "29CharactersDataSourceName29C - dp_7",
                        "29CharactersDataSourceName29C - dp_8",
                        "29CharactersDataSourceName29C - dp_9",
                        "21CharactersDataSourc - dp_1",
                        "21CharactersDataSourc - dp_10",
                        "21CharactersDataSourc - dp_2",
                        "21CharactersDataSourc - dp_3",
                        "21CharactersDataSourc - dp_4",
                        "21CharactersDataSourc - dp_5",
                        "21CharactersDataSourc - dp_6",
                        "21CharactersDataSourc - dp_7",
                        "21CharactersDataSourc - dp_8",
                        "21CharactersDataSourc - dp_9",
                        "13CharactersD - dp_1",
                        "13CharactersD - dp_10",
                        "13CharactersD - dp_2",
                        "13CharactersD - dp_3",
                        "13CharactersD - dp_4",
                        "13CharactersD - dp_5",
                        "13CharactersD - dp_6",
                        "13CharactersD - dp_7",
                        "13CharactersD - dp_8",
                        "13CharactersD - dp_9",
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
                        "test_virtual_ds - 98CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNameP",
                        "test_virtual_ds - 90CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPo",
                        "test_virtual_ds - 89CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataP",
                        "test_virtual_ds - 95CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNa",
                        "test_virtual_ds - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "test_virtual_ds - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "test_virtual_ds - 91CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoi",
                        "test_virtual_ds - 89CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataP",
                        "test_virtual_ds - 95CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNa",
                        "test_virtual_ds - virtual_dp",
                        "ds - dp_1",
                        "meta_ds1 - meta_dp",
                        "modbus_test - modbus_dp",
                        "test_virtual_ds - 86CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDa",
                        "test_virtual_ds - 79CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceh",
                        "test_virtual_ds - 85CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderD",
                        "test_virtual_ds - 60CharactersDataPointNamePlaceholderDataPointNamePlaceholder",
                        "test_virtual_ds - 79CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceh",
                        "test_virtual_ds - 76CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePla",
                        "test_virtual_ds - 84CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholder",
                        "test_virtual_ds - 86CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDa",
                        "test_virtual_ds - 89CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataP",
                        "test_virtual_ds - 90CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPo",
                        "test_virtual_ds - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "test_virtual_ds - 85CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderD",
                        "test_virtual_ds - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "test_virtual_ds - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "test_virtual_ds - 91CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoi",
                        "test_virtual_ds - 91CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoi",
                        "test_virtual_ds - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "test_virtual_ds - 88CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderData",
                        "test_virtual_ds - 88CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderData")),
                        1020, 20, 400, 138},
                {createPointStatisticsList(Arrays.asList(
                        "test_virtual_ds - 86CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDa",
                        "ds - dp_1",
                        "ds_1_virtual - dp-1-binary",
                        "test_virtual_ds - 85CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderD",
                        "test_virtual_ds - 86CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDa",
                        "test_virtual_ds - 96CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNam",
                        "test_virtual_ds - 76CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePla",
                        "test_virtual_ds - 89CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataP",
                        "test_virtual_ds - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "test_virtual_ds - 98CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNameD",
                        "test_virtual_ds - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "test_virtual_ds - 99CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNameDa",
                        "test_virtual_ds - 99CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNameDa")),
                        600, 20, 400, 138},
                {createPointStatisticsList(Arrays.asList(
                        "20CharactersDataSour - dp_10",
                        "40CharactersDataSourceName40CharactersDa - dp_1",
                        "40CharactersDataSourceName40CharactersDa - dp_2",
                        "40CharactersDataSourceName40CharactersDa - dp_1",
                        "40CharactersDataSourceName40CharactersDa - dp_2",
                        "40CharactersDataSourceName40CharactersDa - dp_10",
                        "40CharactersDataSourceName40CharactersDa - dp_10",
                        "40CharactersDataSourceName40CharactersDa - dp_2",
                        "40CharactersDataSourceName40CharactersDa - dp_4",
                        "40CharactersDataSourceName40CharactersDa - dp_3",
                        "20CharactersDataSour - dp_6",
                        "20CharactersDataSour - dp_3",
                        "40CharactersDataSourceName40CharactersDa - dp_6",
                        "20CharactersDataSour - dp_3",
                        "40CharactersDataSourceName40CharactersDa - dp_4",
                        "20CharactersDataSour - dp_1",
                        "40CharactersDataSourceName40CharactersDa - dp_1",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "40CharactersDataSourceName40CharactersDa - dp_8"
                )),600,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        "20CharactersDataSour - dp_10",
                        "40CharactersDataSourceName40CharactersDa - dp_1",
                        "40CharactersDataSourceName40CharactersDa - dp_2",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "40CharactersDataSourceName40CharactersDa - dp_8",
                        "40CharactersDataSourceName40CharactersDa - dp_10",
                        "20CharactersDataSour - dp_6",
                        "40CharactersDataSourceName40CharactersDa - dp_1",
                        "40CharactersDataSourceName40CharactersDa - dp_4",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "20CharactersDataSour - dp_5"
                )),560,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        "20CharactersDataSour - dp_10",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "40CharactersDataSourceName40CharactersDa - dp_8",
                        "20CharactersDataSour - dp_6",
                        "40CharactersDataSourceName40CharactersDa - dp_4",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - dp_5"
                )),520,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "40CharactersDataSourceName40CharactersDa - 95CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointDa",
                        "20CharactersDataSour - dp_1",
                        "40CharactersDataSourceName40CharactersDa - dp_7",
                        "20CharactersDataSour - dp_2",
                        "40CharactersDataSourceName40CharactersDa - dp_1",
                        "20CharactersDataSour - dp_2",
                        "40CharactersDataSourceName40CharactersDa - dp_3",
                        "40CharactersDataSourceName40CharactersDa - dp_9",
                        "20CharactersDataSour - dp_10",
                        "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "40CharactersDataSourceName40CharactersDa - 95CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointDa"
                )),520,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        "30CharactersDataSourceName30Ch - 20CharactersDataPoin",
                        "30CharactersDataSourceName30Ch - 25CharactersDataPointPlac",
                        "30CharactersDataSourceName30Ch - 30CharactersDataPointNamePlace",
                        "30CharactersDataSourceName30Ch - 35CharactersDataPointNamePlaceholde",
                        "30CharactersDataSourceName30Ch - 40CharactersDataPointNamePlaceholderData",
                        "30CharactersDataSourceName30Ch - 45CharactersDataPointNamePlaceholderDataPoint",
                        "30CharactersDataSourceName30Ch - 50CharactersDataPointNamePlaceholderDataPointNameP",
                        "30CharactersDataSourceName30Ch - 55CharactersDataPointNamePlaceholderDataPointNamePlaceh",
                        "30CharactersDataSourceName30Ch - 60CharactersDataPointNamePlaceholderDataPointNamePlaceholder",
                        "30CharactersDataSourceName30Ch - 65CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataP",
                        "30CharactersDataSourceName30Ch - 70CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointN",
                        "30CharactersDataSourceName30Ch - 75CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePl",
                        "30CharactersDataSourceName30Ch - 80CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceho"
                )),600,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                      "20CharactersDataSour - dp_10",
                      "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                      "40CharactersDataSourceName40CharactersDa - 95CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNa",
                      "20CharactersDataSour - dp_1",
                      "20CharactersDataSour - dp_10",
                      "40CharactersDataSourceName40CharactersDa - dp_1	",
                      "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                      "40CharactersDataSourceName40CharactersDa - 95CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNa",
                      "ds_12 - dp_5",
                      "ds_12 - dp_3",
                      "ds_12 - dp_10",
                      "40CharactersDataSourceName40CharactersDa - 95CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNa",
                      "40CharactersDataSourceName40CharactersDa - 95CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPointNa",
                      "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                      "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                      "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint"
                )),620,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        "20CharactersDataSour - dp_10",
                        "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "ds_12 - dp_5",
                        "ds_12 - dp_3",
                        "ds_12 - dp_10",
                        "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "20CharactersDataSour - 92CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoin",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - dp_5",
                        "20CharactersDataSour - dp_3",
                        "20CharactersDataSour - dp_3",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - dp_10",
                        "20CharactersDataSour - dp_10",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - dp_3",
                        "20CharactersDataSour - dp_4",
                        "20CharactersDataSour - dp_5",
                        "20CharactersDataSour - 93CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceholderDataPoint",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - dp_1",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - dp_2",
                        "20CharactersDataSour - dp_4",
                        "20CharactersDataSour - dp_4",
                        "20CharactersDataSour - dp_5",
                        "20CharactersDataSour - dp_6",
                        "20CharactersDataSour - dp_6"
                )),740,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        "30CharactersDataSourceName30Ch - 20CharactersDataPoin",
                        "30CharactersDataSourceName30Ch - 30CharactersDataPointNamePlace",
                        "30CharactersDataSourceName30Ch - 40CharactersDataPointNamePlaceholderData",
                        "30CharactersDataSourceName30Ch - 50CharactersDataPointNamePlaceholderDataPointNameP",
                        "30CharactersDataSourceName30Ch - 60CharactersDataPointNamePlaceholderDataPointNamePlaceholder",
                        "30CharactersDataSourceName30Ch - 70CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointN",
                        "30CharactersDataSourceName30Ch - 80CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePlaceho",
                        "30CharactersDataSourceName30Ch - 75CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataPointNamePl",
                        "30CharactersDataSourceName30Ch - 65CharactersDataPointNamePlaceholderDataPointNamePlaceholderDataP",
                        "30CharactersDataSourceName30Ch - 55CharactersDataPointNamePlaceholderDataPointNamePlaceh",
                        "30CharactersDataSourceName30Ch - 45CharactersDataPointNamePlaceholderDataPoint",
                        "30CharactersDataSourceName30Ch - 35CharactersDataPointNamePlaceholde",
                        "30CharactersDataSourceName30Ch - 25CharactersDataPointPlac"
                )),600,20,400,138},
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