package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

public class UIntegerWrapper extends TypeWrapper<UInteger> {

    public static final long MIN_VALUE = UInteger.MIN_VALUE;
    public static final long MAX_VALUE = UInteger.MAX_VALUE;

    public UIntegerWrapper() {
        super(UInteger.class);
    }
}
