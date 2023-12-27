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

@JsonRemoteEntity
public class LinkComponent extends HtmlComponent {
	public static ImplDefinition DEFINITION = new ImplDefinition("link",
			"LINK", "graphic.link", null);

	@JsonRemoteProperty
	private String link;

	@JsonRemoteProperty
	private String text;

	public LinkComponent() {}

	private LinkComponent(LinkComponent linkComponent) {
		super(linkComponent);
		this.link = linkComponent.getLink();
		this.text = linkComponent.getText();
	}

	@Override
	public ViewComponent copy() {
		return new LinkComponent(this);
	}

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	private void createLink() {
		String content = createLinkContent();
		setContent(content);
	}

	public String createLinkContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href='" + link + "'>");
		sb.append(text);
		sb.append("</a>");
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LinkComponent)) return false;
		if (!super.equals(o)) return false;
		LinkComponent that = (LinkComponent) o;
		return Objects.equals(getLink(), that.getLink()) && Objects.equals(getText(), that.getText());
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getLink(), getText());
	}

	@Override
	public String toString() {
		return "LinkComponent{" +
				"link='" + link + '\'' +
				", text='" + text + '\'' +
				"} " + super.toString();
	}
}
