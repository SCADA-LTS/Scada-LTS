/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.org.scadabr.view.component.AlarmListComponent;
import br.org.scadabr.view.component.ButtonComponent;
import br.org.scadabr.view.component.ChartComparatorComponent;
import br.org.scadabr.view.component.FlexBuilderComponent;
import br.org.scadabr.view.component.LinkComponent;
import br.org.scadabr.view.component.ScriptButtonComponent;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.json.JsonValue;
import com.serotonin.json.TypeFactory;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity(typeFactory = ViewComponent.Factory.class)
abstract public class ViewComponent implements Serializable, JsonSerializable {
	private static List<ImplDefinition> DEFINITIONS;

	public static List<ImplDefinition> getImplementations() {
		if (DEFINITIONS == null) {
			List<ImplDefinition> d = new ArrayList<ImplDefinition>();
			d.add(AnalogGraphicComponent.DEFINITION);
			d.add(BinaryGraphicComponent.DEFINITION);
			d.add(DynamicGraphicComponent.DEFINITION);
			d.add(HtmlComponent.DEFINITION);
			d.add(MultistateGraphicComponent.DEFINITION);
			d.add(ScriptComponent.DEFINITION);
			d.add(SimpleImageComponent.DEFINITION);
			d.add(SimplePointComponent.DEFINITION);
			d.add(ThumbnailComponent.DEFINITION);
			d.add(SimpleCompoundComponent.DEFINITION);
			d.add(ImageChartComponent.DEFINITION);
			d.add(WirelessTempHumSensor.DEFINITION);
			d.add(ButtonComponent.DEFINITION);
			d.add(LinkComponent.DEFINITION);
			d.add(AlarmListComponent.DEFINITION);
			d.add(ScriptButtonComponent.DEFINITION);
			// d.add(FlexBuilderComponent.DEFINITION);
			d.add(ChartComparatorComponent.DEFINITION);
			d.add(FlexBuilderComponent.DEFINITION);
			DEFINITIONS = d;
		}
		return DEFINITIONS;
	}

	public static ViewComponent newInstance(String name) {
		ImplDefinition def = ImplDefinition.findByName(getImplementations(),
				name);
		try {
			return resolveClass(def).newInstance();
		} catch (Exception e) {
			throw new ShouldNeverHappenException(
					"Error finding component with name '" + name + "': "
							+ e.getMessage());
		}
	}

	static Class<? extends ViewComponent> resolveClass(ImplDefinition def) {
		if (def == AnalogGraphicComponent.DEFINITION)
			return AnalogGraphicComponent.class;
		if (def == BinaryGraphicComponent.DEFINITION)
			return BinaryGraphicComponent.class;
		if (def == DynamicGraphicComponent.DEFINITION)
			return DynamicGraphicComponent.class;
		if (def == HtmlComponent.DEFINITION)
			return HtmlComponent.class;
		if (def == MultistateGraphicComponent.DEFINITION)
			return MultistateGraphicComponent.class;
		if (def == ScriptComponent.DEFINITION)
			return ScriptComponent.class;
		if (def == SimpleImageComponent.DEFINITION)
			return SimpleImageComponent.class;
		if (def == SimplePointComponent.DEFINITION)
			return SimplePointComponent.class;
		if (def == ThumbnailComponent.DEFINITION)
			return ThumbnailComponent.class;
		if (def == SimpleCompoundComponent.DEFINITION)
			return SimpleCompoundComponent.class;
		if (def == ImageChartComponent.DEFINITION)
			return ImageChartComponent.class;
		if (def == WirelessTempHumSensor.DEFINITION)
			return WirelessTempHumSensor.class;
		if (def == ButtonComponent.DEFINITION)
			return ButtonComponent.class;
		if (def == LinkComponent.DEFINITION)
			return LinkComponent.class;
		if (def == AlarmListComponent.DEFINITION)
			return AlarmListComponent.class;
		if (def == ScriptButtonComponent.DEFINITION)
			return ScriptButtonComponent.class;
		// if (def == FlexBuilderComponent.DEFINITION)
		// return FlexBuilderComponent.class;
		if (def == ChartComparatorComponent.DEFINITION)
			return ChartComparatorComponent.class;
		if (def == FlexBuilderComponent.DEFINITION)
			return FlexBuilderComponent.class;
		return null;
	}

