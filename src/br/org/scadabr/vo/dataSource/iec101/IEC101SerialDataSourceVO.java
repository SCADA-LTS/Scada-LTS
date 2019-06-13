package br.org.scadabr.vo.dataSource.iec101;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import br.org.scadabr.rt.dataSource.iec101.IEC101SerialDataSource;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class IEC101SerialDataSourceVO extends
		IEC101DataSourceVO<IEC101SerialDataSourceVO> {
	public static final Type TYPE = Type.IEC101_SERIAL;

	@Override
	public LocalizableMessage getConnectionDescription() {
		return new LocalizableMessage("common.default", commPortId);
	}

	@Override
	public com.serotonin.mango.vo.dataSource.DataSourceVO.Type getType() {
		return TYPE;
	}

	@Override
	public DataSourceRT createDataSourceRT() {
		return new IEC101SerialDataSource(this);
	}

	@JsonRemoteProperty
	private String commPortId;
	@JsonRemoteProperty
	private int baudRate = 9600;
	@JsonRemoteProperty
	private int flowControlIn = 0;
	@JsonRemoteProperty
	private int flowControlOut = 0;
	@JsonRemoteProperty
	private int dataBits = 8;
	@JsonRemoteProperty
	private int stopBits = 1;
	@JsonRemoteProperty
	private int parity = 0;

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
		if (StringUtils.isEmpty(commPortId))
			response.addContextualMessage("commPortId", "validate.required");
		if (baudRate <= 0)
			response.addContextualMessage("baudRate", "validate.invalidValue");
		if (!(flowControlIn == 0 || flowControlIn == 1 || flowControlIn == 4))
			response.addContextualMessage("flowControlIn",
					"validate.invalidValue");
		if (!(flowControlOut == 0 || flowControlOut == 2 || flowControlOut == 8))
			response.addContextualMessage("flowControlOut",
					"validate.invalidValue");
		if (dataBits < 5 || dataBits > 8)
			response.addContextualMessage("dataBits", "validate.invalidValue");
		if (stopBits < 1 || stopBits > 3)
			response.addContextualMessage("stopBits", "validate.invalidValue");
		if (parity < 0 || parity > 4)
			response
					.addContextualMessage("parityBits", "validate.invalidValue");
	}

	@Override
	protected void addPropertiesImpl(List<LocalizableMessage> list) {
		super.addPropertiesImpl(list);
	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list,
			IEC101SerialDataSourceVO from) {
		super.addPropertyChangesImpl(list, from);
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
		SerializationHelper.writeSafeUTF(out, commPortId);
		out.writeInt(baudRate);
		out.writeInt(flowControlIn);
		out.writeInt(flowControlOut);
		out.writeInt(dataBits);
		out.writeInt(stopBits);
		out.writeInt(parity);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			commPortId = SerializationHelper.readSafeUTF(in);
			baudRate = in.readInt();
			flowControlIn = in.readInt();
			flowControlOut = in.readInt();
			dataBits = in.readInt();
			stopBits = in.readInt();
			parity = in.readInt();
		}
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

	public int getFlowControlIn() {
		return flowControlIn;
	}

	public void setFlowControlIn(int flowControlIn) {
		this.flowControlIn = flowControlIn;
	}

	public int getFlowControlOut() {
		return flowControlOut;
	}

	public void setFlowControlOut(int flowControlOut) {
		this.flowControlOut = flowControlOut;
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

}
