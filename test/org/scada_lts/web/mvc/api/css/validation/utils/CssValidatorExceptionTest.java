package org.scada_lts.web.mvc.api.css.validation.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CssValidatorExceptionTest {

    @Parameterized.Parameters(name = "{index}: CSS: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"body { color: ; }"},
                {"#id { background-color: #ggg; }"},
                {"div { margin; }"},
                {"@media screen and (max-width: 600px) { h1 { font-size: 30px }"},
                {"body { color red; }"},
                {"\"><img src=x onerror=alert(document.location)>"},
        });
    }

    private final String css;

    public CssValidatorExceptionTest(String css) {
        this.css = css;
    }

    @Test
    public void when_isInvalidCss() {
        CssValidator validator = new W3cCssValidator();
        try {
            validator.validate(css);
            Assert.fail("Invalid CSS did not throw an exception");
        } catch (CssException e) {
            Assert.assertTrue(true);
        }
    }
}
