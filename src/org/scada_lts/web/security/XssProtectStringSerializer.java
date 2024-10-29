package org.scada_lts.web.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class XssProtectStringSerializer extends StdSerializer<String> {

    public XssProtectStringSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        String content = XssProtectHtmlEscapeUtils.escape(value);
        jgen.writeString(content);
    }
}