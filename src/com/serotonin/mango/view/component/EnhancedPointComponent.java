/**
 * 
 */
package com.serotonin.mango.view.component;

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
import com.serotonin.util.SerializationHelper;

/**
 * Point component used by EnhancedImageChartComponent.
 * 
 * @author Jacek Rogoznicki
 */
@JsonRemoteEntity
public class EnhancedPointComponent extends SimplePointComponent {
	public static ImplDefinition DEFINITION = new ImplDefinition(
			"enhancedPoint", "ENHANCED_POINT", "graphic.enhancedPoint",
			new int[] { DataTypes.NUMERIC });

	@JsonRemoteProperty
	private String alias;

	@JsonRemoteProperty
	private String color;

	@JsonRemoteProperty
	private float strokeWidth;

	@JsonRemoteProperty
	private EnhancedPointLineType lineType;

	@JsonRemoteProperty
	private boolean showPoints;

	public EnhancedPointComponent() {
	}

	public EnhancedPointComponent(String color, float strokeWidth,
			EnhancedPointLineType lineType) {
		this.color = color;
		this.strokeWidth = strokeWidth;
		this.lineType = lineType;
	}

	private EnhancedPointComponent(EnhancedPointComponent enhancedPointComponent) {
		super(enhancedPointComponent);
		this.alias = enhancedPointComponent.getAlias();
		this.color = enhancedPointComponent.getColor();
		this.strokeWidth = enhancedPointComponent.getStrokeWidth();
		this.lineType = enhancedPointComponent.getLineType();
		this.showPoints = enhancedPointComponent.isShowPoints();
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public EnhancedPointLineType getLineType() {
		return lineType;
	}

	public void setLineType(EnhancedPointLineType lineType) {
		this.lineType = lineType;
	}

	public boolean isShowPoints() {
		return showPoints;
	}

	public void setShowPoints(boolean showPoints) {
		this.showPoints = showPoints;
	}

	public String getExtendedName() {
		if (tgetDataPoint() != null) {
			return tgetDataPoint().getExtendedName();
		} else {
			return null;
		}
	}

	@Override
	public ViewComponent copy() {
		return new EnhancedPointComponent(this);
	}

	@Override
	public String snippetName() {
		return "basicContent";
	}

	@Override
	public void addDataToModel(Map<String, Object> model,
			PointValueTime pointValue) {
		model.put("displayPointName", displayPointName);
		model.put("styleAttribute", styleAttribute);
		model.put("alias", alias);
		model.put("color", color);
		model.put("strokeWidth", strokeWidth);
		model.put("lineType", lineType.name());
		model.put("showPoints", showPoints);
	}

	@Override
	public ImplDefinition definition() {
		return DEFINITION;
	}

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 1;

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);

		out.writeBoolean(displayPointName);
		SerializationHelper.writeSafeUTF(out, styleAttribute);
		SerializationHelper.writeSafeUTF(out, alias);
		SerializationHelper.writeSafeUTF(out, color);
		out.writeFloat(strokeWidth);
		out.writeInt(lineType.ordinal());
		out.writeBoolean(showPoints);
	}

	public void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();

		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			displayPointName = in.readBoolean();
			styleAttribute = SerializationHelper.readSafeUTF(in);
			alias = SerializationHelper.readSafeUTF(in);
			color = SerializationHelper.readSafeUTF(in);
			strokeWidth = in.readFloat();
			lineType = EnhancedPointLineType.values()[in.readInt()];
			showPoints = in.readBoolean();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EnhancedPointComponent)) return false;
		if (!super.equals(o)) return false;
		EnhancedPointComponent that = (EnhancedPointComponent) o;
		return Float.compare(that.getStrokeWidth(), getStrokeWidth()) == 0 && isShowPoints() == that.isShowPoints() && Objects.equals(getAlias(), that.getAlias()) && Objects.equals(getColor(), that.getColor()) && getLineType() == that.getLineType();
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getAlias(), getColor(), getStrokeWidth(), getLineType(), isShowPoints());
	}

	@Override
	public String toString() {
		return "EnhancedPointComponent{" +
				"alias='" + alias + '\'' +
				", color='" + color + '\'' +
				", strokeWidth=" + strokeWidth +
				", lineType=" + lineType +
				", showPoints=" + showPoints +
				"} " + super.toString();
	}
}
