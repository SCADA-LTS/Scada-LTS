package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.serotonin.mango.view.component.*;
import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.mango.service.DataPointService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoundComponentDeserializer extends JsonDeserializer<CompoundComponent> {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public CompoundComponent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerSubtypes(EnhancedImageChartComponent.class);
        mapper.registerSubtypes(ImageChartComponent.class);
        mapper.registerSubtypes(SimpleCompoundComponent.class);
        mapper.registerSubtypes(WirelessTempHumSensor.class);
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("defName").asText();

        CompoundComponent compoundComponent = null;
        if (type.equals(EnhancedImageChartComponent.DEFINITION.getName())) {
            compoundComponent = mapper.readValue(node.toString(), EnhancedImageChartComponent.class);
        } else if (type.equals(ImageChartComponent.DEFINITION.getName())) {
            compoundComponent = mapper.readValue(node.toString(), ImageChartComponent.class);
        } else if (type.equals(SimpleCompoundComponent.DEFINITION.getName())) {
            compoundComponent = mapper.readValue(node.toString(), SimpleCompoundComponent.class);
        } else if (type.equals(WirelessTempHumSensor.DEFINITION.getName())) {
            compoundComponent = mapper.readValue(node.toString(), WirelessTempHumSensor.class);
        }

        List<CompoundChild> childComponents = compoundComponent.getChildComponents();

        TypeReference<HashMap<String, String>> typeRef
                = new TypeReference<HashMap<String, String>>() {};
        Map<String, String> map = mapper.convertValue(node.get("children"), typeRef);

        for (Map.Entry<String, String> childJson : map.entrySet()) {
            if (childJson.getValue() != null) {
                CompoundChild compoundChild = childComponents.stream().filter(id -> id.getId().equals(childJson.getKey())).findFirst().orElse(null);
                if (compoundChild != null && compoundChild.getViewComponent().isPointComponent()) {
                    PointComponent pointComponent = (PointComponent) compoundChild.getViewComponent();
                    String dataPointXid = childJson.getValue();
                    DataPointVO dataPoint = new DataPointService().getDataPoint(dataPointXid);
                    if (dataPoint != null && pointComponent.definition().supports(dataPoint.getPointLocator().getDataTypeId()))
                        pointComponent.tsetDataPoint(dataPoint);
                }
            }

        }

        return compoundComponent;
    }
}