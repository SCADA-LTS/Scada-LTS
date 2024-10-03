package org.scada_lts.web.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        XssUtilsTest.class,
        BodyXssUtilsTest.class
})
public class XssUtilsTestsSuite {
}