package org.scada_lts.ds.polling.protocol.opcua.vo;

import java.util.stream.Stream;

public enum OpcUaIdentifierType {

    ALL("", -1), STRING("s", 1), NUMERIC("i", 0), BINARY("b", 3), GUID("g", 2);

    public static final OpcUaIdentifierType DEFAULT = OpcUaIdentifierType.STRING;

    private final String code;
    private final int id;

    OpcUaIdentifierType(String code, int id) {
        this.code = code;
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return this.name();
    }

    public static OpcUaIdentifierType valueOf(int id) {
        return Stream.of(OpcUaIdentifierType.values())
                .filter(a -> a.getId() == id)
                .findAny()
                .orElse(OpcUaIdentifierType.DEFAULT);
    }
}
