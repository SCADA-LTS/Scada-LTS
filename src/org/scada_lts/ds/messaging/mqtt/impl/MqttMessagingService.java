package org.scada_lts.ds.messaging.mqtt.impl;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.MessagingChannel;
import org.scada_lts.ds.messaging.MessagingChannels;
import org.scada_lts.ds.messaging.MessagingService;
import org.scada_lts.ds.messaging.UpdatePointValueConsumer;
import org.scada_lts.ds.messaging.mqtt.MqttDataSourceVO;
import org.scada_lts.ds.messaging.mqtt.MqttPointLocatorRT;
import org.scada_lts.ds.messaging.mqtt.MqttPointLocatorVO;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;

import static com.serotonin.mango.util.LoggingUtils.*;

public class MqttMessagingService implements MessagingService {

    private static final Log LOG = LogFactory.getLog(MqttMessagingService.class);

    private final MqttDataSourceVO vo;
    private final MessagingChannels<MqttVClient> channels;
    private volatile boolean blocked;

    public MqttMessagingService(MqttDataSourceVO vo) {
        this.vo = vo;
        this.channels = new MessagingChannels<>(new HashMap<>());
        this.blocked = false;
    }

    @Override
    public boolean isOpen() {
        if(blocked)
            return false;
        return channels.isOpen();
    }

    @Override
    public boolean isOpen(DataPointRT dataPoint) {
        return isOpen() && channels.isOpenChannel(dataPoint);
    }

    @Override
    public void open() throws Exception {
        blocked = false;
    }

    @Override
    public void close() throws Exception {
        blocked = true;
        channels.closeChannels(vo.getConnectionTimeout());
    }

    @Override
    public void initReceiver(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, String updateErrorKey) throws Exception {
        if(blocked) {
            LOG.warn("Stop Init Client: " + dataPointInfo(dataPoint.getVO()) + ", Service of shutting down: "  + dataSourceInfo(vo));
            return;
        }
        channels.initChannel(dataPoint, () -> new MqttChannel(createClient(dataPoint, exceptionHandler, updateErrorKey)));
    }

    @Override
    public void removeReceiver(DataPointRT dataPoint) throws Exception {
        if(blocked) {
            LOG.warn("Stop Remove Client: " + dataPointInfo(dataPoint.getVO()) + ", Service of shutting down: "  + dataSourceInfo(vo));
            return;
        }
        channels.removeChannel(dataPoint, vo.getConnectionTimeout());
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws Exception {
        if(blocked) {
            throw new IllegalStateException("Stop Publish: " + dataPointInfo(dataPoint.getVO()) + ", Service of shutting down:  "  + dataSourceInfo(vo) + ", Value: " + message);
        }
        MessagingChannel<MqttVClient> channel = channels.getChannel(dataPoint)
                .orElseThrow(() -> new RuntimeException("Error Publish: " + dataSourcePointInfo(vo, dataPoint.getVO())
                        + ", Value: " + message));

        MqttPointLocatorRT pointLocator = dataPoint.getPointLocator();
        MqttPointLocatorVO locator = pointLocator.getVO();
        try {
            channel.toOrigin().publish(locator.getTopicFilter(), message.getBytes(StandardCharsets.UTF_8),
                    locator.getQos(), locator.isRetained());
        } catch (Exception ex) {
            throw new RuntimeException("Error Publish: " + dataSourcePointInfo(vo, dataPoint.getVO())+ ", Value: " + message + ", " + exceptionInfo(ex), ex);
        }
    }

    private MqttVClient createClient(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, String updateErrorKey) throws RuntimeException {
        MqttPointLocatorRT pointLocator = dataPoint.getPointLocator();
        MqttPointLocatorVO locator = pointLocator.getVO();
        MqttVClient client;
        try {
            client = vo.getProtocolVersion().newClient(vo, locator);
        } catch (Exception e) {
            throw new RuntimeException("Error Create Client: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
        }
        try {
            client.connect();
        } catch (Exception e) {
            throw new RuntimeException("Error Connect Client: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
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
            throw new RuntimeException("Error Subscribe Client: " + dataSourcePointInfo(vo, dataPoint.getVO()) + ", " + exceptionInfo(e), e);
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
