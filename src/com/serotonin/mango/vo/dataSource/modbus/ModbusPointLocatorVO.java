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
package com.serotonin.mango.vo.dataSource.modbus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.List;
import java.util.Map;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.modbus.ModbusPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class ModbusPointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	public static LocalizableMessage getRangeMessage(int range) {
		return new LocalizableMessage(RANGE_CODES.getKey(range));
	}

	private static ExportCodes RANGE_CODES = new ExportCodes();
	static {
		RANGE_CODES.addElement(RegisterRange.COIL_STATUS, "COIL_STATUS",
				"dsEdit.modbus.coilStatus");
		RANGE_CODES.addElement(RegisterRange.INPUT_STATUS, "INPUT_STATUS",
				"dsEdit.modbus.inputStatus");
		RANGE_CODES.addElement(RegisterRange.HOLDING_REGISTER,
				"HOLDING_REGISTER", "dsEdit.modbus.holdingRegister");
		RANGE_CODES.addElement(RegisterRange.INPUT_REGISTER, "INPUT_REGISTER",
				"dsEdit.modbus.inputRegister");
	};

	private static ExportCodes MODBUS_DATA_TYPE_CODES = new ExportCodes();
	static {
		MODBUS_DATA_TYPE_CODES.addElement(DataType.BINARY, "BINARY",
				"dsEdit.modbus.modbusDataType.binary");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.TWO_BYTE_INT_UNSIGNED,
				"TWO_BYTE_INT_UNSIGNED",
				"dsEdit.modbus.modbusDataType.2bUnsigned");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.TWO_BYTE_INT_SIGNED,
				"TWO_BYTE_INT_SIGNED", "dsEdit.modbus.modbusDataType.2bSigned");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.FOUR_BYTE_INT_UNSIGNED,
				"FOUR_BYTE_INT_UNSIGNED",
				"dsEdit.modbus.modbusDataType.4bUnsigned");
		MODBUS_DATA_TYPE_CODES
				.addElement(DataType.FOUR_BYTE_INT_SIGNED,
						"FOUR_BYTE_INT_SIGNED",
						"dsEdit.modbus.modbusDataType.4bSigned");
		MODBUS_DATA_TYPE_CODES.addElement(
				DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED,
				"FOUR_BYTE_INT_UNSIGNED_SWAPPED",
				"dsEdit.modbus.modbusDataType.4bUnsignedSwapped");
		MODBUS_DATA_TYPE_CODES.addElement(
				DataType.FOUR_BYTE_INT_SIGNED_SWAPPED,
				"FOUR_BYTE_INT_SIGNED_SWAPPED",
				"dsEdit.modbus.modbusDataType.4bSignedSwapped");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.FOUR_BYTE_FLOAT,
				"FOUR_BYTE_FLOAT", "dsEdit.modbus.modbusDataType.4bFloat");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.FOUR_BYTE_FLOAT_SWAPPED,
				"FOUR_BYTE_FLOAT_SWAPPED",
				"dsEdit.modbus.modbusDataType.4bFloatSwapped");
		MODBUS_DATA_TYPE_CODES.addElement(
				DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED,
				"FOUR_BYTE_FLOAT_SWAPPED_INVERTED",
				"dsEdit.modbus.modbusDataType.4bFloatSwappedInverted");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.EIGHT_BYTE_INT_UNSIGNED,
				"EIGHT_BYTE_INT_UNSIGNED",
				"dsEdit.modbus.modbusDataType.8bUnsigned");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.EIGHT_BYTE_INT_SIGNED,
				"EIGHT_BYTE_INT_SIGNED",
				"dsEdit.modbus.modbusDataType.8bSigned");
		MODBUS_DATA_TYPE_CODES.addElement(
				DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED,
				"EIGHT_BYTE_INT_UNSIGNED_SWAPPED",
				"dsEdit.modbus.modbusDataType.8bUnsignedSwapped");
		MODBUS_DATA_TYPE_CODES.addElement(
				DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED,
				"EIGHT_BYTE_INT_SIGNED_SWAPPED",
				"dsEdit.modbus.modbusDataType.8bSignedSwapped");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.EIGHT_BYTE_FLOAT,
				"EIGHT_BYTE_FLOAT", "dsEdit.modbus.modbusDataType.8bFloat");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.EIGHT_BYTE_FLOAT_SWAPPED,
				"EIGHT_BYTE_FLOAT_SWAPPED",
				"dsEdit.modbus.modbusDataType.8bFloatSwapped");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.TWO_BYTE_BCD,
				"TWO_BYTE_BCD", "dsEdit.modbus.modbusDataType.2bBcd");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.FOUR_BYTE_BCD,
				"FOUR_BYTE_BCD", "dsEdit.modbus.modbusDataType.4bBcd");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.CHAR, "CHAR",
				"dsEdit.modbus.modbusDataType.char");
		MODBUS_DATA_TYPE_CODES.addElement(DataType.VARCHAR, "VARCHAR",
				"dsEdit.modbus.modbusDataType.varchar");
	};

	public LocalizableMessage getConfigurationDescription() {
		if (socketMonitor)
			return new LocalizableMessage("dsEdit.modbus.dpconn3");
		if (slaveMonitor)
			return new LocalizableMessage("dsEdit.modbus.dpconn2", slaveId);
		if ((range == RegisterRange.HOLDING_REGISTER || range == RegisterRange.INPUT_REGISTER)
				&& modbusDataType == DataType.BINARY)
			return new LocalizableMessage("dsEdit.modbus.dpconn", slaveId,
					offset + "/" + bit);
		return new LocalizableMessage("dsEdit.modbus.dpconn", slaveId, offset);
	}

	public int getDataTypeId() {
		if (slaveMonitor || socketMonitor)
			return DataTypes.BINARY;
		if (modbusDataType == DataType.BINARY)
			return DataTypes.BINARY;
		if (isString())
			return DataTypes.ALPHANUMERIC;
		return DataTypes.NUMERIC;
	}

	public boolean isSettable() {
		if (slaveMonitor || socketMonitor)
			return false;
		return settableRange() && settableOverride;
	}

	public PointLocatorRT createRuntime() {
		return new ModbusPointLocatorRT(this);
	}

	public LocalizableMessage getRangeMessage() {
		return getRangeMessage(range);
	}

	private int range = RegisterRange.COIL_STATUS;
	private int modbusDataType = DataType.BINARY;
	@JsonRemoteProperty
	private int slaveId = 1;
	@JsonRemoteProperty
	private boolean slaveMonitor;
	@JsonRemoteProperty
	private boolean socketMonitor;
	@JsonRemoteProperty
	private int offset;
	@JsonRemoteProperty
	private byte bit;
	@JsonRemoteProperty
	private int registerCount;
	@JsonRemoteProperty
	private String charset = "ASCII";
	@JsonRemoteProperty
	private boolean settableOverride = true;
	@JsonRemoteProperty
	private double multiplier = 1;
	@JsonRemoteProperty
	private double additive = 0;

	public ModbusPointLocatorVO() {
	}

	public ModbusPointLocatorVO(ModbusPointLocatorVO pointLocator) {
		this.range = pointLocator.getRange();
		this.modbusDataType = pointLocator.getModbusDataType();
		this.slaveId = pointLocator.getSlaveId();
		this.slaveMonitor = pointLocator.isSlaveMonitor();
		this.socketMonitor = pointLocator.isSocketMonitor();
		this.offset = pointLocator.getOffset();
		this.bit = pointLocator.getBit();
		this.registerCount = pointLocator.getRegisterCount();
		this.charset = pointLocator.getCharset();
		this.settableOverride = pointLocator.isSettableOverride();
		this.multiplier = pointLocator.getMultiplier();
		this.additive = pointLocator.getAdditive();
	}

	public double getAdditive() {
		return additive;
	}

	public void setAdditive(double additive) {
		this.additive = additive;
	}

	public byte getBit() {
		return bit;
	}

	public void setBit(byte bit) {
		this.bit = bit;
	}

	public int getRegisterCount() {
		return registerCount;
	}

	public void setRegisterCount(int registerCount) {
		this.registerCount = registerCount;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isSettableOverride() {
		return settableOverride;
	}

	public void setSettableOverride(boolean settableOverride) {
		this.settableOverride = settableOverride;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getModbusDataType() {
		return modbusDataType;
	}

	public void setModbusDataType(int modbusDataType) {
		this.modbusDataType = modbusDataType;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getSlaveId() {
		return slaveId;
	}

	public void setSlaveId(int slaveId) {
		this.slaveId = slaveId;
	}

	public boolean isSlaveMonitor() {
		return slaveMonitor;
	}

	public boolean isSocketMonitor() {
		return socketMonitor;
	}

	public void setSlaveMonitor(boolean slaveMonitor) {
		this.slaveMonitor = slaveMonitor;
	}

	public void setSocketMonitor(boolean socketMonitor) {
		this.socketMonitor = socketMonitor;
	}

	public void validate(DwrResponseI18n response) {
		if (!RANGE_CODES.isValidId(range))
			response.addContextualMessage("range", "validate.invalidValue");

		if (!MODBUS_DATA_TYPE_CODES.isValidId(modbusDataType))
			response.addContextualMessage("modbusDataType",
					"validate.invalidValue");
		validateSlaveId(response);

		if (!slaveMonitor && !socketMonitor) {
			int maxEndOffset = 65536 - DataType
					.getRegisterCount(modbusDataType);
			if (!StringUtils.isBetweenInc(offset, 0, maxEndOffset))
				response.addContextualMessage("offset", "validate.0toArg",
						maxEndOffset);

			if ((range == RegisterRange.HOLDING_REGISTER || range == RegisterRange.INPUT_REGISTER)
					&& modbusDataType == DataType.BINARY) {
				if (!StringUtils.isBetweenInc(bit, 0, 15))
					response.addContextualMessage("bit", "validate.0to15");
			}

			if (isString()) {
				if (registerCount < 1)
					response.addContextualMessage("registerCount",
							"validate.greaterThanZero");

				try {
					Charset.forName(charset);
				} catch (IllegalCharsetNameException e) {
					response.addContextualMessage("charset",
							"validate.invalidCharset");
				}
			}

			if (multiplier == 0)
				response.addContextualMessage("multiplier", "validate.not0");
		}
	}

	public void validateSlaveId(DwrResponseI18n response) {
		if (!StringUtils.isBetweenInc(slaveId, 1, 255) && !socketMonitor)
			response.addContextualMessage("slaveId", "validate.1to255");
	}

	private boolean settableRange() {
		return range == RegisterRange.COIL_STATUS
				|| range == RegisterRange.HOLDING_REGISTER;
	}

	private boolean isString() {
		return modbusDataType == DataType.CHAR
				|| modbusDataType == DataType.VARCHAR;
	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {
		AuditEventType.addPropertyMessage(list, "dsEdit.modbus.slaveId",
				slaveId);
		if (!slaveMonitor && !socketMonitor) {
			AuditEventType.addExportCodeMessage(list,
					"dsEdit.modbus.registerRange", RANGE_CODES, range);
			AuditEventType.addExportCodeMessage(list,
					"dsEdit.modbus.modbusDataType", MODBUS_DATA_TYPE_CODES,
					modbusDataType);
			AuditEventType.addPropertyMessage(list, "dsEdit.modbus.offset",
					offset);
			AuditEventType.addPropertyMessage(list, "dsEdit.modbus.bit", bit);
			AuditEventType.addPropertyMessage(list,
					"dsEdit.modbus.registerCount", registerCount);
			AuditEventType.addPropertyMessage(list, "dsEdit.modbus.charset",
					charset);
			AuditEventType.addPropertyMessage(list,
					"dsEdit.modbus.settableOverride", settableOverride);
			AuditEventType.addPropertyMessage(list, "dsEdit.modbus.multiplier",
					multiplier);
			AuditEventType.addPropertyMessage(list, "dsEdit.modbus.additive",
					additive);
		}
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		ModbusPointLocatorVO from = (ModbusPointLocatorVO) o;
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.modbus.slaveId", from.slaveId, slaveId);
		if (!slaveMonitor && !socketMonitor) {
			AuditEventType.maybeAddExportCodeChangeMessage(list,
					"dsEdit.modbus.registerRange", RANGE_CODES, from.range,
					range);
			AuditEventType.maybeAddExportCodeChangeMessage(list,
					"dsEdit.modbus.registerRange", MODBUS_DATA_TYPE_CODES,
					from.modbusDataType, modbusDataType);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"dsEdit.modbus.offset", from.offset, offset);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"dsEdit.modbus.bit", from.bit, bit);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"dsEdit.modbus.registerCount", from.registerCount,
					registerCount);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"dsEdit.modbus.charset", from.charset, charset);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"dsEdit.modbus.settableOverride", from.settableOverride,
					settableOverride);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"dsEdit.modbus.multiplier", from.multiplier, multiplier);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"dsEdit.modbus.additive", from.additive, additive);
		}
	}

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 6;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(range);
		out.writeInt(modbusDataType);
		out.writeInt(slaveId);
		out.writeBoolean(slaveMonitor);
		out.writeBoolean(socketMonitor);
		out.writeInt(offset);
		out.writeByte(bit);
		out.writeInt(registerCount);
		SerializationHelper.writeSafeUTF(out, charset);
		out.writeBoolean(settableOverride);
		out.writeDouble(multiplier);
		out.writeDouble(additive);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();

		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1)
			throw new ShouldNeverHappenException("Version too old to upgrade");
		else if (ver == 2) {
			range = in.readInt();
			modbusDataType = in.readInt();
			slaveId = in.readInt();
			slaveMonitor = false;
			socketMonitor = false;
			offset = in.readInt();
			bit = in.readByte();
			registerCount = 0;
			charset = "ASCII";
			settableOverride = true;
			multiplier = in.readDouble();
			additive = in.readDouble();
		} else if (ver == 3) {
			range = in.readInt();
			modbusDataType = in.readInt();
			slaveId = in.readInt();
			slaveMonitor = false;
			socketMonitor = false;
			offset = in.readInt();
			bit = in.readByte();
			registerCount = 0;
			charset = "ASCII";
			settableOverride = in.readBoolean();
			multiplier = in.readDouble();
			additive = in.readDouble();
		} else if (ver == 4) {
			range = in.readInt();
			modbusDataType = in.readInt();
			slaveId = in.readInt();
			slaveMonitor = in.readBoolean();
			socketMonitor = false;
			offset = in.readInt();
			bit = in.readByte();
			registerCount = 0;
			charset = "ASCII";
			settableOverride = in.readBoolean();
			multiplier = in.readDouble();
			additive = in.readDouble();
		} else if (ver == 5) {
			range = in.readInt();
			modbusDataType = in.readInt();
			slaveId = in.readInt();
			slaveMonitor = in.readBoolean();
			socketMonitor = false;
			offset = in.readInt();
			bit = in.readByte();
			registerCount = in.readInt();
			charset = SerializationHelper.readSafeUTF(in);
			settableOverride = in.readBoolean();
			multiplier = in.readDouble();
			additive = in.readDouble();
		} else if (ver == 6) {
			range = in.readInt();
			modbusDataType = in.readInt();
			slaveId = in.readInt();
			slaveMonitor = in.readBoolean();
			socketMonitor = in.readBoolean();
			offset = in.readInt();
			bit = in.readByte();
			registerCount = in.readInt();
			charset = SerializationHelper.readSafeUTF(in);
			settableOverride = in.readBoolean();
			multiplier = in.readDouble();
			additive = in.readDouble();
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		// Range
		String text = json.getString("range");
		if (text == null)
			throw new LocalizableJsonException("emport.error.missing", "range",
					RANGE_CODES.getCodeList());
		range = RANGE_CODES.getId(text);
		if (!RANGE_CODES.isValidId(range))
			throw new LocalizableJsonException("emport.error.invalid", "range",
					text, RANGE_CODES.getCodeList());

		// Data type
		text = json.getString("modbusDataType");
		if (text == null)
			throw new LocalizableJsonException("emport.error.missing",
					"modbusDataType", MODBUS_DATA_TYPE_CODES.getCodeList());
		modbusDataType = MODBUS_DATA_TYPE_CODES.getId(text);
		if (!MODBUS_DATA_TYPE_CODES.isValidId(modbusDataType))
			throw new LocalizableJsonException("emport.error.invalid",
					"modbusDataType", text,
					MODBUS_DATA_TYPE_CODES.getCodeList());
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("range", RANGE_CODES.getCode(range));
		map.put("modbusDataType",
				MODBUS_DATA_TYPE_CODES.getCode(modbusDataType));
	}
}
