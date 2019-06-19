package org.scada_lts.danibeni.vo.dataSource.customSerial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

import org.scada_lts.danibeni.rt.dataSource.customSerial.CustomSerialPointLocatorRT;

@JsonRemoteEntity
public class CustomSerialPointLocatorVO extends AbstractPointLocatorVO
		implements JsonSerializable {

	@JsonRemoteProperty
	private String valueRegex = "";
	@JsonRemoteProperty
	private int commandNumber = 1;
	@JsonRemoteProperty
	private String firstCommand = "";
	@JsonRemoteProperty
	private String firstCommandHexASCII = "";
	@JsonRemoteProperty
	private String firstExpectedResponse = "";
	@JsonRemoteProperty
	private String firstExpectedResponseHexASCII = "";
	@JsonRemoteProperty
	private String secondCommand = "";
	@JsonRemoteProperty
	private String secondCommandHexASCII = "";
	@JsonRemoteProperty
	private String secondExpectedResponse = "";
	@JsonRemoteProperty
	private String secondExpectedResponseHexASCII = "";
	@JsonRemoteProperty
	private String thirdCommand = "";
	@JsonRemoteProperty
	private String thirdCommandHexASCII = "";
	@JsonRemoteProperty
	private boolean customTimestamp;
	@JsonRemoteProperty
	private String timestampFormat = "";
	@JsonRemoteProperty
	private String timestampRegex = "";
	@JsonRemoteProperty
	private int dataType = DataTypes.BINARY;
	@JsonRemoteProperty
	private boolean settable;

	@Override
	public void validate(DwrResponseI18n response) {
		if (StringUtils.isEmpty(valueRegex))
			response.addContextualMessage("valueRegex", "validate.required");
		if (customTimestamp) {
			if (StringUtils.isEmpty(timestampFormat))
				response.addContextualMessage("timestampFormat",
						"validate.required");
			if (StringUtils.isEmpty(timestampRegex))
				response.addContextualMessage("timestampRegex",
						"validate.required");
		}
	}

	public String getValueRegex() {
		return valueRegex;
	}

	public void setValueRegex(String valueRegex) {
		this.valueRegex = valueRegex;
	}

	public int getCommandNumber() {
		return commandNumber;
	}

	public void setCommandNumber(int num) {
		this.commandNumber = num;
	}

	public String getFirstCommand() {
		return firstCommand;
	}

	public void setFirstCommand(String command) {
		this.firstCommand = command;
	}

	public String getFirstCommandHexASCII() {
		return firstCommandHexASCII;
	}

	public void setFirstCommandHexASCII(String command) {
		this.firstCommandHexASCII = command;
	}

	public String getFirstExpectedResponse() {
		return firstExpectedResponse;
	}

	public void setFirstExpectedResponse(String command) {
		this.firstExpectedResponse = command;
	}

	public String getFirstExpectedResponseHexASCII() {
		return firstExpectedResponseHexASCII;
	}

	public void setFirstExpectedResponseHexASCII(String command) {
		this.firstExpectedResponseHexASCII = command;
	}

	public String getSecondCommand() {
		return secondCommand;
	}

	public void setSecondCommand(String command) {
		this.secondCommand = command;
	}

	public String getSecondCommandHexASCII() {
		return secondCommandHexASCII;
	}

	public void setSecondCommandHexASCII(String command) {
		this.secondCommandHexASCII = command;
	}

	public String getSecondExpectedResponse() {
		return secondExpectedResponse;
	}

	public void setSecondExpectedResponse(String command) {
		this.secondExpectedResponse = command;
	}

	public String getSecondExpectedResponseHexASCII() {
		return secondExpectedResponseHexASCII;
	}

	public void setSecondExpectedResponseHexASCII(String command) {
		this.secondExpectedResponseHexASCII = command;
	}

	public String getThirdCommand() {
		return thirdCommand;
	}

	public void setThirdCommand(String command) {
		this.thirdCommand = command;
	}

	public String getThirdCommandHexASCII() {
		return thirdCommandHexASCII;
	}

	public void setThirdCommandHexASCII(String command) {
		this.thirdCommandHexASCII = command;
	}

	public String getTimestampRegex() {
		return timestampRegex;
	}

	public void setTimestampRegex(String timestampRegex) {
		this.timestampRegex = timestampRegex;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

	@Override
	public PointLocatorRT createRuntime() {
		return new CustomSerialPointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return null;
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		SerializationHelper.writeSafeUTF(out, valueRegex);
		SerializationHelper.writeSafeUTF(out, firstCommand);
		SerializationHelper.writeSafeUTF(out, firstCommandHexASCII);
		SerializationHelper.writeSafeUTF(out, firstExpectedResponse);
		SerializationHelper.writeSafeUTF(out, firstExpectedResponseHexASCII);
		SerializationHelper.writeSafeUTF(out, secondCommand);
		SerializationHelper.writeSafeUTF(out, secondCommandHexASCII);
		SerializationHelper.writeSafeUTF(out, secondExpectedResponse);
		SerializationHelper.writeSafeUTF(out, secondExpectedResponseHexASCII);
		SerializationHelper.writeSafeUTF(out, thirdCommand);
		SerializationHelper.writeSafeUTF(out, thirdCommandHexASCII);
		SerializationHelper.writeSafeUTF(out, timestampFormat);
		SerializationHelper.writeSafeUTF(out, timestampRegex);
		out.writeInt(dataType);
		out.writeInt(commandNumber);
		out.writeBoolean(settable);
		out.writeBoolean(customTimestamp);

	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			valueRegex = SerializationHelper.readSafeUTF(in);

			firstCommand = SerializationHelper.readSafeUTF(in);
			firstCommandHexASCII = SerializationHelper.readSafeUTF(in);
			firstExpectedResponse = SerializationHelper.readSafeUTF(in);
			firstExpectedResponseHexASCII = SerializationHelper.readSafeUTF(in);
			secondCommand = SerializationHelper.readSafeUTF(in);
			secondCommandHexASCII = SerializationHelper.readSafeUTF(in);
			secondExpectedResponse = SerializationHelper.readSafeUTF(in);
			secondExpectedResponseHexASCII = SerializationHelper
					.readSafeUTF(in);
			thirdCommand = SerializationHelper.readSafeUTF(in);
			thirdCommandHexASCII = SerializationHelper.readSafeUTF(in);
			timestampFormat = SerializationHelper.readSafeUTF(in);
			timestampRegex = SerializationHelper.readSafeUTF(in);
			dataType = in.readInt();
			commandNumber = in.readInt();
			settable = in.readBoolean();
			customTimestamp = in.readBoolean();
		}
	}

	@Override
	public void jsonDeserialize(JsonReader arg0, JsonObject arg1)
			throws JsonException {

	}

	@Override
	public void jsonSerialize(Map<String, Object> arg0) {

	}

	@Override
	public int getDataTypeId() {
		return dataType;
	}

	@Override
	public boolean isSettable() {
		return settable;
	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {
		AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType",
				dataType);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.valueRegex", valueRegex);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.firstCommand", firstCommand);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.secondCommand", secondCommand);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.thirdCommand", thirdCommand);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.customTimestamp", customTimestamp);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.timestampFormat", timestampFormat);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.timestampRegex", timestampRegex);
		AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		CustomSerialPointLocatorVO from = (CustomSerialPointLocatorVO) o;

		AuditEventType.maybeAddDataTypeChangeMessage(list,
				"dsEdit.pointDataType", from.dataType, dataType);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.valueRegex", from.valueRegex, valueRegex);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.firstCommand", from.firstCommand,
				firstCommand);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.secondCommand", from.secondCommand,
				secondCommand);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.thirdCommand", from.thirdCommand,
				thirdCommand);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.customTimestamp", from.customTimestamp,
				customTimestamp);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.timestampFormat", from.timestampFormat,
				timestampFormat);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.timestampRegex", from.timestampRegex,
				timestampRegex);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable",
				from.settable, settable);
	}

	public void setCustomTimestamp(boolean customTimestamp) {
		this.customTimestamp = customTimestamp;
	}

	public boolean isCustomTimestamp() {
		return customTimestamp;
	}

	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}

	public String getTimestampFormat() {
		return timestampFormat;
	}

}

