package com.serotonin.mango.web.email;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface ITemplateContent extends IMsgContent {
    void setPlainTemplate(Template plainTpl, Object model) throws TemplateException, IOException;

    void setHtmlTemplate(Template htmlTpl, Object model) throws TemplateException, IOException;
}
