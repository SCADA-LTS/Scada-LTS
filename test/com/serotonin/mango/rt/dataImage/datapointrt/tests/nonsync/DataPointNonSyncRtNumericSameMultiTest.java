package com.serotonin.mango.rt.dataImage.datapointrt.tests.nonsync;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.datapointrt.config.AbstractDataPointRtMultiTest;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DataPointNonSyncRtNumericSameMultiTest extends AbstractDataPointRtMultiTest {

    @Parameterized.Parameters(name = "{index}: old: {0}, new: {1}, new2: {2}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();
        Random random = new Random();
        double var1 = random.nextDouble();
        double var2 = random.nextDouble();
        double var3 = random.nextDouble();
        for(int i=0; i < getNumberOfTests(); i++) {
            datas.add(new Object[] {
                    var1,
                    var2,
                    var3
            });
        }
        return datas;
    }

    public DataPointNonSyncRtNumericSameMultiTest(Object value1, Object value2, Object value3) {
        super(DataPointSyncMode.MEDIUM, value1, value2, value3, DataTypes.NUMERIC, DataTypes.CODES.getCode(DataTypes.NUMERIC), "123.0");
    }
}