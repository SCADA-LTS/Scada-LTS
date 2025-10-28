package org.scada_lts.web.beans.validation.xss;

import org.scada_lts.web.beans.validation.AbstractConstraintValidator;
import org.scada_lts.web.beans.validation.ScadaValidator;

import java.util.Map;

public class XssConstraintMapValidator extends AbstractConstraintValidator<XssProtect, Map<String, String>> {

    @Override
    public void validate(Map<String, String> map) throws Exception {
        ScadaValidator<String> validator = new XssValidator();
        for(Map.Entry<String, String> value: map.entrySet()) {
            validator.validate(value.getKey());
            validator.validate(value.getValue());
        }
    }
}
