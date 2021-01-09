package com.serotonin.mango.web.email;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

public class MangoTextContent extends MangoEmailContent {
    public MangoTextContent(String templateName, Map<String, Object> model, ResourceBundle bundle, String defaultSubject, String encoding) throws TemplateException, IOException {
        super(templateName, model, bundle, defaultSubject, encoding);
    }

    @Override
    protected int getType() {
        return MangoEmailContent.CONTENT_TYPE_TEXT;
    }
}
