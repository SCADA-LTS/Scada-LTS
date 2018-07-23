/**
 * 
 */
package com.serotonin.mango.web.dwr.beans;

import com.serotonin.mango.view.component.EnhancedPointLineType;

/**
 * Bean containing EnhancedPointComponent properties.
 * 
 * @author Jacek Rogoznicki
 */
public class EnhancedPointComponentProperties {

	private String key;
	private String alias;
	private String color;
	private float strokeWidth;
	private EnhancedPointLineType lineType;
	private boolean showPoints;

	public EnhancedPointComponentProperties() {

	}

	public EnhancedPointComponentProperties(String alias, String color,
			float strokeWidth, EnhancedPointLineType lineType,
			boolean showPoints) {
		this.alias = alias;
		this.color = color;
		this.strokeWidth = strokeWidth;
		this.lineType = lineType;
		this.showPoints = showPoints;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

}
