package org.scada_lts.web.beans.validation.script;

import org.scada_lts.web.beans.validation.ScadaValidator;
import org.scada_lts.web.beans.validation.ScadaValidatorException;

public class ScriptValidator implements ScadaValidator<String> {

    @Override
    public void validate(String input) throws ScadaValidatorException {
        if(!ScriptValidatorUtils.validate(input)) {
            throw new ScadaValidatorException("Script is invalid");
        }
    }
}
