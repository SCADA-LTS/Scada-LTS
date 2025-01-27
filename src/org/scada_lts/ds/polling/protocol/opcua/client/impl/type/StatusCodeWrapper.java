package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

public class StatusCodeWrapper extends TypeWrapper<StatusCode> {

    public StatusCodeWrapper() {
        super(StatusCode.class);
    }
}
