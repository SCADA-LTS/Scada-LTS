package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.ImageSet;

import java.io.IOException;

public class ImageSetFieldDeserializer  extends JsonDeserializer<ImageSet> {
    @Override
    public ImageSet deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String imageSetId = node.asText();
        return Common.ctx.getImageSets().stream().filter(i -> i.getId().equals(imageSetId)).findAny().orElse(null);
    }
}
