package org.scada_lts.web.beans.validation.xss;

import org.scada_lts.web.beans.validation.ScadaValidator;

import static org.scada_lts.web.security.XssUtils.validateHttpBody;

public class XssValidator implements ScadaValidator<String> {

    @Override
    public void validate(String input) throws XssValidatorException {
        if(!validateHttpBody(input)) {
            throw new XssValidatorException("Potential XSS attack detected");
        }
    }
}
