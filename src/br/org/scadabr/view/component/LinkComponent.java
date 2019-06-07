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
public class LinkComponent extends HtmlComponent {
	public static ImplDefinition DEFINITION = new ImplDefinition("link",
			"LINK", "graphic.link", null);

	@JsonRemoteProperty
	private String link;

	@JsonRemoteProperty
	private String text;

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	private void createLink() {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href='" + link + "'>");
		sb.append(text);
		sb.append("</a>");
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

		createLink();

		SerializationHelper.writeSafeUTF(out, getContent());
		SerializationHelper.writeSafeUTF(out, link);
		SerializationHelper.writeSafeUTF(out, text);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();

		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			setContent(SerializationHelper.readSafeUTF(in));
			link = SerializationHelper.readSafeUTF(in);
			text = SerializationHelper.readSafeUTF(in);
		}
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLink() {
		return link;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
