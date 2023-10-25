package br.org.scadabr.vo.dataSource.asciiSerial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.org.scadabr.rt.dataSource.asciiSerial.ASCIISerialPointLocatorRT;

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

@JsonRemoteEntity
public class ASCIISerialPointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	@JsonRemoteProperty
	private String valueRegex = "";
	@JsonRemoteProperty
	private String command = "";
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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
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
		return new ASCIISerialPointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return new LocalizableMessage("common.tp.description", new LocalizableMessage("dsEdit.asciiSerial.command"), this.command);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		SerializationHelper.writeSafeUTF(out, valueRegex);
		SerializationHelper.writeSafeUTF(out, command);
		SerializationHelper.writeSafeUTF(out, timestampFormat);
		SerializationHelper.writeSafeUTF(out, timestampRegex);
		out.writeInt(dataType);
		out.writeBoolean(settable);
		out.writeBoolean(customTimestamp);

	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			valueRegex = SerializationHelper.readSafeUTF(in);
			command = SerializationHelper.readSafeUTF(in);
			timestampFormat = SerializationHelper.readSafeUTF(in);
			timestampRegex = SerializationHelper.readSafeUTF(in);
			dataType = in.readInt();
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
				"dsEdit.asciiSerial.valueRegex", valueRegex);
		AuditEventType.addPropertyMessage(list, "dsEdit.asciiSerial.command",
				command);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.asciiSerial.customTimestamp", customTimestamp);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.asciiSerial.timestampFormat", timestampFormat);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.asciiSerial.timestampRegex", timestampRegex);
		AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		ASCIISerialPointLocatorVO from = (ASCIISerialPointLocatorVO) o;

		AuditEventType.maybeAddDataTypeChangeMessage(list,
				"dsEdit.pointDataType", from.dataType, dataType);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.asciiSerial.valueRegex", from.valueRegex, valueRegex);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.asciiSerial.command", from.command, command);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.asciiSerial.customTimestamp", from.customTimestamp,
				customTimestamp);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.asciiSerial.timestampFormat", from.timestampFormat,
				timestampFormat);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.asciiSerial.timestampRegex", from.timestampRegex,
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
