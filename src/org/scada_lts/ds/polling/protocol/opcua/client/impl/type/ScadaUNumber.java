package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UNumber;

import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaConverterUtils.*;

public class ScadaUNumber extends UNumber {

    private final Object value;

    public ScadaUNumber(Object value) {
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

    public UNumber toUNumber() {
        return ULong.valueOf(Double.valueOf(String.valueOf(value)).longValue());
    }

    public static Class<?> getType() {
        return UNumber.class;
    }
}
