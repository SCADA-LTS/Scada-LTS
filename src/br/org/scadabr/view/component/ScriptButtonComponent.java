package br.org.scadabr.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.component.HtmlComponent;
import com.serotonin.util.SerializationHelper;

@JsonRemoteEntity
public class ScriptButtonComponent extends HtmlComponent {
	public static ImplDefinition DEFINITION = new ImplDefinition(
			"scriptButton", "SCRIPT_BUTTON", "graphic.scriptButton", null);

	@JsonRemoteProperty
	private String scriptXid;

	@JsonRemoteProperty
	private String text;

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	private void createScriptButton() {
		StringBuilder sb = new StringBuilder();
		sb.append("<button onclick='mango.view.executeScript(\"" + scriptXid
				+ "\");'>");
		sb.append(text);
		sb.append("</button>");
		setContent(sb.toString());
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

		createScriptButton();

		SerializationHelper.writeSafeUTF(out, getContent());
		SerializationHelper.writeSafeUTF(out, scriptXid);
		SerializationHelper.writeSafeUTF(out, text);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();

		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			setContent(SerializationHelper.readSafeUTF(in));
			scriptXid = SerializationHelper.readSafeUTF(in);
			text = SerializationHelper.readSafeUTF(in);
		}
	}

	public String getScriptXid() {
		return scriptXid;
	}

	public void setScriptXid(String scriptXid) {
		this.scriptXid = scriptXid;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
