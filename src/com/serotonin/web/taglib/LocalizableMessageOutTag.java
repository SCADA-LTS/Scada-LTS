package com.serotonin.web.taglib;

import com.serotonin.mango.Common;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.taglibs.standard.tag.rt.core.OutTag;

public class LocalizableMessageOutTag extends OutTag {
    public void setMessage(LocalizableMessage message) {
        super.setValue(message.getLocalizedMessage(Common.getBundle()));
    }

    public void setKey(String key) {
        super.setValue(LocalizableMessage.getMessage(Common.getBundle(), key));
    }

    public void setEscapeQuotes(boolean escapeQuotes) {
        super.setEscapeXml(escapeQuotes);
    }

    public void setEscapeDQuotes(boolean escapeDQuotes) {
        super.setEscapeXml(escapeDQuotes);
    }
}
