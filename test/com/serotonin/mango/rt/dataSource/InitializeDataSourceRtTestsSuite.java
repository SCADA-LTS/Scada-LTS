package com.serotonin.mango.rt.dataSource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InitializeDataSourceRtTest.class,
        InitializeWithErrorsDataSourceRtTest.class
})
public class InitializeDataSourceRtTestsSuite {
}
