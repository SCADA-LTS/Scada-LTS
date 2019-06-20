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
import java.util.List;
import java.util.Map;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.base.BACnetUtils;
import com.serotonin.bacnet4j.obj.ObjectProperties;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.bacnet.BACnetIPPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.IpAddressUtils;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class BACnetIPPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public PointLocatorRT createRuntime() {
        return new BACnetIPPointLocatorRT(this);
    }

    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", remoteDeviceIp);
    }

    @JsonRemoteProperty
    private String remoteDeviceIp;
    @JsonRemoteProperty
    private int remoteDevicePort = LocalDevice.DEFAULT_PORT;
    @JsonRemoteProperty
    private int networkNumber;
    @JsonRemoteProperty
    private String networkAddress;
    @JsonRemoteProperty
    private int remoteDeviceInstanceNumber;
    private int objectTypeId;
    @JsonRemoteProperty
    private int objectInstanceNumber;
    private int propertyIdentifierId = PropertyIdentifier.presentValue.intValue();
    @JsonRemoteProperty
    private boolean useCovSubscription;
    @JsonRemoteProperty
    private boolean settable;
    @JsonRemoteProperty
    private int writePriority = 16;
    private int dataTypeId;

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public String getRemoteDeviceIp() {
        return remoteDeviceIp;
    }

    public void setRemoteDeviceIp(String remoteDeviceIp) {
        this.remoteDeviceIp = remoteDeviceIp;
    }

    public int getRemoteDevicePort() {
        return remoteDevicePort;
    }

    public void setRemoteDevicePort(int remoteDevicePort) {
        this.remoteDevicePort = remoteDevicePort;
    }

    public int getNetworkNumber() {
        return networkNumber;
    }

    public void setNetworkNumber(int networkNumber) {
        this.networkNumber = networkNumber;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public void setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
    }

    public int getRemoteDeviceInstanceNumber() {
        return remoteDeviceInstanceNumber;
    }

    public void setRemoteDeviceInstanceNumber(int remoteDeviceInstanceNumber) {
        this.remoteDeviceInstanceNumber = remoteDeviceInstanceNumber;
    }

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public int getObjectInstanceNumber() {
        return objectInstanceNumber;
    }

    public void setObjectInstanceNumber(int objectInstanceNumber) {
        this.objectInstanceNumber = objectInstanceNumber;
    }

    public int getPropertyIdentifierId() {
        return propertyIdentifierId;
    }

    public void setPropertyIdentifierId(int propertyIdentifierId) {
        this.propertyIdentifierId = propertyIdentifierId;
    }

    public boolean isUseCovSubscription() {
        return useCovSubscription;
    }

    public void setUseCovSubscription(boolean useCovSubscription) {
        this.useCovSubscription = useCovSubscription;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public boolean isSettable() {
        return settable;
    }

    public int getWritePriority() {
        return writePriority;
    }

    public void setWritePriority(int writePriority) {
        this.writePriority = writePriority;
    }

    @Override
    public boolean isRelinquishable() {
        return ObjectProperties.isCommandable(new ObjectType(objectTypeId),
                new PropertyIdentifier(propertyIdentifierId));
    }

    public void validate(DwrResponseI18n response) {
        try {
            IpAddressUtils.toIpAddress(remoteDeviceIp);
        }
        catch (IllegalArgumentException e) {
            response.addContextualMessage("remoteDeviceIp", "common.default", e.getMessage());
        }

        if (remoteDevicePort < 0 || remoteDevicePort > 65535)
            response.addContextualMessage("remoteDevicePort", "validate.illegalValue");

        if (!StringUtils.isEmpty(networkAddress)) {
            if (networkNumber < 0)
                response.addContextualMessage("networkNumber", "validate.illegalValue");

            try {
                BACnetUtils.dottedStringToBytes(networkAddress);
            }
            catch (NumberFormatException e) {
                response.addContextualMessage("networkAddress", "validate.illegalValue");
            }
        }

        try {
            new ObjectIdentifier(null, remoteDeviceInstanceNumber);
        }
        catch (IllegalArgumentException e) {
            response.addContextualMessage("remoteDeviceInstanceNumber", "validate.illegalValue");
        }

        if (!OBJECT_TYPE_CODES.isValidId(objectTypeId))
            response.addContextualMessage("objectTypeId", "validate.invalidValue");

        if (!PROPERTY_TYPE_CODES.isValidId(propertyIdentifierId))
            response.addContextualMessage("propertyIdentifierId", "validate.invalidValue");

        try {
            new ObjectIdentifier(null, objectInstanceNumber);
        }
        catch (IllegalArgumentException e) {
            response.addContextualMessage("objectInstanceNumber", "validate.illegalValue");
        }

        if (!DataTypes.CODES.isValidId(dataTypeId))
            response.addContextualMessage("dataTypeId", "validate.invalidValue");

        if (writePriority < 1 || writePriority > 16)
            response.addContextualMessage("writePriority", "validate.illegalValue");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.remoteDeviceIp", remoteDeviceIp);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.remoteDevicePort", remoteDevicePort);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.remoteDeviceNetworkNumber", networkNumber);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.remoteDeviceNetworkAddress", networkAddress);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.remoteDeviceInstanceNumber",
                remoteDeviceInstanceNumber);
        AuditEventType.addExportCodeMessage(list, "dsEdit.bacnetIp.objectType", OBJECT_TYPE_CODES, objectTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.objectInstanceNumber", objectInstanceNumber);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.useCov", useCovSubscription);
        AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", dataTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.bacnetIp.writePriority", writePriority);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        BACnetIPPointLocatorVO from = (BACnetIPPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.remoteDeviceIp", from.remoteDeviceIp,
                remoteDeviceIp);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.remoteDevicePort", from.remoteDevicePort,
                remoteDevicePort);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.remoteDeviceNetworkNumber",
                from.networkNumber, networkNumber);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.remoteDeviceNetworkAddress",
                from.networkAddress, networkAddress);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.remoteDeviceInstanceNumber",
                from.remoteDeviceInstanceNumber, remoteDeviceInstanceNumber);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.points.objectType", OBJECT_TYPE_CODES,
                from.objectTypeId, objectTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.objectInstanceNumber",
                from.objectInstanceNumber, objectInstanceNumber);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.points.objectType", OBJECT_TYPE_CODES,
                from.objectTypeId, objectTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.useCov", from.useCovSubscription,
                useCovSubscription);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, settable);
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.bacnetIp.writePriority", from.writePriority,
                writePriority);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 4;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, remoteDeviceIp);
        out.writeInt(remoteDevicePort);
        out.writeInt(networkNumber);
        SerializationHelper.writeSafeUTF(out, networkAddress);
        out.writeInt(remoteDeviceInstanceNumber);
        out.writeInt(objectTypeId);
        out.writeInt(objectInstanceNumber);
        out.writeInt(propertyIdentifierId);
        out.writeBoolean(useCovSubscription);
        out.writeBoolean(settable);
        out.writeInt(dataTypeId);
        out.writeInt(writePriority);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            remoteDeviceIp = SerializationHelper.readSafeUTF(in);
            remoteDevicePort = in.readInt();
            networkNumber = 0;
            networkAddress = null;
            remoteDeviceInstanceNumber = 0;
            objectTypeId = in.readInt();
            objectInstanceNumber = in.readInt();
            propertyIdentifierId = in.readInt();
            useCovSubscription = in.readBoolean();
            settable = in.readBoolean();
            dataTypeId = in.readInt();
            writePriority = 16;
        }
        else if (ver == 2) {
            remoteDeviceIp = SerializationHelper.readSafeUTF(in);
            remoteDevicePort = in.readInt();
            networkNumber = 0;
            networkAddress = null;
            remoteDeviceInstanceNumber = 0;
            objectTypeId = in.readInt();
            objectInstanceNumber = in.readInt();
            propertyIdentifierId = in.readInt();
            useCovSubscription = in.readBoolean();
            settable = in.readBoolean();
            dataTypeId = in.readInt();
            writePriority = in.readInt();
        }
        else if (ver == 3) {
            remoteDeviceIp = SerializationHelper.readSafeUTF(in);
            remoteDevicePort = in.readInt();
            networkNumber = 0;
            networkAddress = null;
            remoteDeviceInstanceNumber = in.readInt();
            objectTypeId = in.readInt();
            objectInstanceNumber = in.readInt();
            propertyIdentifierId = in.readInt();
            useCovSubscription = in.readBoolean();
            settable = in.readBoolean();
            dataTypeId = in.readInt();
            writePriority = in.readInt();
        }
        else if (ver == 4) {
            remoteDeviceIp = SerializationHelper.readSafeUTF(in);
            remoteDevicePort = in.readInt();
            networkNumber = in.readInt();
            networkAddress = SerializationHelper.readSafeUTF(in);
            remoteDeviceInstanceNumber = in.readInt();
            objectTypeId = in.readInt();
            objectInstanceNumber = in.readInt();
            propertyIdentifierId = in.readInt();
            useCovSubscription = in.readBoolean();
            settable = in.readBoolean();
            dataTypeId = in.readInt();
            writePriority = in.readInt();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        Integer value = deserializeDataType(json, DataTypes.IMAGE);
        if (value != null)
            dataTypeId = value;

        String text = json.getString("objectType");
        if (text != null) {
            objectTypeId = OBJECT_TYPE_CODES.getId(text);
            if (objectTypeId == -1)
                throw new LocalizableJsonException("emport.error.invalid", "objectType", text,
                        OBJECT_TYPE_CODES.getCodeList());
        }

        text = json.getString("propertyIdentifier");
        if (text != null) {
            propertyIdentifierId = PROPERTY_TYPE_CODES.getId(text);
            if (propertyIdentifierId == -1)
                throw new LocalizableJsonException("emport.error.invalid", "propertyIdentifier", text,
                        PROPERTY_TYPE_CODES.getCodeList());
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        serializeDataType(map);
        map.put("objectType", OBJECT_TYPE_CODES.getCode(objectTypeId));
        map.put("propertyIdentifier", PROPERTY_TYPE_CODES.getCode(propertyIdentifierId));
    }

    private static ExportCodes OBJECT_TYPE_CODES = new ExportCodes();
    static {
        OBJECT_TYPE_CODES.addElement(ObjectType.analogInput.intValue(), "ANALOG_INPUT",
                "dsEdit.bacnetIp.objectType.analogInput");
        OBJECT_TYPE_CODES.addElement(ObjectType.analogOutput.intValue(), "ANALOG_OUTPUT",
                "dsEdit.bacnetIp.objectType.analogOutput");
        OBJECT_TYPE_CODES.addElement(ObjectType.analogValue.intValue(), "ANALOG_VALUE",
                "dsEdit.bacnetIp.objectType.analogValue");
        OBJECT_TYPE_CODES.addElement(ObjectType.binaryInput.intValue(), "BINARY_INPUT",
                "dsEdit.bacnetIp.objectType.binaryInput");
        OBJECT_TYPE_CODES.addElement(ObjectType.binaryOutput.intValue(), "BINARY_OUTPUT",
                "dsEdit.bacnetIp.objectType.binaryOutput");
        OBJECT_TYPE_CODES.addElement(ObjectType.binaryValue.intValue(), "BINARY_VALUE",
                "dsEdit.bacnetIp.objectType.binaryValue");
        OBJECT_TYPE_CODES.addElement(ObjectType.calendar.intValue(), "CALENDAR", "dsEdit.bacnetIp.objectType.calendar");
        OBJECT_TYPE_CODES.addElement(ObjectType.command.intValue(), "COMMAND", "dsEdit.bacnetIp.objectType.command");
        OBJECT_TYPE_CODES.addElement(ObjectType.device.intValue(), "DEVICE", "dsEdit.bacnetIp.objectType.device");
        OBJECT_TYPE_CODES.addElement(ObjectType.eventEnrollment.intValue(), "EVENT_ENROLLMENT",
                "dsEdit.bacnetIp.objectType.eventEnrollment");
        OBJECT_TYPE_CODES.addElement(ObjectType.file.intValue(), "FILE", "dsEdit.bacnetIp.objectType.file");
        OBJECT_TYPE_CODES.addElement(ObjectType.group.intValue(), "GROUP", "dsEdit.bacnetIp.objectType.group");
        OBJECT_TYPE_CODES.addElement(ObjectType.loop.intValue(), "LOOP", "dsEdit.bacnetIp.objectType.loop");
        OBJECT_TYPE_CODES.addElement(ObjectType.multiStateInput.intValue(), "MULTISTATE_INPUT",
                "dsEdit.bacnetIp.objectType.multiStateInput");
        OBJECT_TYPE_CODES.addElement(ObjectType.multiStateOutput.intValue(), "MULTISTATE_OUTPUT",
                "dsEdit.bacnetIp.objectType.multiStateOutput");
        OBJECT_TYPE_CODES.addElement(ObjectType.notificationClass.intValue(), "NOTIFICATION_CLASS",
                "dsEdit.bacnetIp.objectType.notificationClass");
        OBJECT_TYPE_CODES.addElement(ObjectType.program.intValue(), "PROGRAM", "dsEdit.bacnetIp.objectType.program");
        OBJECT_TYPE_CODES.addElement(ObjectType.schedule.intValue(), "SCHEDULE", "dsEdit.bacnetIp.objectType.schedule");
        OBJECT_TYPE_CODES.addElement(ObjectType.averaging.intValue(), "AVERAGING",
                "dsEdit.bacnetIp.objectType.averaging");
        OBJECT_TYPE_CODES.addElement(ObjectType.multiStateValue.intValue(), "MULTISTATE_VALUE",
                "dsEdit.bacnetIp.objectType.multiStateValue");
        OBJECT_TYPE_CODES
                .addElement(ObjectType.trendLog.intValue(), "TREND_LOG", "dsEdit.bacnetIp.objectType.trendLog");
        OBJECT_TYPE_CODES.addElement(ObjectType.lifeSafetyPoint.intValue(), "LIFE_SAFETY_POINT",
                "dsEdit.bacnetIp.objectType.lifeSafetyPoint");
        OBJECT_TYPE_CODES.addElement(ObjectType.lifeSafetyZone.intValue(), "LIFE_SAFETY_ZONE",
                "dsEdit.bacnetIp.objectType.lifeSafetyZone");
        OBJECT_TYPE_CODES.addElement(ObjectType.accumulator.intValue(), "ACCUMULATOR",
                "dsEdit.bacnetIp.objectType.accumulator");
        OBJECT_TYPE_CODES.addElement(ObjectType.pulseConverter.intValue(), "PULSE_CONVERTER",
                "dsEdit.bacnetIp.objectType.pulseConverter");
        OBJECT_TYPE_CODES
                .addElement(ObjectType.eventLog.intValue(), "EVENT_LOG", "dsEdit.bacnetIp.objectType.eventLog");
        OBJECT_TYPE_CODES.addElement(ObjectType.trendLogMultiple.intValue(), "TREND_LOG_MULTIPLE",
                "dsEdit.bacnetIp.objectType.trendLogMultiple");
        OBJECT_TYPE_CODES.addElement(ObjectType.loadControl.intValue(), "LOAD_CONTROL",
                "dsEdit.bacnetIp.objectType.loadControl");
        OBJECT_TYPE_CODES.addElement(ObjectType.structuredView.intValue(), "STRUCTURE_VIEW",
                "dsEdit.bacnetIp.objectType.structuredView");
        OBJECT_TYPE_CODES.addElement(ObjectType.accessDoor.intValue(), "ACCESS_DOOR",
                "dsEdit.bacnetIp.objectType.accessDoor");
    }

    private static ExportCodes PROPERTY_TYPE_CODES = new ExportCodes();
    static {
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.ackedTransitions.intValue(), "ACKED_TRANSITIONS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.ackRequired.intValue(), "ACK_REQUIRED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.action.intValue(), "ACTION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.actionText.intValue(), "ACTION_TEXT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.activeText.intValue(), "ACTIVE_TEXT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.activeVtSessions.intValue(), "ACTIVE_VT_SESSIONS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.alarmValue.intValue(), "ALARM_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.alarmValues.intValue(), "ALARM_VALUES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.all.intValue(), "ALL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.allWritesSuccessful.intValue(), "ALL_WRITES_SUCCESSFUL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.apduSegmentTimeout.intValue(), "APDU_SEGMENT_TIMEOUT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.apduTimeout.intValue(), "APDU_TIMEOUT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.applicationSoftwareVersion.intValue(),
                "APPLICATION_SOFTWARE_VERSION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.archive.intValue(), "ARCHIVE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.bias.intValue(), "BIAS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.changeOfStateCount.intValue(), "CHANGE_OF_STATE_COUNT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.changeOfStateTime.intValue(), "CHANGE_OF_STATE_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.notificationClass.intValue(), "NOTIFICATION_CLASS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.controlledVariableReference.intValue(),
                "CONTROLLED_VARIABLE_REFERENCE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.controlledVariableUnits.intValue(),
                "CONTROLLED_VARIABLE_UNITS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.controlledVariableValue.intValue(),
                "CONTROLLED_VARIABLE_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.covIncrement.intValue(), "COV_INCREMENT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.dateList.intValue(), "DATE_LIST");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.daylightSavingsStatus.intValue(), "DAYLIGHT_SAVINGS_STATUS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.deadband.intValue(), "DEADBAND");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.derivativeConstant.intValue(), "DERIVATIVE_CONSTANT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.derivativeConstantUnits.intValue(),
                "DERIVATIVE_CONSTANT_UNITS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.description.intValue(), "DESCRIPTION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.descriptionOfHalt.intValue(), "DESCRIPTION_OF_HALT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.deviceAddressBinding.intValue(), "DEVICE_ADDRESS_BINDING");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.deviceType.intValue(), "DEVICE_TYPE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.effectivePeriod.intValue(), "EFFECTIVE_PERIOD");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.elapsedActiveTime.intValue(), "ELAPSED_ACTIVE_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.errorLimit.intValue(), "ERROR_LIMIT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.eventEnable.intValue(), "EVENT_ENABLE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.eventState.intValue(), "EVENT_STATE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.eventType.intValue(), "EVENT_TYPE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.exceptionSchedule.intValue(), "EXCEPTION_SCHEDULE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.faultValues.intValue(), "FAULT_VALUES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.feedbackValue.intValue(), "FEEDBACK_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.fileAccessMethod.intValue(), "FILE_ACCESS_METHOD");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.fileSize.intValue(), "FILE_SIZE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.fileType.intValue(), "FILE_TYPE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.firmwareRevision.intValue(), "FIRMWARE_REVISION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.highLimit.intValue(), "HIGH_LIMIT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.inactiveText.intValue(), "INACTIVE_TEXT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.inProcess.intValue(), "IN_PROCESS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.instanceOf.intValue(), "INSTANCE_OF");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.integralConstant.intValue(), "INTEGRAL_CONSTANT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.integralConstantUnits.intValue(), "INTEGRAL_CONSTANT_UNITS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.limitEnable.intValue(), "LIMIT_ENABLE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.listOfGroupMembers.intValue(), "LIST_OF_GROUP_MEMBERS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.listOfObjectPropertyReferences.intValue(),
                "LIST_OF_OBJECT_PROPERTY_REFERENCES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.listOfSessionKeys.intValue(), "LIST_OF_SESSION_KEYS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.localDate.intValue(), "LOCAL_DATE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.localTime.intValue(), "LOCAL_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.location.intValue(), "LOCATION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.lowLimit.intValue(), "LOW_LIMIT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.manipulatedVariableReference.intValue(),
                "MANIPULATED_VARIABLE_REFERENCE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maximumOutput.intValue(), "MAXIMUM_OUTPUT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maxApduLengthAccepted.intValue(), "MAX_APDU_LENGTH_ACCEPTED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maxInfoFrames.intValue(), "MAX_INFO_FRAMES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maxMaster.intValue(), "MAX_MASTER");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maxPresValue.intValue(), "MAX_PRES_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.minimumOffTime.intValue(), "MINIMUM_OFF_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.minimumOnTime.intValue(), "MINIMUM_ON_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.minimumOutput.intValue(), "MINIMUM_OUTPUT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.minPresValue.intValue(), "MIN_PRES_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.modelName.intValue(), "MODEL_NAME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.modificationDate.intValue(), "MODIFICATION_DATE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.notifyType.intValue(), "NOTIFY_TYPE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.numberOfApduRetries.intValue(), "NUMBER_OF_APDU_RETRIES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.numberOfStates.intValue(), "NUMBER_OF_STATES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.objectIdentifier.intValue(), "OBJECT_IDENTIFIER");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.objectList.intValue(), "OBJECT_LIST");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.objectName.intValue(), "OBJECT_NAME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.objectPropertyReference.intValue(),
                "OBJECT_PROPERTY_REFERENCE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.objectType.intValue(), "OBJECT_TYPE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.optional.intValue(), "OPTIONAL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.outOfService.intValue(), "OUT_OF_SERVICE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.outputUnits.intValue(), "OUTPUT_UNITS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.eventParameters.intValue(), "EVENT_PARAMETERS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.polarity.intValue(), "POLARITY");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.presentValue.intValue(), "PRESENT_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.priority.intValue(), "PRIORITY");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.priorityArray.intValue(), "PRIORITY_ARRAY");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.priorityForWriting.intValue(), "PRIORITYF_OR_WRITING");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.processIdentifier.intValue(), "PROCESS_IDENTIFIER");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.programChange.intValue(), "PROGRAM_CHANGE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.programLocation.intValue(), "PROGRAM_LOCATION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.programState.intValue(), "PROGRAM_STATE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.proportionalConstant.intValue(), "PROPORTIONAL_CONSTANT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.proportionalConstantUnits.intValue(),
                "PROPORTIONAL_CONSTANT_UNITS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.protocolObjectTypesSupported.intValue(),
                "PROTOCOL_OBJECT_TYPES_SUPPORTED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.protocolServicesSupported.intValue(),
                "PROTOCOL_SERVICES_SUPPORTED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.protocolVersion.intValue(), "PROTOCOL_VERSION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.readOnly.intValue(), "READ_ONLY");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.reasonForHalt.intValue(), "REASONF_OR_HALT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.recipientList.intValue(), "RECIPIENT_LIST");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.reliability.intValue(), "RELIABILITY");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.relinquishDefault.intValue(), "RELINQUISH_DEFAULT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.required.intValue(), "REQUIRED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.resolution.intValue(), "RESOLUTION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.segmentationSupported.intValue(), "SEGMENTATION_SUPPORTED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.setpoint.intValue(), "SET_POINT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.setpointReference.intValue(), "SETPOINT_REFERENCE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.stateText.intValue(), "STATE_TEXT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.statusFlags.intValue(), "STATUS_FLAGS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.systemStatus.intValue(), "SYSTEM_STATUS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.timeDelay.intValue(), "TIME_DELAY");
        PROPERTY_TYPE_CODES
                .addElement(PropertyIdentifier.timeOfActiveTimeReset.intValue(), "TIME_OF_ACTIVE_TIME_RESET");
        PROPERTY_TYPE_CODES
                .addElement(PropertyIdentifier.timeOfStateCountReset.intValue(), "TIME_OF_STATE_COUNT_RESET");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.timeSynchronizationRecipients.intValue(),
                "TIME_SYNCHRONIZATION_RECIPIENTS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.units.intValue(), "UNITS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.updateInterval.intValue(), "UPDATE_INTERVAL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.utcOffset.intValue(), "UTC_OFFSET");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.vendorIdentifier.intValue(), "VENDOR_IDENTIFIER");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.vendorName.intValue(), "VENDOR_NAME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.vtClassesSupported.intValue(), "VT_CLASSES_SUPPORTED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.weeklySchedule.intValue(), "WEEKLY_SCHEDULE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.attemptedSamples.intValue(), "ATTEMPTED_SAMPLES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.averageValue.intValue(), "AVERAGE_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.bufferSize.intValue(), "BUFFER_SIZE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.clientCovIncrement.intValue(), "CLIENT_COV_INCREMENT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.covResubscriptionInterval.intValue(),
                "COV_RESUBSCRIPTION_INTERVAL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.eventTimeStamps.intValue(), "EVENT_TIMESTAMPS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.logBuffer.intValue(), "LOG_BUFFER");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.logDeviceObjectProperty.intValue(),
                "LOG_DEVICE_OBJECT_PROPERTY");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.enable.intValue(), "ENABLE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.logInterval.intValue(), "LOG_INTERVAL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maximumValue.intValue(), "MAXIMUM_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.minimumValue.intValue(), "MINIMUM_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.notificationThreshold.intValue(), "NOTIFICATION_THRESHOLD");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.protocolRevision.intValue(), "PROTOCOL_REVISION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.recordsSinceNotification.intValue(),
                "RECORDS_SINCE_NOTIFICATION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.recordCount.intValue(), "RECORD_COUNT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.startTime.intValue(), "START_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.stopTime.intValue(), "STOP_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.stopWhenFull.intValue(), "STOP_WHEN_FULL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.totalRecordCount.intValue(), "TOTAL_RECORD_COUNT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.validSamples.intValue(), "VALID_SAMPLES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.windowInterval.intValue(), "WINDOW_INTERVAL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.windowSamples.intValue(), "WINDOW_SAMPLES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maximumValueTimestamp.intValue(), "MAXIMUM_VALUE_TIMESTAMP");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.minimumValueTimestamp.intValue(), "MINIMUM_VALUE_TIMESTAMP");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.varianceValue.intValue(), "VARIANCE_VALUE");
        PROPERTY_TYPE_CODES
                .addElement(PropertyIdentifier.activeCovSubscriptions.intValue(), "ACTIVE_COV_SUBSCRIPTIONS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.backupFailureTimeout.intValue(), "BACKUP_FAILURE_TIMEOUT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.configurationFiles.intValue(), "CONFIGURATION_FILES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.databaseRevision.intValue(), "DATABASE_REVISION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.directReading.intValue(), "DIRECT_READING");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.lastRestoreTime.intValue(), "LAST_RESTORE_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maintenanceRequired.intValue(), "MAINTENANCE_REQUIRED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.memberOf.intValue(), "MEMBER_OF");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.mode.intValue(), "MODE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.operationExpected.intValue(), "OPERATION_EXPECTED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.setting.intValue(), "SETTING");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.silenced.intValue(), "SILENCED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.trackingValue.intValue(), "TRACKING_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.zoneMembers.intValue(), "ZONE_MEMBERS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.lifeSafetyAlarmValues.intValue(), "LIFE_SAFETY_ALARM_VALUES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maxSegmentsAccepted.intValue(), "MAX_SEGMENTS_ACCEPTED");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.profileName.intValue(), "PROFILE_NAME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.autoSlaveDiscovery.intValue(), "AUTO_SLAVE_DISCOVERY");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.manualSlaveAddressBinding.intValue(),
                "MANUAL_SLAVE_ADDRESS_BINDING");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.slaveAddressBinding.intValue(), "SLAVE_ADDRESS_BINDING");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.slaveProxyEnable.intValue(), "SLAVE_PROXY_ENABLE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.lastNotifyRecord.intValue(), "LAST_NOTIFY_RECORD");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.scheduleDefault.intValue(), "SCHEDULE_DEFAULT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.acceptedModes.intValue(), "ACCEPTED_MODES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.adjustValue.intValue(), "ADJUST_VALUE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.count.intValue(), "COUNT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.countBeforeChange.intValue(), "COUNT_BEFORE_CHANGE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.countChangeTime.intValue(), "COUNT_CHANGE_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.covPeriod.intValue(), "COV_PERIOD");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.inputReference.intValue(), "INPUT_REFERENCE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.limitMonitoringInterval.intValue(),
                "LIMIT_MONITORING_INTERVAL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.loggingObject.intValue(), "LOGGING_OBJECT");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.loggingRecord.intValue(), "LOGGING_RECORD");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.prescale.intValue(), "PRESCALE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.pulseRate.intValue(), "PULSE_RATE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.scale.intValue(), "SCALE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.scaleFactor.intValue(), "SCALE_FACTOR");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.updateTime.intValue(), "UPDATE_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.valueBeforeChange.intValue(), "VALUE_BEFORE_CHANGE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.valueSet.intValue(), "VALUE_SET");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.valueChangeTime.intValue(), "VALUE_CHANGE_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.alignIntervals.intValue(), "ALIGN_INTERVALS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.intervalOffset.intValue(), "INTERVAL_OFFSET");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.lastRestartReason.intValue(), "LAST_RESTART_REASON");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.loggingType.intValue(), "LOGGING_TYPE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.restartNotificationRecipients.intValue(),
                "RESTART_NOTIFICATION_RECIPIENTS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.timeOfDeviceRestart.intValue(), "TIME_OF_DEVICE_RESTART");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.timeSynchronizationInterval.intValue(),
                "TIME_SYNCHRONIZATION_INTERVAL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.trigger.intValue(), "TRIGGER");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.utcTimeSynchronizationRecipients.intValue(),
                "UTC_TIME_SYNCHRONIZATION_RECIPIENTS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.nodeSubtype.intValue(), "NODE_SUBTYPE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.nodeType.intValue(), "NODE_TYPE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.structuredObjectList.intValue(), "STRUCTURED_OBJECT_LIST");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.subordinateAnnotations.intValue(), "SUBORDINATE_ANNOTATIONS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.subordinateList.intValue(), "SUBORDINATE_LIST");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.actualShedLevel.intValue(), "ACTUAL_SHED_LEVEL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.dutyWindow.intValue(), "DUTY_WINDOW");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.expectedShedLevel.intValue(), "EXPECTED_SHED_LEVEL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.fullDutyBaseline.intValue(), "FULL_DUTY_BASELINE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.requestedShedLevel.intValue(), "REQUESTED_SHED_LEVEL");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.shedDuration.intValue(), "SHED_DURATION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.shedLevelDescriptions.intValue(), "SHED_LEVEL_DESCRIPTIONS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.shedLevels.intValue(), "SHED_LEVELS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.stateDescription.intValue(), "STATE_DESCRIPTION");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.doorAlarmState.intValue(), "DOOR_ALARM_STATE");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.doorExtendedPulseTime.intValue(), "DOOR_EXTENDED_PULSE_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.doorMembers.intValue(), "DOOR_MEMBERS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.doorOpenTooLongTime.intValue(), "DOOR_OPEN_TOO_LONG_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.doorPulseTime.intValue(), "DOOR_PULSE_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.doorStatus.intValue(), "DOOR_STATUS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.doorUnlockDelayTime.intValue(), "DOOR_UNLOCK_DELAY_TIME");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.lockStatus.intValue(), "LOCK_STATUS");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.maskedAlarmValues.intValue(), "MASKED_ALARM_VALUES");
        PROPERTY_TYPE_CODES.addElement(PropertyIdentifier.securedStatus.intValue(), "SECURED_STATUS");
    }
}
