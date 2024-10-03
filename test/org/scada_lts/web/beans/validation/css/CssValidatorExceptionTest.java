package org.scada_lts.web.beans.validation.css;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.web.beans.validation.ScadaValidator;
import org.scada_lts.web.beans.validation.ScadaValidatorException;

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
    public final ScadaValidator<String> validator;

    public CssValidatorExceptionTest(String css) {
        this.css = css;
        this.validator = new SacCssValidator();
    }

    @Test(expected = CssValidatorException.class)
    public void when_isInvalidCss() throws ScadaValidatorException {
        validator.validate(css);
    }
}
