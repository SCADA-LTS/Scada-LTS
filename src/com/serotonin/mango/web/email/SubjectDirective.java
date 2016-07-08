package com.serotonin.mango.web.email;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class SubjectDirective implements TemplateDirectiveModel {
    private final ResourceBundle bundle;
    private String subject;

    public SubjectDirective(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public void execute(Environment env, @SuppressWarnings("rawtypes") Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException {
        if (params.containsKey("message")) {
            BeanModel model = (BeanModel) params.get("message");
            if (model == null)
                subject = "";
            else {
                LocalizableMessage message = (LocalizableMessage) model.getWrappedObject();
                if (message == null)
                    subject = "";
                else
                    subject = message.getLocalizedMessage(bundle);
            }
        }
        else if (params.containsKey("key")) {
            TemplateModel key = (TemplateModel) params.get("key");

            if (key == null)
                subject = "";
            else {
                if (key instanceof TemplateScalarModel) {
                    String keyString = ((TemplateScalarModel) key).getAsString();
                    Object[] keyParams = findParameters(params);
                    if (keyParams == null)
                        subject = I18NUtils.getMessage(bundle, keyString);
                    else {
                        LocalizableMessage m = new LocalizableMessage(keyString, keyParams);
                        subject = m.getLocalizedMessage(bundle);
                    }
                }
                else
                    throw new TemplateModelException("key must be a string");
            }
        }
        else if (params.containsKey("value")) {
            TemplateModel value = (TemplateModel) params.get("value");

            if (value == null)
                subject = "";
            else {
                if (value instanceof TemplateScalarModel)
                    subject = ((TemplateScalarModel) value).getAsString();
                else
                    throw new TemplateModelException("value must be a string");
            }
        }
        else if (body != null) {
            StringWriter sw = new StringWriter();
            try {
                body.render(sw);
            }
            catch (IOException e) {
                throw new TemplateException(e, env);
            }
            subject = sw.toString();
        }
        else
            // No usable parameter was given
            throw new TemplateModelException(
                    "A parameter named either 'key', 'message' or 'value' must be provided, or body content");
    }

    private Object[] findParameters(@SuppressWarnings("rawtypes") Map params) throws TemplateModelException {
        List<Object> result = new ArrayList<Object>();

        while (true) {
            String key = "param" + result.size();
            if (params.containsKey(key)) {
                TemplateModel templateModel = (TemplateModel) params.get(key);

                if (templateModel instanceof SimpleScalar)
                    result.add(((SimpleScalar) templateModel).getAsString());
                else if (templateModel instanceof StringModel)
                    result.add(((StringModel) templateModel).getWrappedObject());
                else if (templateModel instanceof BeanModel)
                    result.add(((BeanModel) templateModel).getWrappedObject());
                else
                    throw new TemplateModelException("key params must be BeanModels instead of " + templateModel);
            }
            else
                break;
        }

        if (result.isEmpty())
            return null;

        return result.toArray(new Object[result.size()]);
    }
}
