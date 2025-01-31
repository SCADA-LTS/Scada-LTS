package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;


public abstract class TypeWrapper<T> {

    private final Class<T> type;

    public TypeWrapper(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }
}
