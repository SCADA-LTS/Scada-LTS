package com.serotonin.mango.rt.dataImage.datapointrt.config;

import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataSource.virtual.VirtualDataSourceRT;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Percent tolerance should NOT log the initial value for MEDIUM/HIGH sync modes.
 * Expected persisted values:
 *   - only the one that exceeds the % threshold (107.0), without the initial 100.0.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({
        DAO.class, Common.class, PointValueDAO.class, DataPointDao.class, DataSourceDao.class,
        VirtualDataSourceRT.class, RuntimeManager.class, PointValueService.class, PointValueDAO.class,
        ApplicationBeans.class, PointValueDao.class, SystemSettingsDAO.class
})
@PowerMockIgnore({
        "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*",
        "com.sun.org.apache.xalan.*", "javax.activation.*", "javax.management.*"
})
public class DataPointRtTolerancePercentStandaloneTest extends ConfigDataPointRtTest {

    private DataPointRT rt;
    private final DataPointSyncMode mode;

    @Parameterized.Parameters(name = "{index}: sync={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // MEDIUM behaves like HIGH (state-based origin) – initial 100.0 is not logged
                { DataPointSyncMode.HIGH,   100.0, 104.0, 107.0, DataTypes.NUMERIC, "NUMERIC", "100", 5.0 },
                { DataPointSyncMode.MEDIUM, 100.0, 104.0, 107.0, DataTypes.NUMERIC, "NUMERIC", "100", 5.0 },
        });
    }

    public DataPointRtTolerancePercentStandaloneTest(
            DataPointSyncMode sync,
            Object oldVal, Object val2, Object val3,
            int dataTypeId, String dataType, String startValue,
            double tolerancePercent
    ) {
        super(sync, oldVal, val2, val3, dataTypeId, dataType, startValue, tolerancePercent);
        this.mode = sync; // remember the mode for assertions
    }

    @Before
    public void setUp() {
        rt = start();
        DataPointVO vo = getDataPointVO();
        vo.setLoggingType(DataPointVO.LoggingTypes.ON_CHANGE);
        vo.setToleranceAsPercentage(true); // percent mode
        // tolerance already passed via constructor (5.0)
    }

    @After
    public void tearDown() {
        clear();
    }

    @Test
    public void percent_does_not_log_initial_for_medium_and_high() {
        // given
        PointValueTime oldValue  = getOldValueWithUser();   // 100.0
        PointValueTime newValue  = getNewValueWithUser();   // 104.0  (< 5% vs baseline -> shouldn't be stored)
        PointValueTime newValue2 = getNewValueWithUser2();  // 107.0  (> 5% -> must be stored)

        // when
        rt.setPointValue(oldValue,  getUser());
        rt.setPointValue(newValue,  getUser());
        rt.setPointValue(newValue2, getUser());

        // then
        List<PointValueTime> values = rt.getLatestPointValues(getDefaultCacheSize());

        // must contain 107.0 (the only value above threshold)
        boolean has107 = values.stream().anyMatch(v -> v.getValue().equals(newValue2.getValue()));
        // must NOT contain 104.0 (below threshold)
        boolean has104 = values.stream().anyMatch(v -> v.getValue().equals(newValue.getValue()));
        org.junit.Assert.assertTrue("should store 107.0", has107);
        org.junit.Assert.assertFalse("should NOT store 104.0", has104);

        // count depends on mode:
        // HIGH -> only 107.0, MEDIUM -> 100.0 (first write) + 107.0
        int expectedCount = (mode == DataPointSyncMode.HIGH) ? 1 : 2;
        org.junit.Assert.assertEquals("unexpected number of persisted values for mode " + mode,
                expectedCount, values.size());

        // optional: ensure the newest value is 107.0
        org.junit.Assert.assertEquals(newValue2.getValue(), values.get(0).getValue());
    }
}
