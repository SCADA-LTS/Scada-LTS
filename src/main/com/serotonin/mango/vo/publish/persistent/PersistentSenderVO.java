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
package com.serotonin.mango.vo.publish.persistent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.publish.PublisherRT;
import com.serotonin.mango.rt.publish.persistent.PersistentSenderRT;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class PersistentSenderVO extends PublisherVO<PersistentPointVO> {
    @Override
    protected void getEventTypesImpl(List<EventTypeVO> eventTypes) {
        eventTypes.add(new EventTypeVO(EventType.EventSources.PUBLISHER, getId(),
                PersistentSenderRT.CONNECTION_FAILED_EVENT, new LocalizableMessage(
                        "event.pb.persistent.connectionFailed"), AlarmLevels.URGENT));
        eventTypes.add(new EventTypeVO(EventType.EventSources.PUBLISHER, getId(),
                PersistentSenderRT.PROTOCOL_FAILURE_EVENT,
                new LocalizableMessage("event.pb.persistent.protocolFailure"), AlarmLevels.URGENT));
        eventTypes.add(new EventTypeVO(EventType.EventSources.PUBLISHER, getId(),
                PersistentSenderRT.CONNECTION_ABORTED_EVENT, new LocalizableMessage(
                        "event.pb.persistent.connectionAborted"), AlarmLevels.URGENT));
        eventTypes.add(new EventTypeVO(EventType.EventSources.PUBLISHER, getId(),
                PersistentSenderRT.CONNECTION_LOST_EVENT, new LocalizableMessage("event.pb.persistent.connectionLost"),
                AlarmLevels.URGENT));
        eventTypes.add(new EventTypeVO(EventType.EventSources.PUBLISHER, getId(),
                PersistentSenderRT.SYNC_COMPLETION_EVENT, new LocalizableMessage("event.pb.persistent.syncCompleted"),
                AlarmLevels.NONE));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        PublisherVO.addDefaultEventCodes(EVENT_CODES);
        EVENT_CODES.addElement(PersistentSenderRT.CONNECTION_FAILED_EVENT, "CONNECTION_FAILED_EVENT");
        EVENT_CODES.addElement(PersistentSenderRT.PROTOCOL_FAILURE_EVENT, "PROTOCOL_FAILURE_EVENT");
        EVENT_CODES.addElement(PersistentSenderRT.CONNECTION_ABORTED_EVENT, "CONNECTION_ABORTED_EVENT");
        EVENT_CODES.addElement(PersistentSenderRT.CONNECTION_LOST_EVENT, "CONNECTION_LOST_EVENT");
        EVENT_CODES.addElement(PersistentSenderRT.SYNC_COMPLETION_EVENT, "SYNC_COMPLETION_EVENT");
    }

    public static final int SYNC_TYPE_NONE = 0;
    public static final int SYNC_TYPE_DAILY = 1;
    public static final int SYNC_TYPE_WEEKLY = 2;
    public static final int SYNC_TYPE_MONTHLY = 3;

    private static ExportCodes SYNC_TYPE_CODES = new ExportCodes();
    static {
        SYNC_TYPE_CODES.addElement(SYNC_TYPE_NONE, "SYNC_TYPE_NONE", "publisherEdit.persistent.sync.none");
        SYNC_TYPE_CODES.addElement(SYNC_TYPE_DAILY, "SYNC_TYPE_DAILY", "publisherEdit.persistent.sync.daily");
        SYNC_TYPE_CODES.addElement(SYNC_TYPE_WEEKLY, "SYNC_TYPE_WEEKLY", "publisherEdit.persistent.sync.weekly");
        SYNC_TYPE_CODES.addElement(SYNC_TYPE_MONTHLY, "SYNC_TYPE_MONTHLY", "publisherEdit.persistent.sync.monthly");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public LocalizableMessage getConfigDescription() {
        return new LocalizableMessage("common.default", host);
    }

    @Override
    public Type getType() {
        return PublisherVO.Type.PERSISTENT;
    }

    @Override
    public PublisherRT<PersistentPointVO> createPublisherRT() {
        return new PersistentSenderRT(this);
    }

    @Override
    protected PersistentPointVO createPublishedPointInstance() {
        return new PersistentPointVO();
    }

    @JsonRemoteProperty
    private String host;
    @JsonRemoteProperty
    private int port;
    @JsonRemoteProperty
    private String authorizationKey;
    @JsonRemoteProperty
    private String xidPrefix;
    private int syncType = SYNC_TYPE_DAILY;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAuthorizationKey() {
        return authorizationKey;
    }

    public void setAuthorizationKey(String authorizationKey) {
        this.authorizationKey = authorizationKey;
    }

    public String getXidPrefix() {
        return xidPrefix;
    }

    public void setXidPrefix(String xidPrefix) {
        this.xidPrefix = xidPrefix;
    }

    public int getSyncType() {
        return syncType;
    }

    public void setSyncType(int syncType) {
        this.syncType = syncType;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);

        if (StringUtils.isEmpty(host))
            response.addContextualMessage("host", "validate.required");
        if (port <= 0 || port >= 65536)
            response.addContextualMessage("port", "validate.illegalValue");

        if (!SYNC_TYPE_CODES.isValidId(syncType))
            response.addContextualMessage("syncType", "validate.invalidValue");
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 2;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, host);
        out.writeInt(port);
        SerializationHelper.writeSafeUTF(out, authorizationKey);
        SerializationHelper.writeSafeUTF(out, xidPrefix);
        out.writeInt(syncType);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            host = SerializationHelper.readSafeUTF(in);
            port = in.readInt();
            authorizationKey = SerializationHelper.readSafeUTF(in);
            xidPrefix = "";
            syncType = in.readInt();
        }
        else if (ver == 2) {
            host = SerializationHelper.readSafeUTF(in);
            port = in.readInt();
            authorizationKey = SerializationHelper.readSafeUTF(in);
            xidPrefix = SerializationHelper.readSafeUTF(in);
            syncType = in.readInt();
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        map.put("syncType", SYNC_TYPE_CODES.getCode(syncType));
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException, LocalizableJsonException {
        super.jsonDeserialize(reader, json);

        String text = json.getString("syncType");
        if (text != null) {
            syncType = SYNC_TYPE_CODES.getId(text);
            if (syncType == -1)
                throw new LocalizableJsonException("emport.error.invalid", "syncType", text,
                        SYNC_TYPE_CODES.getCodeList());
        }
    }
}
