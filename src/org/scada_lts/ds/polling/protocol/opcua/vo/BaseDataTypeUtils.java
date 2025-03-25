package org.scada_lts.ds.polling.protocol.opcua.vo;

import java.util.stream.Stream;

class BaseDataTypeUtils {

    public static OpcUaDataType unknownType() {
        return OpcUaBaseDataType.UNKNOWN;
    }

    public static OpcUaDataType allType() {
        return OpcUaBaseDataType.ALL;
    }

    public static OpcUaDataType valueByNameOf(String name) {
        return Stream.of(OpcUaBaseDataType.values())
                .filter(dataType -> dataType.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(OpcUaBaseDataType.UNKNOWN);
    }

    public static OpcUaDataType valueOf(int opcTypeId) {
        return Stream.of(OpcUaBaseDataType.values())
                .filter(dataType -> dataType.getOpcTypeId() == opcTypeId)
                .findAny()
                .orElse(OpcUaBaseDataType.UNKNOWN);
    }

    public static OpcUaDataType valueOf(Class<?> typeClass) {
        return Stream.of(OpcUaBaseDataType.values())
                .filter(dataType -> dataType.getType().equals(typeClass))
                .findAny()
                .orElse(OpcUaBaseDataType.UNKNOWN);
    }

    public static OpcUaDataType[] types() {
        return OpcUaBaseDataType.values();
    }
}
