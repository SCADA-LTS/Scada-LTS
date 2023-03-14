package com.serotonin.mango.rt.dataImage.datapointrt.tests;

import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointSyncMode;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.datapointrt.config.AbstractDataPointRtTest;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.ChangeTypeVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class DataPointRtNumericIntervalTest extends AbstractDataPointRtTest {

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

    @Override
    protected List<PointValueTime> getPointValuesDaoTestExpected() {
        return Collections.emptyList();
    }

    @Override
    protected List<PointValueTime> getPointValuesWithUserDaoTestExpected() {
        return Collections.emptyList();
    }

    @Override
    protected DataPointVO createDataPoint(int defaultCacheSize, double tolerance, String startValue, int dataTypeId, DataSourceVO<?> dataSourceVO) {
        VirtualPointLocatorVO virtualPointLocatorVO = new VirtualPointLocatorVO();
        virtualPointLocatorVO.setDataTypeId(dataTypeId);
        virtualPointLocatorVO.setChangeTypeId(ChangeTypeVO.Types.NO_CHANGE);
        virtualPointLocatorVO.getNoChange().setStartValue(startValue);

        DataPointVO dataPointVO = new DataPointVO(DataPointVO.LoggingTypes.INTERVAL);
        dataPointVO.setId(321);
        dataPointVO.setName("test_dp");
        dataPointVO.setXid("test_dp_xid");
        dataPointVO.setDefaultCacheSize(defaultCacheSize);
        dataPointVO.setTolerance(tolerance);
        dataPointVO.setPointLocator(virtualPointLocatorVO);
        dataPointVO.setEventDetectors(new ArrayList<>());
        dataPointVO.setEnabled(true);
        dataPointVO.setDataSourceId(dataSourceVO.getId());
        dataPointVO.setDataSourceName(dataSourceVO.getName());
        dataPointVO.setDeviceName(dataSourceVO.getName());
        dataPointVO.setIntervalLoggingPeriodType(Common.TimePeriods.MINUTES);
        dataPointVO.setIntervalLoggingPeriod(10000000);
        dataPointVO.setIntervalLoggingType(DataPointVO.IntervalLoggingTypes.AVERAGE);
        return dataPointVO;
    }

    public DataPointRtNumericIntervalTest(Object value1, Object value2, Object value3) {
        super(DataPointSyncMode.LOW, value1, value2, value3, DataTypes.NUMERIC, DataTypes.CODES.getCode(DataTypes.NUMERIC), "123.0");
    }
}