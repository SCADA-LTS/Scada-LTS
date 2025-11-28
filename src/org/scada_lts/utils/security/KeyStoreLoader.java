package org.scada_lts.utils.security;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeyStoreLoader {

    private static final Pattern IP_ADDR_PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private static final String CLIENT_ALIAS = "scada-lts";

    private final static Logger LOG = LoggerFactory.getLogger(KeyStoreLoader.class);

    private X509Certificate[] clientCertificateChain;
    private X509Certificate clientCertificate;
    private KeyPair clientKeyPair;

    public KeyStoreLoader(String commonName, String keyStoreFile, KeyStoreType keyStoreType, String keyStorePassword,
                          String host, String applicationUri) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType.getCode());
        char[] passwordChars = keyStorePassword.toCharArray();

        File serverKeyStorePath = new File(keyStoreFile);

        LOG.info("Loading KeyStore at {}", serverKeyStorePath);

        if (Files.notExists(serverKeyStorePath.toPath())) {
            if(Files.notExists(serverKeyStorePath.getParentFile().toPath())) {
                serverKeyStorePath.getParentFile().mkdirs();
            }
            serverKeyStorePath.createNewFile();
            keyStore.load(null, passwordChars);
            KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
            SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
                    .setCommonName(commonName)
                    .setOrganization("Scada-LTS")
                    .setOrganizationalUnit("dev")
                    .setLocalityName("Krakow")
                    .setStateName("Malopolska")
                    .setCountryCode("PL")
                    .setApplicationUri(applicationUri);

            if (IP_ADDR_PATTERN.matcher(host).matches()) {
                builder.addIpAddress(host);
            } else {
                builder.addDnsName(host);
            }

            X509Certificate certificate = builder.build();

            keyStore.setKeyEntry(CLIENT_ALIAS, keyPair.getPrivate(), passwordChars, new X509Certificate[]{certificate});
            try (OutputStream out = Files.newOutputStream(serverKeyStorePath.toPath())) {
                keyStore.store(out, passwordChars);
            }
        } else {
            try (InputStream in = Files.newInputStream(serverKeyStorePath.toPath())) {
                keyStore.load(in, passwordChars);
            }
        }

        Key clientPrivateKey = keyStore.getKey(CLIENT_ALIAS, passwordChars);
        if (clientPrivateKey instanceof PrivateKey) {
            clientCertificate = (X509Certificate) keyStore.getCertificate(CLIENT_ALIAS);

            clientCertificateChain = Arrays.stream(keyStore.getCertificateChain(CLIENT_ALIAS))
                    .map(X509Certificate.class::cast)
                    .toArray(X509Certificate[]::new);

            PublicKey serverPublicKey = clientCertificate.getPublicKey();
            clientKeyPair = new KeyPair(serverPublicKey, (PrivateKey) clientPrivateKey);
        }
    }

    public X509Certificate getClientCertificate() {
        return clientCertificate;
    }

    public X509Certificate[] getClientCertificateChain() {
        return clientCertificateChain;
    }

    public KeyPair getClientKeyPair() {
        return clientKeyPair;
    }

}
