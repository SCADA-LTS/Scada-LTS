package br.org.scadabr.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.component.HtmlComponent;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.util.SerializationHelper;
import org.scada_lts.web.beans.validation.xss.XssProtect;

import static org.scada_lts.web.security.XssProtectUtils.escapeHtml;

@JsonRemoteEntity
public class ScriptButtonComponent extends HtmlComponent {
	public static ImplDefinition DEFINITION = new ImplDefinition(
			"scriptButton", "SCRIPT_BUTTON", "graphic.scriptButton", null);

	@JsonRemoteProperty
	@XssProtect
	private String scriptXid;

	@JsonRemoteProperty
	@XssProtect
	private String text;

	/**
 * Creates a new ScriptButtonComponent with default properties.
 *
 * <p>Required for framework instantiation and object deserialization.</p>
 */
public ScriptButtonComponent() {}

	public ScriptButtonComponent(ScriptButtonComponent scriptButtonComponent) {
		super(scriptButtonComponent);
		this.scriptXid = scriptButtonComponent.getScriptXid();
		this.text = scriptButtonComponent.getText();
	}

	@Override
	public ViewComponent copy() {
		return new ScriptButtonComponent(this);
	}

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	private void createScriptButton() {
		String content = createScriptButtonContent();
		setContent(content);
	}

	public String createScriptButtonContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("<button class='viewComponent' onclick='mango.view.executeScript(\"" + escapeHtml(scriptXid)
				+ "\");'>");
		sb.append(escapeHtml(text));
		sb.append("</button>");
		return sb.toString();
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ScriptButtonComponent)) return false;
		if (!super.equals(o)) return false;
		ScriptButtonComponent that = (ScriptButtonComponent) o;
		return Objects.equals(getScriptXid(), that.getScriptXid()) && Objects.equals(getText(), that.getText());
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getScriptXid(), getText());
	}

	@Override
	public String toString() {
		return "ScriptButtonComponent{" +
				"scriptXid='" + scriptXid + '\'' +
				", text='" + text + '\'' +
				"} " + super.toString();
	}
}