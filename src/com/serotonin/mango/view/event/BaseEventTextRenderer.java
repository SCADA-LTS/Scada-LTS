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
package com.serotonin.mango.view.event;

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

abstract public class BaseEventTextRenderer implements EventTextRenderer, JsonSerializable {
    static List<ImplDefinition> definitions;

    static void ensureDefinitions() {
        if (definitions == null) {
            List<ImplDefinition> d = new ArrayList<ImplDefinition>();
            d.add(BinaryEventTextRenderer.getDefinition());
            d.add(MultistateEventRenderer.getDefinition());
            d.add(NoneEventRenderer.getDefinition());
            d.add(PlainEventRenderer.getDefinition());
            d.add(RangeEventRenderer.getDefinition());
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

    public String getShortText(int hint) {
        if (hint == HINT_RAW)
            return "";
        return UNKNOWN_VALUE;
    }

    public String getShortText(PointValueTime valueTime, int hint) {
        if (valueTime == null)
            return getShortText(hint);
        return getShortText(valueTime.getValue(), hint);
    }

    public String getShortText(MangoValue value, int hint) {
        if (value == null)
            return getShortText(hint);
        return getShortTextImpl(value, hint);
    }

    abstract protected String getShortTextImpl(MangoValue value, int hint);

    public String getShortText(double value, int hint) {
        return Double.toString(value);
    }

    public String getShortText(int value, int hint) {
        return Integer.toString(value);
    }

    public String getShortText(boolean value, int hint) {
        return value ? "1" : "0";
    }

    public String getShortText(String value, int hint) {
        return value;
    }

    public String getLongText(int hint) {
        if (hint == HINT_RAW)
            return "";
        return UNKNOWN_VALUE;
    }

    public String getLongText(PointValueTime valueTime, int hint) {
        if (valueTime == null)
            return getLongText(hint);
        return getLongText(valueTime.getValue(), hint);
    }

    public String getLongText(MangoValue value, int hint) {
        if (value == null)
            return getLongText(hint);
        return getLongTextImpl(value, hint);
    }

    abstract protected String getLongTextImpl(MangoValue value, int hint);

    public String getLongText(double value, int hint) {
        return Double.toString(value);
    }

    public String getLongText(int value, int hint) {
        return Integer.toString(value);
    }

    public String getLongText(boolean value, int hint) {
        return value ? "1" : "0";
    }

    public String getLongText(String value, int hint) {
        return value;
    }

    @Override
    public String getMetaShortText() {
        return null;
    }

    @Override
    public String getMetaLongText() {
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

            Class<? extends EventTextRenderer> clazz = null;
            if (def == BinaryEventTextRenderer.getDefinition())
                clazz = BinaryEventTextRenderer.class;
            else if (def == MultistateEventRenderer.getDefinition())
                clazz = MultistateEventRenderer.class;
            else if (def == NoneEventRenderer.getDefinition())
                clazz = NoneEventRenderer.class;
            else if (def == PlainEventRenderer.getDefinition())
                clazz = PlainEventRenderer.class;
            else if (def == RangeEventRenderer.getDefinition())
                clazz = RangeEventRenderer.class;
            else
                throw new ShouldNeverHappenException("What's this?: " + def.getName());

            return clazz;
        }
    }
}
