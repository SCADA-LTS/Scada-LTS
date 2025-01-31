package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

public class UShortWrapper extends TypeWrapper<UShort> {

    public static final int MIN_VALUE = UShort.MIN_VALUE;
    public static final int MAX_VALUE = UShort.MAX_VALUE;

    public UShortWrapper() {
        super(UShort.class);
    }
}
