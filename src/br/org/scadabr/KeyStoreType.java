package br.org.scadabr;

public enum KeyStoreType {

    JKS("jks"), PKCS11("pkcs11"), DKS("dks"), JCEKS("jceks"), PKCS12("pkcs12");

    public static final KeyStoreType DEFAULT = KeyStoreType.PKCS12;

    private final String name;

    KeyStoreType(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.name();
    }

    public String getDescription() {
        return this.name;
    }
}
