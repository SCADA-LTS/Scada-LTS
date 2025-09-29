package com.serotonin.mango.web.dwr.security;

import java.beans.PropertyEditorSupport;

/**
 * PropertyEditor that escapes HTML to mitigate XSS on inbound form/request binding.
 * Applied globally via @ControllerAdvice binder.
 */
public class SanitizingStringEditor extends PropertyEditorSupport {

    private final boolean escape;

    public SanitizingStringEditor(boolean escape) {
        this.escape = escape;
    }

    @Override
    public void setAsText(String text) {
        if (!escape) {
            setValue(text);
        } else {
            setValue(XssSanitizer.escape(text));
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return value == null ? null : value.toString();
    }
}
