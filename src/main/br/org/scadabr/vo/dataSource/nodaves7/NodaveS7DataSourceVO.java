package br.org.scadabr.vo.dataSource.nodaves7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.org.scadabr.rt.dataSource.asciiFile.ASCIIFileDataSource;
import br.org.scadabr.rt.dataSource.nodaves7.NodaveS7DataSource;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

public class NodaveS7DataSourceVO<T extends NodaveS7DataSourceVO<?>> extends
		DataSourceVO<T> {

	public static final Type TYPE = Type.NODAVE_S7;

	@Override
	protected void addEventTypes(List<EventTypeVO> eventTypes) {
		eventTypes.add(createEventType(
				ASCIIFileDataSource.POINT_READ_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointRead")));
		eventTypes.add(createEventType(
				ASCIIFileDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));

	}

	private static final ExportCodes EVENT_CODES = new ExportCodes();
	static {
		EVENT_CODES.addElement(ASCIIFileDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				"DATA_SOURCE_EXCEPTION");
		EVENT_CODES.addElement(ASCIIFileDataSource.POINT_READ_EXCEPTION_EVENT,
				"POINT_READ_EXCEPTION");
	}

	@Override
	protected void addPropertiesImpl(List<LocalizableMessage> list) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list, T from) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataSourceRT createDataSourceRT() {
		return new NodaveS7DataSource(this);
	}

	@Override
	public PointLocatorVO createPointLocator() {
		return new NodaveS7PointLocatorVO();
	}

	@Override
	public LocalizableMessage getConnectionDescription() {
		return new LocalizableMessage("common.default", this.filePath);
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
	private String filePath = "";
	@JsonRemoteProperty
	private boolean quantize;
	@JsonRemoteProperty
	private String nodaveWriteBaseCmd = "";

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
		if (StringUtils.isEmpty(filePath))
			response.addContextualMessage("filePath", "validate.required");
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isQuantize() {
		return quantize;
	}

	public void setQuantize(boolean quantize) {
		this.quantize = quantize;
	}

	public String getNodaveWriteBaseCmd() {
		return nodaveWriteBaseCmd;
	}

	public void setNodaveWriteBaseCmd(String nodaveWriteBaseCmd) {
		this.nodaveWriteBaseCmd = nodaveWriteBaseCmd;
	}

	private static final long serialVersionUID = -1;
	private static final int version = 2;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		SerializationHelper.writeSafeUTF(out, filePath);
		out.writeInt(updatePeriodType);
		out.writeInt(updatePeriods);
		out.writeBoolean(quantize);
		SerializationHelper.writeSafeUTF(out, nodaveWriteBaseCmd);

	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();

		filePath = SerializationHelper.readSafeUTF(in);
		updatePeriodType = in.readInt();
		updatePeriods = in.readInt();
		quantize = in.readBoolean();

		if (ver == 2) {
			nodaveWriteBaseCmd = SerializationHelper.readSafeUTF(in);
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
