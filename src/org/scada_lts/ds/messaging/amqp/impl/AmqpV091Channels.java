package org.scada_lts.ds.messaging.amqp.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import static org.scada_lts.ds.messaging.amqp.impl.AmqpV091ChannelFactory.createReceiver;

public class AmqpV091Channels {

    private final Map<Integer, Channel> channels;
    private final ReentrantReadWriteLock channelsLock;

    public AmqpV091Channels(Map<Integer, Channel> initChannels) {
        this.channels = initChannels;
        this.channelsLock = new ReentrantReadWriteLock();
    }

    public void removeChannel(DataPointRT dataPoint) throws IOException, TimeoutException {
        this.channelsLock.writeLock().lock();
        try {
            Channel channel = channels.get(dataPoint.getId());
            if (channel != null) {
                channels.remove(dataPoint.getId());
                channel.close();
            }
        } finally {
            this.channelsLock.writeLock().unlock();
        }
    }

    public boolean isOpenChannel(DataPointRT dataPoint) {
        this.channelsLock.readLock().lock();
        try {
            if(dataPoint == null)
                throw new IllegalArgumentException("Argument dataPoint, type: " + DataPointRT.class.getName() + " cannot be null.");
            Channel channel = channels.get(dataPoint.getId());
            if(channel == null)
                return false;
            return channel.isOpen();
        } finally {
            this.channelsLock.readLock().unlock();
        }
    }

    public void clearChannels() {
        this.channelsLock.writeLock().lock();
        try {
            channels.clear();
        } finally {
            this.channelsLock.writeLock().unlock();
        }
    }

    public Channel getOrInitChannel(DataPointRT dataPoint, AmqpDataSourceVO vo,
                                    Connection connection, Consumer<Exception> exceptionHandler,
                                    String updateErrorKey) {
        return getChannel(dataPoint).orElseGet(() -> computeIfAbsent(dataPoint, vo, connection, exceptionHandler, updateErrorKey));
    }

    public Optional<Channel> getChannel(DataPointRT dataPoint) {
        this.channelsLock.readLock().lock();
        try {
            Channel channel = channels.get(dataPoint.getId());
            if (channel != null)
                return Optional.of(channel);
        } finally {
            this.channelsLock.readLock().unlock();
        }
        return Optional.empty();
    }

    private Channel computeIfAbsent(DataPointRT dataPoint, AmqpDataSourceVO vo,
                                    Connection connection, Consumer<Exception> exceptionHandler,
                                    String updateErrorKey) {
        this.channelsLock.writeLock().lock();
        try {
            return channels.computeIfAbsent(dataPoint.getId(), a -> {
                try {
                    return createReceiver(dataPoint, connection, exceptionHandler, updateErrorKey);
                } catch (Exception e) {
                    throw new RuntimeException("Error Create AMQP Channel: " + LoggingUtils.dataPointInfo(dataPoint.getVO())
                            + ", Data Source: "  + LoggingUtils.dataSourceInfo(vo) + ", Message: " + e.getMessage(), e);
                }
            });
        } finally {
            this.channelsLock.writeLock().unlock();
        }
    }
}
