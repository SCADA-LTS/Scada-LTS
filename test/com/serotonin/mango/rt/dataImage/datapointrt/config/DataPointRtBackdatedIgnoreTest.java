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
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.virtual.VirtualDataSourceRT;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SystemSettingsDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.mango.service.PointValueService;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Verifies that backdated values (ts < current) are ignored (when source is not a set-point).
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({
        DAO.class, Common.class, PointValueDAO.class, DataPointDao.class, DataSourceDao.class,
        VirtualDataSourceRT.class, RuntimeManager.class, PointValueService.class, PointValueDAO.class,
        ApplicationBeans.class, PointValueDao.class, SystemSettingsDAO.class
})
@PowerMockIgnore({
        "com.sun.org.apache.xerces.*","javax.xml.*","org.xml.*","org.w3c.*",
        "com.sun.org.apache.xalan.*","javax.activation.*","javax.management.*"
})
public class DataPointRtBackdatedIgnoreTest extends ConfigDataPointRtTest {

    private DataPointRT rt;

    public DataPointRtBackdatedIgnoreTest() {
        super(
                DataPointSyncMode.HIGH,   // use HIGH (fully synchronized) here
                10.0, 11.0, 12.0,
                DataTypes.NUMERIC, "NUMERIC", "10",
                0.0
        );
    }

    @Before
    public void setUp() {
        rt = start();
        DataPointVO vo = getDataPointVO();
        vo.setLoggingType(DataPointVO.LoggingTypes.ON_CHANGE);
        vo.setToleranceAsPercentage(false);
        vo.setTolerance(0.0);
    }

    @After
    public void tearDown() {
        clear();
    }

    @Test
    public void backdated_is_ignored() {
        // given: current (initial) value
        PointValueTime init = getOldValue();
        rt.setPointValue(init, null);

        // and a backdated value with earlier timestamp (same numeric value)
        PointValueTime back = new PointValueTime(
                MangoValue.objectToValue(init.getDoubleValue()),
                init.getTime() - 100
        );

        // when
        rt.setPointValue(back, null);

        // then: only the initial (current-time) value should be stored
        List<PointValueTime> values = rt.getLatestPointValues(getDefaultCacheSize());
        assertEquals("exactly one value expected", 1, values.size());
        assertEquals("timestamp should be that of the initial value",
                init.getTime(), values.get(0).getTime());
        assertEquals("value should equal initial value",
                init.getValue(), values.get(0).getValue());
    }
}
