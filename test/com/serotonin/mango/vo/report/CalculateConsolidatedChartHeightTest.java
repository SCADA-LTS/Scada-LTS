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


	@Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeight(points: {0}, expected height: {1},pixels per line: {2} base height of chart: {3}, characters per line: {4}) , expectException: {5}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{List.of(), 400, 20, 400, 138},
				{createPointStatisticsList(_42PointsNamesFromAppDataBaseUsedForTesting), 1020, 20, 400, 138},
				{createPointStatisticsList(_53PointsNamesFromAppDataBaseUsedForTesting), 1000, 20, 400, 138},
				{createPointStatisticsList(_16Short8CharactersPointNames),420,20,400,138},
				{createPointStatisticsList(_12Short8CharactersPointNames),400,20,400,138},
				{createPointStatisticsList(_25PointNames11ShortNamesAnd14LongerNamesMixedOrder),540,20,400,138},
				{createPointStatisticsList(SingleLong127CharactersPointName),400,20,400,138},
				{createPointStatisticsList(_7PointNamesEveryNextPointName10CharactersLongerThanTheLastOneAscending),460,20,400,138},
				{createPointStatisticsList(_7PointNamesEveryNextPointName10CharactersLongerThanTheLastOneDescending),460,20,400,138},
				{createPointStatisticsList(_7PointNamesEveryNextPointName10CharactersLongerThanTheLastOneMixedOrderDifferentHeight),440,20,400,138},
				{createPointStatisticsList(ShortNamesSmallLimitDifferentOrdersOfNames1),420,20,400,16},
				{createPointStatisticsList(ShortNamesSmallLimitDifferentOrdersOfNames2),440,20,400,16},
				{createPointStatisticsList(SemiShortNamesSmallLimitDifferentOrdersOfNames1),460,20,400,18},
				{createPointStatisticsList(SemiShortNamesSmallLimitDifferentOrdersOfNames2),480,20,400,18},
				{createPointStatisticsList(MediumSizeNamesMediumLimitDifferentOrdersOfNames1),460,20,400,35},
				{createPointStatisticsList(MediumSizeNamesMediumLimitDifferentOrdersOfNames2),480,20,400,35},
				{createPointStatisticsList(1, 50), 400, 20, 400, 138},
				{createPointStatisticsList(2, 60), 400, 20, 400, 138},
				{createPointStatisticsList(8, 130), 540, 20, 400, 138},
				{createPointStatisticsList(4, 130), 460, 20, 400, 138},
				{createPointStatisticsList(25, 20), 480, 20, 400, 138},
				{createPointStatisticsList(50, 10), 480, 20, 400, 138},
				{createPointStatisticsList(2, 200), 420, 20, 400, 210},
		});
	}


	static List<String> _42PointsNamesFromAppDataBaseUsedForTesting = Arrays.asList(
			"ds_1_virtual - dp-1-binary",
			"ds_1_virtual - dp_flag",
			"ds_1_virtual - dp_numeric",
			"ds_2-MetaData - dp_2-binary2",
			"ds_2-MetaData - dp_meta-test-numeric",
			"meta_ds - meta_dp",
			"mqtt_test - dp_1-mqtt",
			"mqtt_test - dp_2-mqtt",
			"mqtt_test - dp_3-mqtt",
			"mqtt_test - dp_4-mqtt - setter",
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
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpd",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpd111",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdppdpdpdpdpdpdpd134",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd12",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd17",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd15",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpd13",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdppdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd18"
	);


	static List<String> _53PointsNamesFromAppDataBaseUsedForTesting = Arrays.asList(
			"ds_1_virtual - dp-1-binary",
			"ds_1_virtual - dp_flag",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapoint8",
			"test_virtual_ds - datadatapointdatapointdatapointdatapointdatapoint9",
			"test_virtual_ds - datapointdatapointdatapointdatapoint8",
			"ds_1_virtual - dp_numeric",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpd2",
			"ds_2-MetaData - dp_2-binary2",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpddd2",
			"ds_2-MetaData - dp_meta-test-numeric",
			"test_virtual_ds - dpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd4",
			"meta_ds - meta_dp",
			"test_virtual_ds - pdpdpdpdpddpdpdpdpdpdddpddpdpdpdpdpdpdpdpdpdpddd1",
			"mqtt_test - dp_1-mqtt",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd78",
			"mqtt_test - dp_2-mqtt",
			"test_virtual_ds - pdpdpdpdppdpdpdpdpdpdpdpddd6",
			"mqtt_test - dp_3-mqtt",
			"test_virtual_ds - dpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd3",
			"mqtt_test - dp_4-mqtt - setter",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpddd3",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd5",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd6",
			"test_virtual_ds - virtual_dp",
			"ds - dp_1",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd5",
			"meta_ds1 - meta_dp	",
			"test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd5",
			"test_virtual_ds - dpdpdpdpdpdpddpdpdpdpddpdpdpd7",
			"test_virtual_ds - dpdpdpdpdppdpdpdpdpd1",
			"test_virtual_ds - pdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd3",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd8",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd6",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpddddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd1",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdddpdpddpdpdpdpdpdpdpdpdpdpd12",
			"modbus_test - modbus_dp",
			"test_virtual_ds - pdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpddpdpdpd144",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdddpdpdpdpdpdpdpdpdpdpd",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpd111",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdppdpdpdpdpdpdpd134",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd12",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdppd17",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpd15",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpd13",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpd18",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpddpdpdpdpdpdpdpdpdpdpdpdpddd10",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpddpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd11",
			"test_virtual_ds - pdpddpdpdpdpdpdpdpdpdpd4",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd9",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd7",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdd4",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd67",
			"test_virtual_ds - pdpddd7"
	);

	static List<String> _16Short8CharactersPointNames = Arrays.asList(
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1"
	);

	static List<String> _12Short8CharactersPointNames = Arrays.asList(
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1"
	);

	static List<String> _25PointNames11ShortNamesAnd14LongerNamesMixedOrder = Arrays.asList(
			"test_virtual_ds - datapointdatapointdatapoint2",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapoint2",
			"ds - dp1",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapoint2",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapoint2",
			"ds - dp1",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapoint2",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"ds - dp1",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapoint2"
	);
	static List<String> SingleLong127CharactersPointName = List.of(
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapoint2"
	);
	static List<String> _7PointNamesEveryNextPointName10CharactersLongerThanTheLastOneAscending = Arrays.asList(
			"20characters20charac", "30characters30characters30char", "40characters40characters40characters40ch", "50characters50characters50characters50characters50", "60characters60characters60characters60characters60characters", "70characters70characters70characters70characters70characters70characte", "80characters80characters80characters80characters80characters80characters80charac"
	);
	static List<String> _7PointNamesEveryNextPointName10CharactersLongerThanTheLastOneDescending = Arrays.asList(
			"80characters80characters80characters80characters80characters80characters80charac", "70characters70characters70characters70characters70characters70characte", "60characters60characters60characters60characters60characters", "50characters50characters50characters50characters50",  "40characters40characters40characters40ch", "20characters20charac", "30characters30characters30char"
	);
	static List<String> _7PointNamesEveryNextPointName10CharactersLongerThanTheLastOneMixedOrderDifferentHeight = Arrays.asList(
			"80characters80characters80characters80characters80characters80characters80charac",  "40characters40characters40characters40ch", "50characters50characters50characters50characters50", "60characters60characters60characters60characters60characters", "20characters20charac", "30characters30characters30char", "70characters70characters70characters70characters70characters70characte"
	);

	static List<String> ShortNamesSmallLimitDifferentOrdersOfNames1 = Arrays.asList(
			"abcd", "ab", "a", "abc", "abcd"
	);

	static List<String> ShortNamesSmallLimitDifferentOrdersOfNames2 = Arrays.asList(
			"abcd", "ab", "abc", "a", "abcd"
	);

	static List<String> SemiShortNamesSmallLimitDifferentOrdersOfNames1 = Arrays.asList(
			"5char", "6chars", "7chars7", "8chars8c", "9chars9ch"
	);

	static List<String> SemiShortNamesSmallLimitDifferentOrdersOfNames2 = Arrays.asList(
			"5char", "8chars8c", "6chars", "9chars9ch", "7chars7"
	);

	static List<String> MediumSizeNamesMediumLimitDifferentOrdersOfNames1 = Arrays.asList(
			"10chars10c", "15chars15chars1", "20chars20chars20char", "30chars30chars30chars30chars30", "25chars25chars25chars25ch"
	);

	static List<String> MediumSizeNamesMediumLimitDifferentOrdersOfNames2 = Arrays.asList(
			"10chars10c", "20chars20chars20char", "15chars15chars1", "25chars25chars25chars25ch", "30chars30chars30chars30chars30"
	);

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