	public static List<String> getExportTypes() {
		List<ImplDefinition> definitions = getImplementations();
		List<String> result = new ArrayList<String>(definitions.size());
		for (ImplDefinition def : definitions)
			result.add(def.getExportName());
		return result;
	}

	private int index;
	private String idSuffix;
	private String style;
	@JsonRemoteProperty
	private int x;
	@JsonRemoteProperty
	private int y;

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	abstract public ImplDefinition definition();

	abstract public void validateDataPoint(User user, boolean makeReadOnly);

	abstract public boolean isVisible();

	abstract public boolean isValid();

	abstract public boolean containsValidVisibleDataPoint(int dataPointId);

	public boolean isPointComponent() {
		return false;
	}

	public boolean isCompoundComponent() {
		return false;
	}

	public boolean isCustomComponent() {
		return false;
	}

	public String getDefName() {
		return definition().getName();
	}

	public String getId() {
		if (StringUtils.isEmpty(idSuffix))
			return Integer.toString(index);
		return Integer.toString(index) + idSuffix;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getIdSuffix() {
		return idSuffix;
	}

	public void setIdSuffix(String idSuffix) {
		this.idSuffix = idSuffix;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getStyle() {
		if (style != null)
			return style;

		StringBuilder sb = new StringBuilder();
		sb.append("position:absolute;");
		sb.append("left:").append(x).append("px;");
		sb.append("top:").append(y).append("px;");
		return sb.toString();
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void validate(DwrResponseI18n response) {
		if (x < 0)
			response.addMessage("x", new LocalizableMessage(
					"validate.cannotBeNegative"));
		if (y < 0)
			response.addMessage("y", new LocalizableMessage(
					"validate.cannotBeNegative"));
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
		out.writeInt(index);
		SerializationHelper.writeSafeUTF(out, idSuffix);
		out.writeInt(x);
		out.writeInt(y);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		int ver = in.readInt();

		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			index = in.readInt();
			idSuffix = SerializationHelper.readSafeUTF(in);
			x = in.readInt();
			y = in.readInt();
		}
	}

	protected void writeDataPoint(ObjectOutputStream out, DataPointVO dataPoint)
			throws IOException {
		if (dataPoint == null)
			out.writeInt(0);
		else
			out.writeInt(dataPoint.getId());
	}

	protected DataPointVO readDataPoint(ObjectInputStream in)
			throws IOException {
		return new DataPointDao().getDataPoint(in.readInt());
	}

	/**
	 * @throws JsonException
	 */
	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		// no op
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		map.put("type", definition().getExportName());
	}

	protected void jsonSerializeDataPoint(Map<String, Object> map, String key,
			PointComponent comp) {
		DataPointVO dataPoint = comp.tgetDataPoint();
		if (dataPoint == null)
			map.put(key, null);
		else
			map.put(key, dataPoint.getXid());
	}

	protected void jsonDeserializeDataPoint(JsonValue jsonXid,
			PointComponent comp) throws JsonException {
		if (jsonXid != null) {
			if (jsonXid.isNull())
				comp.tsetDataPoint(null);
			else {
				String xid = jsonXid.toJsonString().getValue();
				DataPointVO dataPoint = new DataPointDao().getDataPoint(xid);
				if (dataPoint == null)
					throw new LocalizableJsonException(
							"emport.error.missingPoint", xid);
				if (!comp.definition().supports(
						dataPoint.getPointLocator().getDataTypeId()))
					throw new LocalizableJsonException(
							"emport.error.component.incompatibleDataType", xid,
							definition().getExportName());
				comp.tsetDataPoint(dataPoint);
			}
		}
	}

	public static class Factory implements TypeFactory {
		@Override
		public Class<?> getType(JsonValue jsonValue) throws JsonException {
			JsonObject json = jsonValue.toJsonObject();

			String type = json.getString("type");
			if (type == null)
				throw new LocalizableJsonException(
						"emport.error.component.missing", "type",
						getExportTypes());

			ImplDefinition def = ImplDefinition.findByExportName(
					getImplementations(), type);

			if (def == null)
				throw new LocalizableJsonException("emport.error.text.invalid",
						"type", type, getExportTypes());

			return resolveClass(def);
		}
	}
}
