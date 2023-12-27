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
package com.serotonin.mango.vo.dataSource.mbus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import net.sf.mbus4j.MBusAddressing;
import net.sf.mbus4j.MBusUtils;
import net.sf.mbus4j.dataframes.MBusMedium;
import net.sf.mbus4j.dataframes.datablocks.DataBlock;
import net.sf.mbus4j.dataframes.datablocks.dif.DataFieldCode;
import net.sf.mbus4j.dataframes.datablocks.dif.FunctionField;

import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.mbus.MBusPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class MBusPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {

    public final static String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * The address of the device.
     */
    @JsonRemoteProperty
    private byte address;
    @JsonRemoteProperty
    private String difCode;
    @JsonRemoteProperty
    private String functionField;
    @JsonRemoteProperty
    private int deviceUnit;
    @JsonRemoteProperty
    private int tariff;
    @JsonRemoteProperty
    private long storageNumber;
    @JsonRemoteProperty
    private String vifType;
    @JsonRemoteProperty
    private String vifLabel;
    @JsonRemoteProperty
    private String unitOfMeasurement;
    @JsonRemoteProperty
    private String siPrefix;
    @JsonRemoteProperty
    private Integer exponent;
    @JsonRemoteProperty
    private String[] vifeLabels = EMPTY_STRING_ARRAY;
    @JsonRemoteProperty
    private String[] vifeTypes = EMPTY_STRING_ARRAY;
    @JsonRemoteProperty
    private String medium;
    @JsonRemoteProperty
    private String responseFrame;
    @JsonRemoteProperty
    private byte version;
    @JsonRemoteProperty
    private int identNumber;
    @JsonRemoteProperty
    private String manufacturer;
    @JsonRemoteProperty
    private double correctionFactor = 1.0;
    private MBusAddressing addressing;

    @Override
    public int getDataTypeId() {
        switch (DataFieldCode.fromLabel(difCode)) {
            case _12_DIGIT_BCD:
            case _16_BIT_INTEGER:
            case _24_BIT_INTEGER:
            case _2_DIGIT_BCD:
            case _32_BIT_INTEGER:
            case _32_BIT_REAL:
            case _48_BIT_INTEGER:
            case _4_DIGIT_BCD:
            case _64_BIT_INTEGER:
            case _6_DIGIT_BCD:
            case _8_BIT_INTEGER:
            case _8_DIGIT_BCD:
                return DataTypes.NUMERIC;
            case VARIABLE_LENGTH:
                return DataTypes.ALPHANUMERIC;
            default:
                return DataTypes.UNKNOWN;
        }
    }

    @Override
    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", address + " " + manufacturer);
    }

    @Override
    public boolean isSettable() {
        return false;
    }

    @Override
    public PointLocatorRT createRuntime() {
        return new MBusPointLocatorRT(this);
    }

    @Override
    public void validate(DwrResponseI18n response) {
        switch (getAddressing()) {
            case PRIMARY:

                if (((address & 0xFF) < MBusUtils.FIRST_REGULAR_PRIMARY_ADDRESS)
                        || (address > MBusUtils.LAST_REGULAR_PRIMARY_ADDRESS)) {
                    response.addContextualMessage("address", "validate.required");
                }
                break;
            case SECONDARY:
                if (((address & 0xFF) == MBusUtils.BROADCAST_NO_ANSWER_PRIMARY_ADDRESS)
                        || ((address & 0xFF) == MBusUtils.BROADCAST_WITH_ANSWER_PRIMARY_ADDRESS)) {
                        response.addContextualMessage("address", "validate.required");
                }
                break;

        }
        try {
            DataFieldCode.fromLabel(difCode);
        } catch (IllegalArgumentException ex) {
            response.addContextualMessage("difCode", "validate.required");
        }

        try {
            FunctionField.fromLabel(functionField);
        } catch (IllegalArgumentException ex) {
            response.addContextualMessage("functionField", "validate.required");
        }

        if (deviceUnit < 0) {
            response.addContextualMessage("deviceUnit", "validate.required");
        }

        if (tariff < 0) {
            response.addContextualMessage("tariff", "validate.required");
        }

        if (storageNumber < 0) {
            response.addContextualMessage("storageNumber", "validate.required");
        }

        try {
            DataBlock.getVif(vifType, vifLabel, unitOfMeasurement, siPrefix, exponent);
        } catch (IllegalArgumentException ex) {
            response.addContextualMessage("vif", "validate.required");
        }

        if (vifeLabels.length > 0) {
            if (vifeLabels.length != vifeTypes.length) {
                response.addContextualMessage("vife and vifetype lenght mismatch", "validate.required");
            }
            for (int i = 0; i < vifeLabels.length; i++) {
                try {
                    DataBlock.getVife(vifeTypes[i], vifeLabels[i]);
                } catch (IllegalArgumentException ex) {
                    response.addContextualMessage("vife", "validate.required");
                }
            }
        }
        try {
            MBusMedium.fromLabel(medium);
        } catch (IllegalArgumentException ex) {
            response.addContextualMessage("medium", "validate.required");
        }
        if ((responseFrame == null) || (responseFrame.length() == 0)) {
            response.addContextualMessage("responseFrame", "validate.required");
        }
        if (((version & 0xFF) < 0) || ((version & 0xFF) > 0xFF)) {
            response.addContextualMessage("version", "validate.required");
        }
        if (identNumber < 0) {
            response.addContextualMessage("id", "validate.required");
        }
        if ((manufacturer == null) || (manufacturer.length() != 3)) {
            response.addContextualMessage("man", "validate.required");
        }
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.correctionFactor", correctionFactor);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.addressing", addressing);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.address", address);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.difCode", difCode);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.functionField", functionField);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.deviceUnit", deviceUnit);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.tariff", tariff);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.storageNumber", storageNumber);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.vifType", vifType);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.vifLabel", vifLabel);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.unitOfMeasurement", unitOfMeasurement);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.siPrefix", siPrefix);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.exponent", exponent);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.vifeLabel", vifeLabels);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.vifeLabel", vifeTypes);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.medium", medium);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.responseFrame", responseFrame);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.version", version);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.id", identNumber);
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.manufacturer", manufacturer);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        MBusPointLocatorVO from = (MBusPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.correctionFactor", from.correctionFactor, correctionFactor);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.addressing", from.addressing, addressing);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.address", from.address, address);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.difCode", from.difCode, difCode);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.functionField", from.functionField,
                functionField);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.deviceUnit", from.deviceUnit, deviceUnit);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.tariff", from.tariff, tariff);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.storageNumber", from.storageNumber,
                storageNumber);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.vifType", from.vifType, vifType);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.vifLabel", from.vifLabel, vifLabel);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.unitOfMeasurement", from.unitOfMeasurement,
                unitOfMeasurement);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.siPrefix", from.siPrefix, siPrefix);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.exponent", from.exponent, exponent);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.vifeLabel", from.vifeLabels, vifeLabels);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.vifeLabel", from.vifeTypes, vifeTypes);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.medium", from.medium, medium);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.responseFrame", from.responseFrame,
                responseFrame);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.version", from.version, version);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.id", from.identNumber, identNumber);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.manufacturer", from.manufacturer, manufacturer);
    }
    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int SERIAL_VERSION = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(SERIAL_VERSION);
        SerializationHelper.writeSafeUTF(out, addressing.name());

        out.writeDouble(correctionFactor);
        out.writeByte(address);
        out.writeByte(version);
        out.writeInt(identNumber);
        SerializationHelper.writeSafeUTF(out, manufacturer);
        SerializationHelper.writeSafeUTF(out, medium);

        SerializationHelper.writeSafeUTF(out, responseFrame);

        SerializationHelper.writeSafeUTF(out, difCode);
        SerializationHelper.writeSafeUTF(out, functionField);
        out.writeInt(deviceUnit);
        out.writeInt(tariff);
        out.writeLong(storageNumber);
        SerializationHelper.writeSafeUTF(out, vifType);
        SerializationHelper.writeSafeUTF(out, vifLabel);
        SerializationHelper.writeSafeUTF(out, unitOfMeasurement);
        SerializationHelper.writeSafeUTF(out, siPrefix);
        out.writeObject(exponent);
        out.writeInt(vifeLabels.length);
        for (int i = 0; i < vifeLabels.length; i++) {
            SerializationHelper.writeSafeUTF(out, vifeTypes[i]);
            SerializationHelper.writeSafeUTF(out, vifeLabels[i]);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            addressing = MBusAddressing.valueOf(SerializationHelper.readSafeUTF(in));
            correctionFactor = in.readDouble();

            address = in.readByte();
            version = in.readByte();
            identNumber = in.readInt();
            manufacturer = SerializationHelper.readSafeUTF(in);
            medium = SerializationHelper.readSafeUTF(in);

            responseFrame = SerializationHelper.readSafeUTF(in);

            difCode = SerializationHelper.readSafeUTF(in);
            functionField = SerializationHelper.readSafeUTF(in);
            deviceUnit = in.readInt();
            tariff = in.readInt();
            storageNumber = in.readLong();
            vifType = SerializationHelper.readSafeUTF(in);
            vifLabel = SerializationHelper.readSafeUTF(in);
            unitOfMeasurement = SerializationHelper.readSafeUTF(in);
            siPrefix = SerializationHelper.readSafeUTF(in);
            exponent = (Integer) in.readObject();
            final int vifeLength = in.readInt();
            if (vifeLength == 0) {
                vifeLabels = EMPTY_STRING_ARRAY;
                vifeTypes = EMPTY_STRING_ARRAY;
            } else {
                vifeLabels = new String[vifeLength];
                vifeTypes = new String[vifeLength];
                for (int i = 0; i < vifeLength; i++) {
                    vifeTypes[i] = SerializationHelper.readSafeUTF(in);
                    vifeLabels[i] = SerializationHelper.readSafeUTF(in);
                }
            }
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) {
        addressing = MBusAddressing.valueOf(json.getString("addressing"));
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("addressing", addressing.name());
    }

    /**
     * @return the address
     */
    public byte getAddress() {
        return address;
    }

    /**
     * @return the address
     */
    public String getAddressHex() {
        return String.format("0x%02x", address);
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(byte address) {
        this.address = address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddressHex(String address) {
        this.address = (byte) Integer.parseInt(address.substring(2), 16);
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setIdentNumber(int identNumber) {
        this.identNumber = identNumber;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public void setVersionHex(String version) {
        this.version = (byte) Integer.parseInt(version.substring(2), 16);
    }

    public void setResponseFrame(String responseFrame) {
        this.responseFrame = responseFrame;
    }

    /**
     * @return the deviceUnit
     */
    public int getDeviceUnit() {
        return deviceUnit;
    }

    /**
     * @param deviceUnit
     *            the deviceUnit to set
     */
    public void setDeviceUnit(int deviceUnit) {
        this.deviceUnit = deviceUnit;
    }

    /**
     * @return the tariff
     */
    public int getTariff() {
        return tariff;
    }

    /**
     * @param tariff
     *            the tariff to set
     */
    public void setTariff(int tariff) {
        this.tariff = tariff;
    }

    /**
     * @return the storageNumber
     */
    public long getStorageNumber() {
        return storageNumber;
    }

    /**
     * @param storageNumber
     *            the storageNumber to set
     */
    public void setStorageNumber(long storageNumber) {
        this.storageNumber = storageNumber;
    }

    /**
     * @return the responseFrame
     */
    public String getResponseFrame() {
        return responseFrame;
    }

    /**
     * @return the version
     */
    public byte getVersion() {
        return version;
    }

    /**
     * @return the version
     */
    public String getVersionHex() {
        return String.format("0x%02x", version);
    }

    /**
     * @return the id
     */
    public int getIdentNumber() {
        return identNumber;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @return the difCode
     */
    public String getDifCode() {
        return difCode;
    }

    /**
     * @param difCode
     *            the difCode to set
     */
    public void setDifCode(String difCode) {
        this.difCode = difCode;
    }

    /**
     * @return the functionField
     */
    public String getFunctionField() {
        return functionField;
    }

    /**
     * @param functionField
     *            the functionField to set
     */
    public void setFunctionField(String functionField) {
        this.functionField = functionField;
    }

    /**
     * @return the vifLabel
     */
    public String getVifLabel() {
        return vifLabel;
    }

    /**
     * @param vifLabel
     *            the vifLabel to set
     */
    public void setVifLabel(String vifLabel) {
        this.vifLabel = vifLabel;
    }

    /**
     * @return the unitOfMeasurement
     */
    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    /**
     * @param unitOfMeasurement
     *            the unitOfMeasurement to set
     */
    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    /**
     * @return the siPrefix
     */
    public String getSiPrefix() {
        return siPrefix;
    }

    /**
     * @param siPrefix
     *            the siPrefix to set
     */
    public void setSiPrefix(String siPrefix) {
        this.siPrefix = siPrefix;
    }

    /**
     * @return the exponent
     */
    public Integer getExponent() {
        return exponent;
    }

    /**
     * @param exponent
     *            the exponent to set
     */
    public void setExponent(Integer exponent) {
        this.exponent = exponent;
    }

    /**
     * @return the vifeLabel
     */
    public String[] getVifeLabels() {
        return vifeLabels;
    }

    /**
     * @param vifeLabel
     *            the vifeLabel to set
     */
    public void setVifeLabels(String[] vifeLabel) {
        vifeLabels = vifeLabel;
    }

    /**
     * @return the medium
     */
    public String getMedium() {
        return medium;
    }

    /**
     * @param medium
     *            the medium to set
     */
    public void setMedium(String medium) {
        this.medium = medium;
        System.out.println("MEDIUM: " + this.medium);
    }

    /**
     * @return the addressing
     */
    public MBusAddressing getAddressing() {
        return addressing;
    }

    /**
     * @param addressing
     *            the addressing to set
     */
    public void setAddressing(MBusAddressing addressing) {
        this.addressing = addressing;
    }

    /**
     * Helper for JSP
     * 
     * @return
     */
    public boolean isPrimaryAddressing() {
        return MBusAddressing.PRIMARY.equals(addressing);
    }

    /**
     * Helper for JSP
     * 
     * @return
     */
    public boolean isSecondaryAddressing() {
        return MBusAddressing.SECONDARY.equals(addressing);
    }

    /**
     * @return the vifType
     */
    public String getVifType() {
        return vifType;
    }

    /**
     * @param vifType
     *            the vifType to set
     */
    public void setVifType(String vifType) {
        this.vifType = vifType;
    }

    /**
     * @return the vifeTypes
     */
    public String[] getVifeTypes() {
        return vifeTypes;
    }

    /**
     * @param vifeTypes
     *            the vifeTypes to set
     */
    public void setVifeTypes(String[] vifeTypes) {
        this.vifeTypes = vifeTypes;
    }

    /**
     * @return the correctionFactor
     */
    public double getCorrectionFactor() {
        return correctionFactor;
    }

    /**
     * @param correctionFactor the correctionFactor to set
     */
    public void setCorrectionFactor(double correctionFactor) {
        this.correctionFactor = correctionFactor;
    }
}
