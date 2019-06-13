package br.org.scadabr.vo.scripting;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.org.scadabr.rt.scripting.ScriptRT;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;

public abstract class ScriptVO<T extends ScriptVO<?>> implements Serializable,
		JsonSerializable {
	abstract public Type getType();

	abstract public ScriptRT createScriptRT();

	public static final String XID_PREFIX = "SC_";

	public enum Type {
		CONTEXTUALIZED_SCRIPT(1, "scripting.systemCommands", true) {
			@Override
			public ScriptVO<?> createScriptVO() {
				return new ContextualizedScriptVO();
			}
		};

		private Type(int id, String key, boolean display) {
			this.id = id;
			this.key = key;
			this.display = display;
		}

		@JsonRemoteProperty
		private final int id;
		@JsonRemoteProperty
		private final String key;
		@JsonRemoteProperty
		private final boolean display;

		public int getId() {
			return id;
		}

		public String getKey() {
			return key;
		}

		public boolean isDisplay() {
			return display;
		}

		public abstract ScriptVO<?> createScriptVO();

		public static Type valueOf(int id) {
			for (Type type : values()) {
				if (type.id == id)
					return type;
			}
			return null;
		}

		public static Type valueOfIgnoreCase(String text) {
			for (Type type : values()) {
				if (type.name().equalsIgnoreCase(text))
					return type;
			}
			return null;
		}

		public static List<String> getTypeList() {
			List<String> result = new ArrayList<String>();
			for (Type type : values())
				result.add(type.name());
			return result;
		}
	}

	private int id = Common.NEW_ID;
	@JsonRemoteProperty
	private String xid;
	@JsonRemoteProperty
	private String name;
	@JsonRemoteProperty
	private String script;
	private int userId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getXid() {
		return xid;
	}

	public void setXid(String xid) {
		this.xid = xid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void validate(DwrResponseI18n response) {
		if (StringUtils.isEmpty(xid))
			response.addContextualMessage("xid", "validate.required");
		else if (!new DataSourceDao().isXidUnique(xid, id))
			response.addContextualMessage("xid", "validate.xidUsed");
		else if (StringUtils.isLengthGreaterThan(xid, 50))
			response.addContextualMessage("xid", "validate.notLongerThan", 50);

		if (StringUtils.isEmpty(name))
			response.addContextualMessage("name", "validate.nameRequired");
		if (StringUtils.isLengthGreaterThan(name, 40))
			response.addContextualMessage("name", "validate.nameTooLong");
	}

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		// out.writeInt(version);
		// out.writeBoolean(enabled);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		// int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		// if (ver == 1) {
		// enabled = in.readBoolean();
		// }
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject object)
			throws JsonException {
		String username = object.getString("user");
		User user = new UserDao().getUser(username);
		this.userId = user.getId();
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("type", getType().name());
		map.put("user", new UserDao().getUser(userId).getUsername());
	}

	public static ScriptVO<?> createScriptVO(int typeId) {
		return Type.valueOf(typeId).createScriptVO();
	}

	public static String generateXid() {
		return Common.generateXid("SC_");
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getScript() {
		return script;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public boolean isNew() {
		return id == Common.NEW_ID;
	}

}
