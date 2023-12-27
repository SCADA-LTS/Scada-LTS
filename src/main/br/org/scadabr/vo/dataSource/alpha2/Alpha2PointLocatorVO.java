package br.org.scadabr.vo.dataSource.alpha2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import br.org.scadabr.rt.dataSource.alpha2.Alpha2PointLocatorRT;

import com.i2msolucoes.alpha24j.DeviceLocator.DeviceCodes;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class Alpha2PointLocatorVO extends AbstractPointLocatorVO {

	public static final byte RUN_STOP_CODE = 0x00;
	public static final int READ_ONLY = 1;
	public static final int WRITE_ONLY = 2;
	public static final int READ_WRITE = 3;
	@JsonRemoteProperty
	private int deviceCodeId = DeviceCodes.SYSTEM_BIT.getId();
	@JsonRemoteProperty
	private int deviceNumber = 1;
	@JsonRemoteProperty
	private int accessMode = READ_ONLY; // 1: readOnly, 2: writeOnly, 3: read

	// and write

	@Override
	public int getDataTypeId() {
		if ((deviceCodeId == RUN_STOP_CODE)
				|| (deviceCodeId == DeviceCodes.SYSTEM_BIT.getId())
				|| (deviceCodeId == DeviceCodes.COMM_BIT_DEVICE.getId()))
			return DataTypes.BINARY;

		if ((deviceCodeId == DeviceCodes.ANALOG_IN.getId())
				|| (deviceCodeId == DeviceCodes.COMM_WORD_DEVICE.getId())
				|| (deviceCodeId == DeviceCodes.OUTPUT_TERMINAL.getId())
				|| (deviceCodeId == DeviceCodes.INPUT_TERMINAL.getId())
				|| (deviceCodeId == DeviceCodes.EXTERNAL_OUTPUT.getId())
				|| (deviceCodeId == DeviceCodes.EXTERNAL_INPUT.getId())
				|| (deviceCodeId == DeviceCodes.LINK_OUTPUT.getId())
				|| (deviceCodeId == DeviceCodes.LINK_INPUT.getId()))
			return DataTypes.NUMERIC;

		return DataTypes.ALPHANUMERIC;
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		String code = "NA";
		if (deviceCodeId == RUN_STOP_CODE)
			code = "RUN/STOP Command";
		else
			code = DeviceCodes.toDeviceCode(deviceCodeId).toString();

		return new LocalizableMessage("dsEdit.alpha2.dpDesc", code,
				deviceNumber);
	}

	@Override
	public boolean isSettable() {
		if (accessMode == WRITE_ONLY || accessMode == READ_WRITE
				|| deviceCodeId == RUN_STOP_CODE)
			return true;
		return false;
	}

	public boolean isReadable() {
		if (deviceCodeId == RUN_STOP_CODE)
			return false;
		if (accessMode == READ_ONLY || accessMode == READ_WRITE)
			return true;
		return false;
	}

	@Override
	public PointLocatorRT createRuntime() {
		return new Alpha2PointLocatorRT(this);
	}

	@Override
	public void validate(DwrResponseI18n response) {
		if ((deviceCodeId != RUN_STOP_CODE)
				&& DeviceCodes.toDeviceCode(deviceCodeId) == null)
			response
					.addContextualMessage("deviceCode", "validate.invalidValue");
		if (deviceNumber < 0)
			response.addContextualMessage("deviceNumber",
					"validate.invalidValue");
	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {
		AuditEventType.addPropertyMessage(list, "dsEdit.alpha2.deviceCode",
				deviceCodeId);
		AuditEventType.addPropertyMessage(list, "dsEdit.alpha2.deviceNumber",
				deviceNumber);
		AuditEventType.addPropertyMessage(list, "dsEdit.alpha2.accessMode",
				accessMode);
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		Alpha2PointLocatorVO from = (Alpha2PointLocatorVO) o;

		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.alpha2.deviceCode", from.deviceCodeId, deviceCodeId);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.alpha2.deviceNumber", from.deviceNumber, deviceNumber);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.alpha2.accessMode", from.accessMode, accessMode);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(deviceCodeId);
		out.writeInt(deviceNumber);
		out.writeInt(accessMode);

	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			deviceCodeId = in.readInt();
			deviceNumber = in.readInt();
			accessMode = in.readInt();
		}
	}

	public int getDeviceCodeId() {
		return deviceCodeId;
	}

	public void setDeviceCodeId(int deviceCodeId) {
		this.deviceCodeId = deviceCodeId;
	}

	public int getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(int deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public int getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(int accessMode) {
		this.accessMode = accessMode;
	}

}
