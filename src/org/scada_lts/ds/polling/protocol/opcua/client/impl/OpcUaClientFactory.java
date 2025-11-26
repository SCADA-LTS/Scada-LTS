package org.scada_lts.ds.polling.protocol.opcua.client.impl;


import com.serotonin.mango.util.LoggingUtils;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.client.security.DefaultClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.scada_lts.ds.polling.protocol.opcua.security.OpcUaMessageSecurityType;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaDataSourceVO;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.security.KeyStoreLoader;
import org.scada_lts.web.beans.ApplicationBeans;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.scada_lts.utils.PathSecureUtils.toSecurePath;

public final class OpcUaClientFactory {

    private OpcUaClientFactory() {}

    public static OpcUaClient createClient(OpcUaDataSourceVO dataSourceVO) throws Exception {
        OpcUaClient client;
        if(dataSourceVO.getMessageSecurity() != OpcUaMessageSecurityType.NONE) {
            OpcUaTrustListManager trustListManager = ApplicationBeans.getBean("opcUaTrustListManager", OpcUaTrustListManager.class);
            client = createClientSecured(dataSourceVO, trustListManager);
        } else {
            client = createClientUnsecured(dataSourceVO);
        }
        return client;
    }

    private static OpcUaClient createClientUnsecured(OpcUaDataSourceVO dataSourceVO) throws Exception {
        EndpointDescription applicationEndpoint = getEndpointDescription(dataSourceVO);
        IdentityProvider identityProvider = createIdentityProvider(dataSourceVO);
        OpcUaClientConfig config = createConfigUnsecured(dataSourceVO, applicationEndpoint, identityProvider);
        return OpcUaClient.create(config);
    }

    private static OpcUaClient createClientSecured(OpcUaDataSourceVO dataSourceVO, OpcUaTrustListManager trustListManager) throws Exception {
        EndpointDescription applicationEndpoint = getEndpointDescription(dataSourceVO);
        IdentityProvider identityProvider = createIdentityProvider(dataSourceVO);
        OpcUaClientConfig config = createConfigSecured(dataSourceVO, applicationEndpoint, identityProvider, trustListManager);
        return OpcUaClient.create(config);
    }

    private static EndpointDescription getEndpointDescription(OpcUaDataSourceVO dataSourceVO) throws InterruptedException, ExecutionException {
        EndpointDescription applicationEndpoint = findEndpoint(dataSourceVO);
        if(applicationEndpoint.getSecurityMode().getValue() == OpcUaMessageSecurityType.INVALID.getId()) {
            throw new IllegalStateException("The correct Message Security Mode could not be established.");
        }
        return applicationEndpoint;
    }

    private static IdentityProvider createIdentityProvider(OpcUaDataSourceVO dataSourceVO) {
        IdentityProvider identityProvider;
        if (StringUtils.isEmpty(dataSourceVO.getUser()) && StringUtils.isEmpty(dataSourceVO.getPassword())) {
            identityProvider = new AnonymousProvider();
        } else {
            identityProvider = new UsernameProvider(dataSourceVO.getUser(), dataSourceVO.getPassword());
        }
        return identityProvider;
    }

    private static EndpointDescription findEndpoint(OpcUaDataSourceVO dataSourceVO) throws InterruptedException, ExecutionException {

        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(dataSourceVO.getServerAddress()).get();
        List<EndpointDescription> endpointsFiltered = endpoints.stream()
                .filter(endpoint -> dataSourceVO.getSecurityPolicy().getUri().equals(endpoint.getSecurityPolicyUri()))
                .collect(Collectors.toList());

        if(endpointsFiltered.isEmpty()) {
            throw new IllegalStateException("Server not available Security Policy: " + dataSourceVO.getSecurityPolicy());
        }

        EndpointDescription endpointDescription = endpointsFiltered.stream()
                .filter(endpoint -> dataSourceVO.getMessageSecurity().getId() == endpoint.getSecurityMode().getValue())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Server not available Message Security Mode: " + dataSourceVO.getMessageSecurity()));

        ApplicationDescription serverDescription = endpointDescription.getServer();

        ApplicationDescription applicationDescription = new ApplicationDescription(serverDescription.getApplicationUri(),
                serverDescription.getProductUri(), serverDescription.getApplicationName(), serverDescription.getApplicationType(),
                serverDescription.getGatewayServerUri(), serverDescription.getDiscoveryProfileUri(),
                new String[]{dataSourceVO.getServerAddress()});

        return new EndpointDescription(dataSourceVO.getServerAddress(), applicationDescription,
                endpointDescription.getServerCertificate(), endpointDescription.getSecurityMode(),
                endpointDescription.getSecurityPolicyUri(), endpointDescription.getUserIdentityTokens(),
                endpointDescription.getTransportProfileUri(), endpointDescription.getSecurityLevel());
    }

