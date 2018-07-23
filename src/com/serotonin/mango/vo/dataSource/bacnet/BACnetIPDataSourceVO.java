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
package com.serotonin.mango.vo.dataSource.bacnet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.bacnet.BACnetIPDataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.IpAddressUtils;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class BACnetIPDataSourceVO extends DataSourceVO<BACnetIPDataSourceVO> {
    public static final Type TYPE = Type.BACNET;

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(BACnetIPDataSourceRT.INITIALIZATION_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.initialization")));
        ets.add(createEventType(BACnetIPDataSourceRT.MESSAGE_EXCEPTION_EVENT,
                new LocalizableMessage("event.ds.message")));
        ets
                .add(createEventType(BACnetIPDataSourceRT.DEVICE_EXCEPTION_EVENT, new LocalizableMessage(
                        "event.ds.device")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(BACnetIPDataSourceRT.INITIALIZATION_EXCEPTION_EVENT, "INITIALIZATION_EXCEPTION");
        EVENT_CODES.addElement(BACnetIPDataSourceRT.MESSAGE_EXCEPTION_EVENT, "MESSAGE_EXCEPTION");
        EVENT_CODES.addElement(BACnetIPDataSourceRT.DEVICE_EXCEPTION_EVENT, "DEVICE_EXCEPTION");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("dsEdit.bacnetIp.dsconn", deviceId);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new BACnetIPDataSourceRT(this);
    }

    @Override
    public BACnetIPPointLocatorVO createPointLocator() {
        return new BACnetIPPointLocatorVO();
    }

    private int updatePeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int updatePeriods = 5;
    @JsonRemoteProperty
    private int deviceId;
    @JsonRemoteProperty
    private String broadcastAddress;
    @JsonRemoteProperty
    private int port;
    @JsonRemoteProperty
    private int timeout;
    @JsonRemoteProperty
    private int segTimeout;
    @JsonRemoteProperty
    private int segWindow;
    @JsonRemoteProperty
    private int retries;
    @JsonRemoteProperty
    private int covSubscriptionTimeoutMinutes = 60; // One hour
    @JsonRemoteProperty
    private int maxReadMultipleReferencesSegmented = 200;
    @JsonRemoteProperty
    private int maxReadMultipleReferencesNonsegmented = 20; // One hour

    public BACnetIPDataSourceVO() {
        // Create a local device object from which to get default values.
        LocalDevice localDevice = new LocalDevice(0, null);
        broadcastAddress = "255.255.255.255";
        port = localDevice.getPort();
        timeout = localDevice.getTimeout();
        segTimeout = localDevice.getSegTimeout();
        segWindow = localDevice.getSegWindow();
        retries = localDevice.getRetries();
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

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    public void setBroadcastAddress(String broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getCovSubscriptionTimeoutMinutes() {
        return covSubscriptionTimeoutMinutes;
    }

    public void setCovSubscriptionTimeoutMinutes(int covSubscriptionTimeoutMinutes) {
        this.covSubscriptionTimeoutMinutes = covSubscriptionTimeoutMinutes;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getSegTimeout() {
        return segTimeout;
    }

    public void setSegTimeout(int segTimeout) {
        this.segTimeout = segTimeout;
    }

    public int getSegWindow() {
        return segWindow;
    }

    public void setSegWindow(int segWindow) {
        this.segWindow = segWindow;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getMaxReadMultipleReferencesSegmented() {
        return maxReadMultipleReferencesSegmented;
    }

    public void setMaxReadMultipleReferencesSegmented(int maxReadMultipleReferencesSegmented) {
        this.maxReadMultipleReferencesSegmented = maxReadMultipleReferencesSegmented;
    }

    public int getMaxReadMultipleReferencesNonsegmented() {
        return maxReadMultipleReferencesNonsegmented;
    }

    public void setMaxReadMultipleReferencesNonsegmented(int maxReadMultipleReferencesNonsegmented) {
        this.maxReadMultipleReferencesNonsegmented = maxReadMultipleReferencesNonsegmented;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);

        if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType))
            response.addContextualMessage("updatePeriodType", "validate.invalidValue");

        if (updatePeriods <= 0)
            response.addContextualMessage("updatePeriods", "validate.cannotBeNegative");

        try {
            new LocalDevice(deviceId, broadcastAddress);
        }
        catch (IllegalArgumentException e) {
            response.addContextualMessage("deviceId", "validate.illegalValue");
        }

        try {
            IpAddressUtils.toIpAddress(broadcastAddress);
        }
        catch (IllegalArgumentException e) {
            response.addContextualMessage("broadcastAddress", "common.default", e.getMessage());
        }

        try {
            new InetSocketAddress(broadcastAddress, port);
        }
        catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("port"))
                response.addContextualMessage("port", "validate.illegalValue");
            else
                response.addContextualMessage("broadcastAddress", "validate.illegalValue");
        }

        if (timeout < 0)
            response.addContextualMessage("timeout", "validate.cannotBeNegative");
        if (segTimeout < 0)
            response.addContextualMessage("segTimeout", "validate.cannotBeNegative");
        if (segWindow < 1)
            response.addContextualMessage("segWindow", "validate.greaterThanZero");
        if (retries < 0)
            response.addContextualMessage("retries", "validate.cannotBeNegative");
        if (covSubscriptionTimeoutMinutes < 1)
            response.addContextualMessage("covSubscriptionTimeoutMinutes", "validate.greaterThanZero");
        if (maxReadMultipleReferencesSegmented < 1)
            response.addContextualMessage("maxReadMultipleReferencesSegmented", "validate.greaterThanZero");
        if (maxReadMultipleReferencesNonsegmented < 1)
            response.addContextualMessage("maxReadMultipleReferencesNonsegmented", "validate.greaterThanZero");
    }

    @Override
    public void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.deviceId", deviceId);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.broadcastAddress", broadcastAddress);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.port", port);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.timeout", timeout);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.segmentTimeout", segTimeout);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.segmentWindow", segWindow);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.retries", retries);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.covLease", covSubscriptionTimeoutMinutes);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.maxReadMultSeg", maxReadMultipleReferencesSegmented);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.maxReadMultNonseg",
                maxReadMultipleReferencesNonsegmented);
    }

    @Override
    public void addPropertyChangesImpl(List<LocalizableMessage> list, BACnetIPDataSourceVO from) {
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType,
                from.updatePeriods, updatePeriodType, updatePeriods);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.deviceId", from.deviceId, deviceId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.broadcastAddress", from.broadcastAddress,
                broadcastAddress);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.port", from.port, port);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.timeout", from.timeout, timeout);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.segmentTimeout", from.segTimeout,
                segTimeout);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.segmentWindow", from.segWindow, segWindow);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.retries", from.retries, retries);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.covLease",
                from.covSubscriptionTimeoutMinutes, covSubscriptionTimeoutMinutes);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.maxReadMultSeg",
                from.maxReadMultipleReferencesSegmented, maxReadMultipleReferencesSegmented);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.maxReadMultNonseg",
                from.maxReadMultipleReferencesNonsegmented, maxReadMultipleReferencesNonsegmented);
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
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
        out.writeInt(deviceId);
        SerializationHelper.writeSafeUTF(out, broadcastAddress);
        out.writeInt(port);
        out.writeInt(timeout);
        out.writeInt(segTimeout);
        out.writeInt(segWindow);
        out.writeInt(retries);
        out.writeInt(covSubscriptionTimeoutMinutes);
        out.writeInt(maxReadMultipleReferencesSegmented);
        out.writeInt(maxReadMultipleReferencesNonsegmented);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            deviceId = in.readInt();
            broadcastAddress = SerializationHelper.readSafeUTF(in);
            port = in.readInt();
            timeout = in.readInt();
            segTimeout = in.readInt();
            segWindow = in.readInt();
            retries = in.readInt();
            covSubscriptionTimeoutMinutes = in.readInt();
            maxReadMultipleReferencesSegmented = 200;
            maxReadMultipleReferencesNonsegmented = 20;
        }
        else if (ver == 2) {
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            deviceId = in.readInt();
            broadcastAddress = SerializationHelper.readSafeUTF(in);
            port = in.readInt();
            timeout = in.readInt();
            segTimeout = in.readInt();
            segWindow = in.readInt();
            retries = in.readInt();
            covSubscriptionTimeoutMinutes = in.readInt();
            maxReadMultipleReferencesSegmented = in.readInt();
            maxReadMultipleReferencesNonsegmented = in.readInt();
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
