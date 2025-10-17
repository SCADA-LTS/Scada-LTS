package com.serotonin.mango.view.component.deserializer;

import br.org.scadabr.view.component.ButtonComponent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.serotonin.mango.view.component.*;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.mvc.api.dto.view.components.point.*;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.ImageSetComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.script.ScriptBaseComponentDTO;

import java.io.IOException;

public class PointComponentDeserializer extends JsonDeserializer<PointComponentDTO> {

    private static ObjectMapper mapper = ApplicationBeans.getObjectMapper();

    @Override
    public PointComponentDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerSubtypes(ImageSetComponentDTO.class);
        mapper.registerSubtypes(ScriptBaseComponentDTO.class);
        mapper.registerSubtypes(DynamicGraphicComponentDTO.class);
        mapper.registerSubtypes(SimplePointComponentDTO.class);
        mapper.registerSubtypes(SimpleImageComponentDTO.class);
        mapper.registerSubtypes(ThumbnailComponentDTO.class);
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("defName").asText();

        PointComponentDTO pointComponent = null;
        if (type.equals(AnalogGraphicComponent.DEFINITION.getName()) ||
                type.equals(BinaryGraphicComponent.DEFINITION.getName()) ||
                type.equals(MultistateGraphicComponent.DEFINITION.getName())
        ) {
            pointComponent = mapper.readValue(node.toString(), ImageSetComponentDTO.class);
        }
        else if (type.equals(ButtonComponent.DEFINITION.getName()) ||
                type.equals(ScriptComponent.DEFINITION.getName()))
        {
            pointComponent = mapper.readValue(node.toString(), ScriptBaseComponentDTO.class);
        }
        if (type.equals(SimplePointComponent.DEFINITION.getName()))
        {
            pointComponent = mapper.readValue(node.toString(), SimplePointComponentDTO.class);
        }
        else if (type.equals(DynamicGraphicComponent.DEFINITION.getName()))
        {
            pointComponent = mapper.readValue(node.toString(), DynamicGraphicComponentDTO.class);
        }
        else if (type.equals(SimpleImageComponent.DEFINITION.getName()))
        {
            pointComponent = mapper.readValue(node.toString(), SimpleImageComponentDTO.class);
        }
        else if (type.equals(ThumbnailComponent.DEFINITION.getName()))
        {
            pointComponent = mapper.readValue(node.toString(), ThumbnailComponentDTO.class);
        }

        return pointComponent;
    }
}