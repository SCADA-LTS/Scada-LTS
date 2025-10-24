package org.scada_lts.web.beans.validation.xss;

import org.scada_lts.web.beans.validation.AbstractConstraintValidator;
import org.scada_lts.web.beans.validation.ScadaValidator;

import java.util.List;

public class XssConstraintListValidator extends AbstractConstraintValidator<XssProtect, List<String>> {

    @Override
    public void validate(List<String> list) throws Exception {
        ScadaValidator<String> validator = new XssValidator();
        for(String value: list) {
            validator.validate(value);
        }
    }
}
