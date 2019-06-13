package br.org.scadabr.rt.scripting.context;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.vo.User;

public abstract class ScriptContextObject {
	public enum Type {
		DATASOURCE_COMMANDS(1, "script.dsCommands", "scriptDSObject") {
			@Override
			public ScriptContextObject createScriptContextObject() {
				return new DSCommandsScriptContextObject();
			}
		},
		DATAPOINT_COMMANDS(2, "script.dpCommands", "scriptDPObject") {
			@Override
			public ScriptContextObject createScriptContextObject() {
				return new DPCommandsScriptContextObject();
			}
		};

		private Type(int id, String key, String help) {
			this.id = id;
			this.key = key;
			this.help = help;
		}

		private final int id;
		private final String key;
		private final String help;

		public int getId() {
			return id;
		}

		public String getKey() {
			return key;
		}

		public String getHelp() {
			return help;
		}

		public abstract ScriptContextObject createScriptContextObject();

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

	protected User user;

	public void setUser(User user) {
		this.user = user;
	}

	abstract public Type getType();

	public void test() {
		System.out.println("User: " + user.getUsername());
		System.out.println("Testing " + getType().getKey() + " context object");
	}
}
