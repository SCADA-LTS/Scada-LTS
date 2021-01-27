package com.serotonin.mango.view.event;

import com.serotonin.json.*;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.ImplDefinition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract public class BaseEventTextRenderer implements EventRenderer, JsonSerializable {

    static List<ImplDefinition> definitions;

    static void ensureDefinitions() {
        if (definitions == null) {
            List<ImplDefinition> d = new ArrayList<ImplDefinition>();
            d.add(EventTextRenderer.getDefinition());
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
    private static final int version = 9;

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
                throw new LocalizableJsonException("emport.error.event.missing", "type", getExportTypes());

            ImplDefinition def = null;
            ensureDefinitions();
            for (ImplDefinition id : definitions) {
                if (id.getExportName().equalsIgnoreCase(type)) {
                    def = id;
                    break;
                }
            }

            if (def == null)
                throw new LocalizableJsonException("emport.error.event.invalid", "type", type, getExportTypes());

            Class<? extends EventRenderer> clazz = EventTextRenderer.class;

            return clazz;
        }
    }

}
