package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaConverterUtils.*;

public class ScadaNumber extends Number {

    private final Object value;

    public ScadaNumber(Object value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return toInt(value);
    }

    @Override
    public long longValue() {
        return toLong(value);
    }

    @Override
    public float floatValue() {
        return toFloat(value);
    }

    @Override
    public double doubleValue() {
        return toDouble(value);
    }

    public Number toNumber() {
        return (Number) value;
    }
}
