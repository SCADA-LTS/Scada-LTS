package org.scada_lts.web.beans.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.serotonin.mango.vo.PointDataType;

import java.io.IOException;

public class PointDataTypeSerializer extends StdSerializer<PointDataType> {

    public PointDataTypeSerializer() {
        super(PointDataType.class);
    }

    @Override
    public void serialize(PointDataType value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.getMessageText());
    }
}
