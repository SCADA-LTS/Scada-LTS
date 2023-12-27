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
package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.MailingListDao;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.maint.work.AfterWork;
import com.serotonin.mango.util.MsgContentUtils;
import com.serotonin.mango.util.timeout.ModelTimeoutClient;
import com.serotonin.mango.util.timeout.ModelTimeoutTask;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.web.email.IMsgSubjectContent;
import com.serotonin.timer.TimerTask;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.service.CommunicationChannelType;
import org.scada_lts.service.ScheduledExecuteInactiveEventService;

import java.io.IOException;
import java.util.Set;

import static com.serotonin.mango.util.LoggingUtils.eventHandlerInfo;
import static com.serotonin.mango.util.LoggingUtils.eventInfo;
import static com.serotonin.mango.util.SendUtils.sendMsg;

public class EmailHandlerRT extends EventHandlerRT implements ModelTimeoutClient<EventInstance> {
    private static final Log LOG = LogFactory.getLog(EmailHandlerRT.class);

    private TimerTask escalationTask;

    private Set<String> activeRecipients;
    private final ScheduledExecuteInactiveEventService service;
    private final MailingListService mailingListService;

    public enum EmailNotificationType implements NotificationType {
        ACTIVE("active", "ftl.subject.active"), //
        ESCALATION("escalation", "ftl.subject.escalation"), //
        INACTIVE("inactive", "ftl.subject.inactive");

        private final String file;
        private final String key;

        EmailNotificationType(String file, String key) {
            this.file = file;
            this.key = key;
        }

        @Override
        public String getFile() {
            return file;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public IMsgSubjectContent createContent(EventInstance evt, String alias) throws TemplateException, IOException {
            return MsgContentUtils.createEmail(evt, this, alias);
        }
    }

    /**
     * The list of all of the recipients - active and escalation - for sending upon inactive if configured to do so.
     */
    private Set<String> inactiveRecipients;

    public EmailHandlerRT(EventHandlerVO vo) {
        this.vo = vo;
        this.service = ScheduledExecuteInactiveEventService.getInstance();
        this.mailingListService = new MailingListService();
    }

    public EmailHandlerRT(EventHandlerVO vo, MailingListService mailingListService) {
        this.vo = vo;
        this.service = ScheduledExecuteInactiveEventService.getInstance();
        this.mailingListService = mailingListService;
    }

    public EmailHandlerRT(EventHandlerVO vo,
                          ScheduledExecuteInactiveEventService service,
                          MailingListService mailingListService) {
        this.vo = vo;
        this.service = service;
        this.mailingListService = mailingListService;
    }

    public Set<String> getActiveRecipients() {
        return activeRecipients;
    }

    @Override
    public void eventRaised(EventInstance evt) {
        // Get the email addresses to send to
        activeRecipients = getActiveRecipients(evt);

        boolean scheduled = service.scheduleEvent(vo, evt);

        // Send an email to the active recipients.
        if(!scheduled)
            sendEmail(evt, activeRecipients);

        // If an inactive notification is to be sent, save the active recipients.
        if (vo.isSendInactive()) {
            if (vo.isInactiveOverride())
                inactiveRecipients = getInactiveRecipients(evt);
            else
                inactiveRecipients = activeRecipients;
        }

        // If an escalation is to be sent, set up timeout to trigger it.
        if (vo.isSendEscalation()) {
            long delayMS = Common.getMillis(vo.getEscalationDelayType(), vo.getEscalationDelay());
            escalationTask = new ModelTimeoutTask<EventInstance>(delayMS, this, evt);
        }
    }

    //
    // TimeoutClient
    //
    synchronized public void scheduleTimeout(EventInstance evt, long fireTime) {
        // Get the email addresses to send to
        Set<String> addresses = new MailingListDao().getRecipientAddresses(vo.getEscalationRecipients(), new DateTime(
                fireTime));

        // Send the escalation.
        sendEmail(evt, EmailNotificationType.ESCALATION, addresses);

        // If an inactive notification is to be sent, save the escalation recipients, but only if inactive recipients
        // have not been overridden.
        if (vo.isSendInactive() && !vo.isInactiveOverride())
            inactiveRecipients.addAll(addresses);
    }

    @Override
    synchronized public void eventInactive(EventInstance evt) {
        // Cancel the escalation job in case it's there
        if (escalationTask != null)
            escalationTask.cancel();

        if (inactiveRecipients != null && inactiveRecipients.size() > 0)
            // Send an email to the inactive recipients.
            sendEmail(evt, EmailNotificationType.INACTIVE, inactiveRecipients);
    }

    public static void sendActiveEmail(EventInstance evt, Set<String> addresses) {
        sendMsg(evt, EmailNotificationType.ACTIVE, addresses, null, new AfterWork() {
            @Override
            public void workFail(Throwable exception) {
                LOG.error("Failed sending email for " + eventInfo(evt)
                        + ", error: " + exception.getMessage());
            }
        }, () -> "sendActiveEmail from: " + EmailHandlerRT.class.getName() + ", " + eventInfo(evt));
    }

    protected Set<String> getActiveRecipients(EventInstance evt) {
        CommunicationChannelType type = CommunicationChannelType.EMAIL;
        Set<String> addresses = mailingListService.getRecipientAddresses(vo.getActiveRecipients(),
                new DateTime(evt.getActiveTimestamp()), type);
        return type.formatAddresses(addresses, "", type.getReplaceRegex());
    }

    protected Set<String> getInactiveRecipients(EventInstance evt) {
        CommunicationChannelType type = CommunicationChannelType.EMAIL;
        Set<String> addresses = mailingListService.getRecipientAddresses(vo.getInactiveRecipients(),
                new DateTime(evt.getActiveTimestamp()), type);
        return type.formatAddresses(addresses, "", type.getReplaceRegex());
    }

    protected EventHandlerVO getVo() {
        return this.vo;
    }

    protected void sendEmail(EventInstance evt, Set<String> addresses) {
        sendEmail(evt, EmailNotificationType.ACTIVE, addresses);
    }

    private void sendEmail(EventInstance evt, NotificationType notificationType, Set<String> addresses) {
        sendMsg(evt, notificationType, addresses, vo.getAlias(), new AfterWork() {
            @Override
            public void workFail(Throwable exception) {
                LOG.error("Failed sending email for " + eventHandlerInfo(getVo()) + ", " + eventInfo(evt)
                        + ", error: " + exception.getMessage());
            }
        }, () -> eventHandlerInfo(getVo()) + ", " + eventInfo(evt));
    }
}
