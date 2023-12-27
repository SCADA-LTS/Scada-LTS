package br.org.scadabr.vo.dataSource.nodaves7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.org.scadabr.rt.dataSource.nodaves7.NodaveS7PointLocatorRT;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

public class NodaveS7PointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	@JsonRemoteProperty
	private String valueRegex = "";
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
	@JsonRemoteProperty
	private String s7writeMemoryArea = "";
	@JsonRemoteProperty
	private int s7writeDBNUM = 0;
	@JsonRemoteProperty
	private int s7writeStarts = 0;
	@JsonRemoteProperty
	private int s7writeBytesQty = 0;
	@JsonRemoteProperty
	private int s7writeBitOffset = 0;

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

	public String getS7writeMemoryArea() {
		return s7writeMemoryArea;
	}

	public void setS7writeMemoryArea(String s7writeMemoryArea) {
		this.s7writeMemoryArea = s7writeMemoryArea;
	}

	public int getS7writeDBNUM() {
		return s7writeDBNUM;
	}

	public void setS7writeDBNUM(int s7writeDBNUM) {
		this.s7writeDBNUM = s7writeDBNUM;
	}

	public int getS7writeStarts() {
		return s7writeStarts;
	}

	public void setS7writeStarts(int s7writeStarts) {
		this.s7writeStarts = s7writeStarts;
	}

	public int getS7writeBytesQty() {
		return s7writeBytesQty;
	}

	public void setS7writeBytesQty(int s7writeBytesQty) {
		this.s7writeBytesQty = s7writeBytesQty;
	}

	public int getS7writeBitOffset() {
		return s7writeBitOffset;
	}

	public void setS7writeBitOffset(int s7writeBitOffset) {
		this.s7writeBitOffset = s7writeBitOffset;
	}

	public String getValueRegex() {
		return valueRegex;
	}

	public void setValueRegex(String valueRegex) {
		this.valueRegex = valueRegex;
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

	@Override
	public int getDataTypeId() {
		return dataType;
	}

	@Override
	public boolean isSettable() {
		return settable;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

	@Override
	public PointLocatorRT createRuntime() {
		return new NodaveS7PointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return new LocalizableMessage("common.tp.description", new LocalizableMessage("dsEdit.nodaves7.s7writeMemoryArea"), this.s7writeMemoryArea);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 3;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		SerializationHelper.writeSafeUTF(out, valueRegex);
		SerializationHelper.writeSafeUTF(out, timestampFormat);
		SerializationHelper.writeSafeUTF(out, timestampRegex);
		out.writeInt(dataType);
		out.writeBoolean(settable);
		out.writeBoolean(customTimestamp);
		SerializationHelper.writeSafeUTF(out, s7writeMemoryArea);
		out.writeInt(s7writeDBNUM);
		out.writeInt(s7writeStarts);
		out.writeInt(s7writeBytesQty);
		out.writeInt(s7writeBitOffset);

	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();

		valueRegex = SerializationHelper.readSafeUTF(in);
		timestampFormat = SerializationHelper.readSafeUTF(in);
		timestampRegex = SerializationHelper.readSafeUTF(in);
		dataType = in.readInt();
		settable = in.readBoolean();
		customTimestamp = in.readBoolean();

		if ((ver == 2) || (ver == 3)) {
			s7writeMemoryArea = SerializationHelper.readSafeUTF(in);
			s7writeDBNUM = in.readInt();
			s7writeStarts = in.readInt();
			s7writeBytesQty = in.readInt();
		}
		if (ver == 3) {
			s7writeBitOffset = in.readInt();
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
	public void addProperties(List<LocalizableMessage> list) {

	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {

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
