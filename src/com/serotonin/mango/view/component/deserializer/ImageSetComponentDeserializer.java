package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.serotonin.mango.view.component.AnalogGraphicComponent;
import com.serotonin.mango.view.component.BinaryGraphicComponent;
import com.serotonin.mango.view.component.ImageSetComponent;
import com.serotonin.mango.view.component.MultistateGraphicComponent;

import java.io.IOException;


public class ImageSetComponentDeserializer  extends JsonDeserializer<ImageSetComponent> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public ImageSetComponent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerSubtypes(AnalogGraphicComponent.class);
        mapper.registerSubtypes(BinaryGraphicComponent.class);
        mapper.registerSubtypes(MultistateGraphicComponent.class);
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("typeName").asText();

        ImageSetComponent imageSetComponent = null;
        if (type.equals(AnalogGraphicComponent.DEFINITION.getExportName())) {
            imageSetComponent = mapper.readValue(node.toString(), AnalogGraphicComponent.class);
        }
        else if (type.equals(BinaryGraphicComponent.DEFINITION.getExportName()))
        {
            imageSetComponent = mapper.readValue(node.toString(), BinaryGraphicComponent.class);
        }
        else if (type.equals(MultistateGraphicComponent.DEFINITION.getExportName()))
        {
            imageSetComponent = mapper.readValue(node.toString(), MultistateGraphicComponent.class);
        }

        return imageSetComponent;
    }
}