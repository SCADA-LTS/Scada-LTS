package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.serotonin.mango.view.ImplDefinition;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.serotonin.mango.view.component.ViewComponent.getImplementations;
import static org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO.resolveClassForDeserializer2;

public class ViewComponentDeserializer extends JsonDeserializer<List<GraphicalViewComponentDTO>> {

    private static ObjectMapper mapper = ApplicationBeans.getObjectMapper();

    @Override
    public List<GraphicalViewComponentDTO> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<GraphicalViewComponentDTO> viewComponents = new ArrayList<>();
        ObjectCodec oc = jsonParser.getCodec();
        ArrayNode viewComponentNode = oc.readTree(jsonParser);
        Iterator<JsonNode> components = viewComponentNode.elements();
        while (components.hasNext()) {
            JsonNode componentNode = components.next();
            ImplDefinition def = ImplDefinition.findByName(getImplementations(),
                    componentNode.get("defName").asText());
            GraphicalViewComponentDTO viewComponent = mapper.readValue(componentNode.toString(), resolveClassForDeserializer2(def));
            viewComponents.add(viewComponent);
        }

        return viewComponents;
    }
}