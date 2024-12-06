package br.org.scadabr.vo.dataSource.opcua;

public enum OpcUaSecurityPolicyType {

    NONE("None"),
    BASIC_128_RSA_15("Basic128Rsa15"),
    BASIC_256("Basic256"),
    BASIC_256_SHA_256("Basic256Sha256"),
    AES_128_SHA_256_RSA_OAEP("Aes128_Sha256_RsaOaep"),
    AES_256_SHA_256_RSA_PSS("Aes256_Sha256_RsaPss");

    public static final OpcUaSecurityPolicyType DEFAULT = OpcUaSecurityPolicyType.NONE;

    private final String description;

    OpcUaSecurityPolicyType(String description) {
        this.description = description;
    }

    public String getCode() {
        return this == NONE ? this.name() : this.description;
    }

    public String getDescription() {
        return description;
    }
}
