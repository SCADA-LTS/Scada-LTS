package org.scada_lts.web.beans.validation.xss;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        XssValidatorTest.class,
        XssValidatorExceptionTest.class
})
public class OwaspXssValidatorTestsSuite {
}