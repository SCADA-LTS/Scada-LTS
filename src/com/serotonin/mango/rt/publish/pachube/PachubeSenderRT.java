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
package com.serotonin.mango.rt.publish.pachube;

import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.pachube.PachubeDataSourceRT;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.PublisherEventType;
import com.serotonin.mango.rt.publish.PublishQueue;
import com.serotonin.mango.rt.publish.PublishQueueEntry;
import com.serotonin.mango.rt.publish.PublisherRT;
import com.serotonin.mango.rt.publish.SendThread;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.mango.vo.publish.pachube.PachubePointVO;
import com.serotonin.mango.vo.publish.pachube.PachubeSenderVO;
import com.serotonin.web.i18n.LocalizableMessage;

public class PachubeSenderRT extends PublisherRT<PachubePointVO> {
    static final Log LOG = LogFactory.getLog(PachubeSenderRT.class);
    private static final int MAX_FAILURES = 5;

    public static final int SEND_EXCEPTION_EVENT = 11;

    final EventType sendExceptionEventType = new PublisherEventType(getId(), SEND_EXCEPTION_EVENT);

    final PachubeSenderVO vo;
    final HttpClient httpClient;

    public PachubeSenderRT(PachubeSenderVO vo) {
        super(vo);
        this.vo = vo;
        httpClient = PachubeDataSourceRT.createHttpClient(vo.getTimeoutSeconds(), vo.getRetries());
    }

    @Override
    protected PublishQueue<PachubePointVO> createPublishQueue(PublisherVO<PachubePointVO> vo) {
        return new PachubePublishQueue(this, vo.getCacheWarningSize());
    }

    //
    // /
    // / Lifecycle
    // /
    //
    @Override
    public void initialize() {
        super.initialize(new PachubeSendThread());
    }

    PublishQueue<PachubePointVO> getPublishQueue() {
        return queue;
    }

    class PachubeSendThread extends SendThread {
        private int failureCount = 0;
        private LocalizableMessage failureMessage;

        PachubeSendThread() {
            super("PachubeSenderRT.SendThread");
        }

        @Override
        protected void runImpl() {
            while (isRunning()) {
                PublishQueueEntry<PachubePointVO> entry = getPublishQueue().next();

                if (entry != null) {
                    if (send(entry))
                        getPublishQueue().remove(entry);
                    else {
                        // The send failed, so take a break so as not to over exert ourselves.
                        try {
                            Thread.sleep(2000);
                        }
                        catch (InterruptedException e1) {
                            // no op
                        }
                    }
                }
                else
                    waitImpl(10000);
            }
        }

        @SuppressWarnings("synthetic-access")
        private boolean send(PublishQueueEntry<PachubePointVO> entry) {
            PachubePointVO point = entry.getVo();

            // Prepare the message
            PutMethod method = new PutMethod("http://www.pachube.com/api/feeds/" + point.getFeedId() + "/datastreams/"
                    + point.getDataStreamId() + ".csv");
            method.addRequestHeader(new Header(PachubeDataSourceRT.HEADER_API_KEY, vo.getApiKey()));
            method.addRequestHeader("User-Agent", "Mango M2M Pachube publisher");
            try {
                method.setRequestEntity(new StringRequestEntity(entry.getPvt().getValue().toString(), "text/csv",
                        "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                throw new ShouldNeverHappenException(e);
            }

            // Send the request. Set message non-null if there is a failure.
            LocalizableMessage message = null;
            boolean permanentFailure = false;
            try {
                int code = httpClient.executeMethod(method);
                if (code != HttpStatus.SC_OK) {
                    message = new LocalizableMessage("event.publish.invalidResponse", code);

                    // 500-type errors are server side, and so may be recoverable.
                    permanentFailure = code < 500;
                }
            }
            catch (Exception e) {
                message = new LocalizableMessage("common.default", e.getMessage());
            }
            finally {
                method.releaseConnection();
            }

            // Check for failure.
            if (message != null) {
                failureCount++;
                if (failureMessage == null)
                    failureMessage = message;

                if (failureCount == MAX_FAILURES + 1)
                    Common.ctx.getEventManager().raiseEvent(sendExceptionEventType, System.currentTimeMillis(), true,
                            AlarmLevels.URGENT, failureMessage, createEventContext());

                return permanentFailure;
            }

            if (failureCount > 0) {
                if (failureCount > MAX_FAILURES)
                    Common.ctx.getEventManager().returnToNormal(sendExceptionEventType, System.currentTimeMillis());

                failureCount = 0;
                failureMessage = null;
            }
            return true;
        }
    }
}
