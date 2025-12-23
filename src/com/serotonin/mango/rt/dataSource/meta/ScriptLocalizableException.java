package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.mango.Common;
import com.serotonin.web.i18n.LocalizableMessage;

import javax.script.ScriptException;

public class ScriptLocalizableException extends ScriptException {
    /**
     * Creates a ScriptLocalizableException whose message is derived from the given LocalizableMessage.
     *
     * @param localizableMessage the LocalizableMessage used to produce the exception's localized message
     */
    public ScriptLocalizableException(LocalizableMessage localizableMessage) {
        super(localizableMessage.getLocalizedMessage(Common.getBundle()));
    }

    /**
     * Create a ScriptException whose message is the localized text from the provided LocalizableMessage
     * and that is associated with the given source file and line number.
     *
     * @param localizableMessage the message to localize for the exception text
     * @param fileName           the name of the source file where the error occurred
     * @param lineNumber         the line number in the source file where the error occurred
     */
    public ScriptLocalizableException(LocalizableMessage localizableMessage, String fileName, int lineNumber) {
        super(localizableMessage.getLocalizedMessage(Common.getBundle()), fileName, lineNumber);
    }

    /**
     * Create an exception whose message is produced from a LocalizableMessage and that includes source location information.
     *
     * @param localizableMessage the message to localize for the exception message
     * @param fileName           the source file name associated with the exception (may be null)
     * @param lineNumber         the line number in the source file where the error occurred
     * @param columnNumber       the column number in the source file where the error occurred
     */
    public ScriptLocalizableException(LocalizableMessage localizableMessage, String fileName, int lineNumber, int columnNumber) {
        super(localizableMessage.getLocalizedMessage(Common.getBundle()), fileName, lineNumber, columnNumber);
    }
}