package com.serotonin.mango.web.dwr;

import com.serotonin.mango.vo.BACnetEngineeringUnit;
import org.directwebremoting.convert.EnumConverter;
import org.directwebremoting.extend.*;
import org.directwebremoting.util.LocalUtil;



public class BACnetEngineeringUnitConverter extends EnumConverter {

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx)
            throws MarshallException {
        String value = LocalUtil.decode(iv.getValue());
        try {
            int code = Integer.parseInt(value);
            return BACnetEngineeringUnit.fromCode(code);
        }
        catch (NumberFormatException e) {
            return BACnetEngineeringUnit.fromLabel(value);
        }
    }

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) {
        if (!(data instanceof BACnetEngineeringUnit)) {
            return super.convertOutbound(data, outctx);
        }
        BACnetEngineeringUnit eu = (BACnetEngineeringUnit) data;
        String codeAsString = String.valueOf(eu.getCode());
        return super.convertOutbound(codeAsString, outctx);
    }
}
