package com.serotonin.mango.vo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.serotonin.mango.vo.TimePeriod;
import com.serotonin.mango.vo.TimePeriodFactory;

import java.io.IOException;

public class TimePeriodDeserializer extends JsonDeserializer<TimePeriod> {

    @Override
    public TimePeriod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        try {
            int code = Integer.parseInt(text);
            return TimePeriodFactory.getByCode(code);
        } catch (NumberFormatException e) {
            return TimePeriodFactory.getByName(text);
        }
    }
}
