package com.serotonin.mango.rt.dataImage.datapointrt.tests.sync;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.datapointrt.config.AbstractDataPointRtMultiTest;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DataPointSyncRtMultistateMultiTest extends AbstractDataPointRtMultiTest {

    @Parameterized.Parameters(name = "{index}: old: {0}, new: {1}, new2: {2}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i < getNumberOfTests(); i++) {
            datas.add(new Object[] {
                    random.nextInt(),
                    random.nextInt(),
                    random.nextInt()
            });
        }
        return datas;
    }

    public DataPointSyncRtMultistateMultiTest(Object value1, Object value2, Object value3) {
        super(DataPointSyncMode.HIGH, value1, value2, value3, DataTypes.MULTISTATE, DataTypes.CODES.getCode(DataTypes.MULTISTATE), "123");
    }
}