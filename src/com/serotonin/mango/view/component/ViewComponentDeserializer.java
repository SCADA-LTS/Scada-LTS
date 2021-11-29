package com.serotonin.mango.view.component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.serotonin.mango.view.ImplDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.serotonin.mango.view.component.ViewComponent.getImplementations;
import static com.serotonin.mango.view.component.ViewComponent.resolveClass;

public class ViewComponentDeserializer extends JsonDeserializer<List<ViewComponent>> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<ViewComponent> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        List<ViewComponent> viewComponents = new ArrayList<>();
        ObjectCodec oc = jsonParser.getCodec();
        ArrayNode viewComponentNode = oc.readTree(jsonParser);
        Iterator<JsonNode> components = viewComponentNode.elements();
        while (components.hasNext()) {
            JsonNode componentNode = components.next();
            ImplDefinition def = ImplDefinition.findByName(getImplementations(),
                    componentNode.get("typeName").asText());
            ViewComponent viewComponent = mapper.readValue(componentNode.toString(), resolveClass(def));

            viewComponents.add(viewComponent);

        }

        return viewComponents;
    }
}
