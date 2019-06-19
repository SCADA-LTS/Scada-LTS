package org.scada_lts.danibeni.vo.dataSource.customSerial;

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

import org.scada_lts.danibeni.rt.dataSource.customSerial.CustomSerialDataSource;

@JsonRemoteEntity
public class CustomSerialDataSourceVO<T extends CustomSerialDataSourceVO<?>>
		extends DataSourceVO<T> {

	public static final Type TYPE = Type.CUSTOM_SERIAL;

	public static final int MAX_COMM_ERRORS = 3;

	@Override
	protected void addEventTypes(List<EventTypeVO> eventTypes) {
		eventTypes.add(createEventType(
				CustomSerialDataSource.POINT_READ_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointRead")));
		eventTypes.add(createEventType(
				CustomSerialDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));
	}

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

	public static NonPrintableAsciiChars[] NON_PRINTABLE_CHARS = new NonPrintableAsciiChars[] {
			new NonPrintableAsciiChars(STX_HEX, STX_STRING),
			new NonPrintableAsciiChars(ETX_HEX, ETX_STRING),
			new NonPrintableAsciiChars(ENQ_HEX, ENQ_STRING),
			new NonPrintableAsciiChars(ACK_HEX, ACK_STRING),
			new NonPrintableAsciiChars(NAK_HEX, NAK_STRING) };

	private static final ExportCodes EVENT_CODES = new ExportCodes();
	static {
		EVENT_CODES.addElement(
				CustomSerialDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				"DATA_SOURCE_EXCEPTION");
		EVENT_CODES.addElement(
				CustomSerialDataSource.POINT_READ_EXCEPTION_EVENT,
				"POINT_READ_EXCEPTION");
	}

	@Override
	public DataSourceRT createDataSourceRT() {
		return new CustomSerialDataSource(this);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public PointLocatorVO createPointLocator() {
		return new CustomSerialPointLocatorVO();
	}

	@Override
	public LocalizableMessage getConnectionDescription() {
		return new LocalizableMessage("common.default", commPortId);
	}

	@Override
	public ExportCodes getEventCodes() {
		return EVENT_CODES;
	}

	@Override
	public com.serotonin.mango.vo.dataSource.DataSourceVO.Type getType() {
		return TYPE;
	}

	private int updatePeriodType = Common.TimePeriods.SECONDS;
	@JsonRemoteProperty
	private int updatePeriods = 1;
	@JsonRemoteProperty
	private String commPortId;
	@JsonRemoteProperty
	private int baudRate = 9600;
	@JsonRemoteProperty
	private int dataBits = 8;
	@JsonRemoteProperty
	private int stopBits = 1;
	@JsonRemoteProperty
	private int parity = 0;
	@JsonRemoteProperty
	private int timeout = 10;
	@JsonRemoteProperty
	private int retries = 2;
	// DBH: Added frame format property
	@JsonRemoteProperty
	private int commandFormat = 0;
	// DBH: Added a property to check if the command and response format are the
	// same
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
	private int stopTimeout = 0;
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

	public String getCommPortId() {
		return commPortId;
	}

	public void setCommPortId(String commPortId) {
		this.commPortId = commPortId;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public int getDataBits() {
		return dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public int getParity() {
		return parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	// DBH: Added command end getter and setter methods
	public int getCommandFormat() {
		return commandFormat;
	}

	public void setCommandFormat(int commandFormat) {
		this.commandFormat = commandFormat;
	}

	// DBH: Added same command and response format access methods
	public boolean isSameFormat() {
		return sameFormat;
	}

	public void isSameFormat(boolean sameFormat) {
		this.sameFormat = sameFormat;
	}

	public int getStopMode() {
		return stopMode;
	}

	public void setStopMode(int stopMode) {
		this.stopMode = stopMode;
	}

	public int getnChar() {
		return nChar;
	}

	public void setnChar(int nChar) {
		this.nChar = nChar;
	}

	public void setCharStopMode(int charStopMode) {
		this.charStopMode = charStopMode;
	}

	public int getCharStopMode() {
		return charStopMode;
	}

	public String getCharX() {
		return charX;
	}

	public void setCharX(String charX) {
		this.charX = charX;
	}

	public void setHexValue(String hexValue) {
		this.hexValue = hexValue;
	}

	public String getHexValue() {
		return hexValue;
	}

	public void setStopTimeout(int stopTimeout) {
		this.stopTimeout = stopTimeout;
	}

	public int getStopTimeout() {
		return stopTimeout;
	}

	public String getInitString() {
		return initString;
	}

	public void setInitString(String initString) {
		this.initString = initString;
	}

	public int getBufferSize() {
		return bufferSize;
	}

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
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.commPortId", commPortId);
		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.baudRate",
				baudRate);
		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.dataBits",
				dataBits);
		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.stopBits",
				stopBits);
		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.parity",
				parity);

		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.timeout",
				timeout);
		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.retries",
				retries);
		// DBH: Added property commandFormat to indicate the concrete format for
		// the frame
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.commandFormat", commandFormat);

		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.nChar",
				nChar);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.charStopMode", charStopMode);

		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.charX",
				charX);
		AuditEventType.addPropertyMessage(list, "dsEdit.customSerial.hexValue",
				hexValue);

		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.stopTimeout", stopTimeout);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.initString", initString);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.bufferSize", bufferSize);

		AuditEventType.addPropertyMessage(list, "dsEdit.quantize", quantize);
		// DBH: Added property sameFormat to check if the response and command
		// have the same format
		AuditEventType.addPropertyMessage(list,
				"dsEdit.customSerial.sameFormat", sameFormat);

	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list, T from) {
		AuditEventType.maybeAddPeriodChangeMessage(list,
				"dsEdit.dnp3.rbePeriod", from.getUpdatePeriodType(),
				from.getUpdatePeriods(), updatePeriodType, updatePeriods);

		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.commPortId", from.getCommPortId(),
				commPortId);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.baudRate", from.getBaudRate(), baudRate);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.dataBits", from.getDataBits(), dataBits);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.stopBits", from.getStopBits(), stopBits);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.parity", from.getParity(), parity);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.timeout", from.getTimeout(), timeout);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.retries", from.getRetries(), retries);
		// DBH: Change property commandFormat
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.commandFormat", from.getCommandFormat(),
				commandFormat);

		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.stopMode", from.getStopMode(), stopMode);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.nChar", from.getnChar(), nChar);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.charStopMode", from.getCharStopMode(),
				charStopMode);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.charX", from.getCharX(), charX);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.hexValue", from.getHexValue(), hexValue);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.stopTimeout", from.getStopTimeout(),
				stopTimeout);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.initString", from.getInitString(),
				initString);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.bufferSize", from.getBufferSize(),
				bufferSize);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.quantize",
				from.isQuantize(), quantize);
		// DBH: Change property sameFormat
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.customSerial.sameFormat", from.isSameFormat(),
				sameFormat);

	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(updatePeriodType);
		out.writeInt(updatePeriods);
		SerializationHelper.writeSafeUTF(out, commPortId);
		out.writeInt(baudRate);
		out.writeInt(stopBits);
		out.writeInt(dataBits);
		out.writeInt(parity);
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
			commPortId = SerializationHelper.readSafeUTF(in);
			baudRate = in.readInt();
			stopBits = in.readInt();
			dataBits = in.readInt();
			parity = in.readInt();
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

