package org.scada_lts.ds.polling.protocol.opcua.vo;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaConverterUtils;
import org.scada_lts.ds.polling.protocol.opcua.client.impl.type.*;

import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaConverterUtils.*;


public enum OpcUaBaseDataType implements OpcUaDataType {

    NUMBER ("Number", DataTypes.NUMERIC, -4, Number.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toNumber(value);
        }

        @Override
        public boolean validate(MangoValue value) {
            return value.toValue() instanceof Number;
        }
    },

    UNUMBER ("UNumber", DataTypes.NUMERIC, -3, ScadaUNumber.getType()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toUNumber(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                double value1 = (double) value;
                return value1 >= 0;
            }
            return false;
        }
    },

    UNKNOWN ("Unknown", DataTypes.ALPHANUMERIC, -2, Object.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toStringType(value);
        }
    },

    ALL ("ALL", DataTypes.UNKNOWN, -1, Objects.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return value.toValue();
        }

        @Override
        public boolean validate(MangoValue value) {
            return false;
        }
    },

    BOOLEAN ("Boolean", DataTypes.BINARY, 1, Boolean.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return value.getBooleanValue();
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            return value instanceof Boolean;
        }
    },

    SBYTE ("SByte", DataTypes.NUMERIC, 2, Byte.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toByte(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                byte min = Byte.MIN_VALUE;
                byte max = Byte.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof Byte;
        }
    },

    BYTE ("Byte", DataTypes.NUMERIC, 3, new UByteWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toUByte(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                short min = UByteWrapper.MIN_VALUE;
                short max = UByteWrapper.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof UByteWrapper;
        }
    },

    INT16 ("Int16", DataTypes.NUMERIC, 4, Short.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toShort(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                short min = Short.MIN_VALUE;
                short max = Short.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof Short;
        }
    },

    UINT16 ("UInt16", DataTypes.NUMERIC, 5, new UShortWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toUShort(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                int min = UShortWrapper.MIN_VALUE;
                int max = UShortWrapper.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof UShortWrapper;
        }
    },

    INT32 ("Int32", DataTypes.NUMERIC, 6, Integer.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toInt(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                int min = Integer.MIN_VALUE;
                int max = Integer.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof Integer;
        }
    },

    UINT32 ("UInt32", DataTypes.NUMERIC, 7, new UIntegerWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toUInteger(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                long min = UIntegerWrapper.MIN_VALUE;
                long max = UIntegerWrapper.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof UIntegerWrapper;
        }
    },

    INT64 ("Int64", DataTypes.NUMERIC, 8, Long.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toLong(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                long min = Long.MIN_VALUE;
                long max = Long.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof Long;
        }
    },

    UINT64 ("UInt64", DataTypes.NUMERIC, 9, new ULongWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toULong(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                BigInteger min = ULongWrapper.MIN_VALUE;
                BigInteger max = ULongWrapper.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof ULongWrapper;
        }
    },

    FLOAT ("Float", DataTypes.NUMERIC, 10, Float.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toFloatFromString(value);
        }

        @Override
        public boolean validate(MangoValue object) {
            Object value = object.toValue();
            if (value instanceof Double) {
                float min = Float.MIN_VALUE;
                float max = Float.MAX_VALUE;
                return compare(value, min, max);
            }
            return value instanceof Float;
        }
    },

    DOUBLE ("Double", DataTypes.NUMERIC, 11, Double.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toDoubleFromString(value);
        }

        @Override
        public boolean validate(MangoValue value) {
            return value.toValue() instanceof Double;
        }
    },

    STRING ("String", DataTypes.ALPHANUMERIC, 12, String.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toStringType(value);
        }
    },

    DATE_TIME ("DateTime", DataTypes.ALPHANUMERIC, 13, new DateTimeWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toDateTime(value);
        }
    },

    GUID ("Guid", DataTypes.ALPHANUMERIC, 14, UUID.class) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toUuid(value);
        }
    },

    BYTE_STRING ("ByteString", DataTypes.ALPHANUMERIC, 15, new ByteStringWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toByteString(value);
        }
    },

    XML_ELEMENT ("XmlElement", DataTypes.ALPHANUMERIC, 16, new XmlElementWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toXmlElement(value);
        }
    },

    NODE_ID ("NodeId", DataTypes.ALPHANUMERIC, 17, new NodeIdWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toNodeId(value);
        }
    },

    EXPANDED_NODE_ID ("ExpandedNodeId", DataTypes.ALPHANUMERIC, 18, new ExpandedNodeIdWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toExpandedNodeId(value);
        }
    },

    STATUS_CODE ("StatusCode", DataTypes.ALPHANUMERIC, 19, new StatusCodeWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toStatusCode(value);
        }
    },

    QUALIFIED_NAME ("QualifiedName", DataTypes.ALPHANUMERIC, 20, new QualifiedNameWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toQualifiedName(value);
        }
    },

    LOCALIZED_TEXT ("LocalizedText", DataTypes.ALPHANUMERIC, 21, new LocalizedTextWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toLocalizedText(value);
        }
    },

    EXTENSION_OBJECT ("ExtensionObject", DataTypes.ALPHANUMERIC, 22, new ExtensionObjectWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toExtensionObject(value);
        }
    },

    DATA_VALUE ("DataValue", DataTypes.ALPHANUMERIC, 23, new DataValueWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toDataValue(value);
        }
    },

    VARIANT ("Variant", DataTypes.ALPHANUMERIC, 24, new VariantWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toVariant(value);
        }
    },

    DIAGNOSTIC_INFO ("DiagnosticInfo", DataTypes.ALPHANUMERIC, 25, new DiagnosticInfoWrapper()) {

        @Override
        public Object convertToWrite(MangoValue value) throws Exception {
            return toStringType(value);
        }
    };

    public static final OpcUaDataType DEFAULT = OpcUaDataType.unknownType();

    private final String description;
    private final int dataTypeId;
    private final int opcTypeId;

    private final Object type;

    OpcUaBaseDataType(String description, int dataTypeId, int opcTypeId, Object type) {
        this.dataTypeId = dataTypeId;
        this.description = description;
        this.opcTypeId = opcTypeId;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public int getDataTypeId() {
        return dataTypeId;
    }

    @Override
    public int getOpcTypeId() {
        return opcTypeId;
    }

    @Override
    public String getDescription() {
        return this.description + " (" + this.opcTypeId + ")";
    }

    public Class<?> getType() {
        if(type instanceof TypeWrapper) {
            return ((TypeWrapper<?>)type).getType();
        }
        if(type instanceof Class) {
            return (Class<?>) type;
        }
        throw new IllegalStateException("Unsupported type: " + type.getClass().getName());
    }

    @Override
    public MangoValue convertToRead(Object value) throws Exception {
        return OpcUaConverterUtils.convertToRead(this, value);
    }

    @Override
    public boolean validate(MangoValue value) {
        return value.toValue() instanceof String;
    }

    @Override
    public boolean toJson() {
        return this.dataTypeId == DataTypes.ALPHANUMERIC && this != STRING && this != GUID;
    }

    @Override
    public boolean isPossibleSettable() {
        return (this.dataTypeId > 0 || this == NUMBER || this == UNUMBER) && (((this.dataTypeId != DataTypes.ALPHANUMERIC) || this == STRING || this == GUID || this == DATE_TIME));
    }

    private static boolean compare(Object value, Object min, Object max) {
        double minValue;
        double maxValue;
        if(max instanceof Float || max instanceof Double) {
            minValue = Double.parseDouble(String.valueOf(max)) * -1;
            maxValue = Double.parseDouble(String.valueOf(max));
        } else {
            minValue = Double.parseDouble(String.valueOf(min));
            maxValue = Double.parseDouble(String.valueOf(max));
        }
        Double valueDouble = Double.parseDouble(String.valueOf(value));
        return valueDouble.compareTo(minValue) >= 0 && valueDouble.compareTo(maxValue) <= 0;
    }
}
