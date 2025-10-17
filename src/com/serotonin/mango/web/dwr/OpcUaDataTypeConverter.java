package com.serotonin.mango.web.dwr;

import org.directwebremoting.convert.EnumConverter;
import org.directwebremoting.extend.*;
import org.directwebremoting.util.LocalUtil;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaDataType;


public class OpcUaDataTypeConverter extends EnumConverter {

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        String value = LocalUtil.decode(iv.getValue());
        try {
            return OpcUaDataType.valueByNameOf(value);
        } catch (Exception var9) {
            throw new MarshallException(paramType, var9);
        }
    }

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) {
        OpcUaDataType dataType = (OpcUaDataType)data;
        String protocolVersionName = dataType.getName();
        return super.convertOutbound(protocolVersionName, outctx);
    }
}
