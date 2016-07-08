/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.email;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * @author Matthew Lohbihler
 */
public class MessageFormatDirective implements TemplateDirectiveModel {
    private final ResourceBundle bundle;

    public MessageFormatDirective(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void execute(Environment env, @SuppressWarnings("rawtypes") Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {
        TemplateModel key = (TemplateModel) params.get("key");

        String out;
        if (key == null) {
            // No key. Look for a message.
            BeanModel model = (BeanModel) params.get("message");
            if (model == null) {
                if (params.containsKey("message"))
                    // The parameter is there, but the value is null.
                    out = "";
                else
                    // The parameter wasn't given
                    throw new TemplateModelException("One of key or message must be provided");
            }
            else {
                LocalizableMessage message = (LocalizableMessage) model.getWrappedObject();
                if (message == null)
                    out = "";
                else
                    out = message.getLocalizedMessage(bundle);
            }
        }
        else {
            if (key instanceof TemplateScalarModel)
                out = I18NUtils.getMessage(bundle, ((TemplateScalarModel) key).getAsString());
            else
                throw new TemplateModelException("key must be a string");
        }

        env.getOut().write(out);
    }
}
