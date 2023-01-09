package com.serotonin.mango.rt.dataImage.datapointrt;


import com.serotonin.mango.rt.dataImage.datapointrt.tests.DataPointRtNumericIntervalTest;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.DataPointRtTest;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.nonsync.DataPointNonSyncRtNumericIntervalTest;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.nonsync.DataPointNonSyncRtTest;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.nonsync.DataPointNonSyncRtToleranceTest;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.sync.DataPointSyncRtTest;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.sync.DataPointSyncRtToleranceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DataPointRtTest.class,
        //DataPointRtToleranceTest.class,
        DataPointRtNumericIntervalTest.class,

        DataPointSyncRtTest.class,
        DataPointSyncRtToleranceTest.class,

        DataPointNonSyncRtTest.class,
        DataPointNonSyncRtToleranceTest.class,

        DataPointNonSyncRtNumericIntervalTest.class
})
public class DataPointRtOneThreadTestsSuite {}
