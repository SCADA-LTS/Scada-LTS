package org.scada_lts.web.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.scada_lts.web.mvc.api.css.CssStyle;

import java.io.IOException;


public class XssProtectCssStyleSerializer extends StdSerializer<CssStyle> {

    public XssProtectCssStyleSerializer() {
        super(CssStyle.class);
    }

    @Override
    public void serialize(CssStyle value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        String content = XssProtectHtmlEscapeUtils.escape(value.getContent());
        jgen.writeStartObject();
        jgen.writeStringField("content", content);
        jgen.writeEndObject();
    }
}