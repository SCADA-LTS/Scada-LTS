package org.scada_lts.ds.polling.protocol.opcua.security;


public enum OpcUaMessageSecurityType {
    INVALID("Invalid", 0),
    NONE("None", 1),
    SIGN("Sign", 2),
    SIGN_ENCRYPT("SignAndEncrypt", 3);

    public static final OpcUaMessageSecurityType DEFAULT = OpcUaMessageSecurityType.SIGN_ENCRYPT;

    private final String decription;
    private final int id;

    OpcUaMessageSecurityType(String decription, int id) {
        this.decription = decription;
        this.id = id;
    }

    public String getCode() {
        return this.name();
    }

    public String getDescription() {
        return this.decription;
    }

    public int getId() {
        return id;
    }
}
