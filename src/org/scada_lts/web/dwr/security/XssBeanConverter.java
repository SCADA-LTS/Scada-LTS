package org.scada_lts.web.dwr.security;

import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.extend.*;

import static org.scada_lts.web.dwr.security.utils.XssBeanConverterUtils.convertObjectEscaped;
import static org.scada_lts.web.dwr.security.utils.XssBeanConverterUtils.convertObjectUnescaped;

public class XssBeanConverter extends BeanConverter {

    @Override
    public OutboundVariable convertOutbound(Object value, OutboundContext outctx) throws MarshallException {
        convertObjectEscaped(value);
        return super.convertOutbound(value, outctx);
    }

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        convertObjectUnescaped(iv.getValue());
        return super.convertInbound(paramType, iv, inctx);
    }

}
