package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

public class MessagingChannels<T> {

    private final Map<Integer, MessagingChannel<T>> channels;
    private final ReentrantReadWriteLock lock;

    public MessagingChannels(Map<Integer, MessagingChannel<T>> initChannels) {
        this.channels = initChannels;
        this.lock = new ReentrantReadWriteLock();
    }

    public void removeChannel(DataPointRT dataPoint, int timeout) throws Exception {
        MessagingChannel<T> channel = getChannels().get(dataPoint.getId());
        if (channel != null) {
            remove(dataPoint.getId());
            channel.close(timeout);
        }
    }

    public boolean isOpenChannel(DataPointRT dataPoint) {
        MessagingChannel<T> channel = getChannels().get(dataPoint.getId());
        if(channel == null)
            return false;
        return channel.isOpen();
    }

    public void initChannel(DataPointRT dataPoint, Supplier<MessagingChannel<T>> create) {
        getChannel(dataPoint).orElseGet(() -> computeIfAbsent(dataPoint.getId(), a -> create.get()));
    }

    public Optional<MessagingChannel<T>> getChannel(DataPointRT dataPoint) {
        MessagingChannel<T> channel = getChannels().get(dataPoint.getId());
        return Optional.ofNullable(channel);
    }

    public boolean isOpen() {
        return !getChannels().isEmpty();
    }

    public void closeChannels(int timeout) throws Exception {
        this.lock.writeLock().lock();
        try {
            Map<Integer, MessagingChannel<T>> chs = new HashMap<>(this.channels);
            for(MessagingChannel<T> channel: chs.values()) {
                channel.close(timeout);
            }
            chs.clear();
            this.channels.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private Map<Integer, MessagingChannel<T>> getChannels() {
        this.lock.readLock().lock();
        try {
            return channels;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void remove(int dataPointId) {
        this.lock.writeLock().lock();
        try {
            channels.remove(dataPointId);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private MessagingChannel<T> computeIfAbsent(int dataPointId, Function<? super Integer, ? extends MessagingChannel<T>> mappingFunction) {
        this.lock.writeLock().lock();
        try {
            return channels.computeIfAbsent(dataPointId, mappingFunction);
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