    private static OpcUaClientConfig createConfigSecured(OpcUaDataSourceVO dataSourceVO,
                                                         EndpointDescription endpoint,
                                                         IdentityProvider identityProvider,
                                                         OpcUaTrustListManager trustListManager) throws Exception {
        KeyStoreLoader keyStore;
        try {
            File file = toSecurePath(Path.of(dataSourceVO.getKeyStoreFile())).orElseThrow(() -> new IllegalArgumentException("The path is invalid."));
            keyStore = new KeyStoreLoader("Scada-LTS [OPC UA]", file.getAbsolutePath(), dataSourceVO.getKeyStoreType(),
                    dataSourceVO.getKeyStorePassword(), dataSourceVO.getServerHost(), endpoint.getServer().getApplicationUri());
        } catch (Exception ex) {
            throw new Exception(LoggingUtils.exceptionInfo(ex), ex);
        }

        byte[] serverCertificateBytes = endpoint.getServerCertificate().bytes();

        if(serverCertificateBytes == null) {
            throw new IllegalStateException("Downloading the certificate from the server is impossible. Check the server configuration.");
        }

        X509Certificate serverCertificate;
        try(InputStream inputStream = new ByteArrayInputStream(serverCertificateBytes)) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            serverCertificate = (X509Certificate) certFactory.generateCertificate(inputStream);
        }

        trustListManager.setTrustedCertificates(Collections.singletonList(serverCertificate));
        DefaultClientCertificateValidator certificateValidator =
                new DefaultClientCertificateValidator(trustListManager);

        return OpcUaClientConfig.builder()
                .setEndpoint(endpoint)
                .setApplicationName(LocalizedText.english("Scada-LTS OPC UA Client for device: " + dataSourceVO.getName()))
                .setApplicationUri(endpoint.getServer().getApplicationUri())

                .setKeyPair(keyStore.getClientKeyPair())
                .setCertificate(keyStore.getClientCertificate())
                .setCertificateChain(keyStore.getClientCertificateChain())
                .setCertificateValidator(certificateValidator)
                .setIdentityProvider(identityProvider)

                .setMaxResponseMessageSize(uint(dataSourceVO.getMaxMessageSize()))
                .setRequestTimeout(uint(dataSourceVO.getRequestTimeout()))
                .setSessionTimeout(uint(dataSourceVO.getSessionTimeout()))
                .setKeepAliveTimeout(uint(dataSourceVO.getSessionTimeout()))
                .setConnectTimeout(uint(dataSourceVO.getDefaultTimeout()))
                .setAcknowledgeTimeout(uint(dataSourceVO.getNegotiationTimeout()))
                .setChannelLifetime(uint(dataSourceVO.getChannelLifetime()))

                .build();
    }

    private static OpcUaClientConfig createConfigUnsecured(OpcUaDataSourceVO dataSourceVO,
                                                           EndpointDescription endpoint,
                                                           IdentityProvider identityProvider) throws Exception {

        return OpcUaClientConfig.builder()
                .setEndpoint(endpoint)
                .setApplicationName(LocalizedText.english("Scada-LTS OPC UA Client for device: " + LoggingUtils.dataSourceInfo(dataSourceVO)))
                .setApplicationUri(endpoint.getServer().getApplicationUri())
                .setIdentityProvider(identityProvider)

                .setMaxResponseMessageSize(uint(dataSourceVO.getMaxMessageSize()))
                .setRequestTimeout(uint(dataSourceVO.getRequestTimeout()))
                .setSessionTimeout(uint(dataSourceVO.getSessionTimeout()))
                .setKeepAliveTimeout(uint(dataSourceVO.getSessionTimeout()))
                .setConnectTimeout(uint(dataSourceVO.getDefaultTimeout()))
                .setAcknowledgeTimeout(uint(dataSourceVO.getNegotiationTimeout()))
                .setChannelLifetime(uint(dataSourceVO.getChannelLifetime()))

                .build();
    }
}
