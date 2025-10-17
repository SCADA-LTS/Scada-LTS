package com.serotonin.mango.view.component.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.serotonin.mango.view.component.*;
import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.beans.ApplicationBeans;
import org.scada_lts.web.mvc.api.dto.view.components.compound.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoundComponentDeserializer extends JsonDeserializer<CompoundComponentDTO> {

    private static ObjectMapper mapper = ApplicationBeans.getObjectMapper();

    @Override
    public CompoundComponentDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerSubtypes(EnhancedImageChartComponentDTO.class);
        mapper.registerSubtypes(ImageChartComponentDTO.class);
        mapper.registerSubtypes(SimpleCompoundComponentDTO.class);
        mapper.registerSubtypes(WirelessTempHumSensorDTO.class);
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        String type = node.get("defName").asText();

        CompoundComponentDTO compoundComponentDTO = null;
        CompoundComponent compoundComponent = null;
        if (type.equals(EnhancedImageChartComponent.DEFINITION.getName())) {
            compoundComponentDTO = mapper.readValue(node.toString(), EnhancedImageChartComponentDTO.class);
            compoundComponent = new EnhancedImageChartComponent();
        } else if (type.equals(ImageChartComponent.DEFINITION.getName())) {
            compoundComponentDTO = mapper.readValue(node.toString(), ImageChartComponentDTO.class);
            compoundComponent = new ImageChartComponent();
        } else if (type.equals(SimpleCompoundComponent.DEFINITION.getName())) {
            compoundComponentDTO = mapper.readValue(node.toString(), SimpleCompoundComponentDTO.class);
            compoundComponent = new SimpleCompoundComponent();
        } else if (type.equals(WirelessTempHumSensor.DEFINITION.getName())) {
            compoundComponentDTO = mapper.readValue(node.toString(), WirelessTempHumSensorDTO.class);
            compoundComponent = new WirelessTempHumSensor();
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
        compoundComponentDTO.setCompoundChildren(childComponents);

        return compoundComponentDTO;
    }
}