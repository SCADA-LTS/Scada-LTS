package com.serotonin.mango.rt.dataImage.datapointrt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        com.serotonin.mango.rt.dataImage.datapointrt.config.DataPointRtToleranceAbsoluteTest.class,
        com.serotonin.mango.rt.dataImage.datapointrt.config.DataPointRtTolerancePercentStandaloneTest.class,
        com.serotonin.mango.rt.dataImage.datapointrt.config.DataPointRtBackdatedIgnoreTest.class,
})
public class DataPointRtToleranceTestsSuite { }
