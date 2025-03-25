package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;

public class ByteStringWrapper extends TypeWrapper<ByteString> {

    public ByteStringWrapper() {
        super(ByteString.class);
    }
}
