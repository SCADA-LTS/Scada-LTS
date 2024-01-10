package com.serotonin.mango.vo.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.serorepl.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.AssertionErrors.fail;

@RunWith(Parameterized.class)
public class CalculateConsolidatedChartHeightTest {

    @Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeight(points: {0}, expected height: {1},pixels per line: {2} base height of chart: {3}, characters per line: {4}) , Dataset name: {5}")
    public static Collection<Object[]> data() {

        String _10CharactersPointName = "10chars10c";
        String _15CharactersPointName = "15chars15chars1";
        String _20CharactersPointName = "20characters20charac";
        String _25CharactersPointName = "25chars25chars25chars25ch";
        String _30CharactersPointName = "30characters30characters30char";
        String _40CharactersPointName = "40characters40characters40characters40ch";
        String _50CharactersPointName = "test_virtual_ds - datapointnameconsisting50charact";
        String _60CharactersPointName = "60characters60characters60characters60characters60characters";
        String _70CharactersPointName = "70characters70characters70characters70characters70characters70characte";
        String _80CharactersPointName = "80characters80characters80characters80characters80characters80characters80charac";
        String _135CharactersPointName = "test_virtual_ds - datapointname_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeho";
        String _138CharactersPointName = "test_virtual_ds - datapointname_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholde";
        String SpecialCharacters = "łąźćżęóśÁÉÍÓÚÂÊÔÀãõçáéíóúâêôà";

        return Arrays.asList(new Object[][]{
            {List.of(), 400, 20, 400, 138, "Empty list test"},
            {createPointStatisticsList(Arrays.asList("", "", "", "", "", "", "", "", "", "")), 420, 20, 400, 15, "10 empty point names test, characters limit per line: 15"},
            {createPointStatisticsList(List.of(_135CharactersPointName)), 400, 20, 400, 138, "Single 135 characters long point name"},
            {createPointStatisticsList(List.of(_138CharactersPointName)), 400, 20, 400, 138, "Single 138 characters long point name"},
            {createPointStatisticsList(List.of(SpecialCharacters)), 400, 20, 400, 34, "Special characters test"},
            {createPointStatisticsList(Arrays.asList(_138CharactersPointName, _138CharactersPointName, _138CharactersPointName)), 440, 20, 400, 138, "3 of 138 characters long point names"},
            {createPointStatisticsList(Arrays.asList(_135CharactersPointName, _135CharactersPointName, _135CharactersPointName)), 440, 20, 400, 138, "3 of 135 characters long point names"},
            {createPointStatisticsList(Arrays.asList(
                _20CharactersPointName, _30CharactersPointName, _40CharactersPointName,
                _50CharactersPointName, _60CharactersPointName,
                _70CharactersPointName, _80CharactersPointName
            )), 460, 20, 400, 138, "7 of different length point names from 20 to 80 characters ascending"},
            {createPointStatisticsList(Arrays.asList(
                _80CharactersPointName, _70CharactersPointName, _60CharactersPointName,
                _50CharactersPointName, _40CharactersPointName, _20CharactersPointName,
                _30CharactersPointName
            )), 460, 20, 400, 138, "7 of different length point names from 80 to 20 characters descending"},
            {createPointStatisticsList(Arrays.asList(
                _80CharactersPointName, _40CharactersPointName, _50CharactersPointName,
                _60CharactersPointName, _20CharactersPointName, _30CharactersPointName,
                _70CharactersPointName
            )), 440, 20, 400, 138, "7 of different length point names from 80 to 20 characters in order to fit in 3 lines"},
            {createPointStatisticsList(Arrays.asList(
                "abcd", "ab", "a", "abc", "abcd"
            )), 420, 20, 400, 16, "5 short names from 1 to 4 characters long in order to fit in 2 lines for character limit 16"},
            {createPointStatisticsList(Arrays.asList(
                "abcd", "ab", "abc", "a", "abcd"
            )), 440, 20, 400, 16, "5 short names from 1 to 4 characters long in order to fit in 3 lines for character limit 16"},
            {createPointStatisticsList(Arrays.asList(
                "5char", "6chars", "7chars7", "8chars8c", "9chars9ch"
            )), 460, 20, 400, 18, "5 short names from 5 to 9 characters long in order to fit in 4 lines for character limit 18"},
            {createPointStatisticsList(Arrays.asList(
                "5char", "8chars8c", "6chars", "9chars9ch", "7chars7"
            )), 480, 20, 400, 18, "5 short names from 5 to 9 characters long in order to fit in 5 lines for character limit 18"},
            {createPointStatisticsList(Arrays.asList(
                _10CharactersPointName, _15CharactersPointName, _20CharactersPointName, _30CharactersPointName, _25CharactersPointName
            )), 460, 20, 400, 35, "5 medium length names from 10 to 30 characters long in order to fit in 4 lines for character limit 35"},
            {createPointStatisticsList(Arrays.asList(
                _10CharactersPointName, _20CharactersPointName, _15CharactersPointName, _25CharactersPointName, _30CharactersPointName
            )), 480, 20, 400, 35, "5 medium length names from 10 to 30 characters long in order to fit in 5 lines for character limit 35"},


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
                720, 20, 400, 138, "Data from app's database used for testing - excpected height needs to be same as in app's report screen for such data"
            },


            {createPointStatisticsList(1000, 100), 20380, 20, 400, 138, "Extreme amount of points generated using createPointStatisticsList method: 1000 points, names length: 100"},
            {createPointStatisticsList(1, 50), 400, 20, 400, 138, "generated data using createPointStatisticsList method: 1 point, names length: 50"},
            {createPointStatisticsList(2, 60), 400, 20, 400, 138, "generated data using createPointStatisticsList method: 2 points, names length: 60"},
            {createPointStatisticsList(8, 130), 540, 20, 400, 138, "generated data using createPointStatisticsList method: 8 point, names length: 130"},
            {createPointStatisticsList(4, 130), 460, 20, 400, 138, "generated data using createPointStatisticsList method: 4 point, names length: 130"},
            {createPointStatisticsList(25, 20), 480, 20, 400, 138, "generated data using createPointStatisticsList method: 25 point, names length: 20"},
            {createPointStatisticsList(50, 10), 480, 20, 400, 138, "generated data using createPointStatisticsList method: 50 point, names length: 10"},
            {createPointStatisticsList(2, 200), 420, 20, 400, 210, "generated data using createPointStatisticsList method: 2 point, names length: 200"},
        });
    }

    private final List<ReportChartCreator.PointStatistics> pointStatistics;
    private final int expectedHeight;
    private final int imageHeightForDataPointNameInLegend;
    private final int imageHeight;
    private final int charAmountPerLine;
    private final String testName;

    public CalculateConsolidatedChartHeightTest(List<ReportChartCreator.PointStatistics> pointStatistics, int expectedHeight, int imageHeightForDataPointNameInLegend, int imageHeight, int charAmountPerLine, String testName) {
        this.pointStatistics = pointStatistics;
        this.expectedHeight = expectedHeight;
        this.imageHeightForDataPointNameInLegend = imageHeightForDataPointNameInLegend;
        this.imageHeight = imageHeight;
        this.charAmountPerLine = charAmountPerLine;
        this.testName = testName;
    }

    @Test
    public void when_calculateConsolidatedChartHeight() {
        int actualHeight = ImageChartUtils.calculateHeightConsolidatedChart(pointStatistics, imageHeight, imageHeightForDataPointNameInLegend, charAmountPerLine);
        if (actualHeight != expectedHeight) {
            fail("Test '" + testName + "' failed: Expected height = " + expectedHeight + ", Actual height = " + actualHeight);
        }
    }

    private static List<ReportChartCreator.PointStatistics> createPointStatisticsList(int numberOfPoints, int pointNameLength) {
        List<ReportChartCreator.PointStatistics> pointStatisticsList = new ArrayList<>();
        for (int i = 0; i < numberOfPoints; i++) {
            ReportChartCreator.PointStatistics pointStatistics = new ReportChartCreator.PointStatistics(1, "");
            pointStatistics.setName(StringUtils.generateRandomString(pointNameLength, "abcdefgijklmnouprstuwyxzsagdfahagfhjagjg"));
            pointStatisticsList.add(pointStatistics);
        }
        return pointStatisticsList;
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

}