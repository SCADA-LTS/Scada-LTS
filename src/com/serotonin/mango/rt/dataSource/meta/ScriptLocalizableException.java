package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.mango.Common;
import com.serotonin.web.i18n.LocalizableMessage;

import javax.script.ScriptException;

public class ScriptLocalizableException extends ScriptException {
    public ScriptLocalizableException(LocalizableMessage localizableMessage) {
        super(localizableMessage.getLocalizedMessage(Common.getBundle()));
    }

    public ScriptLocalizableException(LocalizableMessage localizableMessage, String fileName, int lineNumber) {
        super(localizableMessage.getLocalizedMessage(Common.getBundle()), fileName, lineNumber);
    }

    public ScriptLocalizableException(LocalizableMessage localizableMessage, String fileName, int lineNumber, int columnNumber) {
        super(localizableMessage.getLocalizedMessage(Common.getBundle()), fileName, lineNumber, columnNumber);
    }
}
