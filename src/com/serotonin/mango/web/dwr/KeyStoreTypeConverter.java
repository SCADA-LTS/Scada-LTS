package com.serotonin.mango.web.dwr;

import br.org.scadabr.KeyStoreType;
import org.directwebremoting.convert.EnumConverter;
import org.directwebremoting.extend.*;
import org.directwebremoting.util.LocalUtil;


public class KeyStoreTypeConverter extends EnumConverter {

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        String value = LocalUtil.decode(iv.getValue());
        try {
            return KeyStoreType.valueOf(value);
        } catch (Exception var9) {
            throw new MarshallException(paramType, var9);
        }
    }

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) {
        KeyStoreType type = (KeyStoreType)data;
        String name = type.name();
        return super.convertOutbound(name, outctx);
    }
}
