package br.org.scadabr.vo.dataSource.iec101;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import br.org.scadabr.rt.dataSource.iec101.IEC101DataSource;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

abstract public class IEC101DataSourceVO<T extends IEC101DataSourceVO<?>>
		extends DataSourceVO<T> {

	@Override
	protected void addEventTypes(List<EventTypeVO> ets) {
		ets.add(createEventType(IEC101DataSource.DATA_SOURCE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));
		ets.add(createEventType(IEC101DataSource.POINT_READ_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointRead")));
		ets.add(createEventType(IEC101DataSource.POINT_WRITE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointWrite")));

	}

	private static final ExportCodes EVENT_CODES = new ExportCodes();
	static {
		EVENT_CODES.addElement(IEC101DataSource.DATA_SOURCE_EXCEPTION_EVENT,
				"DATA_SOURCE_EXCEPTION");
		EVENT_CODES.addElement(IEC101DataSource.POINT_READ_EXCEPTION_EVENT,
				"POINT_READ_EXCEPTION");
		EVENT_CODES.addElement(IEC101DataSource.POINT_WRITE_EXCEPTION_EVENT,
				"POINT_WRITE_EXCEPTION");
	}

	@Override
	public ExportCodes getEventCodes() {
		return EVENT_CODES;
	}

	@Override
	public PointLocatorVO createPointLocator() {
		return new IEC101PointLocatorVO();
	}

	@JsonRemoteProperty
	private int transmissionProcedure = 1; // 0 - balanced / 1 - unbalanced

	private int updatePeriodType = Common.TimePeriods.MINUTES;
	@JsonRemoteProperty
	private int updatePeriods = 5;
	@JsonRemoteProperty
	private int giRelativePeriod = 30; // relative to updatePeriod
	@JsonRemoteProperty
	private int clockSynchRelativePeriod = 60; // relative to updatePeriod

	@JsonRemoteProperty
	private int linkLayerAddressSize = 1;
	@JsonRemoteProperty
	private int linkLayerAddress = 1;

	@JsonRemoteProperty
	private int asduAddressSize = 2;
	@JsonRemoteProperty
	private int asduAddress = 1;

	@JsonRemoteProperty
	private int cotSize = 1;
	@JsonRemoteProperty
	private int objectAddressSize = 2;

	@JsonRemoteProperty
	private int timeout = 500;
	@JsonRemoteProperty
	private int retries = 2;
	@JsonRemoteProperty
	private boolean quantize;

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
		if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType))
			response.addContextualMessage("updatePeriodType",
					"validate.invalidValue");
		if (updatePeriods <= 0)
			response.addContextualMessage("updatePeriods",
					"validate.greaterThanZero");

		if (giRelativePeriod <= 0)
			response.addContextualMessage("giRelativePeriod",
					"validate.greaterThanZero");
		if (clockSynchRelativePeriod <= 0)
			response.addContextualMessage("clockSynchRelativePeriod",
					"validate.greaterThanZero");
		if (linkLayerAddress <= 0)
			response.addContextualMessage("linkLayerAddress",
					"validate.greaterThanZero");
		if (asduAddress <= 0)
			response.addContextualMessage("asduAddress",
					"validate.greaterThanZero");

		if (timeout <= 0)
			response
					.addContextualMessage("timeout", "validate.greaterThanZero");
		if (retries < 0)
			response.addContextualMessage("retries",
					"validate.cannotBeNegative");
	}

	@Override
	protected void addPropertiesImpl(List<LocalizableMessage> list) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list, T from) {
		// TODO Auto-generated method stub

	}

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(transmissionProcedure);
		out.writeInt(updatePeriodType);
		out.writeInt(updatePeriods);
		out.writeInt(giRelativePeriod);
		out.writeInt(clockSynchRelativePeriod);
		out.writeInt(linkLayerAddressSize);
		out.writeInt(linkLayerAddress);
		out.writeInt(asduAddressSize);
		out.writeInt(asduAddress);
		out.writeInt(cotSize);
		out.writeInt(objectAddressSize);
		out.writeInt(timeout);
		out.writeInt(retries);
		out.writeBoolean(quantize);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			transmissionProcedure = in.readInt();
			updatePeriodType = in.readInt();
			updatePeriods = in.readInt();
			giRelativePeriod = in.readInt();
			clockSynchRelativePeriod = in.readInt();
			linkLayerAddressSize = in.readInt();
			linkLayerAddress = in.readInt();
			asduAddressSize = in.readInt();
			asduAddress = in.readInt();
			cotSize = in.readInt();
			objectAddressSize = in.readInt();
			timeout = in.readInt();
			retries = in.readInt();
			quantize = in.readBoolean();
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

	public int getTransmissionProcedure() {
		return transmissionProcedure;
	}

	public void setTransmissionProcedure(int transmissionProcedure) {
		this.transmissionProcedure = transmissionProcedure;
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

	public int getGiRelativePeriod() {
		return giRelativePeriod;
	}

	public void setGiRelativePeriod(int giRelativePeriod) {
		this.giRelativePeriod = giRelativePeriod;
	}

	public int getClockSynchRelativePeriod() {
		return clockSynchRelativePeriod;
	}

	public void setClockSynchRelativePeriod(int clockSynchRelativePeriod) {
		this.clockSynchRelativePeriod = clockSynchRelativePeriod;
	}

	public int getLinkLayerAddressSize() {
		return linkLayerAddressSize;
	}

	public void setLinkLayerAddressSize(int linkLayerAddressSize) {
		this.linkLayerAddressSize = linkLayerAddressSize;
	}

	public int getLinkLayerAddress() {
		return linkLayerAddress;
	}

	public void setLinkLayerAddress(int linkLayerAddress) {
		this.linkLayerAddress = linkLayerAddress;
	}

	public int getAsduAddressSize() {
		return asduAddressSize;
	}

	public void setAsduAddressSize(int asduAddressSize) {
		this.asduAddressSize = asduAddressSize;
	}

	public int getAsduAddress() {
		return asduAddress;
	}

	public void setAsduAddress(int asduAddress) {
		this.asduAddress = asduAddress;
	}

	public int getCotSize() {
		return cotSize;
	}

	public void setCotSize(int cotSize) {
		this.cotSize = cotSize;
	}

	public int getObjectAddressSize() {
		return objectAddressSize;
	}

	public void setObjectAddressSize(int objectAddressSize) {
		this.objectAddressSize = objectAddressSize;
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

	public boolean isQuantize() {
		return quantize;
	}

	public void setQuantize(boolean quantize) {
		this.quantize = quantize;
	}

}
