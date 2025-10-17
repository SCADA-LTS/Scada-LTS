package org.scada_lts.ds.polling.protocol.opcua.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.scada_lts.ds.polling.protocol.opcua.client.impl.type.ScadaUNumber;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaDataType;
import org.scada_lts.web.beans.ApplicationBeans;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;


public final class OpcUaConverterUtils {

    private static final Logger LOG = LogManager.getLogger(OpcUaConverterUtils.class);

    private OpcUaConverterUtils() {
    }

    public static boolean toBooleanFromString(MangoValue value) {
        return Boolean.parseBoolean(String.valueOf(value.toValue()));
    }

    public static byte toByte(MangoValue value) {
        return toByteFromDouble(value);
    }

    public static short toShort(MangoValue value) {
        return toShortFromDouble(value);
    }

    public static int toInt(MangoValue value) {
        return toIntFromDouble(value);
    }

    public static long toLong(MangoValue value) {
        return toLongFromDouble(value);
    }

    public static double toDoubleFromString(MangoValue value) {
        return Double.parseDouble(String.valueOf(value.toValue()));
    }

    public static float toFloatFromString(MangoValue value) {
        return Float.parseFloat(String.valueOf(value.toValue()));
    }

    public static Number toNumber(MangoValue value) {
        return value.numberValue();
    }

    public static UNumber toUNumber(MangoValue value) {
        return new ScadaUNumber(value).toUNumber();
    }

    public static MangoValue convertToRead(OpcUaDataType dataType, Object value) throws Exception {
        String stringToSave;
        if(dataType.toJson()) {
            ObjectMapper objectMapper = ApplicationBeans.getBean("objectMapper", ObjectMapper.class);
            stringToSave = objectMapper.writeValueAsString(value);
        } else {
            stringToSave = String.valueOf(value);
        }
        if(dataType.getDataTypeId() == DataTypes.UNKNOWN) {
            return MangoValue.stringToValue(stringToSave, DataTypes.ALPHANUMERIC);
        }
        return MangoValue.stringToValue(stringToSave, dataType.getDataTypeId());
    }

    public static long toLongFromDouble(MangoValue value) {
        BigDecimal result = BigDecimal.valueOf(toDoubleFromString(value));
        return result.longValue();
    }

    public static short toShortFromDouble(MangoValue value) {
        BigDecimal result = BigDecimal.valueOf(toDoubleFromString(value));
        return result.shortValue();
    }

    public static byte toByteFromDouble(MangoValue value) {
        BigDecimal result = BigDecimal.valueOf(toDoubleFromString(value));
        return result.byteValue();
    }

    public static int toIntFromDouble(MangoValue value) {
        BigDecimal result = BigDecimal.valueOf(toDoubleFromString(value));
        return result.intValue();
    }

    public static String toStringType(MangoValue value) {
        return String.valueOf(value);
    }

    public static UUID toUuid(MangoValue value) {
        return UUID.fromString(String.valueOf(value));
    }

    public static DateTime toDateTime(MangoValue value) {
        return getObject(value, DateTime.class);
    }

    public static XmlElement toXmlElement(MangoValue value) {
        return getObject(value, XmlElement.class);
    }

    public static NodeId toNodeId(MangoValue value) {
        return getObject(value, NodeId.class);
    }

    public static ExpandedNodeId toExpandedNodeId(MangoValue value) {
        return getObject(value, ExpandedNodeId.class);
    }

    public static StatusCode toStatusCode(MangoValue value) {
        return getObject(value, StatusCode.class);
    }

    public static Variant toVariant(MangoValue value) {
        VariantConv variantConv = getObject(value, VariantConv.class);
        return variantConv.toVariant();
    }

    public static QualifiedName toQualifiedName(MangoValue value) {
        return getObject(value, QualifiedName.class);
    }

    public static LocalizedText toLocalizedText(MangoValue value) {
        return getObject(value, LocalizedText.class);
    }

    public static DataValue toDataValue(MangoValue value) {
        return getObject(value, DataValue.class);
    }

    public static ExtensionObject toExtensionObject(MangoValue value) {
        return getObject(value, ExtensionObject.class);
    }

    public static UInteger toUInteger(MangoValue value) {
        return UInteger.valueOf(toLongFromDouble(value));
    }

    public static ULong toULong(MangoValue value) {
        return ULong.valueOf(toLongFromDouble(value));
    }

    public static UShort toUShort(MangoValue value) {
        return UShort.valueOf(String.valueOf(toLongFromDouble(value)));
    }

    public static UByte toUByte(MangoValue value) {
        return UByte.valueOf(toLongFromDouble(value));
    }

    public static ByteString toByteString(MangoValue value) {
        return getObject(value, ByteString.class);
    }

    private static <T> T getObject(MangoValue value, Class<T> clazz) {
        ObjectMapper objectMapper = ApplicationBeans.getBean("objectMapper", ObjectMapper.class);
        try {
            return objectMapper.readValue(String.valueOf(value.toValue()), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class VariantConv {
        private Object value;

        public VariantConv() {
        }

        public Object getValue() {
            return value;
        }

        public Variant toVariant() {
            return new Variant(value);
        }
    }
}
