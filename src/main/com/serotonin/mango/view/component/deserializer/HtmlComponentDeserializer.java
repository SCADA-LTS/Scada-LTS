package com.serotonin.mango.view.component.deserializer;

import br.org.scadabr.view.component.ChartComparatorComponent;
import br.org.scadabr.view.component.FlexBuilderComponent;
import br.org.scadabr.view.component.LinkComponent;
import br.org.scadabr.view.component.ScriptButtonComponent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.serotonin.mango.view.component.HtmlComponent;
import org.scada_lts.web.mvc.api.dto.view.components.html.*;

import java.io.IOException;

public class HtmlComponentDeserializer extends JsonDeserializer<HtmlBaseComponentDTO> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public HtmlBaseComponentDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerSubtypes(FlexBuilderComponentDTO.class);
        mapper.registerSubtypes(LinkComponentDTO.class);
        mapper.registerSubtypes(ScriptButtonComponentDTO.class);
        mapper.registerSubtypes(ChartComparatorComponentDTO.class);
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("defName").asText();

        HtmlBaseComponentDTO htmlComponentDTO = null;
        if (type.equals(HtmlComponent.DEFINITION.getName())) {
            htmlComponentDTO = mapper.readValue(node.toString(), HtmlComponentDTO.class);
        } else if (type.equals(FlexBuilderComponent.DEFINITION.getName())) {
            htmlComponentDTO = mapper.readValue(node.toString(), FlexBuilderComponentDTO.class);
        } else if (type.equals(LinkComponent.DEFINITION.getName())) {
            htmlComponentDTO = mapper.readValue(node.toString(), LinkComponentDTO.class);
        } else if (type.equals(ScriptButtonComponent.DEFINITION.getName())) {
            htmlComponentDTO = mapper.readValue(node.toString(), ScriptButtonComponentDTO.class);
        } else if (type.equals(ChartComparatorComponent.DEFINITION.getName())) {
            htmlComponentDTO = mapper.readValue(node.toString(), ChartComparatorComponentDTO.class);
        }

        return htmlComponentDTO;
    }
}
