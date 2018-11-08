package org.scada_lts.danibeni.vo.dataSource.socketComm;


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

import org.scada_lts.danibeni.rt.dataSource.socketComm.SocketCommPointLocatorRT;

@JsonRemoteEntity
public class SocketCommPointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	// DBH: Define the data point configuration parameters
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

	/**
	 * @brief Get the regular expression used to select the part of the response
	 *        sought to store as data pint value.
	 * 
	 * @return Actual regular expression value.
	 */
	public String getValueRegex() {
		return valueRegex;
	}

	/**
	 * @brief Set the regular expression used to select the part of the response
	 *        sought to store as data pint value.
	 * 
	 * @param valueRegex
	 *            New regular expression value.
	 */
	public void setValueRegex(String valueRegex) {
		this.valueRegex = valueRegex;
	}

	/**
	 * @brief Get the value used to indicate the number of commands to be send
	 *        to the server in order to get the sought response for a concrete
	 *        data point.
	 * 
	 * @return Actual number of commands required to get the sought response.
	 */
	public int getCommandNumber() {
		return commandNumber;
	}

	/**
	 * @brief Set the value used to indicate the number of commands to be send
	 *        to the server in order to get the sought response for a concrete
	 *        data point.
	 * 
	 * @param num
	 *            New number of commands value.
	 */
	public void setCommandNumber(int num) {
		this.commandNumber = num;
	}

	/**
	 * @brief Get the string corresponding to the first command to send to the
	 *        server in order to get the sought response for a concrete data
	 *        point.
	 * 
	 * @return Actual first command string value.
	 */
	public String getFirstCommand() {
		return firstCommand;
	}

	/**
	 * @brief Set a new value for the string corresponding to the first command
	 *        to send to the server in order to get the sought response for a
	 *        concrete data point.
	 * 
	 * @param command
	 *            New first command string to send to the server.
	 */
	public void setFirstCommand(String command) {
		this.firstCommand = command;
	}

	/**
	 * @brief Get the string corresponding to the first command in case on being
	 *        an ASCII string or HEX value.
	 * 
	 * @return Actual ASCII string or HEX value for the first command.
	 */
	public String getFirstCommandHexASCII() {
		return firstCommandHexASCII;
	}

	/**
	 * @brief Set the string corresponding to the first command in case on being
	 *        an ASCII string or HEX value.
	 * 
	 * @return New ASCII string or HEX value for the first command.
	 */
	public void setFirstCommandHexASCII(String command) {
		this.firstCommandHexASCII = command;
	}

	/**
	 * @brief Get the expected response string representation to the first
	 *        command in case that at least two commands have to be sent to get
	 *        the expected response from the server.
	 * 
	 * @return Actual expected server response to the first command.
	 */
	public String getFirstExpectedResponse() {
		return firstExpectedResponse;
	}

	/**
	 * @brief Set the expected response string representation to the first
	 *        command in case that at least two commands have to be sent to get
	 *        the expected response from the server.
	 * 
	 * @return New value for the expected server response to the first command.
	 */
	public void setFirstExpectedResponse(String command) {
		this.firstExpectedResponse = command;
	}

	/**
	 * @brief Get the actual expected response to the first command in case that
	 *        the expected response is an ASCII or HEX string, and at least two
	 *        commands have to be sent to get the expected response from the
	 *        server.
	 * 
	 * @return Actual ASCII or HEX expected server response to the first
	 *         command.
	 */
	public String getFirstExpectedResponseHexASCII() {
		return firstExpectedResponseHexASCII;
	}

	/**
	 * @brief Set the expected response to the first command in case that the
	 *        expected response is an ASCII or HEX string, and at least two
	 *        commands have to be sent to get the expected response from the
	 *        server.
	 * 
	 * @return Actual ASCII or HEX expected server response to the first
	 *         command.
	 */
	public void setFirstExpectedResponseHexASCII(String command) {
		this.firstExpectedResponseHexASCII = command;
	}

	/**
	 * @brief Get the string corresponding to the second command to send to the
	 *        server in order to get the sought response for a concrete data
	 *        point.
	 * 
	 * @return Actual second command string value.
	 */
	public String getSecondCommand() {
		return secondCommand;
	}

	/**
	 * @brief Set a new value for the string corresponding to the second command
	 *        to send to the server in order to get the sought response for a
	 *        concrete data point.
	 * 
	 * @param command
	 *            New second command string to send to the server.
	 */
	public void setSecondCommand(String command) {
		this.secondCommand = command;
	}

	/**
	 * @brief Get the string corresponding to the second command in case on
	 *        being an ASCII string or HEX value.
	 * 
	 * @return Actual ASCII string or HEX value for the second command.
	 */
	public String getSecondCommandHexASCII() {
		return secondCommandHexASCII;
	}

	/**
	 * @brief Set the string corresponding to the second command in case on
	 *        being an ASCII string or HEX value.
	 * 
	 * @return New ASCII string or HEX value for the second command.
	 */
	public void setSecondCommandHexASCII(String command) {
		this.secondCommandHexASCII = command;
	}

	/**
	 * @brief Get the expected response string representation to the second
	 *        command in case that at three commands have to be sent to get the
	 *        expected response from the server.
	 * 
	 * @return Actual expected server response to the second command.
	 */
	public String getSecondExpectedResponse() {
		return secondExpectedResponse;
	}

	/**
	 * @brief Set the expected response string representation to the second
	 *        command in case that at three commands have to be sent to get the
	 *        expected response from the server.
	 * 
	 * @return New value for the expected server response to the second command.
	 */
	public void setSecondExpectedResponse(String command) {
		this.secondExpectedResponse = command;
	}

	/**
	 * @brief Get the actual expected response to the second command in case
	 *        that the expected response is an ASCII or HEX string, and three
	 *        commands have to be sent to get the expected response from the
	 *        server.
	 * 
	 * @return Actual ASCII or HEX expected server response to the second
	 *         command.
	 */
	public String getSecondExpectedResponseHexASCII() {
		return secondExpectedResponseHexASCII;
	}

	/**
	 * @brief Set the actual expected response to the second command in case
	 *        that the expected response is an ASCII or HEX string, and three
	 *        commands have to be sent to get the expected response from the
	 *        server.
	 * 
	 * @return New ASCII or HEX expected server response to the second command.
	 */
	public void setSecondExpectedResponseHexASCII(String command) {
		this.secondExpectedResponseHexASCII = command;
	}

	/**
	 * @brief Get the string corresponding to the third command to send to the
	 *        server in order to get the sought response for a concrete data
	 *        point.
	 * 
	 * @return Actual third command string value.
	 */
	public String getThirdCommand() {
		return thirdCommand;
	}

	/**
	 * @brief Set a new value for the string corresponding to the third command
	 *        to send to the server in order to get the sought response for a
	 *        concrete data point.
	 * 
	 * @param command
	 *            New third command string to send to the server.
	 */
	public void setThirdCommand(String command) {
		this.thirdCommand = command;
	}

	/**
	 * @brief Get the string corresponding to the third command in case on being
	 *        an ASCII string or HEX value.
	 * 
	 * @return Actual ASCII string or HEX value for the third command.
	 */
	public String getThirdCommandHexASCII() {
		return thirdCommandHexASCII;
	}

	/**
	 * @brief Set the string corresponding to the third command in case on being
	 *        an ASCII string or HEX value.
	 * 
	 * @return New ASCII string or HEX value for the third command.
	 */
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
		return new SocketCommPointLocatorRT(this);
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
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.valueRegex",
				valueRegex);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.firstCommand", firstCommand);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.secondCommand", secondCommand);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.thirdCommand", thirdCommand);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.customTimestamp", customTimestamp);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.timestampFormat", timestampFormat);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.timestampRegex", timestampRegex);
		AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		SocketCommPointLocatorVO from = (SocketCommPointLocatorVO) o;

		AuditEventType.maybeAddDataTypeChangeMessage(list,
				"dsEdit.pointDataType", from.dataType, dataType);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.valueRegex", from.valueRegex, valueRegex);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.firstCommand", from.firstCommand,
				firstCommand);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.secondCommand", from.secondCommand,
				secondCommand);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.thirdCommand", from.thirdCommand,
				thirdCommand);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.customTimestamp", from.customTimestamp,
				customTimestamp);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.timestampFormat", from.timestampFormat,
				timestampFormat);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.timestampRegex", from.timestampRegex,
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
