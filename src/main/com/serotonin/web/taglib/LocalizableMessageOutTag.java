package com.serotonin.web.taglib;

import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.taglibs.standard.tag.rt.core.OutTag;
import org.scada_lts.serorepl.utils.StringUtils;

import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

public class LocalizableMessageOutTag extends OutTag {
    public void setMessage(LocalizableMessage message) {
        LocalizationContext localizationContext = (LocalizationContext) Config.get(this.pageContext, Config.FMT_LOCALIZATION_CONTEXT, 2);
        if(localizationContext == null) {
            localizationContext = (LocalizationContext) Config.find(this.pageContext, Config.FMT_LOCALIZATION_CONTEXT);
        }
        if(message == null) {
            message = new LocalizableMessage("common.noMessage");
        }
        super.setValue(message.getLocalizedMessage(localizationContext.getResourceBundle()));
    }

    public void setKey(String key) {
        LocalizationContext localizationContext = (LocalizationContext) Config.get(this.pageContext, Config.FMT_LOCALIZATION_CONTEXT, 2);
        if(localizationContext == null) {
            localizationContext = (LocalizationContext) Config.find(this.pageContext, Config.FMT_LOCALIZATION_CONTEXT);
        }
        super.setValue(LocalizableMessage.getMessage(localizationContext.getResourceBundle(), key));
    }

    public void setEscapeQuotes(boolean escapeQuotes) {
        super.setEscapeXml(escapeQuotes);
    }

    public void setEscapeDQuotes(boolean escapeDQuotes) {
        super.setEscapeXml(escapeDQuotes);
    }

    @Override
    public void setDefault(String def) {
        super.setDefault(def);
    }

    public static LocalizableMessage message(String key, String arguments, String separator) {
        if(StringUtils.isEmpty(arguments)) {
            return new LocalizableMessage(key);
        } else {
            String[] args = arguments.split(separator);
            return new LocalizableMessage(key, (Object[]) args);
        }
    }
}
