package org.scada_lts.workdomain.datasource.amqp;

import com.rabbitmq.client.*;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * Real Time Behaviour of AMQP Receiver DataSource
 *
 * @author Radek Jajko
 * @version 1.0
 * @since 2018-09-11
 */
public class AmqpReceiverDataSourceRT extends PollingDataSource {

    private final Log log = LogFactory.getLog(AmqpReceiverDataSourceRT.class);

    private final AmqpReceiverDataSourceVO vo;
    private Connection connection;
    private Channel channel;
    private boolean running = false;

    public AmqpReceiverDataSourceRT(AmqpReceiverDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
        log.debug("DataSourceRT:AMQP - Created!");
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        AmqpReceiverPointLocatorVO dataPointVO = dataPoint.getVO().getPointLocator();

    }

    // Enable DataSource //
    @Override
    public void initialize() {

        ConnectionFactory rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost(vo.getServerIpAddress());
        rabbitFactory.setPort(Integer.parseInt(vo.getServerPortNumber()));

        if (!vo.getServerUsername().isEmpty()) {
            rabbitFactory.setUsername(vo.getServerUsername());
        }

        if (!vo.getServerPassword().isEmpty()) {
            rabbitFactory.setPassword(vo.getServerPassword());
        }

        if (!vo.getServerVirtualHost().isEmpty()) {
            rabbitFactory.setVirtualHost(vo.getServerVirtualHost());
        }

        try {
            connection = rabbitFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.initialize();

    }

    @Override
    protected void doPoll(long time) {

        //If not running initialize DataPoints
        if (!running) {
            for (DataPointRT dp : dataPoints) {
                System.out.println("Initialization: " + dp.getVO().getXid());
                try {
                    initDataPoint(dp, channel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            running = true;
        }

    }

    // Disable DataSource //
    @Override
    public void terminate() {
        try {
            channel.close();
            connection.close();
            running = false;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        super.terminate();

    }

    /**
     * Initialize AMQP Data Point
     * Before initializing make sure you have got created exchange type,
     * exchange name, queue name and bindings between them.
     *
     * Any connection error breaks the connection between ScadaLTS
     * and RabbitMQ server
     *
     * @param dp      - single AMQP DataPoint
     * @param channel - RabbitMQ channel (with prepared connection)
     * @throws IOException - Errors
     */
    private void initDataPoint(DataPointRT dp, Channel channel) throws IOException {

        AmqpReceiverPointLocatorRT locator = dp.getPointLocator();

        String exchangeType = locator.getVO().getExchangeType();
        String exchangeName = locator.getVO().getExchangeName();
        String queueName = locator.getVO().getQueueName();
        String routingKey = locator.getVO().getRoutingKey();
        boolean durable = locator.getVO().getQueueDurability().equalsIgnoreCase("1");

        if (exchangeType.equalsIgnoreCase(AmqpReceiverPointLocatorVO.ExchangeType.A_FANOUT)) {
            channel.exchangeDeclare(exchangeName, AmqpReceiverPointLocatorVO.ExchangeType.A_FANOUT, durable);
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, "");

            Consumer consumer = new ScadaConsumer(channel, dp);
            channel.basicConsume(queueName, true, consumer);

        } else if (exchangeType.equalsIgnoreCase(AmqpReceiverPointLocatorVO.ExchangeType.A_DIRECT)) {

            channel.exchangeDeclare(exchangeName, AmqpReceiverPointLocatorVO.ExchangeType.A_DIRECT, durable);
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);

            Consumer consumer = new ScadaConsumer(channel, dp);
            channel.basicConsume(queueName, true, consumer);

        } else if (exchangeType.equalsIgnoreCase(AmqpReceiverPointLocatorVO.ExchangeType.A_TOPIC)) {
            channel.exchangeDeclare(exchangeName, AmqpReceiverPointLocatorVO.ExchangeType.A_TOPIC, durable);
//            queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, exchangeName, routingKey);


        } else if (exchangeType.isEmpty()) {

            channel.queueDeclare(queueName, durable, false, false, null);
            Consumer consumer = new ScadaConsumer(channel,dp);
            channel.basicConsume(queueName,true,consumer);

        }

    }

    private class ScadaConsumer extends DefaultConsumer {

        DataPointRT dataPoint;

        ScadaConsumer(Channel channel, DataPointRT dataPoint) {
            super(channel);
            this.dataPoint = dataPoint;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            super.handleDelivery(consumerTag, envelope, properties, body);

            String message = new String(body, StandardCharsets.UTF_8);

            if (dataPoint.getDataTypeId() == DataTypes.ALPHANUMERIC) {
                dataPoint.updatePointValue(new PointValueTime(
                        new AlphanumericValue(message), System.currentTimeMillis()
                ));
            } else if (dataPoint.getDataTypeId() == DataTypes.NUMERIC) {
                dataPoint.updatePointValue(new PointValueTime(
                        new NumericValue(Double.parseDouble(message)), System.currentTimeMillis()
                ));
            }

        }
    }

}
