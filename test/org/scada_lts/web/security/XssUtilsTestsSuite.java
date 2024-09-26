package org.scada_lts.web.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        XssUtilsTest.class,
        OwaspXssValidatorTest.class,
        OwaspXssValidatorExceptionTest.class
})
public class XssUtilsTestsSuite {
}