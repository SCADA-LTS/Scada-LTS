package org.scada_lts.ds.messaging.channel;

import net.bull.javamelody.internal.common.LOG;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import static com.serotonin.mango.util.LoggingUtils.exceptionInfo;

class SyncOperationChannels implements OperationChannels {

    private final Map<Integer, MessagingChannel> channels;
    private final ReentrantReadWriteLock lock;

    SyncOperationChannels(Map<Integer, MessagingChannel> initChannels) {
        this.channels = initChannels;
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void closeChannels(int timeout) throws MessagingChannelException {
        this.lock.writeLock().lock();
        try {
            Map<Integer, MessagingChannel> chs = new HashMap<>(this.channels);
            for(MessagingChannel channel: chs.values()) {
                try {
                    channel.close(timeout);
                } catch (Exception ex) {
                    LOG.warn("Error Init Channel: " + channel.getClass().getName() + ", " + exceptionInfo(ex), ex);
                }
            }
            chs.clear();
            this.channels.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }
    @Override
    public Map<Integer, MessagingChannel> getChannels() {
        this.lock.readLock().lock();
        try {
            return channels;
        } finally {
            this.lock.readLock().unlock();
        }
    }
    @Override
    public void removeChannel(int dataPointId) {
        this.lock.writeLock().lock();
        try {
            channels.remove(dataPointId);
        } finally {
            this.lock.writeLock().unlock();
        }
    }
    @Override
    public MessagingChannel createChannelIfNotExists(int dataPointId, Function<? super Integer, ? extends MessagingChannel> mappingFunction) {
        this.lock.writeLock().lock();
        try {
            return channels.computeIfAbsent(dataPointId, mappingFunction);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return "SyncOperationChannels{" +
                "channels=" + channels.getClass().getSimpleName() +
                '}';
    }
}
