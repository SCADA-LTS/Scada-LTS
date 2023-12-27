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
package com.serotonin.mango.vo.dataSource.jmx;

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
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.jmx.JmxDataSourceRT;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.ExportCodes;
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
public class JmxDataSourceVO extends DataSourceVO<JmxDataSourceVO> {
    public static final Type TYPE = Type.JMX;

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(JmxDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource"), EventType.DuplicateHandling.IGNORE_SAME_MESSAGE, AlarmLevels.URGENT));
        ets.add(createEventType(JmxDataSourceRT.POINT_READ_EXCEPTION_EVENT,
                new LocalizableMessage("event.ds.pointRead")));
        ets.add(createEventType(JmxDataSourceRT.POINT_WRITE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.pointWrite")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(JmxDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(JmxDataSourceRT.POINT_READ_EXCEPTION_EVENT, "POINT_READ_EXCEPTION");
        EVENT_CODES.addElement(JmxDataSourceRT.POINT_WRITE_EXCEPTION_EVENT, "POINT_WRITE_EXCEPTION");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        if (useLocalServer)
            return new LocalizableMessage("dsEdit.jmx.dsconn.local");
        return new LocalizableMessage("dsEdit.jmx.dsconn.remote", remoteServerAddr);
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new JmxDataSourceRT(this);
    }

    @Override
    public JmxPointLocatorVO createPointLocator() {
        return new JmxPointLocatorVO();
    }

    @JsonRemoteProperty
    private boolean useLocalServer;
    @JsonRemoteProperty
    private String remoteServerAddr;
    private int updatePeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int updatePeriods = 5;
    @JsonRemoteProperty
    private boolean quantize;

    public boolean isUseLocalServer() {
        return useLocalServer;
    }

    public void setUseLocalServer(boolean useLocalServer) {
        this.useLocalServer = useLocalServer;
    }

    public String getRemoteServerAddr() {
        return remoteServerAddr;
    }

    public void setRemoteServerAddr(String remoteServerAddr) {
        this.remoteServerAddr = remoteServerAddr;
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

    public boolean isQuantize() {
        return quantize;
    }

    public void setQuantize(boolean quantize) {
        this.quantize = quantize;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);

        if (!useLocalServer && StringUtils.isEmpty(remoteServerAddr))
            response.addContextualMessage("remoteServerAddr", "validate.required");
        if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType))
            response.addContextualMessage("updatePeriodType", "validate.invalidValue");
        if (updatePeriods <= 0)
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.jmx.useLocalServer", useLocalServer);
        AuditEventType.addPropertyMessage(list, "dsEdit.jmx.remoteServerAddr", remoteServerAddr);
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.quantize", quantize);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, JmxDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.jmx.useLocalServer", from.useLocalServer,
                useLocalServer);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.jmx.remoteServerAddr", from.remoteServerAddr,
                remoteServerAddr);
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType,
                from.updatePeriods, updatePeriodType, updatePeriods);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.quantize", from.quantize, quantize);
    }

    //
    //
    // Serialization
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeBoolean(useLocalServer);
        SerializationHelper.writeSafeUTF(out, remoteServerAddr);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
        out.writeBoolean(quantize);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            useLocalServer = in.readBoolean();
            remoteServerAddr = SerializationHelper.readSafeUTF(in);
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            quantize = in.readBoolean();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);
        Integer value = deserializeUpdatePeriodType(json);
        if (value != null)
            updatePeriodType = value;
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        serializeUpdatePeriodType(map, updatePeriodType);
    }
}
