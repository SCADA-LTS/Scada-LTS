package org.scada_lts.web.beans.validation.xss;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.web.beans.validation.ScadaValidator;
import org.scada_lts.web.beans.validation.ScadaValidatorException;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class XssValidatorTest {

    @Parameterized.Parameters(name = "{index}: input: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"<b>Bold</b>"},
                {"Hello, World!"},
                {"<i>Italic text</i>"},
                {"<u>Underlined</u>"},
                {"<strong>Strong text</strong>"},
                {"<em>Emphasized text</em>"},
                {"<p>Paragraph with <b>bold</b> and <i>italic</i></p>"},
                {"<ul><li>Item 1</li><li>Item 2</li></ul>"},
                {"<ol><li>First</li><li>Second</li></ol>"},
                {"body { font-size: 14px; }"},
                {"h1 { font-weight: bold; }"},
                {"p { margin: 0; padding: 0; }"},
                {"a > 12"},
                {"a>12"},
                {"a> 12"},
                {"a >12"},
                {"a<12"},
                {"a < 12"},
                {"a< 12"},
                {"a <12"},
                {"'a'"},
                {"\"a\""},
        });
    }

    private final String input;
    private final ScadaValidator<String> validator;

    public XssValidatorTest(String input) {
        this.input = input;
        this.validator = new XssValidator();
    }

    @Test
    public void when_isInvalidXss() throws ScadaValidatorException {
        validator.validate(input);
    }
}