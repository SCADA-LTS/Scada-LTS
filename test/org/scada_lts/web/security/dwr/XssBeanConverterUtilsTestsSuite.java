package org.scada_lts.web.security.dwr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        XssBeanConverterUtilsTest.class,
        XssBeanConverterUtilsExceptionTest.class,
        XssBeanConverterUtilsEscapedTest.class
})
public class XssBeanConverterUtilsTestsSuite {
}
