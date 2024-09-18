package org.scada_lts.web.mvc.api.css.validation.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CssValidatorTest {

    @Parameterized.Parameters(name = "{index}: CSS: {0}, valid: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // Valid CSS
                {"body { color: red; }", true},
                {"h1 { font-size: 20px; }", true},
                {"#id { background-color: #fff; }", true},
                {"div { margin: 0 auto; padding: 10px; }", true},
                {".class { border: 1px solid black; }", true},
                {"p { line-height: 1.5; }", true},

                // Invalid CSS
                {"body { color: ; }", false},
                {"#id { background-color: #ggg; }", false},
                {"div { margin; }", false},
                {"@media screen and (max-width: 600px) { h1 { font-size: 30px }", false},
                {"body { color red; }", false},
        });
    }

    private final String css;
    private final boolean isValid;

    public CssValidatorTest(String css, boolean isValid) {
        this.css = css;
        this.isValid = isValid;
    }

    @Test
    public void when_isValidCss() {
        CssValidator validator = new SacCssValidator();
        try {
            validator.validate(css);
            Assert.assertTrue(isValid);
        } catch (CssException e) {
            Assert.assertFalse(isValid);
        }
    }
}
