package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;

public class DateTimeWrapper extends TypeWrapper<DateTime> {

    public DateTimeWrapper() {
        super(DateTime.class);
    }
}
