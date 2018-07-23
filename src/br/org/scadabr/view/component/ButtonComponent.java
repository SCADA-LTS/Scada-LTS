package br.org.scadabr.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.component.ScriptComponent;
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

	private void createButtonScript() {
		StringBuilder sb = new StringBuilder();
		sb.append("var s = '';");
		sb.append("if (value) ");
		if (width > 0 && height > 0) {
			sb
					.append(" s += \"<input type='button' value='"
							+ whenOnLabel
							+ "' onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", false);return false;' style='width:"
							+ width + "px; height:" + height + "px;'/>\";");
			sb.append(" else");
			sb
					.append(" s += \"<input type='button' value='"
							+ whenOffLabel
							+ "' onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", true);return true;' style='width:"
							+ width + "px; height:" + height + "px;'/>\";");
		} else {
			sb
					.append(" s += \"<input type='button' value='"
							+ whenOnLabel
							+ "' onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", false);return false;' />\";");
			sb.append(" else");
			sb
					.append(" s += \"<input type='button' value='"
							+ whenOffLabel
							+ "' onclick='mango.view.setPoint(\"+ point.id +\",\"+ pointComponent.id +\", true);return true;' />\";");
		}

		sb.append(" return s;");
		setScript(sb.toString());
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
}
