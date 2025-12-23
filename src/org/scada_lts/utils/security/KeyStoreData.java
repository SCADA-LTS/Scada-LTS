package org.scada_lts.utils.security;

public interface KeyStoreData {
    String getKeyStoreFile();
    KeyStoreType getKeyStoreType();
    String getKeyStorePassword();
}
