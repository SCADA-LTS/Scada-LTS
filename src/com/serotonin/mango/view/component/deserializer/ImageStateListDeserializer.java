package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.serotonin.db.IntValuePair;
import org.scada_lts.web.beans.ApplicationBeans;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageStateListDeserializer extends JsonDeserializer<List<IntValuePair>> {

    private static ObjectMapper mapper = ApplicationBeans.getObjectMapper();

    @Override
    public List<IntValuePair> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        ArrayNode node = oc.readTree(jsonParser);

        return Arrays.asList(mapper.readValue(node.toString(), IntValuePair[].class));
    }
}
