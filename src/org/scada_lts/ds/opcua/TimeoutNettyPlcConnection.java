package org.scada_lts.ds.opcua;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.plc4x.java.api.authentication.PlcAuthentication;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.exceptions.PlcIoException;
import org.apache.plc4x.java.api.value.PlcValueHandler;
import org.apache.plc4x.java.spi.configuration.ConfigurationFactory;
import org.apache.plc4x.java.spi.configuration.PlcConnectionConfiguration;
import org.apache.plc4x.java.spi.connection.ChannelFactory;
import org.apache.plc4x.java.spi.connection.DefaultNettyPlcConnection;
import org.apache.plc4x.java.spi.connection.PlcTagHandler;
import org.apache.plc4x.java.spi.connection.ProtocolStackConfigurer;
import org.apache.plc4x.java.spi.events.CloseConnectionEvent;
import org.apache.plc4x.java.spi.events.DisconnectEvent;
import org.apache.plc4x.java.spi.events.DiscoverEvent;
import org.apache.plc4x.java.spi.optimizer.BaseOptimizer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class TimeoutNettyPlcConnection extends DefaultNettyPlcConnection {

    private static final Logger LOG = LogManager.getLogger(TimeoutNettyPlcConnection.class);
    private final long connectingTimeout;
    private final long closingTimeout;


    public TimeoutNettyPlcConnection(boolean canPing, boolean canRead, boolean canWrite,
                                     boolean canSubscribe, boolean canBrowse, PlcTagHandler tagHandler,
                                     PlcValueHandler valueHandler, PlcConnectionConfiguration configuration,
                                     ChannelFactory channelFactory, boolean fireDiscoverEvent,
                                     boolean awaitSessionSetupComplete, boolean awaitSessionDisconnectComplete,
                                     boolean awaitSessionDiscoverComplete, ProtocolStackConfigurer<?> stackConfigurer,
                                     BaseOptimizer optimizer, PlcAuthentication authentication, long connectingTimeout,
                                     long closingTimeout) {
        super(canPing, canRead, canWrite, canSubscribe, canBrowse, tagHandler, valueHandler, configuration, channelFactory, fireDiscoverEvent, awaitSessionSetupComplete, awaitSessionDisconnectComplete, awaitSessionDiscoverComplete, stackConfigurer, optimizer, authentication);
        this.connectingTimeout = connectingTimeout;
        this.closingTimeout = closingTimeout;
    }

    @Override
    public void connect() throws PlcConnectionException {
        try {
            // As we don't just want to wait till the connection is established,
            // define a future we can use to signal back that the s7 session is
            // finished initializing.
            CompletableFuture<Void> sessionSetupCompleteFuture = new CompletableFuture<>();
            CompletableFuture<PlcConnectionConfiguration> sessionDiscoveredCompleteFuture = new CompletableFuture<>();

            if (channelFactory == null) {
                throw new PlcConnectionException("No channel factory provided");
            }

            // Inject the configuration
            ConfigurationFactory.configure(configuration, channelFactory);

            // Have the channel factory create a new channel instance.
            // TODO: Why is this code necessary? Discovery should be an API function that is
            //  explicitly called independently from the connection establishment.
            if (fireDiscoverEvent) {
                channel = channelFactory.createChannel(getChannelHandler(sessionSetupCompleteFuture, sessionDisconnectCompleteFuture, sessionDiscoveredCompleteFuture));
                channel.closeFuture().addListener(future -> {
                    if (!sessionDiscoveredCompleteFuture.isDone()) {
                        //Do Nothing
                        try {
                            sessionDiscoveredCompleteFuture.complete(null);
                        } catch (Exception e) {
                            //Do Nothing
                        }

                    }
                });
                channel.pipeline().fireUserEventTriggered(new DiscoverEvent());
            }
            if (awaitSessionDiscoverComplete) {
                // Wait till the connection is established.
                sessionDiscoveredCompleteFuture.get();
            }

            channel = channelFactory.createChannel(getChannelHandler(sessionSetupCompleteFuture, sessionDisconnectCompleteFuture, sessionDiscoveredCompleteFuture));
            channel.closeFuture().addListener(future -> {
                if (!sessionSetupCompleteFuture.isDone()) {
                    sessionSetupCompleteFuture.completeExceptionally(
                            new PlcIoException("Connection terminated by remote"));
                }
            });
            // Send an event to the pipeline telling the Protocol filters what's going on.
            sendChannelCreatedEvent();

            // Wait till the connection is established.
            if (awaitSessionSetupComplete) {
                sessionSetupCompleteFuture.get(connectingTimeout, TimeUnit.MILLISECONDS);
            }

            // Set the connection to "connected"
            connected = true;
        } catch (InterruptedException e) {
            close();
            Thread.currentThread().interrupt();
            throw new PlcConnectionException(e);
        } catch (Exception e) {
            close();
            throw new PlcConnectionException(e);
        }
    }

    @Override
    public void close() throws PlcConnectionException {
        LOG.debug("Closing connection to PLC, await for disconnect = {}", this.awaitSessionDisconnectComplete);
        this.channel.pipeline().fireUserEventTriggered(new DisconnectEvent());

        try {
            if (this.awaitSessionDisconnectComplete) {
                this.sessionDisconnectCompleteFuture.get(100000000, TimeUnit.MILLISECONDS);
            }
        } catch (Exception var2) {
            LOG.error("Timeout while trying to close connection");
        }

        if (this.channel.isOpen()) {
            try {
                this.channel.pipeline().fireUserEventTriggered(new CloseConnectionEvent());
                this.channel.close().awaitUninterruptibly().get();
            } catch (Exception ex) {
                if (this.channel.isOpen()) {
                    throw new PlcConnectionException(ex.getMessage(), ex);
                }
            }
        }

        if (!this.sessionDisconnectCompleteFuture.isDone()) {
            this.sessionDisconnectCompleteFuture.complete(null);
        }

        this.channelFactory.closeEventLoopForChannel(this.channel);
        this.channel = null;
        this.connected = false;
    }
}
