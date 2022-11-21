package com.serotonin.mango.rt.dataImage.datapointrt.tests;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.datapointrt.config.AbstractDataPointRtMultiTest;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DataPointRtBinaryMultiTest extends AbstractDataPointRtMultiTest {

    @Parameterized.Parameters(name = "{index}: old: {0}, new: {1}, new2: {2}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i < getNumberOfTests(); i++) {
            boolean value1 = random.nextInt() % 2 == 0;
            datas.add(new Object[] {
                    value1,
                    !value1,
                    value1
            });
        }
        return datas;
    }

    public DataPointRtBinaryMultiTest(Object value1, Object value2, Object value3) {
        super(DataPointSyncMode.LOW, value1, value2, value3, DataTypes.BINARY, DataTypes.CODES.getCode(DataTypes.BINARY), "true");
    }
}