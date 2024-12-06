package com.serotonin.mango.web.dwr;

import br.org.scadabr.vo.dataSource.opcua.OpcUaDataType;
import org.apache.plc4x.java.opcua.security.SecurityPolicy;
import org.directwebremoting.convert.EnumConverter;
import org.directwebremoting.extend.*;
import org.directwebremoting.util.LocalUtil;


public class OpcUaDataTypeConverter extends EnumConverter {

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        String value = LocalUtil.decode(iv.getValue());
        try {
            return OpcUaDataType.valueOf(value);
        } catch (Exception var9) {
            throw new MarshallException(paramType, var9);
        }
    }

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) {
        OpcUaDataType type = (OpcUaDataType)data;
        String name = type.name();
        return super.convertOutbound(name, outctx);
    }
}
