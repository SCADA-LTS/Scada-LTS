package org.scada_lts.ds.polling.protocol.opcua.vo;

import com.serotonin.mango.rt.dataImage.types.MangoValue;

public interface OpcUaDataType {

    String getName();

    int getDataTypeId();

    int getOpcTypeId();

    String getDescription();

    Object convertToWrite(Object value) throws Exception;

    MangoValue convertToRead(Object value) throws Exception;

    boolean validate(Object value);

    boolean toJson();

    boolean isPossibleSettable();

    static OpcUaDataType unknownType() {
        return BaseDataTypeUtils.unknownType();
    }

    static OpcUaDataType allType() {
        return BaseDataTypeUtils.allType();
    }

    static OpcUaDataType valueByNameOf(String name) {
        return BaseDataTypeUtils.valueByNameOf(name);
    }

    static OpcUaDataType valueOf(int opcTypeId) {
        return BaseDataTypeUtils.valueOf(opcTypeId);
    }

    static OpcUaDataType valueOf(Class<?> typeClass) {
        return BaseDataTypeUtils.valueOf(typeClass);
    }

    static OpcUaDataType[] types() {
        return BaseDataTypeUtils.types();
    }
}
