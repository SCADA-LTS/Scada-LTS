package org.scada_lts.ds.polling.protocol.opcua.client.impl.type;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UNumber;

import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaConverterUtils.*;

public class ScadaUNumber extends UNumber {

    private final MangoValue value;

    public ScadaUNumber(MangoValue value) {
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
        return toFloatFromString(value);
    }

    @Override
    public double doubleValue() {
        return toDoubleFromString(value);
    }

    public UNumber toUNumber() {
        return ULong.valueOf(Double.valueOf(String.valueOf(value)).longValue());
    }

    public static Class<?> getType() {
        return UNumber.class;
    }
}
