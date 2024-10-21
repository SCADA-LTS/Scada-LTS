package org.scada_lts.web.beans.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;


public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    public LocalDateTimeSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("nano", value.getNano());
        jgen.writeNumberField("year", value.getYear());
        jgen.writeNumberField("monthValue", value.getMonthValue());
        jgen.writeNumberField("dayOfMonth", value.getDayOfMonth());
        jgen.writeNumberField("hour", value.getHour());
        jgen.writeNumberField("minute", value.getMinute());
        jgen.writeNumberField("second", value.getSecond());
        jgen.writeStringField("month", value.getMonth().name());
        jgen.writeStringField("dayOfWeek", value.getDayOfWeek().name());
        jgen.writeNumberField("dayOfYear", value.getDayOfYear());
        jgen.writeObjectField("chronology", value.getChronology());
        jgen.writeEndObject();
    }
}