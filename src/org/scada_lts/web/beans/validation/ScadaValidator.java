package org.scada_lts.web.beans.validation;

public interface ScadaValidator<T> {

    void validate(T object) throws ScadaValidatorException;

}
