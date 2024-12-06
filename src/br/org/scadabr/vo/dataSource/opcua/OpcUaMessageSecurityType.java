package br.org.scadabr.vo.dataSource.opcua;

public enum OpcUaMessageSecurityType {
    NONE("None"), SIGN("Sign"), SIGN_ENCRYPT("SignAndEncrypt");

    public static final OpcUaMessageSecurityType DEFAULT = OpcUaMessageSecurityType.SIGN_ENCRYPT;

    private final String decription;

    OpcUaMessageSecurityType(String decription) {
        this.decription = decription;
    }

    public String getCode() {
        return this.name();
    }

    public String getDescription() {
        return this.decription;
    }

}
