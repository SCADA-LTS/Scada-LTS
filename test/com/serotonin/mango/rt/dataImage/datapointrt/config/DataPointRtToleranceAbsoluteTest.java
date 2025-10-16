package com.serotonin.mango.rt.dataImage.datapointrt.config;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.Before;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * ON_CHANGE + absolute tolerance.
 * Scenario:
 *  - old = 10.0
 *  - new = 10.5  (stored because origin starts at 0 -> diff > tol)
 *  - new2 = 11.2 (NOT stored because |11.2-10.5| = 0.7 <= 1.0)
 */
public class DataPointRtToleranceAbsoluteTest extends AbstractDataPointRtToleranceTest {

    public DataPointRtToleranceAbsoluteTest(
            DataPointSyncMode sync, Object v1, Object v2, Object v3,
            int dataTypeId, String dataType, String startValue, double tolerance,
            java.util.List<Double> expected) {
        super(sync, v1, v2, v3, dataTypeId, dataType, startValue, tolerance, expected);
    }

    @Before
    public void setVoFlags() {
        DataPointVO vo = getDataPointVO();
        vo.setLoggingType(DataPointVO.LoggingTypes.ON_CHANGE);
        vo.setToleranceAsPercentage(false);
    }

    @Parameterized.Parameters(name = "{index}: sync={0}, absTol={7}, expected={8}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // HIGH (PointValueState-based): baseline = first stored (10.0) -> store 11.2, not 10.5
                { DataPointSyncMode.HIGH, 10.0, 10.5, 11.2, DataTypes.NUMERIC, "NUMERIC", "10", 1.0, Arrays.asList(11.2) },

                // MEDIUM: pick according to how DataPointNonSyncRT behaves in your tree.
                { DataPointSyncMode.MEDIUM, 10.0, 10.5, 11.2, DataTypes.NUMERIC, "NUMERIC", "10", 1.0, Arrays.asList(11.2) },

                // LOW (plain DataPointRT): baseline starts at ~0 -> store 10.5, then 11.2 diff=0.7 <= 1.0 not stored
                { DataPointSyncMode.LOW,  10.0, 10.5, 11.2, DataTypes.NUMERIC, "NUMERIC", "10", 1.0, Arrays.asList(10.5) },
        });
    }
}
