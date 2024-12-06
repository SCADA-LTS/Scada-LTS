package org.scada_lts.ds.opcua;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.authentication.PlcAuthentication;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.opcua.OpcuaPlcDriver;
import org.apache.plc4x.java.opcua.config.OpcuaConfiguration;
import org.apache.plc4x.java.spi.configuration.ConfigurationFactory;
import org.apache.plc4x.java.spi.configuration.PlcConnectionConfiguration;
import org.apache.plc4x.java.spi.configuration.PlcTransportConfiguration;
import org.apache.plc4x.java.spi.connection.ChannelFactory;
import org.apache.plc4x.java.spi.transport.Transport;
import org.apache.plc4x.java.transport.tcp.DefaultTcpTransportConfiguration;

import java.util.ServiceLoader;
import java.util.regex.Matcher;

import static org.apache.plc4x.java.spi.configuration.ConfigurationFactory.configure;

public class TimeoutOpcuaPlcDriver extends OpcuaPlcDriver {

    public TimeoutOpcuaPlcDriver() {}

    @Override
    public PlcConnection getConnection(String connectionString, PlcAuthentication authentication) throws PlcConnectionException {
        ConfigurationFactory configurationFactory = new ConfigurationFactory();
        // Split up the connection string into its individual segments.
        Matcher matcher = URI_PATTERN.matcher(connectionString);
        if (!matcher.matches()) {
            throw new PlcConnectionException(
                    "Connection string doesn't match the format '{protocol-code}:({transport-code})?//{transport-config}(?{parameter-string)?'");
        }
        final String protocolCode = matcher.group("protocolCode");
        String transportCodeMatch = matcher.group("transportCode");
        if (transportCodeMatch == null && getMetadata().getDefaultTransportCode().isEmpty()) {
            throw new PlcConnectionException(
                    "This driver has no default transport and no transport code was provided.");
        }
        final String transportCode = (transportCodeMatch != null) ? transportCodeMatch : getMetadata().getDefaultTransportCode().get();
        final String transportConfig = matcher.group("transportConfig");
        final String paramString = matcher.group("paramString");

        // Check if the protocol code matches this driver.
        if (!protocolCode.equals(getProtocolCode())) {
            // Actually this shouldn't happen as the DriverManager should have not used this driver in the first place.
            throw new PlcConnectionException(
                    "This driver is not suited to handle this connection string");
        }

        // Create the configuration object.
        PlcConnectionConfiguration configuration = configurationFactory
                .createConfiguration(getConfigurationClass(), protocolCode, transportCode, transportConfig, paramString);
        if (configuration == null) {
            throw new PlcConnectionException("Unsupported configuration");
        }

        // Try to find a suitable transport-type for creating the communication channel.
        Transport transport = null;
        ServiceLoader<Transport> transportLoader = ServiceLoader.load(
                Transport.class, Thread.currentThread().getContextClassLoader());
        for (Transport curTransport : transportLoader) {
            if (curTransport.getTransportCode().equals(transportCode)) {
                transport = curTransport;
                break;
            }
        }
        if (transport == null) {
            throw new PlcConnectionException("Unsupported transport " + transportCode);
        }

        // Find out the type of the transport configuration.
        Class<? extends PlcTransportConfiguration> transportConfigurationType = transport.getTransportConfigType();
        if (getTransportConfigurationClass(transportCode).isPresent()) {
            transportConfigurationType = getTransportConfigurationClass(transportCode).get();
        }
        // Use the transport configuration type to actually configure the transport instance.
        PlcTransportConfiguration plcTransportConfiguration = configurationFactory
                .createTransportConfiguration(transportConfigurationType,
                        protocolCode, transportCode, transportConfig, paramString);
        configure(plcTransportConfiguration, transport);

        // Create an instance of the communication channel which the driver should use.
        ChannelFactory channelFactory = transport.createChannelFactory(transportConfig);
        if (channelFactory == null) {
            throw new PlcConnectionException("Unable to get channel factory from url " + transportConfig);
        }
        configure(configuration, channelFactory);

        // Give drivers the option to customize the channel.
        initializePipeline(channelFactory);

        // Make the "fire discover event" overridable via system property.
        boolean fireDiscoverEvent = fireDiscoverEvent();
        if (System.getProperty(PROPERTY_PLC4X_FORCE_FIRE_DISCOVER_EVENT) != null) {
            fireDiscoverEvent = Boolean.parseBoolean(System.getProperty(PROPERTY_PLC4X_FORCE_FIRE_DISCOVER_EVENT));
        }

        // Make the "await setup complete" overridable via system property.
        boolean awaitSetupComplete = awaitSetupComplete();
        if (System.getProperty(PROPERTY_PLC4X_FORCE_AWAIT_SETUP_COMPLETE) != null) {
            awaitSetupComplete = Boolean.parseBoolean(System.getProperty(PROPERTY_PLC4X_FORCE_AWAIT_SETUP_COMPLETE));
        }

        // Make the "await disconnect complete" overridable via system property.
        boolean awaitDisconnectComplete = awaitDisconnectComplete();
        if (System.getProperty(PROPERTY_PLC4X_FORCE_AWAIT_DISCONNECT_COMPLETE) != null) {
            awaitDisconnectComplete = Boolean.parseBoolean(System.getProperty(PROPERTY_PLC4X_FORCE_AWAIT_DISCONNECT_COMPLETE));
        }

        // Make the "await disconnect complete" overridable via system property.
        boolean awaitDiscoverComplete = awaitDiscoverComplete();
        if (System.getProperty(PROPERTY_PLC4X_FORCE_AWAIT_DISCOVER_COMPLETE) != null) {
            awaitDiscoverComplete = Boolean.parseBoolean(System.getProperty(PROPERTY_PLC4X_FORCE_AWAIT_DISCOVER_COMPLETE));
        }

        long connectingTimeout = 4000;
        long closingTimeout = 4000;

        if(configuration instanceof OpcuaConfiguration) {
            OpcuaConfiguration tcpTransportConfiguration = (OpcuaConfiguration) configuration;
            connectingTimeout = tcpTransportConfiguration.getNegotiationTimeout();
            closingTimeout = tcpTransportConfiguration.getNegotiationTimeout();
        }

        return new TimeoutNettyPlcConnection(
                canPing(), canRead(), canWrite(), canSubscribe(), canBrowse(),
                getTagHandler(),
                getValueHandler(),
                configuration,
                channelFactory,
                fireDiscoverEvent,
                awaitSetupComplete,
                awaitDisconnectComplete,
                awaitDiscoverComplete,
                getStackConfigurer(transport),
                getOptimizer(),
                authentication,
                connectingTimeout,
                closingTimeout);
    }
}
