package com.serotonin.mango.vo;

import com.serotonin.web.dwr.DwrResponseI18n;

public interface ScadaValidation {
    void validate(DwrResponseI18n response);
    void validateForCreate(DwrResponseI18n response);
}
