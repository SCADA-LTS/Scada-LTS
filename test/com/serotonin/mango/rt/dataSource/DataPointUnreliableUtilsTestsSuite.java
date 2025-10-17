package com.serotonin.mango.rt.dataSource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CyclicGetRunningMetaDataPointsTest.class,
        GetRunningMetaDataPointsTest.class,
        SetDataPointUnreliableUtilsTest.class,
        ResetDataPointUnreliableUtilsTest.class
})
public class DataPointUnreliableUtilsTestsSuite {
}
