package org.scada_lts.web.dwr.security;

import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.convert.StringConverter;
import org.directwebremoting.extend.MarshallException;
import org.directwebremoting.extend.OutboundContext;
import org.directwebremoting.extend.OutboundVariable;
import org.scada_lts.web.security.XssProtectUtils;

public class XssLocalizableMessageConverter extends StringConverter {

    @Override
    public OutboundVariable convertOutbound(Object data, OutboundContext outctx) throws MarshallException {
        if (data == null) {
            return super.convertOutbound("", outctx);
        }
        LocalizableMessage lm = (LocalizableMessage) data;

        String localized = lm.getLocalizedMessage(
                I18NUtils.getBundle(WebContextFactory.get().getHttpServletRequest())
        );

        String escaped = XssProtectUtils.escapeHtml(localized);
        return super.convertOutbound(escaped, outctx);
    }
}
