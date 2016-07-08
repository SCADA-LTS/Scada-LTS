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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.SystemSettingsDao;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.web.email.MangoEmailContent;
import com.serotonin.web.email.EmailContent;
import com.serotonin.web.email.EmailSender;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 * 
 */
public class EmailWorkItem implements WorkItem {
    public int getPriority() {
        return WorkItem.PRIORITY_MEDIUM;
    }

    public static void queueEmail(String toAddr, MangoEmailContent content) throws AddressException {
        queueEmail(new String[] { toAddr }, content);
    }

    public static void queueEmail(String[] toAddrs, MangoEmailContent content) throws AddressException {
        queueEmail(toAddrs, content, null);
    }

    public static void queueEmail(String[] toAddrs, MangoEmailContent content, Runnable[] postSendExecution)
            throws AddressException {
        queueEmail(toAddrs, content.getSubject(), content, postSendExecution);
    }

    public static void queueEmail(String[] toAddrs, String subject, EmailContent content, Runnable[] postSendExecution)
            throws AddressException {
        EmailWorkItem item = new EmailWorkItem();

        item.toAddresses = new InternetAddress[toAddrs.length];
        for (int i = 0; i < toAddrs.length; i++)
            item.toAddresses[i] = new InternetAddress(toAddrs[i]);

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
                String addr = SystemSettingsDao.getValue(SystemSettingsDao.EMAIL_FROM_ADDRESS);
                String pretty = SystemSettingsDao.getValue(SystemSettingsDao.EMAIL_FROM_NAME);
                fromAddress = new InternetAddress(addr, pretty);
            }

            EmailSender emailSender = new EmailSender(SystemSettingsDao.getValue(SystemSettingsDao.EMAIL_SMTP_HOST),
                    SystemSettingsDao.getIntValue(SystemSettingsDao.EMAIL_SMTP_PORT),
                    SystemSettingsDao.getBooleanValue(SystemSettingsDao.EMAIL_AUTHORIZATION),
                    SystemSettingsDao.getValue(SystemSettingsDao.EMAIL_SMTP_USERNAME),
                    SystemSettingsDao.getValue(SystemSettingsDao.EMAIL_SMTP_PASSWORD),
                    SystemSettingsDao.getBooleanValue(SystemSettingsDao.EMAIL_TLS));

            emailSender.send(fromAddress, toAddresses, subject, content);
        }
        catch (Exception e) {
            String to = "";
            for (InternetAddress addr : toAddresses) {
                if (to.length() > 0)
                    to += ", ";
                to += addr.getAddress();
            }
            SystemEventType.raiseEvent(new SystemEventType(SystemEventType.TYPE_EMAIL_SEND_FAILURE),
                    System.currentTimeMillis(), false,
                    new LocalizableMessage("event.email.failure", subject, to, e.getMessage()));
        }
        finally {
            if (postSendExecution != null) {
                for (Runnable runnable : postSendExecution)
                    runnable.run();
            }
        }
    }
}
