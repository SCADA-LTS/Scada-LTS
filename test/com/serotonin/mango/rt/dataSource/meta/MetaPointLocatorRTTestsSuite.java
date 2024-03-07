package com.serotonin.mango.rt.dataSource.meta;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        MetaPointLocatorRTExecutionDelayTimeoutRunTest.class,
        MetaPointLocatorRTScheduledUpdateTimeoutRunTest.class,
        MetaPointLocatorRTPointChangedPointUpdatedTest.class,
        MetaPointLocatorRTInitializeTest.class,
})

public class MetaPointLocatorRTTestsSuite {

}