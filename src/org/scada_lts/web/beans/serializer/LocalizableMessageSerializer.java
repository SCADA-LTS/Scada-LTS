package org.scada_lts.web.beans.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.serotonin.mango.Common;
import com.serotonin.web.i18n.LocalizableMessage;

import java.io.IOException;

public class LocalizableMessageSerializer extends StdSerializer<LocalizableMessage> {

    public LocalizableMessageSerializer() {
        super(LocalizableMessage.class);
    }

    @Override
    public void serialize(LocalizableMessage value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.getLocalizedMessage(Common.getBundle()));
    }
}
