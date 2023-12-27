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
package com.serotonin.mango.vo.publish;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonArray;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.json.JsonValue;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.PublisherDao;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.publish.PublisherRT;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.publish.httpSender.HttpSenderVO;
import com.serotonin.mango.vo.publish.pachube.PachubeSenderVO;
import com.serotonin.mango.vo.publish.persistent.PersistentSenderVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
abstract public class PublisherVO<T extends PublishedPointVO> implements Serializable, JsonSerializable {
    public enum Type {
        HTTP_SENDER(1, "publisherEdit.httpSender") {
            @Override
            public PublisherVO<?> createPublisherVO() {
                return new HttpSenderVO();
            }
        },
        PACHUBE(2, "publisherEdit.pachube") {
            @Override
            public PublisherVO<?> createPublisherVO() {
                return new PachubeSenderVO();
            }
        },
        PERSISTENT(3, "publisherEdit.persistent") {
            @Override
            public PublisherVO<?> createPublisherVO() {
                return new PersistentSenderVO();
            }
        };

        private Type(int id, String key) {
            this.id = id;
            this.key = key;
        }

        private final int id;
        private final String key;

        public int getId() {
            return id;
        }

        public String getKey() {
            return key;
        }

        public abstract PublisherVO<?> createPublisherVO();

        public static Type valueOf(int id) {
            for (Type type : values()) {
                if (type.id == id)
                    return type;
            }
            return null;
        }

        public static Type valueOfIgnoreCase(String text) {
            for (Type type : values()) {
                if (type.name().equalsIgnoreCase(text))
                    return type;
            }
            return null;
        }

        public static List<String> getTypeList() {
            List<String> result = new ArrayList<String>();
            for (Type type : values())
                result.add(type.name());
            return result;
        }
    }

    public static final String XID_PREFIX = "PUB_";

    public static PublisherVO<? extends PublishedPointVO> createPublisherVO(int typeId) {
        return Type.valueOf(typeId).createPublisherVO();
    }

    abstract public Type getType();

    abstract public LocalizableMessage getConfigDescription();

    abstract public PublisherRT<T> createPublisherRT();

    public LocalizableMessage getTypeMessage() {
        return new LocalizableMessage(getType().getKey());
    }

    public List<EventTypeVO> getEventTypes() {
        List<EventTypeVO> eventTypes = new ArrayList<EventTypeVO>();
        eventTypes.add(new EventTypeVO(EventType.EventSources.PUBLISHER, getId(), PublisherRT.POINT_DISABLED_EVENT,
                new LocalizableMessage("event.pb.pointMissing"), AlarmLevels.URGENT));
        eventTypes.add(new EventTypeVO(EventType.EventSources.PUBLISHER, getId(), PublisherRT.QUEUE_SIZE_WARNING_EVENT,
                new LocalizableMessage("event.pb.queueSize"), AlarmLevels.URGENT));

        getEventTypesImpl(eventTypes);

        return eventTypes;
    }

    protected static void addDefaultEventCodes(ExportCodes codes) {
        codes.addElement(PublisherRT.POINT_DISABLED_EVENT, "POINT_DISABLED_EVENT");
        codes.addElement(PublisherRT.QUEUE_SIZE_WARNING_EVENT, "QUEUE_SIZE_WARNING_EVENT");
    }

    abstract public ExportCodes getEventCodes();

    abstract protected void getEventTypesImpl(List<EventTypeVO> eventTypes);

    abstract protected T createPublishedPointInstance();

    public boolean isNew() {
        return id == Common.NEW_ID;
    }

    private int id = Common.NEW_ID;
    private String xid;
    @JsonRemoteProperty
    private String name;
    @JsonRemoteProperty
    private boolean enabled;
    protected List<T> points = new ArrayList<T>();
    @JsonRemoteProperty
    private boolean changesOnly;
    @JsonRemoteProperty
    private int cacheWarningSize = 100;
    @JsonRemoteProperty
    private boolean sendSnapshot;
    private int snapshotSendPeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int snapshotSendPeriods = 5;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<T> getPoints() {
        return points;
    }

