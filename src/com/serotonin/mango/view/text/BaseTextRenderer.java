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
package com.serotonin.mango.view.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonSerializable;
import com.serotonin.json.JsonValue;
import com.serotonin.json.TypeFactory;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.ImplDefinition;

abstract public class BaseTextRenderer implements TextRenderer, JsonSerializable {
    static List<ImplDefinition> definitions;

    static void ensureDefinitions() {
        if (definitions == null) {
            List<ImplDefinition> d = new ArrayList<ImplDefinition>();
            d.add(AnalogRenderer.getDefinition());
            d.add(BinaryTextRenderer.getDefinition());
            d.add(MultistateRenderer.getDefinition());
            d.add(NoneRenderer.getDefinition());
            d.add(PlainRenderer.getDefinition());
            d.add(RangeRenderer.getDefinition());
            d.add(TimeRenderer.getDefinition());
            definitions = d;
        }
    }

    public static List<ImplDefinition> getImplementation(int dataType) {
        ensureDefinitions();
        List<ImplDefinition> impls = new ArrayList<ImplDefinition>(definitions.size());
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

    public String getText(int hint) {
        if (hint == HINT_RAW)
            return "";
        return UNKNOWN_VALUE;
    }

    public String getText(PointValueTime valueTime, int hint) {
        if (valueTime == null)
            return getText(hint);
        return getText(valueTime.getValue(), hint);
    }

    public String getText(MangoValue value, int hint) {
        if (value == null)
            return getText(hint);
        return getTextImpl(value, hint);
    }

    abstract protected String getTextImpl(MangoValue value, int hint);

    public String getText(double value, int hint) {
        return Double.toString(value);
    }

    public String getText(int value, int hint) {
        return Integer.toString(value);
    }

    public String getText(boolean value, int hint) {
        return value ? "1" : "0";
    }

    public String getText(String value, int hint) {
        return value;
    }

    @Override
    public String getMetaText() {
        return null;
    }

    //
    // / Colours
    //
    public String getColour() {
        return null;
    }

    public String getColour(PointValueTime valueTime) {
        if (valueTime == null)
            return getColour();
        return getColour(valueTime.getValue());
    }

    public String getColour(MangoValue value) {
        if (value == null)
            return getColour();
        return getColourImpl(value);
    }

    abstract protected String getColourImpl(MangoValue value);

    public String getColour(double value) {
        return null;
    }

    public String getColour(int value) {
        return null;
    }

    public String getColour(boolean value) {
        return null;
    }

    public String getColour(String value) {
        return null;
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
        in.readInt(); // Read the version. Value is currently not used.
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) {
        // no op. The type value is used by the factory.
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("type", getDef().getExportName());
    }

    public static class Factory implements TypeFactory {
        @Override
        public Class<?> getType(JsonValue jsonValue) throws JsonException {
            JsonObject json = jsonValue.toJsonObject();

            String type = json.getString("type");
            if (type == null)
                throw new LocalizableJsonException("emport.error.text.missing", "type", getExportTypes());

            ImplDefinition def = null;
            ensureDefinitions();
            for (ImplDefinition id : definitions) {
                if (id.getExportName().equalsIgnoreCase(type)) {
                    def = id;
                    break;
                }
            }

            if (def == null)
                throw new LocalizableJsonException("emport.error.text.invalid", "type", type, getExportTypes());

            Class<? extends TextRenderer> clazz = null;
            if (def == AnalogRenderer.getDefinition())
                clazz = AnalogRenderer.class;
            else if (def == BinaryTextRenderer.getDefinition())
                clazz = BinaryTextRenderer.class;
            else if (def == MultistateRenderer.getDefinition())
                clazz = MultistateRenderer.class;
            else if (def == NoneRenderer.getDefinition())
                clazz = NoneRenderer.class;
            else if (def == PlainRenderer.getDefinition())
                clazz = PlainRenderer.class;
            else if (def == RangeRenderer.getDefinition())
                clazz = RangeRenderer.class;
            else if (def == TimeRenderer.getDefinition())
                clazz = TimeRenderer.class;
            else
                throw new ShouldNeverHappenException("What's this?: " + def.getName());

            return clazz;
        }
    }
}
