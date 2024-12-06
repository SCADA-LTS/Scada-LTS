package br.org.scadabr.vo.dataSource.opcua;

public enum OpcUaIdentifierType {

    STRING("s"), NUMERIC("i"), BINARY("b"), GUID("g");

    public static final OpcUaIdentifierType DEFAULT = OpcUaIdentifierType.STRING;

    private final String code;

    OpcUaIdentifierType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return this.name();
    }
}
