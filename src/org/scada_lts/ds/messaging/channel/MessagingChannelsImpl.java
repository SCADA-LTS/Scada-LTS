package org.scada_lts.ds.messaging.channel;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;

import java.util.Optional;
import java.util.function.Supplier;

import static com.serotonin.mango.util.LoggingUtils.*;

class MessagingChannelsImpl implements MessagingChannels {
    private final OperationChannels operationChannels;
    private final int timeout;

    MessagingChannelsImpl(OperationChannels operationChannels, int timeout) {
        this.operationChannels = operationChannels;
        this.timeout = timeout;
    }

    @Override
    public void removeChannel(DataPointRT dataPoint) throws MessagingChannelException {
        MessagingChannel channel = operationChannels.getChannels().get(dataPoint.getId());
        if (channel != null) {
            operationChannels.removeChannel(dataPoint.getId());
            try {
                channel.close(timeout);
            } catch (MessagingChannelException e) {
                throw e;
            } catch (Throwable e) {
                throw new MessagingChannelException("Error Remove Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
            }
        }
    }

    @Override
    public boolean isOpenChannel(DataPointRT dataPoint) {
        MessagingChannel channel = operationChannels.getChannels().get(dataPoint.getId());
        if(channel == null)
            return false;
        return channel.isOpen();
    }

    @Override
    public void initChannel(DataPointRT dataPoint, Supplier<MessagingChannel> create) {
        try {
            getChannelIfOpen(dataPoint).orElseGet(() -> operationChannels.createChannelIfNotExists(dataPoint.getId(), a -> create.get()));
        } catch (MessagingChannelException e) {
            throw e;
        } catch (Throwable e) {
            throw new MessagingChannelException("Error Init Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) {
        getChannelIfOpen(dataPoint).ifPresent(channel -> {
            try {
                channel.publish(message);
            } catch (MessagingChannelException e) {
                throw e;
            } catch (Throwable e) {
                throw new MessagingChannelException("Error Publish: " + dataPointInfo(dataPoint.getVO()) + ", Value: " + message + ", " + exceptionInfo(e), e);
            }
        });
    }

    @Override
    public void closeChannels() throws MessagingChannelException {
        operationChannels.closeChannels(timeout);
    }

    @Override
    public boolean isOpenConnection() {
        return !operationChannels.getChannels().isEmpty();
    }

    @Override
    public int size() {
        return operationChannels.getChannels().size();
    }

    @Override
    public String toString() {
        return "MessagingChannelsImpl{" +
                "operationChannels=" + operationChannels + '}';
    }

    private Optional<MessagingChannel> getChannelIfOpen(DataPointRT dataPoint) {
        MessagingChannel channel = operationChannels.getChannels().get(dataPoint.getId());
        if(channel != null && channel.isOpen())
            return Optional.of(channel);
        removeChannel(dataPoint);
        return Optional.empty();
    }
}
