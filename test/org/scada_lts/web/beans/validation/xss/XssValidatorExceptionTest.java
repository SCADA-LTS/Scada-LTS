package org.scada_lts.web.beans.validation.xss;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.web.beans.validation.ScadaValidator;
import org.scada_lts.web.beans.validation.ScadaValidatorException;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class XssValidatorExceptionTest {

    @Parameterized.Parameters(name = "{index}: input: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null},
                {""},
                {"<script>alert(1)</script>"},
                {"<a href=\"javascript:alert(1)\">Link</a>"},
                {"<div onclick=\"alert(1)\">Click me</div>"},
                {"body { background-image: url('\"><img src=x onerror=alert(document.location)>'); }"},
                {"div { content: \"<script>alert('XSS')</script>\"; }"},
                {"h1 { font-family: \"<img src=x onerror=alert('XSS')>\"; }"},
                {"@import url(\"javascript:alert('XSS')\");"},
                {"div { /* comment: <img src=x onerror=alert('XSS')> */ }"},
                {"span { content: '\"><script>alert(1)</script>'; }"},
                {"h2 { color: expression(alert('XSS')); }"},
                {"\"><img src=x onerror=alert(document.location)>"},
                {"<img src='x' onerror='alert(1)'>"},
                {"<input type=\"text\" value=\"\" onfocus=\"alert(1)\">"},
                {"<iframe src=\"javascript:alert(1)\"></iframe>"},
                {"<form action=\"javascript:alert(1)\"><input type=\"submit\"></form>"},
                {"<object data=\"javascript:alert(1)\"></object>"},
                {"<embed src=\"javascript:alert(1)\">"},
                {"<base href=\"javascript:alert(1)//\">"},
                {"<svg onload=\"alert(1)\">"},
                {"<svg><script>alert(1)</script></svg>"},
                {"<math><a xlink:href=\"javascript:alert(1)\">XSS</a></math>"},
                {"<img src=x onerror=\"alert(String.fromCharCode(88,83,83))\">"},
                {"<b onmouseover=\"alert(1)\">XSS</b>"},
                {"<video><source onerror=\"javascript:alert(1)\"></video>"},
                {"<details open ontoggle=\"alert(1)\">"},
                {"<input onfocus=\"alert('XSS')\">"},
                {"<div style=\"width: expression(alert('XSS'))\">"},
                {"<meta http-equiv=\"refresh\" content=\"0;url=javascript:alert(1)\">"},
                {"<link rel=\"stylesheet\" href=\"javascript:alert(1)\">"},
                {"<textarea onfocus=\"alert(1)\"></textarea>"},
                {"<a href=\"//example.com\" onclick=\"alert(1)\">Link</a>"},
                {"<button onclick=\"alert(1)\">Click me</button>"},
                {"<div style=\"background-image: url(javascript:alert(1))\">XSS</div>"},
                {"<audio src=\"javascript:alert(1)\"></audio>"},
                {"<marquee onstart=\"alert(1)\">XSS</marquee>"},
                {"<keygen autofocus onfocus=\"alert(1)\">"},
                {"<command onclick=\"alert(1)\">Click me</command>"},
                {"<img src=\"https://example.com/image.jpg\" alt=\"Example Image\" width=\"600\" height=\"400\" border=\"0\" />"},
                {"<img src=\"http://example.com/image.jpg\" alt=\"Example Image\" width=\"600\" height=\"400\" border=\"0\" />"},
                {"><img src=x onerror=alert(document.location)>"},
        });
    }

    private final String input;
    private final ScadaValidator<String> validator;

    public XssValidatorExceptionTest(String input) {
        this.input = input;
        this.validator = new XssValidator();
    }

    @Test(expected = XssValidatorException.class)
    public void when_isInvalidXss() throws ScadaValidatorException {
        validator.validate(input);
    }
}
