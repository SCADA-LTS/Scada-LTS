package com.serotonin.mango.web.email;


import java.util.ArrayList;
import java.util.List;

class EmailContentImpl implements IMsgContent {
    protected String plainContent;
    protected String htmlContent;
    private final List<AbstractEmailAttachment> attachments;
    private final List<AbstractEmailInline> inlineParts;
    protected String encoding;

    protected EmailContentImpl() {
        this.attachments = new ArrayList(2);
        this.inlineParts = new ArrayList(2);
    }

    protected EmailContentImpl(String plainContent, String htmlContent) {
        this(plainContent, htmlContent, (String)null);
    }

    protected EmailContentImpl(String plainContent, String htmlContent, String encoding) {
        this.attachments = new ArrayList(2);
        this.inlineParts = new ArrayList(2);
        this.plainContent = plainContent;
        this.htmlContent = htmlContent;
        this.encoding = encoding;
    }

    @Override
    public boolean isMultipart() {
        return this.plainContent != null && this.htmlContent != null || !this.attachments.isEmpty() || !this.inlineParts.isEmpty();
    }

    @Override
    public String getHtmlContent() {
        return this.htmlContent;
    }

    @Override
    public String getPlainContent() {
        return this.plainContent;
    }

    @Override
    public void addAttachment(AbstractEmailAttachment emailAttachment) {
        this.attachments.add(emailAttachment);
    }

    @Override
    public List<AbstractEmailAttachment> getAttachments() {
        return this.attachments;
    }

    @Override
    public void addInline(AbstractEmailInline emailInline) {
        this.inlineParts.add(emailInline);
    }

    @Override
    public List<AbstractEmailInline> getInlines() {
        return this.inlineParts;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }
}
