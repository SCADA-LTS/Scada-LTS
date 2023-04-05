package cc.radiuino.scadabr.vo.datasource.radiuino;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import cc.radiuino.scadabr.rt.datasource.radiuino.RadiuinoEventDataSource;
import cc.radiuino.scadabr.rt.datasource.radiuino.RadiuinoPollingDataSource;

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

@JsonRemoteEntity
public class RadiuinoDataSourceVO<T extends RadiuinoDataSourceVO<?>> extends
		DataSourceVO<T> {

	public static final Type TYPE = Type.RADIUINO;

	@Override
	protected void addEventTypes(List<EventTypeVO> eventTypes) {
		eventTypes.add(createEventType(
				RadiuinoPollingDataSource.POINT_READ_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointRead")));
		eventTypes.add(createEventType(
				RadiuinoPollingDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));

	}

	private static final ExportCodes EVENT_CODES = new ExportCodes();
	static {
		EVENT_CODES.addElement(
				RadiuinoPollingDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				"DATA_SOURCE_EXCEPTION");
		EVENT_CODES.addElement(
				RadiuinoPollingDataSource.POINT_READ_EXCEPTION_EVENT,
				"POINT_READ_EXCEPTION");
	}

	@Override
	public DataSourceRT createDataSourceRT() {
		if (pollingMode) {
			return new RadiuinoPollingDataSource(this);
		} else {
			return new RadiuinoEventDataSource(this);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public PointLocatorVO createPointLocator() {
		return new RadiuinoPointLocatorVO();
	}

	@Override
	public LocalizableMessage getConnectionDescription() {
		return new LocalizableMessage("common.default", this.commPortId);
	}

	@Override
	public ExportCodes getEventCodes() {
		return EVENT_CODES;
	}

	@Override
	public com.serotonin.mango.vo.dataSource.DataSourceVO.Type getType() {
		return TYPE;
	}

	@JsonRemoteProperty
	private boolean pollingMode = true;
	private int updatePeriodType = Common.TimePeriods.MINUTES;
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
	private int timeout = 1000;
	@JsonRemoteProperty
	private int retries = 1;

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
	}

	public boolean isPollingMode() {
		return pollingMode;
	}

	public void setPollingMode(boolean pollingMode) {
		this.pollingMode = pollingMode;
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

	@Override
	protected void addPropertiesImpl(List<LocalizableMessage> list) {
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.pollingMode",
				pollingMode);
		AuditEventType.addPeriodMessage(list, "dsEdit.dnp3.rbePeriod",
				updatePeriodType, updatePeriods);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.commPortId",
				commPortId);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.baudRate",
				baudRate);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.dataBits",
				dataBits);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.stopBits",
				stopBits);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.parity",
				parity);

		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.timeout",
				timeout);
		AuditEventType.addPropertyMessage(list, "dsEdit.radiuino.retries",
				retries);
	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list, T from) {
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.pollingMode", from.isPollingMode(),
				pollingMode);

		AuditEventType.maybeAddPeriodChangeMessage(list,
				"dsEdit.dnp3.rbePeriod", from.getUpdatePeriodType(),
				from.getUpdatePeriods(), updatePeriodType, updatePeriods);

		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.commPortId", from.getCommPortId(), commPortId);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.baudRate", from.getBaudRate(), baudRate);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.dataBits", from.getDataBits(), dataBits);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.stopBits", from.getStopBits(), stopBits);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.parity", from.getParity(), parity);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.timeout", from.getTimeout(), timeout);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.radiuino.retries", from.getRetries(), retries);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeBoolean(pollingMode);
		out.writeInt(updatePeriodType);
		out.writeInt(updatePeriods);
		SerializationHelper.writeSafeUTF(out, commPortId);
		out.writeInt(baudRate);
		out.writeInt(stopBits);
		out.writeInt(dataBits);
		out.writeInt(parity);
		out.writeInt(timeout);
		out.writeInt(retries);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			pollingMode = in.readBoolean();
			updatePeriodType = in.readInt();
			updatePeriods = in.readInt();
			commPortId = SerializationHelper.readSafeUTF(in);
			baudRate = in.readInt();
			stopBits = in.readInt();
			dataBits = in.readInt();
			parity = in.readInt();
			timeout = in.readInt();
			retries = in.readInt();
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
