package com.serotonin.mango.web.email;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

public interface ITextContent extends IMsgSubjectContent {

    @Override
    void setPlainTemplate(Template plainTpl, Object model) throws TemplateException, IOException;

    @Override
    int getType();

    static ITextContent newInstance(String templateName, Map<String, Object> model, ResourceBundle bundle,
                                    String defaultSubject, String encoding) throws TemplateException, IOException {
        return new MangoTextContentImpl(templateName, model, bundle, defaultSubject, encoding);
    }
}
