package com.serotonin.mango.view.component.deserializer;

import br.org.scadabr.view.component.ButtonComponent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.serotonin.mango.view.component.*;

import java.io.IOException;

public class PointComponentDeserializer extends JsonDeserializer<PointComponent> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public PointComponent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.registerSubtypes(ImageSetComponent.class);
        mapper.registerSubtypes(ScriptComponent.class);
        mapper.registerSubtypes(DynamicGraphicComponent.class);
        mapper.registerSubtypes(SimplePointComponent.class);
        mapper.registerSubtypes(SimpleImageComponent.class);
        mapper.registerSubtypes(ThumbnailComponent.class);
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("typeName").asText();

        PointComponent pointComponent = null;
        if (type.equals(AnalogGraphicComponent.DEFINITION.getExportName()) ||
                type.equals(BinaryGraphicComponent.DEFINITION.getExportName()) ||
                type.equals(MultistateGraphicComponent.DEFINITION.getExportName())
        ) {
            pointComponent = mapper.readValue(node.toString(), ImageSetComponent.class);
        }
        else if (type.equals(ButtonComponent.DEFINITION.getExportName()) ||
                type.equals(ScriptComponent.DEFINITION.getExportName()))
        {
            pointComponent = mapper.readValue(node.toString(), ScriptComponent.class);
        }
        else if (type.equals(SimplePointComponent.DEFINITION.getExportName()))
        {
            pointComponent = mapper.readValue(node.toString(), SimplePointComponent.class);
        }
        else if (type.equals(DynamicGraphicComponent.DEFINITION.getExportName()))
        {
            pointComponent = mapper.readValue(node.toString(), DynamicGraphicComponent.class);
        }
        else if (type.equals(SimpleImageComponent.DEFINITION.getExportName()))
        {
            pointComponent = mapper.readValue(node.toString(), SimpleImageComponent.class);
        }
        else if (type.equals(ThumbnailComponent.DEFINITION.getExportName()))
        {
            pointComponent = mapper.readValue(node.toString(), ThumbnailComponent.class);
        }

        return pointComponent;
    }
}