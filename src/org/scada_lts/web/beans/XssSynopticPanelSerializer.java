package org.scada_lts.web.beans;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.scada_lts.service.model.SynopticPanel;

import java.io.IOException;


public class XssSynopticPanelSerializer extends StdSerializer<SynopticPanel> {

    public XssSynopticPanelSerializer() {
        super(SynopticPanel.class);
    }

    @Override
    public void serialize(SynopticPanel value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        String name = XssUtils.escape(value.getName());
        int id = value.getId();
        String xid = XssUtils.escape(value.getXid());
        String componentData = XssUtils.escape(value.getComponentData());
        String vectorImage = value.getVectorImage();

        jgen.writeStartObject();
        jgen.writeStringField("id", String.valueOf(id));
        jgen.writeStringField("xid", xid);
        jgen.writeStringField("name", name);
        jgen.writeStringField("vectorImage", vectorImage);
        jgen.writeStringField("componentData", componentData);
        jgen.writeEndObject();
    }
}