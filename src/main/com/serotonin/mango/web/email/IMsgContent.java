package com.serotonin.mango.web.email;

import java.util.List;

public interface IMsgContent {
    boolean isMultipart();

    String getHtmlContent();

    String getPlainContent();

    void addAttachment(AbstractEmailAttachment emailAttachment);

    List<AbstractEmailAttachment> getAttachments();

    void addInline(AbstractEmailInline emailInline);

    List<AbstractEmailInline> getInlines();

    String getEncoding();

    static IMsgContent newInstance(String plainContent, String htmlContent) {
        return new EmailContentImpl(plainContent, htmlContent, null);
    }

    static IMsgContent newInstance(String plainContent, String htmlContent, String encoding) {
        return new EmailContentImpl(plainContent, htmlContent, encoding);
    }
}
