package org.scada_lts.web.beans.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.chrono.Chronology;


public class ChronologySerializer extends StdSerializer<Chronology> {

    public ChronologySerializer() {
        super(Chronology.class);
    }

    @Override
    public void serialize(Chronology value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("id", value.getId());
        jgen.writeStringField("calendarType", value.getCalendarType());
        jgen.writeEndObject();
    }
}