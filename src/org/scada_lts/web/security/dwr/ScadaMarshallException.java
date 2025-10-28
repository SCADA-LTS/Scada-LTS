package org.scada_lts.web.security.dwr;

import org.directwebremoting.extend.MarshallException;

public class ScadaMarshallException extends MarshallException {

    public ScadaMarshallException(Class paramType) {
        super(paramType);
    }

    public ScadaMarshallException(Class paramType, Throwable ex) {
        super(paramType, ex);
    }

    public ScadaMarshallException(Class paramType, String message) {
        super(paramType, message);
    }
}
