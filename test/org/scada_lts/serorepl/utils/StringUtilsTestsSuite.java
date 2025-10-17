package org.scada_lts.serorepl.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
    TruncateStringUtilsTest.class,
    TruncateStringUtilsExceptionTest.class,
})

public class StringUtilsTestsSuite { }