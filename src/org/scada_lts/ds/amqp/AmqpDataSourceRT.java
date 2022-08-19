package org.scada_lts.ds.amqp;


import com.rabbitmq.client.*;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * Real Time Behaviour of AMQP Receiver DataSource
 * Receive messages from RabbitMQ Queue by AMQP protocol datapoints.
 * AMQP Receiver extends PollingDataSource to check the connection to RabbitMQ server
 * with interval set in update period time. While polling it checks the amqpBindEstablished
 * flag to not duplicate connections within data points and Rabbit server.
 * RabbitMQ AMQP version = 0.9.1 (default)
 *
 * @author Radek Jajko
 * @version 1.0
 * @since 2018-09-11
 */
public class AmqpDataSourceRT extends PollingDataSource {
    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int DATA_POINT_EXCEPTION_EVENT = 2;
    public static final int DATA_POINT_WRITE_EXCEPTION_EVENT = 3;

    private static final Log LOG = LogFactory.getLog(AmqpDataSourceRT.class);

    private final AmqpDataSourceVO vo;
    private Connection connection;
    private Channel channel;
    private volatile boolean amqpBindEstablished = false;

    public AmqpDataSourceRT(AmqpDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        if (channel == null) {
            raiseEvent(DATA_POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    new LocalizableMessage("event.ds.writeFailed", dataPoint.getVO().getName()));
            LOG.warn(LoggingUtils.dataPointInfo(dataPoint.getVO()));
            return;
        }
        AmqpPointLocatorRT locator = dataPoint.getPointLocator();
        String message;
        if (locator.getVO().getDataTypeId() == DataTypes.ALPHANUMERIC) {
            message = valueTime.getStringValue();
        } else if (locator.getVO().getDataTypeId() == DataTypes.NUMERIC) {
            message = String.valueOf(valueTime.getDoubleValue());
        } else if (locator.getVO().getDataTypeId() == DataTypes.BINARY) {
            message = String.valueOf(valueTime.getBooleanValue());
        } else if (locator.getVO().getDataTypeId() == DataTypes.MULTISTATE) {
            message = String.valueOf(valueTime.getIntegerValue());
        } else {
            LOG.error("AMQP DataPoint - Unsupported data type! - " + LoggingUtils.dataPointInfo(dataPoint.getVO()));
            return;
        }
        try {
            switch (locator.getVO().getExchangeType()) {
                case DIRECT:
                case TOPIC:
                    channel.basicPublish(locator.getExchangeName(), locator.getRoutingKey(), null, message.getBytes());
                    break;
                case FANOUT:
                    channel.basicPublish(locator.getExchangeName(), "", null, message.getBytes());
                    break;
                default:
                    channel.basicPublish("", locator.getQueueName(), null, message.getBytes());
                    break;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage() + " - " + LoggingUtils.dataPointInfo(dataPoint.getVO()), e);
        }
    }

    // Enable DataSource //
    @Override
    public void initialize() {

        ConnectionFactory rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost(vo.getServerIpAddress());
        rabbitFactory.setPort(vo.getServerPortNumber());

        rabbitFactory.setConnectionTimeout(vo.getConnectionTimeout());
        rabbitFactory.setNetworkRecoveryInterval(vo.getNetworkRecoveryInterval());
        rabbitFactory.setChannelRpcTimeout(vo.getChannelRpcTimeout());
        rabbitFactory.setAutomaticRecoveryEnabled(vo.isAutomaticRecoveryEnabled());

        if (!vo.getServerUsername().isBlank()) {
            rabbitFactory.setUsername(vo.getServerUsername());
        }

        if (!vo.getServerPassword().isBlank()) {
            rabbitFactory.setPassword(vo.getServerPassword());
        }

        if (!vo.getServerVirtualHost().isBlank()) {
            rabbitFactory.setVirtualHost(vo.getServerVirtualHost());
        }

        initializeChannel(rabbitFactory);

        super.initialize();
    }

