package org.scada_lts.web.mvc.api.css.validation.utils;

import org.junit.Assert;
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
        });
    }

    private final String css;

    public CssValidatorTest(String css) {
        this.css = css;
    }

    @Test
    public void when_isValidCss() {
        CssValidator validator = new SacCssValidator();
        try {
            validator.validate(css);
            Assert.assertTrue(true);
        } catch (CssException e) {
            Assert.fail("Valid CSS threw an exception");
        }
    }
}
