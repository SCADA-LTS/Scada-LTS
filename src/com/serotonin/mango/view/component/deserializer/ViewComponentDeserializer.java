package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.component.ViewComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.serotonin.mango.view.component.ViewComponent.*;

public class ViewComponentDeserializer extends JsonDeserializer<List<ViewComponent>> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<ViewComponent> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<ViewComponent> viewComponents = new ArrayList<>();
        ObjectCodec oc = jsonParser.getCodec();
        ArrayNode viewComponentNode = oc.readTree(jsonParser);
        Iterator<JsonNode> components = viewComponentNode.elements();
        while (components.hasNext()) {
            JsonNode componentNode = components.next();
            ImplDefinition def = ImplDefinition.findByName(getImplementations(), componentNode.get("defName").asText());
            ViewComponent viewComponent = mapper.readValue(componentNode.toString(), resolveClassForDeserializer(def));
            viewComponents.add(viewComponent);
        }

        return viewComponents;
    }
}
