package org.scada_lts.ds.messaging.protocol.mqtt.client;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.channel.InitMessagingChannels;
import org.scada_lts.ds.messaging.channel.MessagingChannel;
import org.scada_lts.ds.messaging.channel.MessagingChannels;
import org.scada_lts.ds.messaging.channel.UpdatePointValueConsumer;
import org.scada_lts.ds.messaging.exception.MessagingChannelException;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttPointLocatorRT;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttPointLocatorVO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttVersion;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.serotonin.mango.util.LoggingUtils.*;

public class MqttMessagingChannels implements InitMessagingChannels {

    private static final Log LOG = LogFactory.getLog(MqttMessagingChannels.class);
    private final MqttDataSourceVO vo;
    private final MessagingChannels channels;

    public MqttMessagingChannels(MqttDataSourceVO vo, MessagingChannels channels) {
        this.vo = vo;
        this.channels = channels;
    }

    @Override
    public void removeChannel(DataPointRT dataPoint) throws MessagingChannelException {
        try {
            channels.removeChannel(dataPoint);
        } catch (MessagingChannelException e) {
            throw e;
        } catch (Exception e) {
            throw new MessagingChannelException("Error Remove Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public boolean isOpenChannel(DataPointRT dataPoint) {
        return isOpenConnection() && channels.isOpenChannel(dataPoint);
    }

    @Override
    public void initChannel(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, String updateErrorKey) throws MessagingChannelException {
        try {
            channels.initChannel(dataPoint, () -> {
                try {
                    return new MqttMessagingChannel(createClient(dataPoint, exceptionHandler, updateErrorKey), dataPoint);
                } catch (Exception e) {
                    throw new MessagingChannelException("Error Create Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + causeInfo(e), e.getCause());
                }
            });
        } catch (MessagingChannelException e) {
            throw e;
        } catch (Exception e) {
            throw new MessagingChannelException("Error Init Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public void initChannel(DataPointRT dataPoint, Supplier<MessagingChannel> create) throws MessagingChannelException {
        try {
            channels.initChannel(dataPoint, create);
        } catch (MessagingChannelException e) {
            throw e;
        } catch (Exception e) {
            throw new MessagingChannelException("Error Init Channel: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws MessagingChannelException {
        try {
            channels.publish(dataPoint, message);
        } catch (MessagingChannelException e) {
            throw e;
        } catch (Exception e) {
            throw new MessagingChannelException("Error Publish: " + dataPointInfo(dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public boolean isOpenConnection() {
        return channels.isOpenConnection();
    }

    @Override
    public void closeChannels() throws MessagingChannelException {
        try {
            channels.closeChannels();
        } catch (Exception ex) {
            LOG.warn("Error Close Channels: " + exceptionInfo(ex), ex);
        }
    }

    @Override
    public int size() {
        return channels.size();
    }

    private MqttVClient createClient(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, String updateErrorKey) throws MessagingChannelException {
        MqttPointLocatorRT pointLocator = dataPoint.getPointLocator();
        MqttPointLocatorVO locator = pointLocator.getVO();

        MqttVClient client;
        try {
            MqttVersion mqttVersion = (MqttVersion) vo.getProtocolVersion();
            client = mqttVersion.newClient(vo, locator);
        } catch (Exception e) {
            throw new MessagingChannelException("Error Create Client: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
        try {
            client.connect();
        } catch (Exception e) {
            throw new MessagingChannelException("Error Connect Client: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
        try {
            client.subscribe(locator.getTopicFilter(), locator.getQos(), (topic, mqttMessage) ->
                    new UpdatePointValueConsumer(dataPoint, locator::isWritable, updateErrorKey, exceptionHandler).accept(mqttMessage.getPayload()));
        } catch (Exception e) {
            try {
                close(client);
            } catch (Exception ex) {
                LOG.warn("Error Close Client: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(ex), ex);
            }
            throw new MessagingChannelException("Error Subscribe Client: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
        return client;
    }

    private void close(MqttVClient client) throws Exception {
        try {
            client.disconnect(vo.getConnectionTimeout());
        } finally {
            client.close();
        }
    }
}
