package org.scada_lts.web.dwr.security;

import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.convert.StringConverter;
import org.directwebremoting.extend.*;

import static org.scada_lts.web.dwr.security.utils.XssBeanConverterUtils.convertObjectEscaped;
import static org.scada_lts.web.dwr.security.utils.XssBeanConverterUtils.convertObjectUnescaped;

public class XssLocalizableMessageConverter  extends StringConverter {
    public XssLocalizableMessageConverter() {
    }

    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) throws MarshallException {
        WebContext webctx = WebContextFactory.get();
        LocalizableMessage lm = (LocalizableMessage)data;
        convertObjectEscaped(data);
        String s = lm.getLocalizedMessage(I18NUtils.getBundle(webctx.getHttpServletRequest()));
        return super.convertOutbound(s, outctx);
    }

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        convertObjectUnescaped(iv.getValue());
        return super.convertInbound(paramType, iv, inctx);
    }
}
