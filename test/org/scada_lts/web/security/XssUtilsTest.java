package org.scada_lts.web.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class XssUtilsTest {

    private final String input;
    private final boolean expectedResult;

    public XssUtilsTest(String input, boolean expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                // Test for a legitimate Query
                {"validUser", true},
                {"123", true},

                // Tests for queries containing banned patterns.
                {"javascript:alert(1)", false},
                {"onerror=alert(1)", false},
                {"<script>alert(1)</script>", false},
                {"onload=alert(1)", false},
                {"onmouseover=alert(1)", false},
                {"%6A%61%76%61%73%63%72%69%70%74", false},

                // Borderline cases
                {"=&", false},
                {"a”.repeat(33)", false},
                {"invalid!key", false},
                {"validKey", true},
                {"onerror=alert(1)&onload=alert(1)", false},
                {"!key=value", false},

        });
    }

    @Test
    public void testValidate() {
        assertEquals("Validation failed for input: " + input, expectedResult, XssUtils.validate(input));
    }
}
