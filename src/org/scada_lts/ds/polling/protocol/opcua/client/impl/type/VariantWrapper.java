package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

public class VariantWrapper extends TypeWrapper<Variant> {

    public VariantWrapper() {
        super(Variant.class);
    }
}
