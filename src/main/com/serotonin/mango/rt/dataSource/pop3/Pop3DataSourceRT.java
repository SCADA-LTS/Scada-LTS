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
package com.serotonin.mango.rt.dataSource.pop3;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.DataSourceUtils;
import com.serotonin.mango.rt.dataSource.NoMatchException;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.vo.dataSource.pop3.Pop3DataSourceVO;
import com.serotonin.web.i18n.LocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class Pop3DataSourceRT extends PollingDataSource {
    public static final int INBOX_EXCEPTION_EVENT = 1;
    public static final int MESSAGE_READ_EXCEPTION_EVENT = 2;
    public static final int PARSE_EXCEPTION_EVENT = 3;

    private final Pop3DataSourceVO vo;

    public Pop3DataSourceRT(Pop3DataSourceVO vo) {
        super(vo);
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
        this.vo = vo;
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        returnToNormal(PARSE_EXCEPTION_EVENT, System.currentTimeMillis());
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        // no op
    }

    @Override
    protected void doPoll(long time) {
        Folder folder = null;
        Store store = null;

        try {
            boolean messagesRead = false;
            LocalizableMessage messageReadError = null;
            LocalizableMessage parseError = null;

            Properties props = System.getProperties();
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("pop3");

            // Connect to POP3 server
            store.connect(vo.getPop3Server(), vo.getUsername(), vo.getPassword());

            // Get default folder
            folder = store.getDefaultFolder();
            if (folder == null)
                throw new Exception("No default folder");

            folder = folder.getFolder("INBOX");
            if (folder == null)
                throw new Exception("No POP3 inbox");
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                messagesRead = true;
                Pop3Email pop3Email;
                try {
                    pop3Email = readMessage(message);
                }
                catch (Exception e) {
                    if (messageReadError == null)
                        messageReadError = new LocalizableMessage("common.default", e.getMessage());
                    continue;
                }
                finally {
                    message.setFlag(Flags.Flag.DELETED, true);
                }

                try {
                    processMessage(pop3Email, time);
                }
                catch (LocalizableException e) {
                    if (parseError == null)
                        parseError = e.getLocalizableMessage();
                }
            }

            returnToNormal(INBOX_EXCEPTION_EVENT, time);

            if (messagesRead) {
                if (messageReadError != null)
                    raiseEvent(MESSAGE_READ_EXCEPTION_EVENT, time, false, messageReadError);
                else
                    returnToNormal(MESSAGE_READ_EXCEPTION_EVENT, time);

                if (parseError != null)
                    raiseEvent(PARSE_EXCEPTION_EVENT, time, false, parseError);
                else
                    returnToNormal(PARSE_EXCEPTION_EVENT, time);
            }
        }
        catch (Exception e) {
            raiseEvent(INBOX_EXCEPTION_EVENT, time, false, new LocalizableMessage("common.default", e.getMessage()));
        }
        finally {
            try {
                if (folder != null)
                    folder.close(true);
            }
            catch (MessagingException e) {
                // no op
            }

            try {
                if (store != null)
                    store.close();
            }
            catch (MessagingException e) {
                // no op
            }
        }
    }

    private Pop3Email readMessage(Message message) throws MessagingException, IOException {
        Pop3Email email = new Pop3Email();

        // Get the body
        StringBuffer body = new StringBuffer();
        Object content = message.getContent();
        if (content instanceof String)
            // The content of the message is probably text/plain or text/html.
            body.append((String) content);
        else if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bp = multipart.getBodyPart(i);
                String contentType = bp.getContentType();
                if (contentType.startsWith("text/"))
                    body.append(bp.getContent());
                // Ignore everything else.
            }
        }

        email.setBody(body.toString());
        email.setSubject(message.getSubject());

        Date date = message.getSentDate();
        if (date == null)
            date = message.getReceivedDate();
        email.setDate(date);

        return email;
    }

    private void processMessage(Pop3Email pop3Email, long time) throws LocalizableException {
        for (DataPointRT dp : dataPoints) {
            Pop3PointLocatorRT locator = dp.getPointLocator();

            // Get the value
            String content;
            if (locator.isFindInSubject())
                content = pop3Email.getSubject();
            else
                content = pop3Email.getBody();

            MangoValue value;
            try {
                value = DataSourceUtils.getValue(locator.getValuePattern(), content, locator.getDataTypeId(), locator
                        .getBinary0Value(), dp.getVO().getTextRenderer(), locator.getValueFormat(), dp.getVO()
                        .getName());
            }
            catch (NoMatchException e) {
                if (locator.isIgnoreIfMissing())
                    continue;
                throw e;
            }

            // Get the time.
            long valueTime;

            if (locator.isUseReceivedTime()) {
                if (pop3Email.getDate() != null)
                    valueTime = pop3Email.getDate().getTime();
                else
                    valueTime = System.currentTimeMillis();
            }
            else
                valueTime = DataSourceUtils.getValueTime(time, locator.getTimePattern(), pop3Email.getBody(),
                        locator.getTimeFormat(), dp.getVO().getName());

            // Save the new value
            dp.updatePointValue(new PointValueTime(value, valueTime));
        }
    }
}
