package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;

import java.math.BigInteger;

public class ULongWrapper extends TypeWrapper<ULong> {

    public static final BigInteger MIN_VALUE = ULong.MIN_VALUE;
    public static final BigInteger MAX_VALUE = ULong.MAX_VALUE;

    public ULongWrapper() {
        super(ULong.class);
    }
}
