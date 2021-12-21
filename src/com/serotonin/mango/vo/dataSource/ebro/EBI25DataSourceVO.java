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
package com.serotonin.mango.vo.dataSource.ebro;

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
import com.serotonin.mango.rt.dataSource.ebro.EBI25DataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.modbus4j.base.ModbusUtils;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class EBI25DataSourceVO extends DataSourceVO<EBI25DataSourceVO> {
    public static final Type TYPE = Type.EBI25;

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new EBI25DataSourceRT(this);
    }

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(EBI25DataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(EBI25DataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public PointLocatorVO createPointLocator() {
        return new EBI25PointLocatorVO();
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.default", host + ":" + port);
    }

    @JsonRemoteProperty
    private String host;
    @JsonRemoteProperty
    private int port = ModbusUtils.TCP_PORT;
    @JsonRemoteProperty
    private boolean keepAlive = true;
    @JsonRemoteProperty
    private int updatePeriods = 5;
    private int updatePeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int timeout = 500;
    @JsonRemoteProperty
    private int retries = 2;

    // Read-only properties (i.e. not editable by users)
    private String serialNumber;
    private String productionDate;
    private String hardwareVersion;
    private String firmwareVersion;

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

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getUpdatePeriods() {
        return updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    public int getUpdatePeriodType() {
        return updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (StringUtils.isEmpty(host))
            response.addContextualMessage("host", "validate.required");
        if (port <= 0 || port > 0xffff)
            response.addContextualMessage("port", "validate.invalidValue");
        if (updatePeriods <= 0)
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero");
        if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType))
            response.addContextualMessage("updatePeriodType", "validate.invalidValue");
        if (timeout <= 0)
            response.addContextualMessage("timeout", "validate.greaterThanZero");
        if (retries < 0)
            response.addContextualMessage("retries", "validate.cannotBeNegative");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.ebi25.host", host);
        AuditEventType.addPropertyMessage(list, "dsEdit.ebi25.port", port);
        AuditEventType.addPropertyMessage(list, "dsEdit.ebi25.keepAlive", keepAlive);
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.ebi25.timeout", timeout);
        AuditEventType.addPropertyMessage(list, "dsEdit.ebi25.retries", retries);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, EBI25DataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.ebi25.host", from.host, host);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.ebi25.port", from.port, port);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.ebi25.keepAlive", from.keepAlive, keepAlive);
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, updatePeriods,
                from.updatePeriodType, updatePeriods);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.ebi25.timeout", from.timeout, timeout);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.ebi25.retries", from.retries, retries);
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
        SerializationHelper.writeSafeUTF(out, host);
        out.writeInt(port);
        out.writeBoolean(keepAlive);
        out.writeInt(updatePeriods);
        out.writeInt(updatePeriodType);
        out.writeInt(timeout);
        out.writeInt(retries);
        SerializationHelper.writeSafeUTF(out, serialNumber);
        SerializationHelper.writeSafeUTF(out, productionDate);
        SerializationHelper.writeSafeUTF(out, hardwareVersion);
        SerializationHelper.writeSafeUTF(out, firmwareVersion);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            host = SerializationHelper.readSafeUTF(in);
            port = in.readInt();
            keepAlive = in.readBoolean();
            updatePeriods = in.readInt();
            updatePeriodType = in.readInt();
            timeout = in.readInt();
            retries = in.readInt();
            serialNumber = SerializationHelper.readSafeUTF(in);
            productionDate = SerializationHelper.readSafeUTF(in);
            hardwareVersion = SerializationHelper.readSafeUTF(in);
            firmwareVersion = SerializationHelper.readSafeUTF(in);
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
