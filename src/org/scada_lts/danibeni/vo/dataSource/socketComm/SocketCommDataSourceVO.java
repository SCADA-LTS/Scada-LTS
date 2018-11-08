package org.scada_lts.danibeni.vo.dataSource.socketComm;

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
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

import org.scada_lts.danibeni.rt.dataSource.socketComm.SocketCommDataSource;

@JsonRemoteEntity
public class SocketCommDataSourceVO<T extends SocketCommDataSourceVO<?>>
		extends DataSourceVO<T> {

	public static final Type TYPE = Type.SOCKET_COMM;

	public static final int MAX_COMM_ERRORS = 3;

	@Override
	protected void addEventTypes(List<EventTypeVO> eventTypes) {
		eventTypes.add(createEventType(
				SocketCommDataSource.POINT_READ_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointRead")));
		eventTypes.add(createEventType(
				SocketCommDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));
	}

	/**
	 * Handles non printable ASCII chars, including their corresponding ASCII
	 * and HEX values.
	 * 
	 * @author danibeni(DBH)
	 * 
	 */
	public static class NonPrintableAsciiChars {
		char ascii_hex;
		String ascii_str;

		NonPrintableAsciiChars(char hex, String str) {
			this.ascii_hex = hex;
			this.ascii_str = str;
		}

		public char getAsciiHex() {
			return this.ascii_hex;
		}

		public String getAsciiStr() {
			return this.ascii_str;
		}

	}

	/**
	 * Non printable ASCII characters HEX values and string representation
	 * 
	 */
	public static final char STX_HEX = 0x02;
	public static final String STX_STRING = "STX";
	public static final char ETX_HEX = 0x03;
	public static final String ETX_STRING = "ETX";
	public static final char ENQ_HEX = 0x05;
	public static final String ENQ_STRING = "ENQ";
	public static final char ACK_HEX = 0x06;
	public static final String ACK_STRING = "ACK";
	public static final char NAK_HEX = 0x15;
	public static final String NAK_STRING = "NAK";
	public static final String HEX_STRING = "HEX";
	public static final String ASCII_STRING = "ASCII";
	public static final String NONE_STRING = "NONE";

	/**
	 * Array containing all the non printable ASCII characters used by the
	 * socket communication datasource
	 */
	public static NonPrintableAsciiChars[] NON_PRINTABLE_CHARS = new NonPrintableAsciiChars[] {
			new NonPrintableAsciiChars(STX_HEX, STX_STRING),
			new NonPrintableAsciiChars(ETX_HEX, ETX_STRING),
			new NonPrintableAsciiChars(ENQ_HEX, ENQ_STRING),
			new NonPrintableAsciiChars(ACK_HEX, ACK_STRING),
			new NonPrintableAsciiChars(NAK_HEX, NAK_STRING) };

	/**
	 * Codes used to identify events at datasource
	 */
	private static final ExportCodes EVENT_CODES = new ExportCodes();
	static {
		EVENT_CODES.addElement(
				SocketCommDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				"DATA_SOURCE_EXCEPTION");
		EVENT_CODES.addElement(SocketCommDataSource.POINT_READ_EXCEPTION_EVENT,
				"POINT_READ_EXCEPTION");
	}

	@Override
	public DataSourceRT createDataSourceRT() {
		return new SocketCommDataSource(this);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public PointLocatorVO createPointLocator() {
		return new SocketCommPointLocatorVO();
	}

	@Override
	public LocalizableMessage getConnectionDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExportCodes getEventCodes() {
		return EVENT_CODES;
	}

	@Override
	public com.serotonin.mango.vo.dataSource.DataSourceVO.Type getType() {
		return TYPE;
	}

	// DBH: Define all the datasource configuration parameters
	private int updatePeriodType = Common.TimePeriods.SECONDS;
	@JsonRemoteProperty
	private int updatePeriods = 1;
	@JsonRemoteProperty
	private String host = "localhost";
	@JsonRemoteProperty
	private int port = 10000;
	@JsonRemoteProperty
	private int timeout = 10;
	@JsonRemoteProperty
	private int retries = 2;
	@JsonRemoteProperty
	private int commandFormat = 0;
	@JsonRemoteProperty
	private boolean sameFormat = false;
	@JsonRemoteProperty
	private int stopMode = 0;
	@JsonRemoteProperty
	private int nChar = 1;
	@JsonRemoteProperty
	private int charStopMode = 0;
	@JsonRemoteProperty
	private String charX = "";
	@JsonRemoteProperty
	private String hexValue = "";
	@JsonRemoteProperty
	private int stopTimeout = 100;
	@JsonRemoteProperty
	private String initString = "";
	@JsonRemoteProperty
	private int bufferSize = 2;
	@JsonRemoteProperty
	private boolean quantize;

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
	}

	/**
	 * @brief Get the type (seconds, minutes, hours) used to configure the
	 *        update period for the datasource.
	 * 
	 * @return Update period type.
	 */
	public int getUpdatePeriodType() {
		return updatePeriodType;
	}

	/**
	 * @brief Set the type (seconds, minutes, hours) used to configure the
	 *        update period for the datasource.
	 * 
	 * @param updatePeriodType
	 *            Integer representing the update period type.
	 */
	public void setUpdatePeriodType(int updatePeriodType) {
		this.updatePeriodType = updatePeriodType;
	}

	/**
	 * @brief Get the period used to update the data points values.
	 * 
	 * @return Data points values update period.
	 */
	public int getUpdatePeriods() {
		return updatePeriods;
	}

	/**
	 * @brief Set the period used to update the data points values.
	 * 
	 * @param updatePeriods
	 *            New update period for the data points values.
	 */
	public void setUpdatePeriods(int updatePeriods) {
		this.updatePeriods = updatePeriods;
	}

	/**
	 * @brief Get the datasource server hostname.
	 * 
	 * @return Hostname
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @brief Set new server hostname.
	 * 
	 * @param host
	 *            New hostname for the datasource server.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @brief Get port number where the server is listening to.
	 * 
	 * @return Port number configured.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @brief Set a new port number where the server is listening to.
	 * 
	 * @param port
	 *            New server port number.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @brief Get the timeout value used to limit the time waiting for a
	 *        response from the server.
	 * 
	 * @return Server timeout value.
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @brief Set the timeout value used to limit the time waiting for a
	 *        response from the server.
	 * 
	 * @param timeout
	 *            New server timeout value.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @brief Get the number of retries for a success connection to the server.
	 * 
	 * @return Number of connection retries.
	 */
	public int getRetries() {
		return retries;
	}

	/**
	 * @brief Set the number of retries for a success connection to the server.
	 * 
	 * @param retries
	 *            New number of connection retries.
	 */

	public void setRetries(int retries) {
		this.retries = retries;
	}

	/**
	 * @brief Get the command format used to build the command according to the
	 *        server communication protocol.
	 * 
	 * @return Command format used to build the command.
	 */
	public int getCommandFormat() {
		return commandFormat;
	}

	/**
	 * @brief Set the command format used to build the command according to the
	 *        server communication protocol.
	 * 
	 * @param commandFormat
	 *            New command format used to build the command.
	 */
	public void setCommandFormat(int commandFormat) {
		this.commandFormat = commandFormat;
	}

	/**
	 * @brief Get the value indicating if the expected response from the server
	 *        has the same format that the command.
	 * 
	 * @return True if the command and the response shares the same format.
	 */
	public boolean isSameFormat() {
		return sameFormat;
	}

	/**
	 * @brief Update the value indicating if the expected response from the
	 *        server has the same format that the command.
	 * 
	 * @param sameFormat
	 *            New value to indicate if the command and the response shares
	 *            the same format.
	 */
	public void isSameFormat(boolean sameFormat) {
		this.sameFormat = sameFormat;
	}

	/**
	 * @brief Get the value that represents receiving data stop mode for each
	 *        server response.
	 * 
	 * @return Stop mode representation value
	 */
	public int getStopMode() {
		return stopMode;
	}

	/**
	 * @brief Set the value that represents receiving data stop mode for each
	 *        server response.
	 */
	public void setStopMode(int stopMode) {
		this.stopMode = stopMode;
	}

	/**
	 * @brief Get the value that represents the number of characters to receive
	 *        for each response if its corresponding stop mode is selected.
	 * 
	 * @return Number of characters to receive for each server response.
	 */
	public int getnChar() {
		return nChar;
	}

	/**
	 * @ brief Set the value that represents the number of characters to receive
	 * for each response if its corresponding stop mode is selected.
	 * 
	 * @param nChar
	 *            New number of characters to receive for each server response.
	 */
	public void setnChar(int nChar) {
		this.nChar = nChar;
	}

	/**
	 * @brief Get the value that represents if the receiving data stop mode are
	 *        characters.
	 * 
	 * @return Actual stop mode based on characters.
	 */
	public int getCharStopMode() {
		return charStopMode;
	}

	/**
	 * @brief Set the value that represents if the receiving data stop mode are
	 *        characters.
	 * 
	 * @param charStopMode
	 *            New stop mode based on characters.
	 */
	public void setCharStopMode(int charStopMode) {
		this.charStopMode = charStopMode;
	}

	/**
	 * @brief Get the string with the ASCII characters in case the stop mode
	 *        based on characters is selected.
	 * 
	 * @return Characters to stop receiving data for each response from the
	 *         server.
	 */
	public String getCharX() {
		return charX;
	}

	/**
	 * @brief Set the string with the ASCII characters in case the stop mode
	 *        based on ASCII characters is selected.
	 * 
	 * @param charX
	 *            New characters value to stop receiving data for each response
	 *            from the server.
	 */
	public void setCharX(String charX) {
		this.charX = charX;
	}

	/**
	 * @brief Get the string with the HEX value in case the stop mode based on
	 *        HEX characters is selected.
	 * 
	 * @return Characters to stop receiving data for each response from the
	 *         server.
	 */
	public void setHexValue(String hexValue) {
		this.hexValue = hexValue;
	}

	/**
	 * @brief Set the string with the HEX value in case the stop mode based on
	 *        HEX characters is selected.
	 * 
	 * @param charX
	 *            New characters value to stop receiving data for each response
	 *            from the server.
	 */
	public String getHexValue() {
		return hexValue;
	}

	/**
	 * @brief Get the time limit to stop receiving characters from server .
	 * 
	 * @return Time to stop receiving characters value.
	 */
	public int getStopTimeout() {
		return stopTimeout;
	}

	/**
	 * @brief Set the time limit to stop receiving characters from server.
	 * 
	 * @param timeout
	 *            New Time to stop receiving characters value..
	 */
	public void setStopTimeout(int timeout) {
		this.stopTimeout = stopTimeout;
	}

	public String getInitString() {
		return initString;
	}

	public void setInitString(String initString) {
		this.initString = initString;
	}

	/**
	 * @brief Get the buffer size configured to store a data response from the
	 *        server.
	 * 
	 * @return Actual buffer size for data response.
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @brief Set the buffer size configured to store a data response from the
	 *        server.
	 * 
	 * @param bufferSize
	 *            New buffer size for data response.
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public boolean isQuantize() {
		return quantize;
	}

	public void setQuantize(boolean quantize) {
		this.quantize = quantize;
	}

	@Override
	protected void addPropertiesImpl(List<LocalizableMessage> list) {
		AuditEventType.addPeriodMessage(list, "dsEdit.dnp3.rbePeriod",
				updatePeriodType, updatePeriods);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.host", host);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.port", port);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.timeout",
				timeout);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.retries",
				retries);
		// DBH: Added property commandFormat to indicate the concrete format for
		// the frame
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.commandFormat", commandFormat);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.stopMode",
				stopMode);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.nChar",
				nChar);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.charStopMode", charStopMode);

		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.charX",
				charX);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.hexValue",
				hexValue);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.socketComm.stopTimeout", hexValue);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.initString",
				initString);
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.bufferSize",
				bufferSize);

		AuditEventType.addPropertyMessage(list, "dsEdit.quantize", quantize);
		// DBH: Added property sameFormat to check if the response and command
		// have the same format
		AuditEventType.addPropertyMessage(list, "dsEdit.socketComm.sameFormat",
				sameFormat);

	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list, T from) {
		AuditEventType.maybeAddPeriodChangeMessage(list,
				"dsEdit.dnp3.rbePeriod", from.getUpdatePeriodType(),
				from.getUpdatePeriods(), updatePeriodType, updatePeriods);

		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.host", from.getHost(), host);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.port", from.getPort(), port);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.timeout", from.getTimeout(), timeout);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.retries", from.getRetries(), retries);
		// DBH: Change property commandFormat
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.commandFormat", from.getCommandFormat(),
				commandFormat);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.stopMode", from.getStopMode(), stopMode);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.nChar", from.getnChar(), nChar);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.charStopMode", from.getCharStopMode(),
				charStopMode);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.charX", from.getCharX(), charX);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.hexValue", from.getHexValue(), hexValue);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.stopTimeout", from.getStopTimeout(),
				stopTimeout);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.initString", from.getInitString(),
				initString);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.socketComm.bufferSize", from.getBufferSize(),
				bufferSize);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.quantize",
				from.isQuantize(), quantize);
		// DBH: Change property sameFormat
		AuditEventType
				.maybeAddPropertyChangeMessage(list,
						"dsEdit.socketComm.sameFormat", from.isSameFormat(),
						sameFormat);

	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(updatePeriodType);
		out.writeInt(updatePeriods);
		SerializationHelper.writeSafeUTF(out, host);
		out.writeInt(port);
		out.writeInt(timeout);
		out.writeInt(retries);
		// DBH: Write command end format option to BLOB field into database.
		out.writeInt(commandFormat);
		out.writeInt(stopMode);
		out.writeInt(nChar);
		out.writeInt(charStopMode);
		SerializationHelper.writeSafeUTF(out, charX);
		SerializationHelper.writeSafeUTF(out, hexValue);
		out.writeInt(stopTimeout);
		out.writeInt(bufferSize);
		out.writeBoolean(quantize);
		// DBH: Write same command and response format check to BLOB field into
		// database
		out.writeBoolean(sameFormat);

	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			updatePeriodType = in.readInt();
			updatePeriods = in.readInt();
			host = SerializationHelper.readSafeUTF(in);
			port = in.readInt();
			timeout = in.readInt();
			retries = in.readInt();
			// DBH: Read command format end option from database
			commandFormat = in.readInt();
			stopMode = in.readInt();
			nChar = in.readInt();
			charStopMode = in.readInt();
			charX = SerializationHelper.readSafeUTF(in);
			hexValue = SerializationHelper.readSafeUTF(in);
			stopTimeout = in.readInt();
			bufferSize = in.readInt();
			quantize = in.readBoolean();
			// DBH: Read the field containing if the command and response format
			// are the same
			sameFormat = in.readBoolean();
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
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
