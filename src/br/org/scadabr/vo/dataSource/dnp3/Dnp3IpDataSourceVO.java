package br.org.scadabr.vo.dataSource.dnp3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import br.org.scadabr.rt.dataSource.dnp3.Dnp3IpDataSource;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class Dnp3IpDataSourceVO extends Dnp3DataSourceVO<Dnp3IpDataSourceVO> {
	public static final Type TYPE = Type.DNP3_IP;

	@Override
	public LocalizableMessage getConnectionDescription() {
		return new LocalizableMessage("common.default", host + ":" + port);
	}

	@Override
	public Type getType() {
		return TYPE;
	}

	@Override
	public DataSourceRT createDataSourceRT() {
		return new Dnp3IpDataSource(this);
	}

	@JsonRemoteProperty
	private String host = "localhost";
	@JsonRemoteProperty
	private int port = 20000;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
		if (StringUtils.isEmpty(host))
			response.addContextualMessage("host", "validate.required");
		if (port <= 0 || port > 0xffff)
			response.addContextualMessage("port", "validate.invalidValue");
	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list,
			Dnp3IpDataSourceVO from) {
		// TODO Auto-generated method stub

	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		SerializationHelper.writeSafeUTF(out, host);
		out.writeInt(port);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			host = SerializationHelper.readSafeUTF(in);
			port = in.readInt();
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		super.jsonDeserialize(reader, json);

	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		super.jsonSerialize(map);

	}

}
