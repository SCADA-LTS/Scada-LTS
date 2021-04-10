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
package com.serotonin.mango.rt.maint.work;

import javax.mail.internet.InternetAddress;

import com.serotonin.mango.Common;
import com.serotonin.mango.util.SendMsgUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.SystemSettingsDAO;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.web.email.EmailContent;
import com.serotonin.web.email.EmailSender;
import com.serotonin.web.i18n.LocalizableMessage;

import java.io.IOException;

/**
 * @author Matthew Lohbihler
 * 
 */
public class EmailWorkItem implements WorkItem {

    private static final Log LOG = LogFactory.getLog(EmailWorkItem.class);

    public int getPriority() {
        return WorkItem.PRIORITY_MEDIUM;
    }

    public static void queueEmail(String toAddr, MangoEmailContent content) {
        queueEmail(new String[] { toAddr }, content);
    }

    public static void queueEmail(String[] toAddrs, MangoEmailContent content) {
        queueEmail(toAddrs, content, null);
    }

    public static void queueEmail(String[] toAddrs, MangoEmailContent content, Runnable[] postSendExecution) {
        queueEmail(toAddrs, content.getSubject(), content, postSendExecution);
    }

    public static void queueEmail(String[] toAddrs, String subject, EmailContent content, Runnable[] postSendExecution) {
        EmailWorkItem item = new EmailWorkItem();

        item.toAddresses = SendMsgUtils.convertToInternetAddresses(toAddrs);
        item.subject = subject;
        item.content = content;
        item.postSendExecution = postSendExecution;

        Common.ctx.getBackgroundProcessing().addWorkItem(item);
    }

    private InternetAddress fromAddress;
    private InternetAddress[] toAddresses;
    private String subject;
    private EmailContent content;
    private Runnable[] postSendExecution;

    public void execute() {
        try {
            if (fromAddress == null) {
                String addr = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_FROM_ADDRESS);
                String pretty = SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_FROM_NAME);
                fromAddress = new InternetAddress(addr, pretty);
            }

            EmailSender emailSender = new EmailSender(SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_HOST),
                    SystemSettingsDAO.getIntValue(SystemSettingsDAO.EMAIL_SMTP_PORT),
                    SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EMAIL_AUTHORIZATION),
                    SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_USERNAME),
                    SystemSettingsDAO.getValue(SystemSettingsDAO.EMAIL_SMTP_PASSWORD),
                    SystemSettingsDAO.getBooleanValue(SystemSettingsDAO.EMAIL_TLS));

            emailSender.send(fromAddress, toAddresses, subject, content);
        }
        catch (Exception e) {
            LOG.error(e);
            String to = "";
            for (InternetAddress addr : toAddresses) {
                if (to.length() > 0)
                    to += ", ";
                to += addr.getAddress();
            }
            Boolean doNotCreateEventForEmailError = false;
            try {
                doNotCreateEventForEmailError = ScadaConfig.getInstance().getBoolean(ScadaConfig.DO_NOT_CREATE_EVENTS_FOR_EMAIL_ERROR,false);
            } catch (IOException er) {
                LOG.error(er);
            }
            if (doNotCreateEventForEmailError == false) {
                SystemEventType.raiseEvent(new SystemEventType(SystemEventType.TYPE_EMAIL_SEND_FAILURE),
                        System.currentTimeMillis(), false,
                        new LocalizableMessage("event.email.failure", subject, to, e.getMessage()));
            }
        }
        finally {
            if (postSendExecution != null) {
                for (Runnable runnable : postSendExecution)
                    runnable.run();
            }
        }
    }
}
