package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.serotonin.mango.view.component.AnalogGraphicComponent;
import com.serotonin.mango.view.component.BinaryGraphicComponent;
import com.serotonin.mango.view.component.MultistateGraphicComponent;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.AnalogGraphicComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.BinaryGraphicComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.ImageSetComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.MultistateGraphicComponentDTO;

import java.io.IOException;

public class ImageSetComponentDeserializer extends JsonDeserializer<ImageSetComponentDTO> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public ImageSetComponentDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerSubtypes(AnalogGraphicComponentDTO.class);
        mapper.registerSubtypes(BinaryGraphicComponentDTO.class);
        mapper.registerSubtypes(MultistateGraphicComponentDTO.class);
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("defName").asText();

        ImageSetComponentDTO imageSetComponent = null;
        if (type.equals(AnalogGraphicComponent.DEFINITION.getName())) {
            imageSetComponent = mapper.readValue(node.toString(), AnalogGraphicComponentDTO.class);
        } else if (type.equals(BinaryGraphicComponent.DEFINITION.getName())) {
            imageSetComponent = mapper.readValue(node.toString(), BinaryGraphicComponentDTO.class);
        } else if (type.equals(MultistateGraphicComponent.DEFINITION.getName())) {
            imageSetComponent = mapper.readValue(node.toString(), MultistateGraphicComponentDTO.class);
        }

        return imageSetComponent;
    }
}