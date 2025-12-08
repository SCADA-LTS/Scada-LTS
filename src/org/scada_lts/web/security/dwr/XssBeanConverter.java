package org.scada_lts.web.security.dwr;

import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.extend.*;

import static org.scada_lts.web.security.dwr.XssBeanConverterUtils.convertObjectEscaped;
import static org.scada_lts.web.security.dwr.XssBeanConverterUtils.convertObjectUnescaped;

public class XssBeanConverter extends BeanConverter {

    @Override
    public OutboundVariable convertOutbound(Object value, OutboundContext outctx) throws MarshallException {
        Object escaped = convertObjectEscaped(value);
        return super.convertOutbound(escaped, outctx);
    }

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        Object converted = super.convertInbound(paramType, iv, inctx);
        return convertObjectUnescaped(converted);
    }

}
