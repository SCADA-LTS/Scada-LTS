package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.DynamicImage;

import java.io.IOException;

public class DynamicImageFieldDeserializer extends JsonDeserializer<DynamicImage> {
    @Override
    public DynamicImage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String dynamicImageId = node.asText();
        return Common.ctx.getDynamicImage(dynamicImageId);
    }
}