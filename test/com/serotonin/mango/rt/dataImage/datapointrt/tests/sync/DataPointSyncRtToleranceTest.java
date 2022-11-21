package com.serotonin.mango.rt.dataImage.datapointrt.tests.sync;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.datapointrt.config.AbstractDataPointRtToleranceTest;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataPointSyncRtToleranceTest extends AbstractDataPointRtToleranceTest {

    @Parameterized.Parameters(name = "{index}: old: {0}, new: {1}, new2: {2}, tolerance: {3}, expected values: {4}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();
        datas.add(new Object[] {2.0, 3.0, 4.0, 1.1, Arrays.asList(2.0, 4.0)});
        datas.add(new Object[] {2.0, 3.0, 4.0, 1.0, Arrays.asList(2.0, 4.0)});
        datas.add(new Object[] {2.0, 3.0, 4.0, 0.9, Arrays.asList(2.0, 3.0, 4.0)});
        datas.add(new Object[] {2.0, 3.0, 4.0, 0.9999, Arrays.asList(2.0, 3.0, 4.0)});
        datas.add(new Object[] {-2.0, -3.0, -4.0, 1.1, Arrays.asList(-2.0, -4.0)});
        datas.add(new Object[] {-2.0, -3.0, -4.0, 1.0, Arrays.asList(-2.0, -4.0)});
        datas.add(new Object[] {-2.0, -3.0, -4.0, 0.9, Arrays.asList(-2.0, -3.0, -4.0)});
        datas.add(new Object[] {-2.0, -3.0, -4.0, 0.9999, Arrays.asList(-2.0, -3.0, -4.0)});

        datas.add(new Object[] {-2.0, 3.0, 3.0, 1.1, Arrays.asList(-2.0, 3.0)});
        datas.add(new Object[] {-2.0, 3.0, 3.0, 1.0, Arrays.asList(-2.0, 3.0)});
        datas.add(new Object[] {-2.0, 3.0, 3.0, 0.9, Arrays.asList(-2.0, 3.0)});
        datas.add(new Object[] {-2.0, 3.0, 3.0, 0.9999, Arrays.asList(-2.0, 3.0)});
        datas.add(new Object[] {-2.0, 3.0, 3.0, 5.0, Arrays.asList(-2.0)});

        datas.add(new Object[] {2.0, -3.0, 3.0, 1.1, Arrays.asList(2.0, -3.0, 3.0)});
        datas.add(new Object[] {2.0, -3.0, 3.0, 1.0, Arrays.asList(2.0, -3.0, 3.0)});
        datas.add(new Object[] {2.0, -3.0, 3.0, 0.9, Arrays.asList(2.0, -3.0, 3.0)});
        datas.add(new Object[] {2.0, -3.0, 3.0, 0.9999, Arrays.asList(2.0, -3.0, 3.0)});
        datas.add(new Object[] {2.0, -3.0, 3.0, 5.0, Arrays.asList(2.0)});

        return datas;
    }

    public DataPointSyncRtToleranceTest(Object value1, Object value2, Object value3, double tolerance, List<Double> expected) {
        super(DataPointSyncMode.HIGH, value1, value2, value3, DataTypes.NUMERIC, DataTypes.CODES.getCode(DataTypes.NUMERIC), "99", tolerance, expected);
    }
}