    // Disable DataSource //
    @Override
    public void terminate() {
        if(channel != null) {
            try {
                channel.close();
            } catch (IOException | TimeoutException | AlreadyClosedException e) {
                raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                        new LocalizableMessage("event.exception2", e.getClass().getName(), e.getMessage()));
            } finally {
                amqpBindEstablished = false;
            }
        }

        if(connection != null) {
            try {
                connection.close();
            } catch (IOException | AlreadyClosedException e) {
                raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                        new LocalizableMessage("event.exception2", e.getClass().getName(), e.getMessage()));
            } finally {
                amqpBindEstablished = false;
            }
        }
        this.vo.setEnabled(false);
        super.terminate();
    }

    @Override
    protected void doPoll(long time) {

        //If not amqpBindEstablished initialize DataPoints
        if (channel != null && channel.isOpen() && !amqpBindEstablished) {
            for (DataPointRT dp : dataPoints) {
                try {
                    initDataPoint(dp, channel);
                    basicConsume(dp, channel);
                    returnToNormal(DATA_POINT_EXCEPTION_EVENT, System.currentTimeMillis());
                } catch (IOException e) {
                    raiseEvent(DATA_POINT_EXCEPTION_EVENT, System.currentTimeMillis(), false,
                            new LocalizableMessage("event.amqp.bindError", dp.getVO().getXid()));
                }
            }
            amqpBindEstablished = true;
        }
    }

    /**
     * Initialize Connection and Channel
     *
     * @param connectionFactory - RabbitMQ connector with prepared connection values
     */
    private void initializeChannel(ConnectionFactory connectionFactory) {

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, DataSourceRT.getExceptionMessage(e));
        }

    }

    /**
     * Initialize AMQP Data Point
     * Before initializing make sure you have got created exchange type,
     * exchange name, queue name and bindings between them.
     * <p>
     * Any connection error breaks the connection between ScadaLTS
     * and RabbitMQ server
     *
     * @param dp      - single AMQP DataPoint
     * @param channel - RabbitMQ channel (with prepared connection)
     * @throws IOException - Errors
     */
    private static void initDataPoint(DataPointRT dp, Channel channel) throws IOException {

        AmqpPointLocatorRT locator = dp.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        ExchangeType exchangeType = vo.getExchangeType();

        boolean durable = vo.getQueueDurability().ordinal() == 1;

        if (channel != null) {
            if(exchangeType != ExchangeType.NONE) {
                setQueueName(channel, vo, exchangeType, durable);
                queueBind(channel, vo, exchangeType);
            }
        }
    }

    private static void basicConsume(DataPointRT dp, Channel channel) throws IOException {
        AmqpPointLocatorRT locator = dp.getPointLocator();
        AmqpPointLocatorVO vo = locator.getVO();
        boolean noAck = vo.getMessageAck().ordinal() == 1;
        channel.basicConsume(vo.getQueueName(), noAck, new ScadaConsumer(channel, dp));
    }

    private static void queueBind(Channel channel, AmqpPointLocatorVO vo, ExchangeType exchangeType) throws IOException {
        if (exchangeType == ExchangeType.FANOUT) {
            channel.queueBind(vo.getQueueName(), vo.getExchangeName(), "");
        } else {
            channel.queueBind(vo.getQueueName(), vo.getExchangeName(), vo.getRoutingKey());
        }
    }

    private static void setQueueName(Channel channel, AmqpPointLocatorVO vo, ExchangeType exchangeType, boolean durable) throws IOException {
        channel.exchangeDeclare(vo.getExchangeName(), exchangeType.toString(), durable);
        vo.setQueueName(channel.queueDeclare().getQueue());
    }

    static class ScadaConsumer extends DefaultConsumer {

        private final DataPointRT dataPoint;
        private final AmqpPointLocatorRT locator;

        ScadaConsumer(Channel channel, DataPointRT dataPoint) {
            super(channel);
            this.dataPoint = dataPoint;
            this.locator = dataPoint.getPointLocator();
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            super.handleDelivery(consumerTag, envelope, properties, body);

            if (locator.getVO().isWritable()) {
                String message = new String(body, StandardCharsets.UTF_8);
                dataPoint.updatePointValue(message);
            }
        }
    }
}
