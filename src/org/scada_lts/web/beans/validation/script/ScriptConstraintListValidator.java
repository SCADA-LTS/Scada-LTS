package org.scada_lts.web.beans.validation.script;

import org.scada_lts.web.beans.validation.AbstractConstraintValidator;
import org.scada_lts.web.beans.validation.ScadaValidator;

public class ScriptConstraintListValidator extends AbstractConstraintValidator<ScriptProtect, String> {

    /**
     * Validates the given script string against the script protection rules.
     *
     * @param value the script content to validate
     * @throws Exception if the script fails validation
     */
    @Override
    public void validate(String value) throws Exception {
        ScadaValidator<String> validator = new ScriptValidator();
        validator.validate(value);
    }
}