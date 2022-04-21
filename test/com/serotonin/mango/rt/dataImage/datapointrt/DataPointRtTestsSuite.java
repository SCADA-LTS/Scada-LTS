package com.serotonin.mango.rt.dataImage.datapointrt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        /*PointValueStateTest.class,
        DataPointRtOneThreadTestsSuite.class,*/
        DataPointRtMultiThreadTestsSuite.class
})
public class DataPointRtTestsSuite {}
