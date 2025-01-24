package org.scada_lts.ds.polling.protocol.opcua.client.impl;

import com.google.common.collect.ImmutableList;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

public class OpcUaTrustListManager extends DefaultTrustListManager {

    private static final Logger LOG = LogManager.getLogger(OpcUaTrustListManager.class);

    public OpcUaTrustListManager() throws IOException {
        super(TrustListUtils.createDir());
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Throwable e) {
            LOG.error(LoggingUtils.exceptionInfo(e), e);
        }
    }

    @Override
    public void addIssuerCertificate(X509Certificate certificate) {
        super.addIssuerCertificate(certificate);
    }

    @Override
    public ImmutableList<X509Certificate> getIssuerCertificates() {
        return super.getIssuerCertificates();
    }

    @Override
    public boolean removeIssuerCertificate(ByteString thumbprint) {
        return super.removeIssuerCertificate(thumbprint);
    }

    @Override
    public void setIssuerCertificates(List<X509Certificate> issuerCertificates) {
        super.setIssuerCertificates(issuerCertificates);
    }

    @Override
    public File getIssuerCertsDir() {
        return super.getIssuerCertsDir();
    }

    @Override
    public void addRejectedCertificate(X509Certificate certificate) {
        super.addRejectedCertificate(certificate);
    }

    @Override
    public File getRejectedDir() {
        return super.getRejectedDir();
    }

    @Override
    public ImmutableList<X509CRL> getTrustedCrls() {
        return super.getTrustedCrls();
    }

    @Override
    public File getIssuerDir() {
        return super.getIssuerDir();
    }

    @Override
    public File getBaseDir() {
        return super.getBaseDir();
    }

    @Override
    public ImmutableList<X509CRL> getIssuerCrls() {
        return super.getIssuerCrls();
    }

    @Override
    public void setTrustedCrls(List<X509CRL> trustedCrls) {
        super.setTrustedCrls(trustedCrls);
    }

    @Override
    public void addTrustedCertificate(X509Certificate certificate) {
        super.addTrustedCertificate(certificate);
    }

    @Override
    public File getTrustedCrlDir() {
        return super.getTrustedCrlDir();
    }

    @Override
    public void setIssuerCrls(List<X509CRL> issuerCrls) {
        super.setIssuerCrls(issuerCrls);
    }

    @Override
    public boolean removeRejectedCertificate(ByteString thumbprint) {
        return super.removeRejectedCertificate(thumbprint);
    }

    @Override
    public File getTrustedCertsDir() {
        return super.getTrustedCertsDir();
    }

    @Override
    public File getTrustedDir() {
        return super.getTrustedDir();
    }

    @Override
    public ImmutableList<X509Certificate> getRejectedCertificates() {
        return super.getRejectedCertificates();
    }

    @Override
    public void setTrustedCertificates(List<X509Certificate> trustedCertificates) {
        super.setTrustedCertificates(trustedCertificates);
    }

    @Override
    public ImmutableList<X509Certificate> getTrustedCertificates() {
        return super.getTrustedCertificates();
    }

    @Override
    public File getIssuerCrlDir() {
        return super.getIssuerCrlDir();
    }

    @Override
    public boolean removeTrustedCertificate(ByteString thumbprint) {
        return super.removeTrustedCertificate(thumbprint);
    }
}
