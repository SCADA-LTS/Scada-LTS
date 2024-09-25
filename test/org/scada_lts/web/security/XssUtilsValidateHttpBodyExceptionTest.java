package org.scada_lts.web.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class XssUtilsValidateHttpBodyExceptionTest {

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {null},
                {"<script>alert(1)</script>"},
                {"<a href=\"javascript:alert(1)\">Link</a>"},
                {"<div onclick=\"alert(1)\">Click me</div>"},
                {"body { background-image: url('\"><img src=x onerror=alert(document.location)>'); }"},
                {"div { content: \"<script>alert('XSS')</script>\"; }"},
                {"h1 { font-family: \"<img src=x onerror=alert('XSS')>\"; }"},
                {"@import url(\"javascript:alert('XSS')\");"},
                {"div { /* comment: <img src=x onerror=alert('XSS')> */ }"},
                {"span { content: '\"><script>alert(1)</script>'; }"},
                {"h2 { color: expression(alert('XSS')); }"}
        });
    }

    private final String input;
    private final OwaspXssValidator owaspXssValidator = new OwaspXssValidator();

    public XssUtilsValidateHttpBodyExceptionTest(String input) {
        this.input = input;
    }

    @Test(expected = XssValidatorException.class)
    public void testValidateHttpBodyException() throws XssValidatorException {
        owaspXssValidator.validate(input);
    }
}
