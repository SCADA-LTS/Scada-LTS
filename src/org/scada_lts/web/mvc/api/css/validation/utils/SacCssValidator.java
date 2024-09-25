package org.scada_lts.web.mvc.api.css.validation.utils;

import com.steadystate.css.parser.SACParserCSS3;
import org.w3c.css.sac.*;

import java.io.IOException;
import java.io.StringReader;

public final class SacCssValidator implements CssValidator {

    private final Parser parser;

    public SacCssValidator() {
        this.parser = createParser();
    }

    @Override
    public void validate(String style) throws CustomCssException {
        try {
            validateStyle(parser, style);
        } catch (Exception e) {
            throw new CustomCssException(e.getMessage(), e);
        }
    }

    private static void validateStyle(Parser parser, String value) throws CSSParseException, IOException {
        try (StringReader stringReader = new StringReader(value)) {
            InputSource inputSource = new InputSource(stringReader);
            parser.parseStyleSheet(inputSource);
        }
    }

    private static Parser createParser() {
        Parser parser = new SACParserCSS3();
        parser.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(CSSParseException e) throws CSSException {
                throw e;
            }

            @Override
            public void error(CSSParseException e) throws CSSException {
                throw e;
            }

            @Override
            public void fatalError(CSSParseException e) throws CSSException {
                throw e;
            }
        });
        return parser;
    }
}
