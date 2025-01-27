package org.scada_lts.ds.polling.protocol.opcua.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;
import org.scada_lts.ds.polling.protocol.opcua.client.impl.type.ScadaNumber;
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

    public static boolean toBoolean(Object value) {
        return Boolean.parseBoolean(String.valueOf(value));
    }

    public static byte toByte(Object value) {
        return (byte) toStringToLong(value);
    }

    public static short toShort(Object value) {
        return (short) toStringToLong(value);
    }

    public static int toInt(Object value) {
        return (int) toStringToLong(value);
    }

    public static long toLong(Object value) {
        return toStringToLong(value);
    }

    public static double toDouble(Object value) {
        return Double.parseDouble(String.valueOf(value));
    }

    public static float toFloat(Object value) {
        return Float.parseFloat(String.valueOf(value));
    }

    public static Number toNumber(Object value) {
        return new ScadaNumber(value).toNumber();
    }

    public static UNumber toUNumber(Object value) {
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

    public static long toStringToLong(Object value) {
        BigDecimal result = BigDecimal.valueOf(toDouble(value));
        return result.longValue();
    }

    public static String toStringType(Object value) {
        return String.valueOf(value);
    }

    public static UUID toUuid(Object value) {
        return UUID.fromString(String.valueOf(value));
    }

    public static DateTime toDateTime(Object value) {
        return getObject(value, DateTime.class);
    }

    public static XmlElement toXmlElement(Object value) {
        return getObject(value, XmlElement.class);
    }

    public static NodeId toNodeId(Object value) {
        return getObject(value, NodeId.class);
    }

    public static ExpandedNodeId toExpandedNodeId(Object value) {
        return getObject(value, ExpandedNodeId.class);
    }

    public static StatusCode toStatusCode(Object value) {
        return getObject(value, StatusCode.class);
    }

    public static Variant toVariant(Object value) {
        VariantConv variantConv = getObject(value, VariantConv.class);
        return variantConv.toVariant();
    }

    public static QualifiedName toQualifiedName(Object value) {
        return getObject(value, QualifiedName.class);
    }

    public static LocalizedText toLocalizedText(Object value) {
        return getObject(value, LocalizedText.class);
    }

    public static DataValue toDataValue(Object value) {
        return getObject(value, DataValue.class);
    }

    public static ExtensionObject toExtensionObject(Object value) {
        return getObject(value, ExtensionObject.class);
    }

    public static UInteger toUInteger(Object value) {
        return UInteger.valueOf(toStringToLong(value));
    }

    public static ULong toULong(Object value) {
        return ULong.valueOf(toStringToLong(value));
    }

    public static UShort toUShort(Object value) {
        return UShort.valueOf(String.valueOf(toStringToLong(value)));
    }

    public static UByte toUByte(Object value) {
        return UByte.valueOf(toStringToLong(value));
    }

    public static ByteString toByteString(Object value) {
        return getObject(value, ByteString.class);
    }

    private static <T> T getObject(Object value, Class<T> clazz) {
        ObjectMapper objectMapper = ApplicationBeans.getBean("objectMapper", ObjectMapper.class);
        try {
            return objectMapper.readValue(String.valueOf(value), clazz);
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
