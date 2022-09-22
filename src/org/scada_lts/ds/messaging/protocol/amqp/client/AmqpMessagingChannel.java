package org.scada_lts.ds.messaging.protocol.amqp.client;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.scada_lts.ds.messaging.channel.MessagingChannel;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpPointLocatorRT;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpPointLocatorVO;
import org.scada_lts.ds.messaging.protocol.amqp.ExchangeType;

import java.io.IOException;

import static com.serotonin.mango.util.LoggingUtils.*;

public class AmqpMessagingChannel implements MessagingChannel {

    private final AmqpChannel channel;
    private final DataPointRT dataPointRT;

    public AmqpMessagingChannel(AmqpChannel channel, DataPointRT dataPointRT) {
        this.channel = channel;
        this.dataPointRT = dataPointRT;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close(int timeout) throws MessagingChannelException {
        try {
            channel.close();
        } catch (IOException e) {
            throw new MessagingChannelException("Error Close Channel: " + dataPointInfo(dataPointRT.getVO()) + ", " + causeInfo(e), e.getCause());
        } catch (Exception e) {
            throw new MessagingChannelException("Error Close Channel: " + dataPointInfo(dataPointRT.getVO()) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public void publish(String message) throws MessagingChannelException {
        try {
            basicPublish(dataPointRT, channel, message);
        } catch (IOException e) {
            throw new MessagingChannelException("Error Publish: " + dataPointInfo(dataPointRT.getVO()) + ", " + causeInfo(e), e.getCause());
        } catch (Exception e) {
            throw new MessagingChannelException("Error Publish: " + dataPointInfo(dataPointRT.getVO()) + ", " + exceptionInfo(e), e);
        }
    }

    private static void basicPublish(DataPointRT dataPoint, AmqpChannel channel, String message) throws Exception {
        AmqpPointLocatorRT locatorRT = dataPoint.getPointLocator();
        AmqpPointLocatorVO locator = locatorRT.getVO();
        ExchangeType exchangeType = locator.getExchangeType();
        exchangeType.basicPublish(channel, locator, message);
    }
}
