package com.serotonin.mango.web.email;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

class TemplateEmailContentImpl extends EmailContentImpl implements ITemplateContent {
    protected static final Map<Pattern, String> REPLACEMENT_EMPTY_TAG = new HashMap();

    protected TemplateEmailContentImpl(String encoding) {
        this.encoding = encoding;
    }

    protected TemplateEmailContentImpl(Template plainTpl, Template htmlTpl, Object model,
                                       String encoding) throws TemplateException, IOException {
        this.setPlainTemplate(plainTpl, model);
        this.setHtmlTemplate(htmlTpl, model);
        this.encoding = encoding;
    }

    @Override
    public void setPlainTemplate(Template plainTpl, Object model) throws TemplateException, IOException {
        if (plainTpl != null) {
            StringWriter plain = new StringWriter();
            plainTpl.process(model, plain);
            this.plainContent = plain.toString();

            Map.Entry entry;
            for(Iterator i$ = REPLACEMENT_EMPTY_TAG.entrySet().iterator(); i$.hasNext(); this.plainContent = ((Pattern)entry.getKey()).matcher(this.plainContent).replaceAll((String)entry.getValue())) {
                entry = (Map.Entry)i$.next();
            }
        }

    }

    @Override
    public void setHtmlTemplate(Template htmlTpl, Object model) throws TemplateException, IOException {
        if (htmlTpl != null) {
            StringWriter html = new StringWriter();
            htmlTpl.process(model, html);
            this.htmlContent = html.toString();
        }

    }

    static {
        REPLACEMENT_EMPTY_TAG.put(Pattern.compile("<br\\s*/>"), "\r\n");
        REPLACEMENT_EMPTY_TAG.put(Pattern.compile("&nbsp;"), " ");
    }
}
