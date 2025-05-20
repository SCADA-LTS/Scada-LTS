//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.serotonin.modbus4j.locator;

import com.serotonin.modbus4j.exception.IllegalDataTypeException;
import java.nio.charset.Charset;

public class StringLocator extends BaseLocator<String> {
    public static final Charset ASCII = Charset.forName("ASCII");
    private final int dataType;
    private final int registerCount;
    private final Charset charset;

    public StringLocator(int slaveId, int range, int offset, int dataType, int registerCount) {
        this(slaveId, range, offset, dataType, registerCount, ASCII);
    }

    public StringLocator(int slaveId, int range, int offset, int dataType, int registerCount, Charset charset) {
        super(slaveId, range, offset);
        this.dataType = dataType;
        this.registerCount = registerCount;
        this.charset = charset;
        this.validate();
    }

    private void validate() {
        super.validate(this.registerCount);
        if (this.range != 1 && this.range != 2) {
            if (this.dataType != 18 && this.dataType != 19) {
                throw new IllegalDataTypeException("Invalid data type");
            }
        } else {
            throw new IllegalDataTypeException("Only binary values can be read from Coil and Input ranges");
        }
    }

    public int getDataType() {
        return this.dataType;
    }

    public int getRegisterCount() {
        return this.registerCount;
    }

    public String toString() {
        return "StringLocator(slaveId=" + this.getSlaveId() + ", range=" + this.range + ", offset=" + this.offset + ", dataType=" + this.dataType + ", registerCount=" + this.registerCount + ", charset=" + this.charset + ")";
    }

    public String bytesToValueRealOffset(byte[] data, int offset) {
        offset *= 2;
        int length = this.registerCount * 2;
        if (this.dataType == 18) {
            return new String(data, offset, length, this.charset);
        } else if (this.dataType != 19) {
            throw new RuntimeException("Unsupported data type: " + this.dataType);
        } else {
            int nullPos = -1;

            for(int i = offset; i < offset + length; ++i) {
                if (data[i] == 0) {
                    nullPos = i;
                    break;
                }
            }

            return nullPos == -1 ? new String(data, offset, length, this.charset) : new String(data, nullPos, length, this.charset);
        }
    }

    public short[] valueToShorts(String value) {
        short[] result = new short[this.registerCount];
        int resultByteLen = this.registerCount * 2;
        int length;
        if (value != null) {
            byte[] bytes = value.getBytes(this.charset);
            length = resultByteLen;
            if (resultByteLen > bytes.length) {
                length = bytes.length;
            }

            for(int i = 0; i < length; ++i) {
                this.setByte(result, i, bytes[i] & 255);
            }
        } else {
            length = 0;
        }

        int i;
        if (this.dataType == 18) {
            for(i = length; i < resultByteLen; ++i) {
                this.setByte(result, i, 32);
            }
        } else {
            if (this.dataType != 19) {
                throw new RuntimeException("Unsupported data type: " + this.dataType);
            }

            if (length >= resultByteLen) {
                int var10001 = this.registerCount - 1;
                result[var10001] = (short)(result[var10001] & '\uff00');
            } else {
                for(i = length; i < resultByteLen; ++i) {
                    this.setByte(result, i, 0);
                }
            }
        }

        return result;
    }

    private void setByte(short[] s, int byteIndex, int value) {
        if (byteIndex % 2 == 0) {
            s[byteIndex / 2] = (short)(s[byteIndex / 2] | value << 8);
        } else {
            s[byteIndex / 2] = (short)(s[byteIndex / 2] | value);
        }

    }
}
