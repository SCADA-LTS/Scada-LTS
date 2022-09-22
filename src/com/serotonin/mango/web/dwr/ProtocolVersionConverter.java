package com.serotonin.mango.web.dwr;

import org.directwebremoting.convert.EnumConverter;
import org.directwebremoting.extend.*;
import org.directwebremoting.util.LocalUtil;
import org.scada_lts.ds.messaging.protocol.ProtocolVersion;

import java.lang.reflect.Method;

public class ProtocolVersionConverter extends EnumConverter {

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        String value = LocalUtil.decode(iv.getValue());
        try {
            Method getter = paramType.getMethod("protocolVersion", String.class);
            return getter.invoke(paramType, value);
        } catch (NoSuchMethodException var8) {
            throw new MarshallException(paramType);
        } catch (Exception var9) {
            throw new MarshallException(paramType, var9);
        }
    }

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) {
        ProtocolVersion lm = (ProtocolVersion)data;
        String s = lm.getName();
        return super.convertOutbound(s, outctx);
    }
}