    public void setPoints(List<T> points) {
        this.points = points;
    }

    public boolean isChangesOnly() {
        return changesOnly;
    }

    public void setChangesOnly(boolean changesOnly) {
        this.changesOnly = changesOnly;
    }

    public int getCacheWarningSize() {
        return cacheWarningSize;
    }

    public void setCacheWarningSize(int cacheWarningSize) {
        this.cacheWarningSize = cacheWarningSize;
    }

    public boolean isSendSnapshot() {
        return sendSnapshot;
    }

    public void setSendSnapshot(boolean sendSnapshot) {
        this.sendSnapshot = sendSnapshot;
    }

    public int getSnapshotSendPeriodType() {
        return snapshotSendPeriodType;
    }

    public void setSnapshotSendPeriodType(int snapshotSendPeriodType) {
        this.snapshotSendPeriodType = snapshotSendPeriodType;
    }

    public int getSnapshotSendPeriods() {
        return snapshotSendPeriods;
    }

    public void setSnapshotSendPeriods(int snapshotSendPeriods) {
        this.snapshotSendPeriods = snapshotSendPeriods;
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isEmpty(name))
            response.addContextualMessage("name", "validate.required");
        if (StringUtils.isLengthGreaterThan(name, 40))
            response.addContextualMessage("name", "validate.nameTooLong");

        if (StringUtils.isEmpty(xid))
            response.addContextualMessage("xid", "validate.required");
        else if (!new PublisherDao().isXidUnique(xid, id))
            response.addContextualMessage("xid", "validate.xidUsed");
        else if (StringUtils.isLengthGreaterThan(xid, 50))
            response.addContextualMessage("xid", "validate.notLongerThan", 50);

        if (sendSnapshot) {
            if (snapshotSendPeriods <= 0)
                response.addContextualMessage("snapshotSendPeriods", "validate.greaterThanZero");
        }

        if (cacheWarningSize < 1)
            response.addContextualMessage("cacheWarningSize", "validate.greaterThanZero");

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
        SerializationHelper.writeSafeUTF(out, name);
        out.writeBoolean(enabled);
        out.writeObject(points);
        out.writeBoolean(changesOnly);
        out.writeInt(cacheWarningSize);
        out.writeBoolean(sendSnapshot);
        out.writeInt(snapshotSendPeriodType);
        out.writeInt(snapshotSendPeriods);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            name = SerializationHelper.readSafeUTF(in);
            enabled = in.readBoolean();
            points = (List<T>) in.readObject();
            changesOnly = in.readBoolean();
            cacheWarningSize = in.readInt();
            sendSnapshot = false;
            snapshotSendPeriodType = Common.TimePeriods.MINUTES;
            snapshotSendPeriods = 5;
        }
        else if (ver == 2) {
            name = SerializationHelper.readSafeUTF(in);
            enabled = in.readBoolean();
            points = (List<T>) in.readObject();
            changesOnly = in.readBoolean();
            cacheWarningSize = in.readInt();
            sendSnapshot = in.readBoolean();
            snapshotSendPeriodType = in.readInt();
            snapshotSendPeriods = in.readInt();
        }
    }

    public void jsonSerialize(Map<String, Object> map) {
        map.put("xid", xid);
        map.put("type", getType().name());
        map.put("points", points);
        map.put("snapshotSendPeriodType", Common.TIME_PERIOD_CODES.getCode(snapshotSendPeriodType));
    }

    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException, LocalizableJsonException {
        JsonArray arr = json.getJsonArray("points");
        if (arr != null) {
            points.clear();
            for (JsonValue jv : arr.getElements()) {
                T point = createPublishedPointInstance();
                reader.populateObject(point, jv.toJsonObject());
                points.add(point);
            }
        }

        String text = json.getString("snapshotSendPeriodType");
        if (text != null) {
            snapshotSendPeriodType = Common.TIME_PERIOD_CODES.getId(text);
            if (snapshotSendPeriodType == -1)
                throw new LocalizableJsonException("emport.error.invalid", "snapshotSendPeriodType", text,
                        Common.TIME_PERIOD_CODES.getCodeList());
        }
    }
}
