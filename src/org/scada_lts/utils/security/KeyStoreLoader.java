package org.scada_lts.utils.security;

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

import static org.scada_lts.utils.PathSecureUtils.toSecurePath;

public class KeyStoreLoader {

    private static final Pattern IP_ADDR_PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private final static Logger LOG = LoggerFactory.getLogger(KeyStoreLoader.class);

    private final KeyStore keyStore;

    public KeyStoreLoader(KeyStoreData keyStoreData, CertificateData certificateData, String clientAlias) throws Exception {
         keyStore = loadKeyStore(keyStoreData, certificateData, clientAlias);
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    private static KeyStore loadKeyStore(KeyStoreData keyStoreData, CertificateData certificateData, String clientAlias) throws Exception {

        char[] passwordChars = keyStoreData.getKeyStorePassword().toCharArray();
        File keyStoreFileSecured = toSecurePath(Path.of(keyStoreData.getKeyStoreFile())).orElseThrow(() -> new IllegalArgumentException("The path is invalid."));
        File keyStorePath = new File(keyStoreFileSecured.getPath());
        LOG.info("Loading KeyStore at {}", keyStorePath);
        KeyStore keyStore = KeyStore.getInstance(keyStoreData.getKeyStoreType().getCode());
        if (Files.notExists(keyStorePath.toPath())) {
            doCreateFileKeyStore(certificateData, keyStorePath, keyStore, passwordChars, clientAlias);
        } else {
            doLoadFileKeyStore(keyStorePath, keyStore, passwordChars);
        }

        return keyStore;
    }

    private static void doLoadFileKeyStore(File serverKeyStorePath, KeyStore keyStore, char[] passwordChars) throws IOException, NoSuchAlgorithmException, CertificateException {
        try (InputStream in = Files.newInputStream(serverKeyStorePath.toPath())) {
            keyStore.load(in, passwordChars);
        }
    }

    private static void doCreateFileKeyStore(CertificateData certificateData,
                                             File serverKeyStorePath, KeyStore keyStore,
                                             char[] passwordChars, String clientAlies) throws Exception {

        if(Files.notExists(serverKeyStorePath.getParentFile().toPath())) {
            serverKeyStorePath.getParentFile().mkdirs();
        }
        serverKeyStorePath.createNewFile();
        keyStore.load(null, passwordChars);
        KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
        SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
                .setCommonName(certificateData.getCommonName())
                .setOrganization(certificateData.getOrganization())
                .setOrganizationalUnit(certificateData.getOrganizationalUnit())
                .setLocalityName(certificateData.getLocalityName())
                .setStateName(certificateData.getStateName())
                .setCountryCode(certificateData.getCountryCode())
                .setApplicationUri(certificateData.getApplicationUri())
                .setValidityPeriod(certificateData.getValidityPeriod());

        if (IP_ADDR_PATTERN.matcher(certificateData.getHost()).matches()) {
            builder.addIpAddress(certificateData.getHost());
        } else {
            builder.addDnsName(certificateData.getHost());
        }

        X509Certificate certificate = builder.build();

        keyStore.setKeyEntry(clientAlies, keyPair.getPrivate(), passwordChars, new X509Certificate[]{certificate});
        try (OutputStream out = Files.newOutputStream(serverKeyStorePath.toPath())) {
            keyStore.store(out, passwordChars);
        }
    }
}
