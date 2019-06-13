/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.vo.dataSource.openv4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import net.sf.openv4j.DataPoint;
import net.sf.openv4j.Devices;
import net.sf.openv4j.Group;
import net.sf.openv4j.Protocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.openv4j.OpenV4JDataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class OpenV4JDataSourceVO extends DataSourceVO<OpenV4JDataSourceVO> {

    private final static Log LOG = LogFactory.getLog(OpenV4JDataSourceVO.class);
    private static final ExportCodes EVENT_CODES = new ExportCodes();

    static {
        EVENT_CODES.addElement(OpenV4JDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(OpenV4JDataSourceRT.POINT_READ_EXCEPTION_EVENT, "POINT_READ_EXCEPTION");
        EVENT_CODES.addElement(OpenV4JDataSourceRT.POINT_WRITE_EXCEPTION_EVENT, "POINT_WRITE_EXCEPTION");
    }
    @JsonRemoteProperty
    private String commPortId;
    private int updatePeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int updatePeriods = 1;
    @JsonRemoteProperty
    private Devices device;
    @JsonRemoteProperty
    private Protocol protocol;

    @Override
    public Type getType() {
        return Type.OPEN_V_4_J;
    }

    @Override
    protected void addEventTypes(List<EventTypeVO> eventTypes) {
        eventTypes.add(createEventType(OpenV4JDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource")));
        eventTypes.add(createEventType(OpenV4JDataSourceRT.POINT_READ_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.pointRead")));
        eventTypes.add(createEventType(OpenV4JDataSourceRT.POINT_WRITE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.pointWrite")));
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.default", commPortId);
    }

    @Override
    public PointLocatorVO createPointLocator() {
        return new OpenV4JPointLocatorVO();
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new OpenV4JDataSourceRT(this);
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.openv4j.port", commPortId);
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, OpenV4JDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.openv4j.port", from.commPortId, commPortId);
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType,
                from.updatePeriods, updatePeriodType, updatePeriods);
    }

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

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);

        if (StringUtils.isEmpty(commPortId)) {
            response.addContextualMessage("commPortId", "validate.required");
        }
        if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType)) {
            response.addContextualMessage("updatePeriodType", "validate.invalidValue");
        }
        if (updatePeriods <= 0) {
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero");
        }
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int SERIAL_VERSION = 1;

    // Serialization for saveDataSource
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(SERIAL_VERSION);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
        SerializationHelper.writeSafeUTF(out, commPortId);
        SerializationHelper.writeSafeUTF(out, device.name());
        SerializationHelper.writeSafeUTF(out, protocol.name());
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        switch (ver) {
        case 1:
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            commPortId = SerializationHelper.readSafeUTF(in);
            device = Devices.valueOf(SerializationHelper.readSafeUTF(in));
            protocol = Protocol.valueOf(SerializationHelper.readSafeUTF(in));
            break;
        case 2:
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            commPortId = SerializationHelper.readSafeUTF(in);
            device = Devices.valueOf(SerializationHelper.readSafeUTF(in));
            protocol = Protocol.valueOf(SerializationHelper.readSafeUTF(in));
            break;
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        LOG.info("WRITE TO JSON");
        super.jsonDeserialize(reader, json);
        LOG.info("SUPER TO JSON");
        Integer value = deserializeUpdatePeriodType(json);
        if (value != null) {
            updatePeriodType = value;
        }
        LOG.info("JSON OK");
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        serializeUpdatePeriodType(map, updatePeriodType);
    }

    /**
     * @return the device
     */
    public Devices getDevice() {
        return device;
    }

    /**
     * @param device
     *            the device to set
     */
    public void setDevice(Devices device) {
        this.device = device;
    }

    /**
     * @return the device
     */
    public Devices[] getDevices() {
        return Devices.values();
    }

    /**
     * @return the
     */
    public DataPoint[] getProperties(@SuppressWarnings("unused") Group g) {
        return DataPoint.values();
    }

    /**
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the protocols
     */
    public Protocol[] getProtocols() {
        return Protocol.values();
    }

    public Group[] getGroups() {
        return Group.values();
    }

    public DataPoint[] getDataPoints() {
        return DataPoint.values();
    }
}
