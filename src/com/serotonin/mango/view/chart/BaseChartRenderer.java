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
package com.serotonin.mango.view.chart;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonSerializable;
import com.serotonin.json.JsonValue;
import com.serotonin.json.TypeFactory;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.ImplDefinition;

abstract public class BaseChartRenderer implements ChartRenderer, JsonSerializable {
    private static ImplDefinition noneDefinition = new ImplDefinition("chartRendererNone", "NONE",
            "chartRenderer.none", new int[] { DataTypes.ALPHANUMERIC, DataTypes.BINARY, DataTypes.MULTISTATE,
                    DataTypes.NUMERIC, DataTypes.IMAGE });

    static List<ImplDefinition> definitions;

    static void ensureDefinitions() {
        if (definitions == null) {
            List<ImplDefinition> d = new ArrayList<ImplDefinition>();
            d.add(noneDefinition);
            d.add(TableChartRenderer.getDefinition());
            d.add(ImageChartRenderer.getDefinition());
            d.add(StatisticsChartRenderer.getDefinition());
            // d.add(ImageFlipbookRenderer.getDefinition());
            definitions = d;
        }
    }

    public static List<ImplDefinition> getImplementations(int dataType) {
        ensureDefinitions();
        List<ImplDefinition> impls = new ArrayList<ImplDefinition>();
        for (ImplDefinition def : definitions) {
            if (def.supports(dataType))
                impls.add(def);
        }
        return impls;
    }

    public static List<String> getExportTypes() {
        ensureDefinitions();
        List<String> result = new ArrayList<String>(definitions.size());
        for (ImplDefinition def : definitions)
            result.add(def.getExportName());
        return result;
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
    }

    private void readObject(ObjectInputStream in) throws IOException {
        in.readInt();
    }

    /**
     * @throws JsonException
     */
    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        // no op. The type value is used by the factory.
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("type", getDef().getExportName());
    }

    public static class Factory implements TypeFactory {
        @Override
        public Class<?> getType(JsonValue jsonValue) throws JsonException {
            if (jsonValue.isNull())
                return null;

            JsonObject json = jsonValue.toJsonObject();

            String type = json.getString("type");
            if (type == null)
                throw new LocalizableJsonException("emport.error.chart.missing", "type", getExportTypes());

            ImplDefinition def = null;
            ensureDefinitions();
            for (ImplDefinition id : definitions) {
                if (id.getExportName().equalsIgnoreCase(type)) {
                    def = id;
                    break;
                }
            }

            if (def == null)
                throw new LocalizableJsonException("emport.error.chart.invalid", "type", type, getExportTypes());

            Class<? extends ChartRenderer> clazz = null;
            if (def == TableChartRenderer.getDefinition())
                clazz = TableChartRenderer.class;
            else if (def == ImageChartRenderer.getDefinition())
                clazz = ImageChartRenderer.class;
            else if (def == StatisticsChartRenderer.getDefinition())
                clazz = StatisticsChartRenderer.class;

            return clazz;
        }
    }
}
