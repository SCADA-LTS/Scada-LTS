package br.org.scadabr.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Objects;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.component.ScriptComponent;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.util.SerializationHelper;

@JsonRemoteEntity
public class ButtonComponent extends ScriptComponent {
	public static ImplDefinition DEFINITION = new ImplDefinition("button",
			"BUTTON", "graphic.button", new int[] { DataTypes.BINARY });
	@JsonRemoteProperty
	private String whenOffLabel = "ON";

	@JsonRemoteProperty
	private String whenOnLabel = "OFF";

	@JsonRemoteProperty
	private int width;
	@JsonRemoteProperty
	private int height;

	public ButtonComponent() {}

	private ButtonComponent(ButtonComponent buttonComponent) {
		super(buttonComponent);
		this.whenOffLabel = buttonComponent.getWhenOffLabel();
		this.whenOnLabel = buttonComponent.getWhenOnLabel();
		this.width = buttonComponent.getWidth();
		this.height = buttonComponent.getHeight();
	}

	public String getWhenOffLabel() {
		return whenOffLabel;
	}

	public void setWhenOffLabel(String whenOffLabel) {
		this.whenOffLabel = whenOffLabel;
	}

	public String getWhenOnLabel() {
		return whenOnLabel;
	}

	public void setWhenOnLabel(String whenOnLabel) {
		this.whenOnLabel = whenOnLabel;
	}

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public ViewComponent copy() {
		return new ButtonComponent(this);
	}

	private void createButtonScript() {
		String content = createButtonScriptContent();
		setScript(content);
	}

	public String createButtonScriptContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("var s = '';").append("if (value) ");
		sb.append(" s += \"<input type='button' class='simpleRenderer' value='").append(whenOnLabel).append("' ");
		sb.append("onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", false);return false;' style='");
		if (width > 0 && height > 0) {
			sb.append("width:").append(width).append("px; ");
			sb.append("height:").append(height).append("px; ");
		}
		sb.append("background-color:").append(getBkgdColorOverride()).append(";");
		sb.append("'/>").append("\"; ");

		sb.append("else");

		sb.append(" s += \"<input type='button' class='simpleRenderer' value='").append(whenOffLabel).append("' ");
		sb.append("onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", true);return true;' style='");
		if (width > 0 && height > 0) {
			sb.append("width:").append(width).append("px; ");
			sb.append("height:").append(height).append("px; ");
		}
		sb.append("background-color:").append(getBkgdColorOverride()).append(";");
		sb.append("'/>").append("\"; ");
		sb.append(" return s;");
		return sb.toString();
	}

	@Override
	public void addDataToModel(Map<String, Object> model, PointValueTime value) {
		createButtonScript();
		super.addDataToModel(model, value);
	}

	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		createButtonScript();
		SerializationHelper.writeSafeUTF(out, getScript());
		SerializationHelper.writeSafeUTF(out, whenOnLabel);
		SerializationHelper.writeSafeUTF(out, whenOffLabel);
		out.writeInt(width);
		out.writeInt(height);

	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();

		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			setScript(SerializationHelper.readSafeUTF(in));
			whenOnLabel = SerializationHelper.readSafeUTF(in);
			whenOffLabel = SerializationHelper.readSafeUTF(in);
			width = in.readInt();
			height = in.readInt();
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ButtonComponent)) return false;
		if (!super.equals(o)) return false;
		ButtonComponent that = (ButtonComponent) o;
		return getWidth() == that.getWidth() && getHeight() == that.getHeight() && Objects.equals(getWhenOffLabel(), that.getWhenOffLabel()) && Objects.equals(getWhenOnLabel(), that.getWhenOnLabel());
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getWhenOffLabel(), getWhenOnLabel(), getWidth(), getHeight());
	}

	@Override
	public String toString() {
		return "ButtonComponent{" +
				"whenOffLabel='" + whenOffLabel + '\'' +
				", whenOnLabel='" + whenOnLabel + '\'' +
				", width=" + width +
				", height=" + height +
				"} " + super.toString();
	}
}
