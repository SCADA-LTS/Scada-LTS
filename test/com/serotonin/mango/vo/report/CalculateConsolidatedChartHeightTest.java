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

    @Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeight(points: {0}, expected height: {1},pixels per line: {2} base height of chart: {3}, characters per line: {4}) , expectException: {5}")
    public static Collection<Object[]> data() {

        String _8CharactersPointName = "ds - dp1";
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
        String _127CharactersPointName = "test_virtual_ds - datapointname_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder";
        String _135CharactersPointName = "test_virtual_ds - datapointname_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeho";
        String _138CharactersPointName = "test_virtual_ds - datapointname_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholder_placeholde";

        return Arrays.asList(new Object[][]{
                {List.of(), 400, 20, 400, 138},
                {createPointStatisticsList(Arrays.asList(
                _8CharactersPointName,_8CharactersPointName,_8CharactersPointName,
                _8CharactersPointName,_8CharactersPointName,_8CharactersPointName,
                _8CharactersPointName,_8CharactersPointName,_8CharactersPointName,
                _8CharactersPointName,_8CharactersPointName,_8CharactersPointName,
                _8CharactersPointName,_8CharactersPointName,_8CharactersPointName, _8CharactersPointName) ),420,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        _8CharactersPointName,_8CharactersPointName,
                        _8CharactersPointName,_8CharactersPointName,
                        _8CharactersPointName,_8CharactersPointName,
                        _8CharactersPointName,_8CharactersPointName,
                        _8CharactersPointName,_8CharactersPointName,
                        _8CharactersPointName,_8CharactersPointName)),400,20,400,138},
                {createPointStatisticsList(Arrays.asList(_8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName,_50CharactersPointName,
                        _8CharactersPointName)),520,20,400,138},
                {createPointStatisticsList(Arrays.asList(_127CharactersPointName)),400,20,400,138},
                {createPointStatisticsList(Arrays.asList(_135CharactersPointName)),400,20,400,138},
                {createPointStatisticsList(Arrays.asList(_138CharactersPointName)),400,20,400,138},


                {createPointStatisticsList(Arrays.asList(_138CharactersPointName, _138CharactersPointName, _138CharactersPointName)),440,20,400,138},
                {createPointStatisticsList(Arrays.asList(_135CharactersPointName, _135CharactersPointName, _135CharactersPointName)),440,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        _20CharactersPointName, _30CharactersPointName, _40CharactersPointName,
                        _50CharactersPointName, _60CharactersPointName,
                        _70CharactersPointName, _80CharactersPointName
                )),460,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        _80CharactersPointName, _70CharactersPointName, _60CharactersPointName,
                        _50CharactersPointName,  _40CharactersPointName, _20CharactersPointName,
                        _30CharactersPointName
                )),460,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        _80CharactersPointName, _40CharactersPointName, _50CharactersPointName,
                        _60CharactersPointName, _20CharactersPointName, _30CharactersPointName,
                        _70CharactersPointName
                )),440,20,400,138},
                {createPointStatisticsList(Arrays.asList(
                        "abcd", "ab", "a", "abc", "abcd"
                )),420,20,400,16},
                {createPointStatisticsList(Arrays.asList(
                        "abcd", "ab", "abc", "a", "abcd"
                )),440,20,400,16},
                {createPointStatisticsList(Arrays.asList(
                        "5char", "6chars", "7chars7", "8chars8c", "9chars9ch"
                )),460,20,400,18},
                {createPointStatisticsList(Arrays.asList(
                        "5char", "8chars8c", "6chars", "9chars9ch", "7chars7"
                )),480,20,400,18},
                {createPointStatisticsList(Arrays.asList(
                        _10CharactersPointName, _15CharactersPointName, _20CharactersPointName, _30CharactersPointName, _25CharactersPointName
                )),460,20,400,35},
                {createPointStatisticsList(Arrays.asList(
                        _10CharactersPointName, _20CharactersPointName, _15CharactersPointName, _25CharactersPointName, _30CharactersPointName
                )),480,20,400,35},


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
                        "ds_12 - dp_9")), 720, 20, 400, 138},


                {createPointStatisticsList(1, 50), 400, 20, 400, 138},
                {createPointStatisticsList(2, 60), 400, 20, 400, 138},
                {createPointStatisticsList(8, 130), 540, 20, 400, 138},
                {createPointStatisticsList(4, 130), 460, 20, 400, 138},
                {createPointStatisticsList(25, 20), 480, 20, 400, 138},
                {createPointStatisticsList(50, 10), 480, 20, 400, 138},
                {createPointStatisticsList(2, 200), 420, 20, 400, 210},
        });
    }

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

	@Test
	public void when_calculateConsolidatedChartHeight() {
		int actualHeight = ImageChartUtils.calculateHeightConsolidatedChart(pointStatistics, imageHeight, imageHeightForDataPointNameInLegend, charAmountPerLine);
		assertEquals(expectedHeight, actualHeight);
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