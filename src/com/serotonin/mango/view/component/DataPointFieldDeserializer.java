package com.serotonin.mango.view.component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.mango.service.DataPointService;

import java.io.IOException;

public class DataPointFieldDeserializer extends JsonDeserializer<DataPointVO> {
    @Override
    public DataPointVO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String datapointXid = node.asText();
        return new DataPointService().getDataPoint(datapointXid);
    }
}
