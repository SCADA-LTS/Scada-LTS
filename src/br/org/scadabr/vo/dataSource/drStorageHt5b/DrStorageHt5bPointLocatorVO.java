package br.org.scadabr.vo.dataSource.drStorageHt5b;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.org.scadabr.rt.dataSource.drStorageHt5b.DrStorageHt5bPointLocatorRT;

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
public class DrStorageHt5bPointLocatorVO extends AbstractPointLocatorVO
		implements JsonSerializable {

	@JsonRemoteProperty
	private String pointType;
	@JsonRemoteProperty
	private boolean settable;

	private int dataType = DataTypes.ALPHANUMERIC;

	@Override
	public void validate(DwrResponseI18n response) {
		if (StringUtils.isEmpty(pointType))
			response.addContextualMessage("pointType", "validate.required");
	}

	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

	@Override
	public boolean isSettable() {
		return settable;
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		SerializationHelper.writeSafeUTF(out, pointType);
		out.writeBoolean(settable);

	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			pointType = SerializationHelper.readSafeUTF(in);
			settable = in.readBoolean();
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
	public PointLocatorRT createRuntime() {
		return new DrStorageHt5bPointLocatorRT(this);
	}

	@Override
	public LocalizableMessage getConfigurationDescription() {
		return new LocalizableMessage("common.tp.description", new LocalizableMessage("dsEdit.drStorageHt5b.pointType"), this.pointType);
	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {

	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list, Object o) {

	}

	@Override
	public int getDataTypeId() {
		// TODO Auto-generated method stub
		return 4;
	}

}
