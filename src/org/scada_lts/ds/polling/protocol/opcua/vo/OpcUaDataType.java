package org.scada_lts.ds.polling.protocol.opcua.vo;


import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaConverterUtils;

import java.util.stream.Stream;

public interface OpcUaDataType {

    String getName();

    int getDataTypeId();

    int getOpcTypeId();

    String getDescription();

    Object convertToWrite(Object value) throws Exception;

    default MangoValue convertToRead(Object value) throws Exception {
        return OpcUaConverterUtils.convertToRead(this, value);
    }

    boolean validate(Object value);

    boolean toJson();

    boolean isPossibleSettable();

    static OpcUaDataType unknownType() {
        return OpcUaBaseDataType.UNKNOWN;
    }

    static OpcUaDataType allType() {
        return OpcUaBaseDataType.ALL;
    }

    static OpcUaDataType valueByNameOf(String name) {
        return Stream.of(OpcUaBaseDataType.values())
                .filter(data -> data.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(OpcUaBaseDataType.UNKNOWN);
    }

    static OpcUaDataType valueOf(int opcTypeId) {
        return Stream.of(OpcUaBaseDataType.values())
                .filter(type -> type.getOpcTypeId() == opcTypeId)
                .findAny()
                .orElse(OpcUaBaseDataType.UNKNOWN);
    }

    static OpcUaDataType valueOf(Class<?> typeClass) {
        return Stream.of(OpcUaBaseDataType.values())
                .filter(type -> type.getType().equals(typeClass))
                .findAny()
                .orElse(OpcUaBaseDataType.UNKNOWN);
    }

    static OpcUaDataType[] types() {
        return OpcUaBaseDataType.values();
    }
}
