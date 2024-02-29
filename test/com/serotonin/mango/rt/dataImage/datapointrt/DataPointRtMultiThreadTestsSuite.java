package com.serotonin.mango.rt.dataImage.datapointrt;

import com.serotonin.mango.rt.dataImage.datapointrt.tests.*;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.sync.*;
import com.serotonin.mango.rt.dataImage.datapointrt.tests.nonsync.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        /*DataPointRtMultiTest.class,
        DataPointRtBinaryMultiTest.class,
        DataPointRtNumericMultiTest.class,
        DataPointRtAlphanumericMultiTest.class,
        DataPointRtMultistateMultiTest.class,
        DataPointRtToleranceMultiTest.class,
        DataPointRtNumericSameMultiTest.class,*/

        DataPointSyncRtMultiTest.class,
        DataPointSyncRtBinaryMultiTest.class,
        DataPointSyncRtNumericMultiTest.class,
        DataPointSyncRtAlphanumericMultiTest.class,
        DataPointSyncRtMultistateMultiTest.class,
        DataPointSyncRtToleranceMultiTest.class,
        DataPointSyncRtNumericSameMultiTest.class,

        /*DataPointNonSyncRtMultiTest.class,
        DataPointNonSyncRtBinaryMultiTest.class,
        DataPointNonSyncRtNumericMultiTest.class,
        DataPointNonSyncRtAlphanumericMultiTest.class,
        DataPointNonSyncRtMultistateMultiTest.class,
        DataPointNonSyncRtToleranceMultiTest.class,
        DataPointNonSyncRtNumericSameMultiTest.class,*/
})
public class DataPointRtMultiThreadTestsSuite {}
