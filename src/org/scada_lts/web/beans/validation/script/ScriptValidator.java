package org.scada_lts.web.beans.validation.script;

import org.scada_lts.web.beans.validation.ScadaValidator;
import org.scada_lts.web.beans.validation.ScadaValidatorException;

public class ScriptValidator implements ScadaValidator<String> {

    /**
     * Validates the provided script source and throws an exception when it is invalid.
     *
     * @param input the script source to validate
     * @throws ScadaValidatorException if the script is invalid
     */
    @Override
    public void validate(String input) throws ScadaValidatorException {
        if(!ScriptValidatorUtils.validate(input)) {
            throw new ScadaValidatorException("Script is invalid");
        }
    }
}