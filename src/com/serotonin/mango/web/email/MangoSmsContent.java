package com.serotonin.mango.web.email;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

public class MangoSmsContent extends MangoEmailContent {
    public MangoSmsContent(String templateName, Map<String, Object> model, ResourceBundle bundle, String defaultSubject, String encoding) throws TemplateException, IOException {
        super(templateName, model, bundle, defaultSubject, encoding);
    }

    public MangoSmsContent(Map<String, Object> model, ResourceBundle bundle, String defaultSubject, String encoding) throws TemplateException, IOException {
        super("activeSms", model, bundle, defaultSubject, encoding);
    }

    @Override
    protected int getType() {
        return MangoEmailContent.CONTENT_TYPE_TEXT;
    }
}
