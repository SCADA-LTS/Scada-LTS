package br.org.scadabr.vo.dataSource.asciiFile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.org.scadabr.rt.dataSource.asciiFile.ASCIIFilePointLocatorRT;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class ASCIIFilePointLocatorVO extends AbstractPointLocatorVO implements
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
		return new ASCIIFilePointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return new LocalizableMessage("common.tp.description", new LocalizableMessage("dsEdit.asciiFile.valueRegex"), this.valueRegex);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		SerializationHelper.writeSafeUTF(out, valueRegex);
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
