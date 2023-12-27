package com.serotonin.mango.web.email;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

public interface IMsgSubjectContent extends ITemplateContent {

    String getSubject();
    default int getType() {
        return MangoEmailContentImpl.CONTENT_TYPE_HTML;
    }
    Template getTemplate(String name, boolean html) throws IOException;

    static IMsgSubjectContent withContentSubject(IMsgContent emailContent, ResourceBundle bundle, String defaultSubject,
                                                 String encoding) {
        return new MangoEmailContentImpl(emailContent, bundle, defaultSubject, encoding);
    }

    static IMsgSubjectContent newInstance(String templateName, Map<String, Object> model, ResourceBundle bundle,
                                          String defaultSubject, String encoding) throws TemplateException, IOException {
        return new MangoEmailContentImpl(templateName, model, bundle, defaultSubject, encoding);
    }
}
