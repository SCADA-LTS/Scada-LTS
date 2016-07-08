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
package com.serotonin.mango.vo.publish.pachube;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.publish.PublisherRT;
import com.serotonin.mango.rt.publish.httpSender.HttpSenderRT;
import com.serotonin.mango.rt.publish.pachube.PachubeSenderRT;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class PachubeSenderVO extends PublisherVO<PachubePointVO> {
    @Override
    protected void getEventTypesImpl(List<EventTypeVO> eventTypes) {
        eventTypes.add(new EventTypeVO(EventType.EventSources.PUBLISHER, getId(), HttpSenderRT.SEND_EXCEPTION_EVENT,
                new LocalizableMessage("event.pb.httpSend"), AlarmLevels.URGENT));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        PublisherVO.addDefaultEventCodes(EVENT_CODES);
        EVENT_CODES.addElement(HttpSenderRT.SEND_EXCEPTION_EVENT, "SEND_EXCEPTION_EVENT");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public LocalizableMessage getConfigDescription() {
        return new LocalizableMessage("common.noMessage");
    }

    @Override
    public Type getType() {
        return PublisherVO.Type.PACHUBE;
    }

    @Override
    public PublisherRT<PachubePointVO> createPublisherRT() {
        return new PachubeSenderRT(this);
    }

    @Override
    protected PachubePointVO createPublishedPointInstance() {
        return new PachubePointVO();
    }

    @JsonRemoteProperty
    private String apiKey;
    @JsonRemoteProperty
    private int timeoutSeconds = 30;
    @JsonRemoteProperty
    private int retries = 2;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);

        if (StringUtils.isEmpty(apiKey))
            response.addContextualMessage("apiKey", "validate.required");
        if (timeoutSeconds <= 0)
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero");
        if (retries < 0)
            response.addContextualMessage("retries", "validate.cannotBeNegative");

        for (PachubePointVO point : points) {
            if (StringUtils.isEmpty(point.getDataStreamId())) {
                response.addContextualMessage("points", "validate.pachube.dataStreadIdRequired");
                break;
            }
        }
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, apiKey);
        out.writeInt(timeoutSeconds);
        out.writeInt(retries);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            apiKey = SerializationHelper.readSafeUTF(in);
            timeoutSeconds = in.readInt();
            retries = in.readInt();
        }
    }
}
