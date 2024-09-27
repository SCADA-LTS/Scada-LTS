package org.scada_lts.web.beans.validation.xss;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.web.beans.validation.ScadaValidatorException;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class XssValidatorTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"<b>Bold</b>"},
                {"Hello, World!"},
                {""},
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
                {"<img src=\"https://example.com/image.jpg\" alt=\"Example Image\" width=\"600\" height=\"400\" border=\"0\" />"},
                {"<img src=\"http://example.com/image.jpg\" alt=\"Example Image\" width=\"600\" height=\"400\" border=\"0\" />"}
        });
    }

    private final String input;
    private final OwaspXssValidator validator;

    public XssValidatorTest(String input) {
        this.input = input;
        this.validator = new OwaspXssValidator();
    }

    @Test
    public void when_isInvalidXss() throws ScadaValidatorException {
        validator.validate(input);
    }
}
