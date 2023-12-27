package br.org.scadabr.vo.dataSource.iec101;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import br.org.scadabr.rt.dataSource.iec101.IEC101Master;
import br.org.scadabr.rt.dataSource.iec101.IEC101PointLocatorRT;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class IEC101PointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	private static ExportCodes IEC101_DATA_TYPE_CODES = new ExportCodes();
	static {
		IEC101_DATA_TYPE_CODES.addElement(
				IEC101Master.SINGLE_POINT_INFORMATION,
				"SINGLE_POINT_INFORMATION",
				"dsEdit.iec101.iec101DataType.singlePoint");
		IEC101_DATA_TYPE_CODES.addElement(
				IEC101Master.DOUBLE_POINT_INFORMATION,
				"DOUBLE_POINT_INFORMATION",
				"dsEdit.iec101.iec101DataType.doublePoint");
	};

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return new LocalizableMessage("dsEdit.iec101.dpconn", iec101DataType,
				objectAddress);
	}

	@Override
	public int getDataTypeId() {
		if (iec101DataType == IEC101Master.SINGLE_POINT_INFORMATION)
			return DataTypes.BINARY;
		else if (iec101DataType == IEC101Master.DOUBLE_POINT_INFORMATION)
			return DataTypes.ALPHANUMERIC;
		else if (iec101DataType == IEC101Master.NORMALIZED_MEASURE)
			return DataTypes.NUMERIC;
		return DataTypes.ALPHANUMERIC;
	}

	@Override
	public PointLocatorRT createRuntime() {
		return new IEC101PointLocatorRT(this);
	}

	private int iec101DataType = DataType.BINARY;
	@JsonRemoteProperty
	private int objectAddress = 1;
	@JsonRemoteProperty
	private boolean settable = true;

	private int controlMode = IEC101Master.EXECUTE_ONLY;
	private int offset = 2000;
	private int qualifier = IEC101Master.DEFAULT;

	@Override
	public boolean isSettable() {
		return settable;
	}

	public int getIec101DataType() {
		return iec101DataType;
	}

	public void setIec101DataType(int iec101DataType) {
		this.iec101DataType = iec101DataType;
	}

	public int getObjectAddress() {
		return objectAddress;
	}

	public void setObjectAddress(int objectAddress) {
		this.objectAddress = objectAddress;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

	public int getControlMode() {
		return controlMode;
	}

	public void setControlMode(int controlMode) {
		this.controlMode = controlMode;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getQualifier() {
		return qualifier;
	}

	public void setQualifier(int qualifier) {
		this.qualifier = qualifier;
	}

	@Override
	public void validate(DwrResponseI18n response) {
		if (objectAddress < 1)
			response.addContextualMessage("objectAddress",
					"validate.invalidValue");
		if (offset < 0)
			response.addContextualMessage("offset", "validate.invalidValue");

	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		// TODO Auto-generated method stub

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
		out.writeInt(iec101DataType);
		out.writeInt(objectAddress);
		out.writeBoolean(settable);
		out.writeInt(controlMode);
		out.writeInt(offset);
		out.writeInt(qualifier);
	}

	@SuppressWarnings("deprecation")
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			iec101DataType = in.readInt();
			objectAddress = in.readInt();
			settable = in.readBoolean();
			controlMode = in.readInt();
			offset = in.readInt();
			qualifier = in.readInt();
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		// Data type
		String text = json.getString("iec101DataType");
		if (text == null)
			throw new LocalizableJsonException("emport.error.missing",
					"iec101DataType", IEC101_DATA_TYPE_CODES.getCodeList());

		iec101DataType = IEC101_DATA_TYPE_CODES.getId(text);
		if (!IEC101_DATA_TYPE_CODES.isValidId(iec101DataType))
			throw new LocalizableJsonException("emport.error.invalid",
					"iec101DataType", text, IEC101_DATA_TYPE_CODES
							.getCodeList());

	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("iec101DataType", IEC101_DATA_TYPE_CODES
				.getCode(iec101DataType));

	}

}
