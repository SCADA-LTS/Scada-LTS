package org.scada_lts.utils.security;

import com.serotonin.mango.util.LoggingUtils;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Arrays;


public class ClientCertificate {

    private final X509Certificate[] certificateChain;
    private final X509Certificate certificate;
    private final KeyPair keyPair;

    private ClientCertificate(KeyStore keyStore, char[] passwordChars, String clientAlias) throws Exception {
        Key clientPrivateKey = keyStore.getKey(clientAlias, passwordChars);
        if (clientPrivateKey instanceof PrivateKey) {
            certificate = getClientCertificate(keyStore, clientAlias);
            certificateChain = getClientCertificateChain(keyStore, clientAlias);
            PublicKey serverPublicKey = certificate.getPublicKey();
            keyPair = crateKeyPairClient(serverPublicKey, (PrivateKey) clientPrivateKey);
        } else if (clientPrivateKey == null) {
            throw new IllegalStateException("There is no keystore for alias: " + clientAlias);
        } else {
            throw new IllegalStateException("Keystore is invalid for alias: " + clientAlias + ", key: " + clientPrivateKey.getClass().getName());
        }
    }

    public static ClientCertificate newInstance(KeyStoreData keyStoreData, CertificateData certificateData, String clientAlias) throws Exception {
        ClientCertificate clientCertificate;
        try {
            char[] passwordChars = keyStoreData.getKeyStorePassword().toCharArray();
            KeyStoreLoader loader = new KeyStoreLoader(keyStoreData, certificateData, clientAlias);
            clientCertificate = new ClientCertificate(loader.getKeyStore(), passwordChars, clientAlias);
        } catch (Exception ex) {
            throw new Exception(LoggingUtils.exceptionInfo(ex), ex);
        }
        return clientCertificate;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public X509Certificate[] getCertificateChain() {
        return certificateChain;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    private static KeyPair crateKeyPairClient(PublicKey serverPublicKey, PrivateKey clientPrivateKey) {
        return new KeyPair(serverPublicKey, clientPrivateKey);
    }

    private static X509Certificate[] getClientCertificateChain(KeyStore keyStore, String clientAlias) throws KeyStoreException {
        return Arrays.stream(keyStore.getCertificateChain(clientAlias))
                .map(X509Certificate.class::cast)
                .toArray(X509Certificate[]::new);
    }

    private static X509Certificate getClientCertificate(KeyStore keyStore, String clientAlias) throws KeyStoreException {
        return (X509Certificate) keyStore.getCertificate(clientAlias);
    }
}
