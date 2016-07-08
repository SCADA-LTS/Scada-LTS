package br.org.scadabr.vo.dataSource.dnp3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import br.org.scadabr.rt.dataSource.dnp3.Dnp3PointLocatorRT;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class Dnp3PointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	public static final int BINARY_INPUT = 0;
	public static final int BINARY_OUTPUT = 0x10;
	public static final int RUNNING_COUNTER = 0x20;
	public static final int ANALOG_INPUT = 0x30;
	public static final int ANALOG_OUTPUT = 0x40;

	public static final int SBO = 1;
	public static final int DIRECT = 2;
	public static final int DIRECT_NO_ACK = 3;

	public static final int CLOSE_TRIP = 1;
	public static final int PULSE = 2;
	public static final int LATCH = 3;

	@Override
	public PointLocatorRT createRuntime() {
		return new Dnp3PointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return null;
	}

	@Override
	public int getDataTypeId() {
		if (dnp3DataType == BINARY_INPUT)
			return DataTypes.BINARY;
		else if (dnp3DataType == BINARY_OUTPUT)
			return DataTypes.ALPHANUMERIC;
		return DataTypes.NUMERIC;
	}

	@JsonRemoteProperty
	private int dnp3DataType = BINARY_INPUT;
	@JsonRemoteProperty
	private int index = 0;
	@JsonRemoteProperty
	private double multiplier = 1;
	@JsonRemoteProperty
	private double additive = 0;

	@JsonRemoteProperty
	private int operateMode = DIRECT;

	@JsonRemoteProperty
	private int controlCommand = LATCH;

	@JsonRemoteProperty
	private int timeOn = 0;

	@JsonRemoteProperty
	private int timeOff = 0;

	@JsonRemoteProperty
	private boolean settable;

	public int getDnp3DataType() {
		return dnp3DataType;
	}

	public void setDnp3DataType(int dnp3DataType) {
		this.dnp3DataType = dnp3DataType;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public double getAdditive() {
		return additive;
	}

	public void setAdditive(double additive) {
		this.additive = additive;
	}

	public int getOperateMode() {
		return operateMode;
	}

	public void setOperateMode(int operateMode) {
		this.operateMode = operateMode;
	}

	public int getControlCommand() {
		return controlCommand;
	}

	public void setControlCommand(int controlCommand) {
		this.controlCommand = controlCommand;
	}

	public int getTimeOn() {
		return timeOn;
	}

	public void setTimeOn(int timeOn) {
		this.timeOn = timeOn;
	}

	public int getTimeOff() {
		return timeOff;
	}

	public void setTimeOff(int timeOff) {
		this.timeOff = timeOff;
	}

	@Override
	public void validate(DwrResponseI18n response) {
		if (index < 0)
			response.addContextualMessage("index", "validate.invalidValue");
	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		// TODO Auto-generated method stub

	}

	private static final long serialVersionUID = -1;
	private static final int version = 2;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(dnp3DataType);
		out.writeInt(index);
		out.writeDouble(multiplier);
		out.writeDouble(additive);
		out.writeInt(operateMode);
		out.writeInt(controlCommand);
		out.writeInt(timeOn);
		out.writeInt(timeOff);
		out.writeBoolean(settable);

	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 2) {
			dnp3DataType = in.readInt();
			index = in.readInt();
			multiplier = in.readDouble();
			additive = in.readDouble();
			operateMode = in.readInt();
			controlCommand = in.readInt();
			timeOn = in.readInt();
			timeOff = in.readInt();
			settable = in.readBoolean();
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		// Range
		// String text = json.getString("range");
		// if (text == null)
		// throw new LocalizableJsonException("emport.error.missing", "range",
		// RANGE_CODES.getCodeList());
		// range = RANGE_CODES.getId(text);
		// if (!RANGE_CODES.isValidId(range))
		// throw new LocalizableJsonException("emport.error.invalid", "range",
		// text, RANGE_CODES.getCodeList());
		//        
		// // Data type
		// text = json.getString("modbusDataType");
		// if (text == null)
		// throw new LocalizableJsonException("emport.error.missing",
		// "modbusDataType",
		// MODBUS_DATA_TYPE_CODES.getCodeList());
		// modbusDataType = MODBUS_DATA_TYPE_CODES.getId(text);
		// if (!MODBUS_DATA_TYPE_CODES.isValidId(modbusDataType))
		// throw new LocalizableJsonException("emport.error.invalid",
		// "modbusDataType", text,
		// MODBUS_DATA_TYPE_CODES.getCodeList());
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		// map.put("range", RANGE_CODES.getCode(range));
		// map.put("modbusDataType",
		// MODBUS_DATA_TYPE_CODES.getCode(modbusDataType));
	}

	@Override
	public boolean isSettable() {
		// TODO Auto-generated method stub
		return settable;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

}
