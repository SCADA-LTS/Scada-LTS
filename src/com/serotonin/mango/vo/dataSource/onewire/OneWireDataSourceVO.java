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
package com.serotonin.mango.vo.dataSource.onewire;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.onewire.OneWireDataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class OneWireDataSourceVO extends DataSourceVO<OneWireDataSourceVO> {
    public static final Type TYPE = Type.ONE_WIRE;
    public static final int RESCAN_NONE = 0;
    public static final String RESCAN_NONE_TEXT = "NONE";

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(OneWireDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource")));
        ets.add(createEventType(OneWireDataSourceRT.POINT_READ_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.pointRead")));
        ets.add(createEventType(OneWireDataSourceRT.POINT_WRITE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.pointWrite")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(OneWireDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(OneWireDataSourceRT.POINT_READ_EXCEPTION_EVENT, "POINT_READ_EXCEPTION");
        EVENT_CODES.addElement(OneWireDataSourceRT.POINT_WRITE_EXCEPTION_EVENT, "POINT_WRITE_EXCEPTION");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.default", commPortId);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new OneWireDataSourceRT(this);
    }

    @Override
    public OneWirePointLocatorVO createPointLocator() {
        return new OneWirePointLocatorVO();
    }

    @JsonRemoteProperty
    private String commPortId;
    private int updatePeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int updatePeriods = 5;
    private int rescanPeriodType = RESCAN_NONE;
    @JsonRemoteProperty
    private int rescanPeriods = 1;

    public String getCommPortId() {
        return commPortId;
    }

    public void setCommPortId(String commPortId) {
        this.commPortId = commPortId;
    }

    public int getUpdatePeriodType() {
        return updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getUpdatePeriods() {
        return updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    public int getRescanPeriodType() {
        return rescanPeriodType;
    }

    public void setRescanPeriodType(int rescanPeriodType) {
        this.rescanPeriodType = rescanPeriodType;
    }

    public int getRescanPeriods() {
        return rescanPeriods;
    }

    public void setRescanPeriods(int rescanPeriods) {
        this.rescanPeriods = rescanPeriods;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);

        if (StringUtils.isEmpty(commPortId))
            response.addContextualMessage("commPortId", "validate.required");
        if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType))
            response.addContextualMessage("updatePeriodType", "validate.invalidValue");
        if (updatePeriods <= 0)
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero");
        if (rescanPeriodType != RESCAN_NONE && rescanPeriods <= 0)
            response.addContextualMessage("rescanPeriods", "validate.greaterThanZero");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.1wire.port", commPortId);
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
        if (rescanPeriodType == RESCAN_NONE)
            AuditEventType.addPropertyMessage(list, "dsEdit.1wire.scheduledRescan", new LocalizableMessage(
                    "dsEdit.1wire.none"));
        else
            AuditEventType.addPeriodMessage(list, "dsEdit.1wire.scheduledRescan", rescanPeriodType, rescanPeriods);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, OneWireDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.1wire.port", from.commPortId, commPortId);
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType,
                from.updatePeriods, updatePeriodType, updatePeriods);
        if (from.rescanPeriodType != rescanPeriodType || from.rescanPeriods != rescanPeriods) {
            LocalizableMessage fromMessage;
            if (from.rescanPeriodType == RESCAN_NONE)
                fromMessage = new LocalizableMessage("dsEdit.1wire.none");
            else
                fromMessage = Common.getPeriodDescription(from.rescanPeriodType, from.rescanPeriods);

            LocalizableMessage toMessage;
            if (rescanPeriodType == RESCAN_NONE)
                toMessage = new LocalizableMessage("dsEdit.1wire.none");
            else
                toMessage = Common.getPeriodDescription(rescanPeriodType, rescanPeriods);

            AuditEventType.addPropertyChangeMessage(list, "dsEdit.1wire.scheduledRescan", fromMessage, toMessage);
        }
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
        SerializationHelper.writeSafeUTF(out, commPortId);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
        out.writeInt(rescanPeriodType);
        out.writeInt(rescanPeriods);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            commPortId = SerializationHelper.readSafeUTF(in);
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            rescanPeriodType = RESCAN_NONE;
            rescanPeriods = 1;
        }
        else if (ver == 2) {
            commPortId = SerializationHelper.readSafeUTF(in);
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            rescanPeriodType = in.readInt();
            rescanPeriods = in.readInt();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);

        Integer value = deserializeUpdatePeriodType(json);
        if (value != null)
            updatePeriodType = value;

        String text = json.getString("rescanPeriodType");
        if (text != null) {
            if (RESCAN_NONE_TEXT.equalsIgnoreCase(text))
                rescanPeriodType = RESCAN_NONE;
            else {
                rescanPeriodType = Common.TIME_PERIOD_CODES.getId(text);
                if (rescanPeriodType == -1) {
                    List<String> result = new ArrayList<String>();
                    result.add(RESCAN_NONE_TEXT);
                    result.addAll(Common.TIME_PERIOD_CODES.getCodeList());
                    throw new LocalizableJsonException("emport.error.invalid", "rescanPeriodType", text, result);
                }
            }
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        serializeUpdatePeriodType(map, updatePeriodType);

        if (rescanPeriodType == RESCAN_NONE)
            map.put("rescanPeriodType", RESCAN_NONE_TEXT);
        else
            map.put("rescanPeriodType", Common.TIME_PERIOD_CODES.getCode(rescanPeriodType));
    }
}
