package org.scada_lts.web.mvc.api.css.validation.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CssValidatorTest {

    @Parameterized.Parameters(name = "{index}: CSS: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"body { color: red; }"},
                {"h1 { font-size: 20px; }"},
                {"#id { background-color: #fff; }"},
                {"div { margin: 0 auto; padding: 10px; }"},
                {".class { border: 1px solid black; }"},
                {"p { line-height: 1.5; }"},
                {"body { background-image: url('\"><img src=x onerror=alert(document.location)>'); }"},
                {"div { content: \"<script>alert('XSS')</script>\"; }"},
                {"h1 { font-family: \"<img src=x onerror=alert('XSS')>\"; }"},
                {"p { margin: 10px; background-color: red; }"},
                {"@import url(\"javascript:alert('XSS')\");"},
                {"div { /* comment: <img src=x onerror=alert('XSS')> */ }"},
                {"span { content: '\"><script>alert(1)</script>'; }"},
                {"h2 { color: expression(alert('XSS')); }"}
        });
    }

    private final String css;
    public final CssValidator validator;

    public CssValidatorTest(String css) {
        this.css = css;
        this.validator = new SacCssValidator();
    }

    @Test
    public void when_isValidCss() throws CustomCssException {
        validator.validate(css);
    }
}
