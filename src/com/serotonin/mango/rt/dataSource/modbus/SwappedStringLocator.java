package com.serotonin.mango.rt.dataSource.modbus;

import com.serotonin.modbus4j.locator.StringLocator;

import java.nio.charset.Charset;

/** String byte order within each register; register order is unchanged. */
final class SwappedStringLocator extends StringLocator {
    SwappedStringLocator(int slaveId, int range, int offset, int dataType,
                         int registerCount, Charset charset) {
        super(slaveId, range, offset, dataType, registerCount, charset);
    }

    @Override
    public String bytesToValueRealOffset(byte[] data, int offset) {
        // Batch responses may be shared with other locators. Copy only this point's registers.
        byte[] swapped = new byte[getRegisterCount() * 2];
        int start = offset * 2;
        for (int i = 0; i < swapped.length; i += 2) {
            swapped[i] = data[start + i + 1];
            swapped[i + 1] = data[start + i];
        }
        // Swap before charset decoding and VARCHAR's null-byte search.
        return super.bytesToValueRealOffset(swapped, 0);
    }

    @Override
    public short[] valueToShorts(String value) {
        short[] registers = super.valueToShorts(value);
        for (int i = 0; i < registers.length; i++)
            registers[i] = Short.reverseBytes(registers[i]);
        return registers;
    }
}
