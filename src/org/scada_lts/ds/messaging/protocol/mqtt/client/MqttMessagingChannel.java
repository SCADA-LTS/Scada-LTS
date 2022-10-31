package org.scada_lts.ds.messaging.protocol.mqtt.client;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.channel.MessagingChannel;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttPointLocatorRT;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttPointLocatorVO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.serotonin.mango.util.LoggingUtils.*;

public class MqttMessagingChannel implements MessagingChannel {

    private static final Log LOG = LogFactory.getLog(MqttMessagingChannel.class);

    private final MqttVClient client;
    private final DataPointRT dataPointRT;

    public MqttMessagingChannel(MqttVClient client, DataPointRT dataPointRT) {
        this.client = client;
        this.dataPointRT = dataPointRT;
    }

    @Override
    public boolean isOpen() {
        return client.isConnected();
    }

    @Override
    public void close(int timeout) throws MessagingChannelException {
        try {
            client.disconnect(timeout);
        } catch (IOException e) {
            throw new MessagingChannelException("Error Disconnect Channel: " + dataPointInfo(dataPointRT.getVO()) + ", " + causeInfo(e), e.getCause());
        } catch (Exception e) {
            throw new MessagingChannelException("Error Disconnect Channel: " + dataPointInfo(dataPointRT.getVO()) + ", " + exceptionInfo(e), e);
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                LOG.warn("Error Close Channel: " + dataPointInfo(dataPointRT.getVO()) + ", " + exceptionInfo(e), e);
            }
        }
    }

    @Override
    public void publish(String message) throws MessagingChannelException {
        MqttPointLocatorRT pointLocator = dataPointRT.getPointLocator();
        MqttPointLocatorVO locator = pointLocator.getVO();
        try {
            client.publish(locator.getTopicFilter(), message.getBytes(StandardCharsets.UTF_8),
                    locator.getQos(), locator.isRetained());
        } catch (Exception ex) {
            throw new RuntimeException("Error Publish: " + dataPointInfo(dataPointRT.getVO())+ ", Value: " + message + ", " + exceptionInfo(ex), ex);
        }
    }
}
