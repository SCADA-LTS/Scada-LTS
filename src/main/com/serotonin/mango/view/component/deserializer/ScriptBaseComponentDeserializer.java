package com.serotonin.mango.view.component.deserializer;

import br.org.scadabr.view.component.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.serotonin.mango.view.component.ScriptComponent;
import org.scada_lts.web.mvc.api.dto.view.components.point.script.ButtonComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.script.ScriptBaseComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.script.ScriptComponentDTO;

import java.io.IOException;

public class ScriptBaseComponentDeserializer extends JsonDeserializer<ScriptBaseComponentDTO> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public ScriptBaseComponentDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerSubtypes(ScriptComponentDTO.class);
        mapper.registerSubtypes(ButtonComponentDTO.class);
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("defName").asText();

        ScriptBaseComponentDTO scriptBaseComponentDTO = null;
        if (type.equals(ScriptComponent.DEFINITION.getName())) {
            scriptBaseComponentDTO = mapper.readValue(node.toString(), ScriptComponentDTO.class);
        } else if (type.equals(ButtonComponent.DEFINITION.getName())) {
            scriptBaseComponentDTO = mapper.readValue(node.toString(), ButtonComponentDTO.class);
        }

        return scriptBaseComponentDTO;
    }
}
