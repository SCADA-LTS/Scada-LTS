package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.handlers.EmailHandlerRT;
import com.serotonin.mango.rt.event.handlers.EmailToSmsHandlerRT;
import com.serotonin.mango.rt.event.handlers.NotificationType;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.mango.web.email.MangoTextContent;
import com.serotonin.mango.web.email.UsedImagesDirective;
import com.serotonin.util.StringUtils;
import com.serotonin.web.email.EmailInline;
import com.serotonin.web.i18n.LocalizableMessage;
import freemarker.template.TemplateException;
import org.scada_lts.dao.SystemSettingsDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public final class EmailContentUtils {

    private EmailContentUtils(){}

    public static MangoTextContent createTextContent(EventInstance evt, NotificationType notificationType, String alias) throws TemplateException, IOException {
        ResourceBundle bundle = Common.getBundle();
        String subject = getSubject(evt, notificationType, alias, bundle);
        Map<String, Object> model = createSmsModel(evt);
        return new MangoTextContent(notificationType.getFile(), model, bundle, subject, Common.UTF8);
    }

    public static MangoEmailContent createContent(EventInstance evt, NotificationType notificationType, String alias) throws TemplateException, IOException {
        ResourceBundle bundle = Common.getBundle();
        String subject = getSubject(evt, notificationType, alias, bundle);
        UsedImagesDirective inlineImages = new UsedImagesDirective();

        Map<String, Object> model = createModel(evt, inlineImages);
        MangoEmailContent content = new MangoEmailContent(notificationType.getFile(), model, bundle, subject,
                Common.UTF8);

        addInLineToContent(inlineImages, content);
        return content;
    }

    private static void addInLineToContent(UsedImagesDirective inlineImages, MangoEmailContent content) {
        for (String s : inlineImages.getImageList())
            content.addInline(new EmailInline.FileInline(s, Common.ctx.getServletContext().getRealPath(s)));
    }

    private static Map<String, Object> createModel(EventInstance evt, UsedImagesDirective inlineImages) {
        Map<String, Object> model = new HashMap<>();
        model.put("evt", evt);
        if (evt.getContext() != null)
            model.putAll(evt.getContext());
        model.put("img", inlineImages);
        model.put("instanceDescription", SystemSettingsDAO.getValue(SystemSettingsDAO.INSTANCE_DESCRIPTION));
        return model;
    }

    private static Map<String, Object> createSmsModel(EventInstance evt) {
        Map<String, Object> model = new HashMap<>();
        model.put("evt", evt);
        if (evt.getContext() != null)
            model.putAll(evt.getContext());
        return model;
    }

    private static String getSubject(EventInstance evt, NotificationType notificationType, String alias, ResourceBundle bundle) {
        // Determine the subject to use.
        LocalizableMessage subjectMsg;
        LocalizableMessage notifTypeMsg = new LocalizableMessage(notificationType.getKey());
        if (StringUtils.isEmpty(alias)) {
            if (evt.getId() == Common.NEW_ID)
                subjectMsg = new LocalizableMessage("ftl.subject.default", notifTypeMsg);
            else
                subjectMsg = new LocalizableMessage("ftl.subject.default.id", notifTypeMsg, evt.getId());
        } else {
            if (evt.getId() == Common.NEW_ID)
                subjectMsg = new LocalizableMessage("ftl.subject.alias", alias, notifTypeMsg);
            else
                subjectMsg = new LocalizableMessage("ftl.subject.alias.id", alias, notifTypeMsg, evt.getId());
        }

        return subjectMsg.getLocalizedMessage(bundle);
    }
}
