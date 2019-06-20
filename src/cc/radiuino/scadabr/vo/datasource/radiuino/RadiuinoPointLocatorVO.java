package cc.radiuino.scadabr.vo.datasource.radiuino;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import cc.radiuino.scadabr.rt.datasource.radiuino.RadiuinoPointLocatorRT;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

public class RadiuinoPointLocatorVO extends AbstractPointLocatorVO implements
		JsonSerializable {

	private static ExportCodes RADIUINO_DATA_TYPE_CODES = new ExportCodes();
	static {
		RADIUINO_DATA_TYPE_CODES.addElement(1, "BINARY",
				"dsEdit.radiuino.radiuinoDataType.binary");
		RADIUINO_DATA_TYPE_CODES.addElement(2, "ONE_BYTE_INT_UNSIGNED",
				"dsEdit.radiuino.radiuinoDataType.1bUnsigned");
		RADIUINO_DATA_TYPE_CODES.addElement(3, "TWO_BYTE_INT_UNSIGNED",
				"dsEdit.radiuino.radiuinoDataType.2bUnsigned");
		RADIUINO_DATA_TYPE_CODES.addElement(4, "FOUR_BYTE_INT_UNSIGNED",
				"dsEdit.radiuino.radiuinoDataType.4bUnsigned");
		RADIUINO_DATA_TYPE_CODES.addElement(5, "FOUR_BYTE_FLOAT",
				"dsEdit.radiuino.radiuinoDataType.4bFloat");
		RADIUINO_DATA_TYPE_CODES.addElement(6, "RSSI",
				"dsEdit.radiuino.radiuinoDataType.RSSI");
	};

	@JsonRemoteProperty
	private int enderecoSensor = 1;

	@JsonRemoteProperty
	private int radiuinoDataType = 1;

	@JsonRemoteProperty
	private int indiceByte = 0;

	@JsonRemoteProperty
	private boolean sleepMode = false;

	@JsonRemoteProperty
	private int sleepTime = 60;

	@JsonRemoteProperty
	private String mapaBytesRequisicao = "                                                                                                                                                            ";

	@JsonRemoteProperty
	private String mapaBytesEnvio = "                                                                                                                                                            ";

	@JsonRemoteProperty
	private float multiplicador = 1;

	@JsonRemoteProperty
	private float offset = 0;

	@JsonRemoteProperty
	private boolean settableOverride = true;

	@Override
	public void validate(DwrResponseI18n response) {
		if (!RADIUINO_DATA_TYPE_CODES.isValidId(radiuinoDataType))
			response.addContextualMessage("radiuinoDataType",
					"validate.invalidValue");
	}

	@Override
	public int getDataTypeId() {
		if (radiuinoDataType == 1)
			return DataTypes.BINARY;
		if (isString())
			return DataTypes.ALPHANUMERIC;
		return DataTypes.NUMERIC;
	}

	@Override
	public boolean isSettable() {
		return settableOverride;
	}

	private boolean isString() {
		return radiuinoDataType == 10 || radiuinoDataType == 11;
	}

	public int getEnderecoSensor() {
		return enderecoSensor;
	}

	public void setEnderecoSensor(int enderecoSensor) {
		this.enderecoSensor = enderecoSensor;
	}

	public int getRadiuinoDataType() {
		return radiuinoDataType;
	}

	public void setRadiuinoDataType(int radiuinoDataType) {
		this.radiuinoDataType = radiuinoDataType;
	}

	public int getIndiceByte() {
		return indiceByte;
	}

	public void setIndiceByte(int indiceByte) {
		this.indiceByte = indiceByte;
	}

	public boolean isSleepMode() {
		return sleepMode;
	}

	public void setSleepMode(boolean sleepMode) {
		this.sleepMode = sleepMode;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public String getMapaBytesRequisicao() {
		return mapaBytesRequisicao;
	}

	public void setMapaBytesRequisicao(String mapaBytesRequisicao) {
		this.mapaBytesRequisicao = mapaBytesRequisicao;
	}

	public String getMapaBytesEnvio() {
		return mapaBytesEnvio;
	}

	public void setMapaBytesEnvio(String mapaBytesEnvio) {
		this.mapaBytesEnvio = mapaBytesEnvio;
	}

	public float getMultiplicador() {
		return multiplicador;
	}

	public void setMultiplicador(float multiplicador) {
		this.multiplicador = multiplicador;
	}

	public float getOffset() {
		return offset;
	}

	public void setOffset(float offset) {
		this.offset = offset;
	}

	public boolean isSettableOverride() {
		return settableOverride;
	}

	public void setSettableOverride(boolean settableOverride) {
		this.settableOverride = settableOverride;
	}

	@Override
	public PointLocatorRT createRuntime() {
		return new RadiuinoPointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return new LocalizableMessage("dsEdit.radiuino.dpconn", enderecoSensor,
				indiceByte);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(enderecoSensor);
		out.writeInt(radiuinoDataType);
		out.writeInt(indiceByte);
		out.writeBoolean(sleepMode);
		out.writeInt(sleepTime);
		SerializationHelper.writeSafeUTF(out, mapaBytesRequisicao);
		SerializationHelper.writeSafeUTF(out, mapaBytesEnvio);
		out.writeFloat(multiplicador);
		out.writeFloat(offset);
		out.writeBoolean(settableOverride);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			enderecoSensor = in.readInt();
			radiuinoDataType = in.readInt();
			indiceByte = in.readInt();
			sleepMode = in.readBoolean();
			sleepTime = in.readInt();
			mapaBytesRequisicao = SerializationHelper.readSafeUTF(in);
			mapaBytesEnvio = SerializationHelper.readSafeUTF(in);
			multiplicador = in.readFloat();
			offset = in.readFloat();
			settableOverride = in.readBoolean();
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
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.endereco",
				enderecoSensor);
		AuditEventType.addExportCodeMessage(list,
				"dsEdit.radiuino.radiuinoDataType", RADIUINO_DATA_TYPE_CODES,
				radiuinoDataType);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.indiceByte",
				indiceByte);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.sleepMode",
				sleepMode);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.sleepTime",
				sleepTime);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.radiuino.mapaBytesRequisicao", mapaBytesRequisicao);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.radiuino.mapaBytesEnvio", mapaBytesEnvio);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.radiuino.multiplicador", multiplicador);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.offset",
				offset);
		AuditEventType.addPropertyMessage(list,
				"dsEdit.radiuino.settableOverride", settableOverride);
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
		RadiuinoPointLocatorVO from = (RadiuinoPointLocatorVO) o;

		AuditEventType
				.maybeAddPropertyChangeMessage(list,
						"dsEdit.radiuino.endereco", from.enderecoSensor,
						enderecoSensor);
		AuditEventType.maybeAddExportCodeChangeMessage(list,
				"dsEdit.radiuino.radiuinoDataType", RADIUINO_DATA_TYPE_CODES,
				from.radiuinoDataType, radiuinoDataType);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.indiceByte", from.indiceByte, indiceByte);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.sleepMode", from.sleepMode, sleepMode);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.sleepTime", from.sleepTime, sleepTime);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.mapaBytesRequisicao",
				from.mapaBytesRequisicao, mapaBytesRequisicao);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.mapaBytesEnvio", from.mapaBytesEnvio,
				mapaBytesEnvio);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.multiplicador", from.multiplicador,
				multiplicador);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.offset", from.offset, offset);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.settableOverride", from.settableOverride,
				settableOverride);
	}
}
