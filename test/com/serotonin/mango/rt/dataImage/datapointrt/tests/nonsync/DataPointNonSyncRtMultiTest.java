package com.serotonin.mango.rt.dataImage.datapointrt.tests.nonsync;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.datapointrt.config.AbstractDataPointRtMultiTest;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;


public class DataPointNonSyncRtMultiTest extends AbstractDataPointRtMultiTest {

    @Parameterized.Parameters(name = "{index}: old: {0}, new: {1}, new2: {2}, dataType: {4}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();
        datas.add(new Object[] {
                2.0,
                3.0,
                1.0,
                DataTypes.NUMERIC,
                DataTypes.CODES.getCode(DataTypes.NUMERIC),
                "123"
        });

        datas.add(new Object[] {
                true,
                false,
                true,
                DataTypes.BINARY,
                DataTypes.CODES.getCode(DataTypes.BINARY),
                "true"
        });

        datas.add(new Object[] {
                "A",
                "B",
                "C",
                DataTypes.ALPHANUMERIC,
                DataTypes.CODES.getCode(DataTypes.ALPHANUMERIC),
                "alpha"
        });

        datas.add(new Object[] {
                22323,
                32144,
                32145,
                DataTypes.MULTISTATE,
                DataTypes.CODES.getCode(DataTypes.MULTISTATE),
                "321"
        });

        datas.add(new Object[] {
                232423.0,
                3234.0,
                -3234.0,
                DataTypes.NUMERIC,
                DataTypes.CODES.getCode(DataTypes.NUMERIC),
                "567"
        });
        return datas;
    }

    public DataPointNonSyncRtMultiTest(Object value1, Object value2, Object value3, int dataTypeId, String dataType, String startValue) {
        super(DataPointSyncMode.MEDIUM, value1, value2, value3, dataTypeId, dataType, startValue);
    }
}