package org.scada_lts.web.mvc.api.css;

import com.steadystate.css.parser.SACParserCSS3;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.Parser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.StringReader;

class CustomErrorHandler implements ErrorHandler {
    private StringBuilder errorMessages = new StringBuilder();

    @Override
    public void warning(CSSParseException exception) {
        errorMessages.append("Warning: ").append(exception.getMessage()).append("\n");
    }

    @Override
    public void error(CSSParseException exception) {
        errorMessages.append("Error: ").append(exception.getMessage()).append("\n");
    }

    @Override
    public void fatalError(CSSParseException exception) {
        errorMessages.append("Fatal Error: ").append(exception.getMessage()).append("\n");
    }

    public boolean hasErrors() {
        return errorMessages.length() > 0;
    }

    public String getErrorMessages() {
        return errorMessages.toString();
    }
}

public class CssValidator implements ConstraintValidator<CssValid, String> {

    @Override
    public void initialize(CssValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            Parser parser = new SACParserCSS3();
            InputSource inputSource = new InputSource(new StringReader(value));

            CustomErrorHandler errorHandler = new CustomErrorHandler();
            parser.setErrorHandler(errorHandler);

            parser.parseStyleSheet(inputSource);

            if (errorHandler.hasErrors()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(errorHandler.getErrorMessages()).addConstraintViolation();
                return false;
            }

            return true;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Unexpected error: " + e.getMessage()).addConstraintViolation();
            return false;
        }
    }
}
