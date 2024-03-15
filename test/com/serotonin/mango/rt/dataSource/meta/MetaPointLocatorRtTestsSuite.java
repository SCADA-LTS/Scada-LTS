package com.serotonin.mango.rt.dataSource.meta;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        MetaPointLocatorRtExecutionDelayTest.class,
        MetaPointLocatorRtUpdateEventCronAndStartOfTest.class,
        MetaPointLocatorRtUpdateEventChangeAndUpdateContextTest.class,
        MetaPointLocatorRtInitializeTest.class,
})

public class MetaPointLocatorRtTestsSuite {

}