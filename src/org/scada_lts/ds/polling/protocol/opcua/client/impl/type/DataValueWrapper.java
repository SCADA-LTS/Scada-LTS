package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

public class DataValueWrapper extends TypeWrapper<DataValue> {

    public DataValueWrapper() {
        super(DataValue.class);
    }
}
