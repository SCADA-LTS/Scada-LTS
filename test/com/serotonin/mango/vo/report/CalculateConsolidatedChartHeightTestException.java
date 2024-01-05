package com.serotonin.mango.vo.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.serorepl.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class CalculateConsolidatedChartHeightTestException {

	private final List<ReportChartCreator.PointStatistics> pointStatistics;
	private final int expectedHeight;
	private final int imageHeightForDataPointNameInLegend;
	private final int imageHeight;
	private final int charAmountPerLine;

	public CalculateConsolidatedChartHeightTestException(List<ReportChartCreator.PointStatistics> pointStatistics, int expectedHeight, int imageHeightForDataPointNameInLegend, int imageHeight, int charAmountPerLine) {
		this.pointStatistics = pointStatistics;
		this.expectedHeight = expectedHeight;
		this.imageHeightForDataPointNameInLegend = imageHeightForDataPointNameInLegend;
		this.imageHeight = imageHeight;
		this.charAmountPerLine = charAmountPerLine;
	}




	@Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeightException(points: {0}, expected height: {1},pixels per line: {2} base height of chart: {3}, characters per line: {4})")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{createPointStatisticsList(_42PointNamesFromDataBaseCharsPerLineSetTo110FewPointsWithNameLongerThanCharsPerLineLimit), 1020, 20, 400, 110},
				{createPointStatisticsList(_53PointNamesFromDataBaseCharsPerLineSetTo100FewPointsWithNameLongerThanCharsPerLineLimit), 1180, 20, 400, 100},
				{createPointStatisticsList(StandardAppLimitOneOutOfFiveNamesExceedingSetLimitForCharactersPerLine), 400, 20, 400, 138},
				{createPointStatisticsList(SinglePointExceedingStandardAppLimitForCharactersPerLine),400,20,400,138},
				{createPointStatisticsList(2, 140), 420, 20, 400, 138},
				{createPointStatisticsList(4, 30), 420, 20, 400, 20},
				{createPointStatisticsList(2, 60), 420, 20, 400, 59},
		});
	}


	static List<String> _42PointNamesFromDataBaseCharsPerLineSetTo110FewPointsWithNameLongerThanCharsPerLineLimit = Arrays.asList(
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


	static List<String> _53PointNamesFromDataBaseCharsPerLineSetTo100FewPointsWithNameLongerThanCharsPerLineLimit = Arrays.asList(
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

	static List<String> StandardAppLimitOneOutOfFiveNamesExceedingSetLimitForCharactersPerLine = Arrays.asList(
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapoint3",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapoint2",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapoint7",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapoint2"
	);

	static List<String> SinglePointExceedingStandardAppLimitForCharactersPerLine = List.of(
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapoint2"
	);

	@Test(expected = IllegalArgumentException.class)
	public void when_calculateConsolidatedChartHeightExpectException() {
		ImageChartUtils.calculateHeightConsolidatedChart(pointStatistics, imageHeight, imageHeightForDataPointNameInLegend, charAmountPerLine);
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