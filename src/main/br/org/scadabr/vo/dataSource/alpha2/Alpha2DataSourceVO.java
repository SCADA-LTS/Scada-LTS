package br.org.scadabr.vo.dataSource.alpha2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import br.org.scadabr.rt.dataSource.alpha2.Alpha2DataSource;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.modbus.ModbusDataSource;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class Alpha2DataSourceVO<T extends Alpha2DataSourceVO<?>> extends
		DataSourceVO<T> {

	public static final Type TYPE = Type.ALPHA_2;

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
	private int timeout = 300;
	@JsonRemoteProperty
	private int retries = 2;
	@JsonRemoteProperty
	private int station = 0;

	@Override
	public com.serotonin.mango.vo.dataSource.DataSourceVO.Type getType() {
		return TYPE;
	}

	@Override
	public LocalizableMessage getConnectionDescription() {
		return new LocalizableMessage("common.default", commPortId);
	}

	@Override
	public PointLocatorVO createPointLocator() {
		return new Alpha2PointLocatorVO();
	}

	@Override
	public DataSourceRT createDataSourceRT() {
		return new Alpha2DataSource(this);
	}

	@Override
	public ExportCodes getEventCodes() {
		return EVENT_CODES;
	}

	@Override
	protected void addPropertiesImpl(List<LocalizableMessage> list) {
		AuditEventType.addPropertyMessage(list, "dsEdit.dnp3Serial.port",
				commPortId);
		AuditEventType.addPropertyMessage(list, "dsEdit.dnp3Serial.baud",
				baudRate);
		AuditEventType.addPropertyMessage(list, "dsEdit.dnp3Serial.dataBits",
				dataBits);
		AuditEventType.addPropertyMessage(list, "dsEdit.dnp3Serial.stopBits",
				stopBits);
		AuditEventType.addPropertyMessage(list, "dsEdit.dnp3Serial.parity",
				parity);
		AuditEventType.addPropertyMessage(list, "dsEdit.alpha2.station",
				station);

	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list, T from) {
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.dnp3Serial.port", from.getCommPortId(), commPortId);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.dnp3Serial.baud", from.getBaudRate(), baudRate);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.dnp3Serial.dataBits", from.getDataBits(), dataBits);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.dnp3Serial.stopBits", from.getStopBits(), stopBits);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.dnp3Serial.parity", from.getParity(), parity);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.alpha2.station", from.getStation(), station);

	}

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
		if (station < 0)
			response.addContextualMessage("station", "validate.invalidValue");
		if (timeout <= 0)
			response
					.addContextualMessage("timeout", "validate.greaterThanZero");
		if (retries < 0)
			response.addContextualMessage("retries",
					"validate.cannotBeNegative");
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
		out.writeInt(station);

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
			station = in.readInt();
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

	public int getStation() {
		return station;
	}

	public void setStation(int station) {
		this.station = station;
	}

	@Override
	protected void addEventTypes(List<EventTypeVO> ets) {
		ets.add(createEventType(ModbusDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));
		ets.add(createEventType(ModbusDataSource.POINT_READ_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointRead")));
		ets.add(createEventType(ModbusDataSource.POINT_WRITE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointWrite")));
	}

	private static final ExportCodes EVENT_CODES = new ExportCodes();
	static {
		EVENT_CODES.addElement(ModbusDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				"DATA_SOURCE_EXCEPTION");
		EVENT_CODES.addElement(ModbusDataSource.POINT_READ_EXCEPTION_EVENT,
				"POINT_READ_EXCEPTION");
		EVENT_CODES.addElement(ModbusDataSource.POINT_WRITE_EXCEPTION_EVENT,
				"POINT_WRITE_EXCEPTION");
	}

}
