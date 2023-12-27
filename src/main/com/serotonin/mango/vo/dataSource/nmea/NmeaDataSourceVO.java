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
package com.serotonin.mango.vo.dataSource.nmea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.nmea.NmeaDataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
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
public class NmeaDataSourceVO extends DataSourceVO<NmeaDataSourceVO> {
    public static final Type TYPE = Type.NMEA;

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(NmeaDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource")));
        ets.add(createEventType(NmeaDataSourceRT.PARSE_EXCEPTION_EVENT, new LocalizableMessage("event.ds.dataParse")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(NmeaDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(NmeaDataSourceRT.PARSE_EXCEPTION_EVENT, "PARSE_EXCEPTION");
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
        return new NmeaDataSourceRT(this);
    }

    @Override
    public NmeaPointLocatorVO createPointLocator() {
        return new NmeaPointLocatorVO();
    }

    @JsonRemoteProperty
    private String commPortId;
    @JsonRemoteProperty
    private int baudRate = 4800;
    @JsonRemoteProperty
    private int resetTimeout = 30;

    public String getCommPortId() {
        return commPortId;
    }

    public void setCommPortId(String commPortId) {
        this.commPortId = commPortId;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getResetTimeout() {
        return resetTimeout;
    }

    public void setResetTimeout(int resetTimeout) {
        this.resetTimeout = resetTimeout;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (StringUtils.isEmpty(commPortId))
            response.addContextualMessage("commPortId", "validate.required");
        if (resetTimeout < 2)
            response.addContextualMessage("resetTimeout", "validate.greaterThan1s");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.nmea.port", commPortId);
        AuditEventType.addPropertyMessage(list, "dsEdit.nmea.baud", baudRate);
        AuditEventType.addPropertyMessage(list, "dsEdit.nmea.resetTimeout", resetTimeout);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, NmeaDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.nmea.port", from.commPortId, commPortId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.nmea.baud", from.baudRate, baudRate);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.nmea.resetTimeout", from.resetTimeout, resetTimeout);
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
        out.writeInt(baudRate);
        out.writeInt(resetTimeout);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            commPortId = SerializationHelper.readSafeUTF(in);
            baudRate = in.readInt();
            resetTimeout = 30;
        }
        else if (ver == 2) {
            commPortId = SerializationHelper.readSafeUTF(in);
            baudRate = in.readInt();
            resetTimeout = in.readInt();
        }
    }
}
