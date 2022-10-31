package com.serotonin.mango.web.email;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

class MangoTextContentImpl extends MangoEmailContentImpl implements ITextContent {

    protected MangoTextContentImpl(String templateName, Map<String, Object> model, ResourceBundle bundle,
                                   String defaultSubject, String encoding) throws TemplateException, IOException {
        super(templateName, model, bundle, defaultSubject, encoding);
    }

    @Override
    public void setPlainTemplate(Template plainTpl, Object model) throws TemplateException, IOException {
        if (plainTpl != null) {
            try (StringWriter plain = new StringWriter()) {
                plainTpl.process(model, plain);
                String text = Pattern.compile("\r\n").matcher(plain.toString()).replaceAll("");
                this.plainContent = Pattern.compile("\n").matcher(text).replaceAll("");
            }
        }
    }

    @Override
    public int getType() {
        return CONTENT_TYPE_TEXT;
    }
}
