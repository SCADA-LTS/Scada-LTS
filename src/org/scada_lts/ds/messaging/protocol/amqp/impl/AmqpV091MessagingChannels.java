package org.scada_lts.ds.messaging.protocol.amqp.impl;

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

public class AmqpV091MessagingChannels implements InitMessagingChannels {

    private static final Log LOG = LogFactory.getLog(AmqpV091MessagingChannels.class);
    private final AmqpDataSourceVO vo;
    private final MessagingChannels channels;
    private final AmqpV091Connection connection;

    public AmqpV091MessagingChannels(AmqpDataSourceVO vo, MessagingChannels channels) {
        this.vo = vo;
        this.channels = channels;
        this.connection = new AmqpV091Connection();
    }

    @Override
    public void openConnection() throws MessagingChannelException {
        try {
            if (!isOpenConnection())
                connection.open(vo);
        } catch (IOException e) {
            try {
                connection.close();
            } catch (Exception ex) {
                LOG.warn("Error Close Channel: " + exceptionInfo(e), e);
            }
            throw new MessagingChannelException("Error Open Channel: " + causeInfo(e), e.getCause());
        } catch (Exception e) {
            try {
                connection.close();
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
        } catch (Exception e) {
            throw new MessagingChannelException("Error Remove Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        } finally {
            if (channels.size() == 0) {
                try {
                    connection.close();
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
    public void initChannel(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, String updateErrorKey) throws MessagingChannelException {
        connection.getIfOpen().or(() -> {
            try {
                return Optional.ofNullable(connection.open(vo));
            } catch (IOException e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
            } catch (Exception e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        }).ifPresent(conn -> {
            try {
                channels.initChannel(dataPoint, () -> {
                    try {
                        return new AmqpV091MessagingChannel(AmqpV091ChannelFactory.createReceiver(dataPoint, conn, exceptionHandler, updateErrorKey), dataPoint);
                    } catch (IOException e) {
                        throw new MessagingChannelException("Error Create Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
                    } catch (Exception e) {
                        throw new MessagingChannelException("Error Create Channel: "+ dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
                    }});
            } catch (MessagingChannelException e) {
                throw e;
            } catch (Exception e) {
                throw new MessagingChannelException("Error Init Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        });
    }

    @Override
    public void initChannel(DataPointRT dataPoint, Supplier<MessagingChannel> create) throws MessagingChannelException {
        connection.getIfOpen().or(() -> {
            try {
                return Optional.ofNullable(connection.open(vo));
            } catch (IOException e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
            } catch (Exception e) {
                throw new MessagingChannelException("Error Open Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        }).ifPresent(conn -> {
            try {
                channels.initChannel(dataPoint, create);
            } catch (MessagingChannelException e) {
                throw e;
            } catch (Exception e) {
                throw new MessagingChannelException("Error Init Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        });
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws MessagingChannelException {
        connection.getIfOpen().ifPresent(conn -> {
            try {
                channels.publish(dataPoint, message);
            } catch (MessagingChannelException e) {
                throw e;
            } catch (Exception e) {
                throw new MessagingChannelException("Error Publish: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        });
    }

    @Override
    public boolean isOpenConnection() {
        return connection.isOpen();
    }

    @Override
    public void closeChannels() throws MessagingChannelException {
        try {
            channels.closeChannels();
        } catch (Exception ex) {
            LOG.warn("Error Close Channels: " + exceptionInfo(ex), ex);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                LOG.warn("Error Close Connection: " + exceptionInfo(e), e);
            }
        }
    }

    @Override
    public int size() {
        return channels.size();
    }
}
