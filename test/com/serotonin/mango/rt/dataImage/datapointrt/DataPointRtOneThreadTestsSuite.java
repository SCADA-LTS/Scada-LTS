package com.serotonin.mango.rt.dataImage.datapointrt;


import com.serotonin.mango.rt.dataImage.datapointrt.tests.DataPointRtTest;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.DataPointRtToleranceTest;
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

        DataPointSyncRtTest.class,
        DataPointSyncRtToleranceTest.class,

        DataPointNonSyncRtTest.class,
        DataPointNonSyncRtToleranceTest.class,
})
public class DataPointRtOneThreadTestsSuite {}
