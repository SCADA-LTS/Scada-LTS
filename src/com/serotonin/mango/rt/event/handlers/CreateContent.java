package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.web.email.IMsgSubjectContent;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface CreateContent {
    IMsgSubjectContent createContent(EventInstance evt, String alias) throws TemplateException, IOException;
}
