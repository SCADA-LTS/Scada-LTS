package com.serotonin.mango.web.dwr;

import org.directwebremoting.convert.EnumConverter;
import org.directwebremoting.extend.*;
import org.directwebremoting.util.LocalUtil;
import org.scada_lts.ds.messaging.protocol.ProtocolVersion;


public class ProtocolVersionConverter extends EnumConverter {

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        String value = LocalUtil.decode(iv.getValue());
        try {
            return ProtocolVersion.protocolVersion(value);
        } catch (Exception var9) {
            throw new MarshallException(paramType, var9);
        }
    }

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) {
        ProtocolVersion protocolVersion = (ProtocolVersion)data;
        String protocolVersionName = protocolVersion.getName();
        return super.convertOutbound(protocolVersionName, outctx);
    }
}
