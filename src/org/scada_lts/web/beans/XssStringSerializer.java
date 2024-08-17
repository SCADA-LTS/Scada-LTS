package org.scada_lts.web.beans;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class XssStringSerializer extends StdSerializer<String> {

    public XssStringSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        String content = XssUtils.escape(value);
        jgen.writeString(content);
    }
}