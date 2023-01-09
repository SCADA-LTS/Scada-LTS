package com.serotonin.mango.web.email;

import com.serotonin.mango.Common;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.scada_lts.dao.SystemSettingsDAO;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

class MangoEmailContentImpl extends TemplateEmailContentImpl implements IMsgSubjectContent {

    static final int CONTENT_TYPE_BOTH = 0;
    static final int CONTENT_TYPE_HTML = 1;
    static final int CONTENT_TYPE_TEXT = 2;

    private final String defaultSubject;
    private final SubjectDirective subjectDirective;

    protected MangoEmailContentImpl(String templateName, Map<String, Object> model, ResourceBundle bundle,
                                 String defaultSubject, String encoding) throws TemplateException, IOException {
        super(encoding);

        int type = getType();

        this.defaultSubject = defaultSubject;
        this.subjectDirective = new SubjectDirective(bundle);

        model.put("fmt", new MessageFormatDirective(bundle));
        model.put("subject", subjectDirective);

        if (type == CONTENT_TYPE_HTML || type == CONTENT_TYPE_BOTH)
            setHtmlTemplate(getTemplate(templateName, true), model);

        if (type == CONTENT_TYPE_TEXT || type == CONTENT_TYPE_BOTH)
            setPlainTemplate(getTemplate(templateName, false), model);
    }

    protected MangoEmailContentImpl(IMsgContent emailContent, ResourceBundle bundle, String defaultSubject, String encoding) {
        super(encoding);
        this.defaultSubject = defaultSubject;
        this.subjectDirective = new SubjectDirective(bundle);
        this.htmlContent = emailContent.getHtmlContent();
        this.plainContent = emailContent.getPlainContent();
        for(AbstractEmailAttachment emailAttachment : emailContent.getAttachments()) {
            this.addAttachment(emailAttachment);
        }
        for(AbstractEmailInline emailInline : emailContent.getInlines()) {
            this.addInline(emailInline);
        }
    }

    @Override
    public String getSubject() {
        String subject = subjectDirective.getSubject();
        if (subject == null)
            return defaultSubject;
        return subject;
    }

    @Override
    public int getType() {
        return SystemSettingsDAO.getIntValue(SystemSettingsDAO.EMAIL_CONTENT_TYPE);
    }

    @Override
    public Template getTemplate(String name, boolean html) throws IOException {
        if (html)
            name = "html/" + name + ".ftl";
        else
            name = "text/" + name + ".ftl";

        return Common.ctx.getFreemarkerConfig().getTemplate(name);
    }

    @Override
    public String toString() {
        return "MangoEmailContentImpl{" +
                "defaultSubject='" + defaultSubject + '\'' +
                ", subjectDirective=" + subjectDirective +
                super.toString() +
                '}';
    }
}
