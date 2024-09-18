package org.scada_lts.ds.messaging.protocol.amqp.client;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.channel.InitMessagingChannels;
import org.scada_lts.ds.messaging.channel.MessagingChannel;
import org.scada_lts.ds.messaging.channel.MessagingChannels;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.serotonin.mango.util.LoggingUtils.*;

public class AmqpMessagingChannels implements InitMessagingChannels {

    private static final Log LOG = LogFactory.getLog(AmqpMessagingChannels.class);
    private final AmqpDataSourceVO vo;
    private final MessagingChannels channels;
    private final AmqpConnectionManager connectionManager;

    public AmqpMessagingChannels(AmqpDataSourceVO vo, MessagingChannels channels, AmqpConnectionManager connection) {
        this.vo = vo;
        this.channels = channels;
        this.connectionManager = connection;
    }

    @Override
    public void openConnection() throws MessagingChannelException {
        try {
            if (!isOpenConnection())
                connectionManager.open(vo);
        } catch (IOException e) {
            try {
                connectionManager.close();
            } catch (Throwable ex) {
                LOG.warn("Error Close Channel: " + exceptionInfo(e), e);
            }
            throw new MessagingChannelException("Error Open Channel: " + causeInfo(e), e.getCause());
        } catch (Throwable e) {
            try {
                connectionManager.close();
            } catch (Exception ex) {
                LOG.warn("Error Close Channel: " + exceptionInfo(e), e);
            }
            throw new MessagingChannelException("Error Open Channel: " + exceptionInfo(e), e);
        }
    }

    @Override
    public void removeChannel(DataPointRT dataPoint) throws MessagingChannelException {
        try {
            channels.removeChannel(dataPoint);
        } catch (MessagingChannelException e) {
            throw e;
        } catch (Throwable e) {
            throw new MessagingChannelException("Error Remove Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        } finally {
            if (channels.size() == 0) {
                try {
                    connectionManager.close();
                } catch (Exception e) {
                    LOG.warn("Error Close Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
                }
            }
        }
    }

    @Override
    public boolean isOpenChannel(DataPointRT dataPoint) {
        return isOpenConnection() && channels.isOpenChannel(dataPoint);
    }

    @Override
    public void initChannel(DataPointRT dataPoint, Consumer<Throwable> exceptionHandler, Supplier<Void> returnToNormal) throws MessagingChannelException {
        connectionManager.getIfOpen().or(() -> {
            try {
                return Optional.ofNullable(connectionManager.open(vo));
            } catch (IOException e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
            } catch (Throwable e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        }).ifPresent(conn -> {
            try {
                channels.initChannel(dataPoint, () -> {
                    try {
                        return new AmqpMessagingChannel(AmqpChannelFactory.createReceiver(dataPoint, conn, exceptionHandler, returnToNormal), dataPoint);
                    } catch (IOException e) {
                        throw new MessagingChannelException("Error Create Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
                    } catch (Exception e) {
                        throw new MessagingChannelException("Error Create Channel: "+ dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
                    }});
            } catch (MessagingChannelException e) {
                throw e;
            } catch (Throwable e) {
                throw new MessagingChannelException("Error Init Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        });
    }

    @Override
    public void initChannel(DataPointRT dataPoint, Supplier<MessagingChannel> create) throws MessagingChannelException {
        connectionManager.getIfOpen().or(() -> {
            try {
                return Optional.ofNullable(connectionManager.open(vo));
            } catch (IOException e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
            } catch (Throwable e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        }).ifPresent(conn -> {
            try {
                channels.initChannel(dataPoint, create);
            } catch (MessagingChannelException e) {
                throw e;
            } catch (Throwable e) {
                throw new MessagingChannelException("Error Init Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        });
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws MessagingChannelException {
        connectionManager.getIfOpen().or(() -> {
            try {
                return Optional.ofNullable(connectionManager.open(vo));
            } catch (IOException e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
            } catch (Throwable e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        }).ifPresent(conn -> {
            try {
                channels.publish(dataPoint, message);
            } catch (MessagingChannelException e) {
                throw e;
            } catch (Throwable e) {
                throw new MessagingChannelException("Error Publish: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        });
    }

    @Override
    public boolean isOpenConnection() {
        return connectionManager.isOpen();
    }

    @Override
    public void closeChannels() throws MessagingChannelException {
        try {
            channels.closeChannels();
        } catch (Throwable ex) {
            LOG.warn("Error Close Channels: " + exceptionInfo(ex), ex);
        } finally {
            try {
                connectionManager.close();
            } catch (Throwable e) {
                LOG.warn("Error Close Connection: " + exceptionInfo(e), e);
            }
        }
    }

    @Override
    public int size() {
        return channels.size();
    }
}
