package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;

public class UByteWrapper extends TypeWrapper<UByte> {

    public static final short MIN_VALUE = UByte.MIN_VALUE;
    public static final short MAX_VALUE = UByte.MAX_VALUE;

    public UByteWrapper() {
        super(UByte.class);
    }
}
