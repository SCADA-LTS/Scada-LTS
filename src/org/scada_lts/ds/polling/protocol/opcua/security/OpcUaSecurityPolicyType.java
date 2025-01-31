package org.scada_lts.ds.polling.protocol.opcua.security;

public enum OpcUaSecurityPolicyType {

    NONE("None", "http://opcfoundation.org/UA/SecurityPolicy#None"),
    BASIC_128_RSA_15("Basic128Rsa15", "http://opcfoundation.org/UA/SecurityPolicy#Basic128Rsa15"),
    BASIC_256("Basic256", "http://opcfoundation.org/UA/SecurityPolicy#Basic256"),
    BASIC_256_SHA_256("Basic256Sha256", "http://opcfoundation.org/UA/SecurityPolicy#Basic256Sha256"),
    AES_128_SHA_256_RSA_OAEP("Aes128_Sha256_RsaOaep", "http://opcfoundation.org/UA/SecurityPolicy#Aes128_Sha256_RsaOaep"),
    AES_256_SHA_256_RSA_PSS("Aes256_Sha256_RsaPss", "http://opcfoundation.org/UA/SecurityPolicy#Aes256_Sha256_RsaPss");

    public static final OpcUaSecurityPolicyType DEFAULT = OpcUaSecurityPolicyType.NONE;

    private final String description;
    private final String securityPolicyUri;

    OpcUaSecurityPolicyType(String description, String securityPolicyUri) {
        this.description = description;
        this.securityPolicyUri = securityPolicyUri;
    }

    public String getCode() {
        return this == NONE ? this.name() : this.description;
    }

    public String getDescription() {
        return description;
    }

    public String getUri() {
        return securityPolicyUri;
    }
}
