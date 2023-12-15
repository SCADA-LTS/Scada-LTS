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

	@Parameterized.Parameters(name = "{index}: calculateConsolidatedChartHeight({0}, {1}, {2}) = {3}, expectException: {4}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{List.of(), 400, 20, 400, 138, false},
				{createPointStatisticsList(pointNames), 1020, 20, 400, 138, false},
				{createPointStatisticsList(pointNames2), 1240, 20, 400, 138, false},
				{createPointStatisticsList(pointNames3), 1260, 20, 400, 138, false},
				{createPointStatisticsList(pointNames4), 1000, 20, 400, 138, false},
				{createPointStatisticsList(pointNames4), 780, 20, 400, 200, false},
				{createPointStatisticsList(pointNames4), 1180, 20, 400, 100, true},
				{createPointStatisticsList(OneLongNameRestMedium), 400, 20, 400, 138, true},
				{createPointStatisticsList(FewSmaller),420,20,400,138, false},
				{createPointStatisticsList(FewSmaller2),400,20,400,138, false},
				{createPointStatisticsList(Mixed),540,20,400,138, false},
				{createPointStatisticsList(OneLong),400,20,400,138, false},
				{createPointStatisticsList(OneLongerThanCharAmountPerLine),400,20,400,138, true},
				{createPointStatisticsList(OrderCheck1),460,20,400,138, false},
				{createPointStatisticsList(OrderCheck2),460,20,400,138, false},
				{createPointStatisticsList(OrderCheck3),440,20,400,138, false},
				{createPointStatisticsList(1, 50), 400, 20, 400, 138, false},
				{createPointStatisticsList(1, 125), 400, 20, 400, 138, false},
				{createPointStatisticsList(2, 70), 420, 20, 400, 138, false},
				{createPointStatisticsList(2, 60), 400, 20, 400, 138, false},
				{createPointStatisticsList(4, 60), 420, 20, 400, 138, false},
				{createPointStatisticsList(8, 130), 540, 20, 400, 138, false},
				{createPointStatisticsList(5, 50), 440, 20, 400, 138, false},
				{createPointStatisticsList(4, 130), 460, 20, 400, 138, false},
				{createPointStatisticsList(25, 20), 480, 20, 400, 138, false},
				{createPointStatisticsList(50, 10), 480, 20, 400, 138, false},
				{createPointStatisticsList(2, 140), 420, 20, 400, 138, true},
				{createPointStatisticsList(4, 30), 420, 20, 400, 20, true},
				{createPointStatisticsList(2, 60), 420, 20, 400, 59, true},
				{createPointStatisticsList(2, 200), 420, 20, 400, 210, false},
		});
	}

	private final List<ReportChartCreator.PointStatistics> pointStatistics;
	private final int expectedHeight;
	private final int imageHeightForDataPointNameInLegend;
	private final int imageHeight;
	private final int charAmountPerLine;
	private final boolean expectException;
	static List<String> pointNames = Arrays.asList(
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

	static List<String> pointNames2 = Arrays.asList(
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
			"meta_ds1 - meta_dp	",
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
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdppdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd18",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpddpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpddd10",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpddpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd11",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpddpdpdpdpdpdpddd8",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd9pdpd",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd8",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpd4",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpddd9",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd7",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd4",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdppdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd67",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdppdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd7"
	);

	static List<String> pointNames3 = Arrays.asList(
			"ds_1_virtual - dp-1-binary",
			"ds_1_virtual - dp_flag",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpddpdpdpdpdpdpddd8",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd9pdpd",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd8",
			"ds_1_virtual - dp_numeric",
			"test_virtual_ds - pdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpd2",
			"ds_2-MetaData - dp_2-binary2",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpddd2",
			"ds_2-MetaData - dp_meta-test-numeric",
			"test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd4",
			"meta_ds - meta_dp",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpddpdpdpdpdpdpdpdpdpdpddd1",
			"mqtt_test - dp_1-mqtt",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd78",
			"mqtt_test - dp_2-mqtt",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdppdpdpdpdpdpdpdpddd6",
			"mqtt_test - dp_3-mqtt",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd3",
			"mqtt_test - dp_4-mqtt - setter",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd3",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpddd5",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdppdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd6",
			"test_virtual_ds - virtual_dp",
			"ds - dp_1",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpddpdpdpdpdpdpdpdpdpd5",
			"meta_ds1 - meta_dp	",
			"test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd5",
			"test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpd7",
			"test_virtual_ds - dpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd1",
			"test_virtual_ds - pdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd3",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd8",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd6",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpddddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd1",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdddpdpddpdpdpdpdpdpdpdpdpdpd12",
			"modbus_test - modbus_dp",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpddpdpdpd144",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpd",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpd111",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdddpdpdpdpdpdppdpdpdpdpdpdpd134",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd12",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd17",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd15",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpd13",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdppdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd18",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpddpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpddd10",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpddpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd11",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpd4",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpddd9",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd7",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdppdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd4",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdppdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpd67",
			"test_virtual_ds - pdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdppdpdpdpdpdpdddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpddd7"
	);

	static List<String> pointNames4 = Arrays.asList(
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

	static List<String> OneLongNameRestMedium = Arrays.asList(
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapoint3",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapoint2",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapoint7",
			"test_virtual_ds - datapointdatapointdatapoint2",
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapoint2"
	);

	static List<String> FewSmaller = Arrays.asList(
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1","ds - dp1",
			"ds - dp1"
	);

	static List<String> FewSmaller2 = Arrays.asList(
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1",
			"ds - dp1","ds - dp1"
	);

	static List<String> Mixed = Arrays.asList(
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
	static List<String> OneLong = List.of(
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapoint2"
	);
	static List<String> OneLongerThanCharAmountPerLine = List.of(
			"test_virtual_ds - datapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapointdatapoint2"
	);
	static List<String> OrderCheck1 = Arrays.asList(
			"20characters20charac", "30characters30characters30char", "40characters40characters40characters40ch", "50characters50characters50characters50characters50", "60characters60characters60characters60characters60characters", "70characters70characters70characters70characters70characters70characte", "80characters80characters80characters80characters80characters80characters80charac"
	);
	static List<String> OrderCheck2 = Arrays.asList(
			"80characters80characters80characters80characters80characters80characters80charac", "70characters70characters70characters70characters70characters70characte", "60characters60characters60characters60characters60characters", "50characters50characters50characters50characters50",  "40characters40characters40characters40ch", "20characters20charac", "30characters30characters30char"
	);
	static List<String> OrderCheck3 = Arrays.asList(
			"80characters80characters80characters80characters80characters80characters80charac",  "40characters40characters40characters40ch", "50characters50characters50characters50characters50", "60characters60characters60characters60characters60characters", "20characters20charac", "30characters30characters30char", "70characters70characters70characters70characters70characters70characte"
	);
	public CalculateConsolidatedChartHeightTest(List<ReportChartCreator.PointStatistics> pointStatistics, int expectedHeight, int imageHeightForDataPointNameInLegend, int imageHeight, int charAmountPerLine, boolean expectException) {
		this.pointStatistics = pointStatistics;
		this.expectedHeight = expectedHeight;
		this.imageHeightForDataPointNameInLegend = imageHeightForDataPointNameInLegend;
		this.imageHeight = imageHeight;
		this.charAmountPerLine = charAmountPerLine;
		this.expectException = expectException;
	}

	@Test
	public void when_calculateConsolidatedChartHeight() {

		try {
			int actualHeight = ImageChartUtils.calculateHeightConsolidatedChart(pointStatistics, imageHeight, imageHeightForDataPointNameInLegend, charAmountPerLine);
			assertEquals(expectedHeight, actualHeight);
			if (expectException) {
				fail("Expected IllegalArgumentException");
			}
		} catch (IllegalArgumentException e) {
			if (!expectException) {
				throw e;
			}
		}

	}

	/*@Test(expected = IllegalArgumentException.class)
	public void when_calculateConsolidatedChartHeight_withTooLongName_thenExpectException() {

		String tooLongName = "dpdpdpdpdpdpdpdpdpdpdpdpddpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdpdddddddddddddddddddddddddddddddddddddddddddddpdpdpdpdpdpdpdpdpdpdpd20";

		List<ReportChartCreator.PointStatistics> pointStatistics = new ArrayList<>();
		ReportChartCreator.PointStatistics pointStat = new ReportChartCreator.PointStatistics(1, tooLongName);
		pointStatistics.add(pointStat);

		// Przekazywanie listy do metody, która powinna wyrzucić wyjątek
		ImageChartUtils.calculateHeightConsolidatedChart(pointStatistics, imageHeight, imageHeightForDataPointNameInLegend, charAmountPerLine);
	}*/

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