package br.org.scadabr.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.component.HtmlComponent;
import com.serotonin.util.SerializationHelper;

@JsonRemoteEntity
public class FlexBuilderComponent extends HtmlComponent {
	public static ImplDefinition DEFINITION = new ImplDefinition("flex",
			"FLEX", "graphic.flexBuilder", null);

	public static final int MIN_WIDTH = 800;
	public static final int MAX_WIDTH = 1920;

	public static final int MIN_HEIGHT = 600;
	public static final int MAX_HEIGHT = 1080;

	@JsonRemoteProperty
	private boolean projectDefined = false;

	@JsonRemoteProperty
	private String projectSource = "http://localhost:8080/ScadaBR/services/API?wsdl";

	@JsonRemoteProperty
	private int projectId = 1;

	@JsonRemoteProperty
	private boolean runtimeMode = false;

	@JsonRemoteProperty
	private int width = 1024;

	@JsonRemoteProperty
	private int height = 768;

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	private void createContent() {

		StringBuilder sb = new StringBuilder();
		sb.append("<object classid='clsid:d27cdb6e-ae6d-11cf-96b8-444553540000' ");
		sb.append("codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0' ");

		sb.append("width='" + width + "' height='" + height + "'>");

		sb.append("<param name='movie'  value='builder/ScadaBRFlexBuilder.swf' /> ");

		sb.append("<param name='quality' value='high' /> ");
		sb.append("<param name='bgcolor' value='#ffffff' />");
		sb.append("<embed src='builder/ScadaBRFlexBuilder.swf");

		if (projectDefined) {

			String projectsSourceEncoded = encodeURL(projectSource);

			if (projectsSourceEncoded != null) {
				String parameters = "projectsSource=" + projectsSourceEncoded
						+ "&projectId=" + projectId + "&runtimeMode="
						+ runtimeMode;
				sb.append("?");
				sb.append(parameters);
			}
		}
		sb.append("' quality='high' bgcolor='#ffffff' width='" + width
				+ "' height='" + height + "' ");
		sb.append("type='application/x-shockwave-flash' pluginspage='http://www.macromedia.com/go/getflashplayer'>");
		sb.append("</embed>");
		sb.append("</object>");

		setContent(sb.toString());
	}

	private String encodeURL(String string) {
		try {
			String url = URLEncoder.encode(string, "UTF-8");
			return url;
		} catch (Exception e) {
			return null;
		}
	}

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		createContent();
		out.writeInt(version);

		createContent();

		SerializationHelper.writeSafeUTF(out, getContent());
		out.writeBoolean(projectDefined);
		SerializationHelper.writeSafeUTF(out, projectSource);
		out.writeInt(projectId);
		out.writeBoolean(runtimeMode);

		out.writeInt(width);
		out.writeInt(height);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		createContent();
		projectId = 1;
		int ver = in.readInt();
		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			setContent(SerializationHelper.readSafeUTF(in));
			projectDefined = in.readBoolean();

			projectSource = SerializationHelper.readSafeUTF(in);
			projectId = in.readInt();
			runtimeMode = in.readBoolean();
			width = in.readInt();
			height = in.readInt();
		}
	}

	public boolean isProjectDefined() {
		return projectDefined;
	}

	public void setProjectDefined(boolean projectDefined) {
		this.projectDefined = projectDefined;
	}

	public String getProjectSource() {
		return projectSource;
	}

	public void setProjectSource(String projectSource) {
		this.projectSource = projectSource;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public boolean isRuntimeMode() {
		return runtimeMode;
	}

	public void setRuntimeMode(boolean runtimeMode) {
		this.runtimeMode = runtimeMode;
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

}
