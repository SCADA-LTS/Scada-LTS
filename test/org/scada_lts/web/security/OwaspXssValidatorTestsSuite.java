package org.scada_lts.web.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        OwaspXssValidatorTest.class,
        OwaspXssValidatorExceptionTest.class
})
public class OwaspXssValidatorTestsSuite {
}