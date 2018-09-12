package org.scada_lts.workdomain.datasource.amqp;

import com.rabbitmq.client.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.EventDataSource;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * Real Time Behaviour of AMQP Receiver DataSource
 *
 * @author Radek Jajko
 * @version 1.0
 * @since 2018-09-11
 */
public class AmqpReceiverDataSourceRT extends PollingDataSource{

    private final Log log = LogFactory.getLog(AmqpReceiverDataSourceRT.class);

    private final AmqpReceiverDataSourceVO vo;
    private ConnectionFactory factory;
    private Connection  connection;
    private Channel     channel;

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

    @Override
    public void initialize() {



        ConnectionFactory rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost(vo.getServerIpAddress());
        rabbitFactory.setPort(Integer.parseInt(vo.getServerPortNumber()));

        if(!vo.getServerUsername().isEmpty() || !vo.getServerPassword().isEmpty()){
            rabbitFactory.setUsername(vo.getServerUsername());
            rabbitFactory.setPassword(vo.getServerPassword());
        }

        try {
            connection = rabbitFactory.newConnection();
        } catch (IOException e) {

        } catch (TimeoutException e) {

        }

        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.debug("DataSourceRT:AMQP - Initialized!");
        super.initialize();

    }

    @Override
    protected void doPoll(long time) {
        data(time);
    }

    @Override
    public void terminate(){
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.terminate();

    }

    private void updatePoint(DataPointRT dp, Channel channel, long time) {
        log.info("AQMP DataPoint: " + String.valueOf(dp.getId()) + " update");
        AmqpReceiverPointLocatorRT locator = dp.getPointLocator();
        String queueName = locator.getVO().getQueueName();
        System.out.print("Queue name: " + queueName);
        String receivedMessage = null;

        try {
            channel.queueDeclare(queueName, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                throws IOException {
                String message = new String(body, "UTF-8");
//                System.out.print(message);



                switch (dp.getDataTypeId()) {
                    case DataTypes.BINARY: {
                        dp.updatePointValue(new PointValueTime(
                                new BinaryValue(message.endsWith("2")),time
                        ));
                        break;
                    }
                    case DataTypes.NUMERIC: {
                        dp.updatePointValue(new PointValueTime(
                                new NumericValue(Double.valueOf(message)), time
                        ));
                        break;
                    }
                    case DataTypes.ALPHANUMERIC: {
                        dp.updatePointValue(new PointValueTime(
                                new AlphanumericValue(message), time
                        ));
                        break;
                    }
                }

            }
        };

        log.info("AMQP Locator string: " + locator.toString());
        log.info("AMQP Channel string: " + channel.toString());

        try {
            receivedMessage = channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("AMQP Received Message: " + receivedMessage);
        System.out.println("AMQP UpdatePoint Received Message: " + receivedMessage);
//        dp.updatePointValue(
//                new PointValueTime(
//                        new AlphanumericValue(receivedMessage), time));
    }


    public void data(long time) {
        if( channel.isOpen() ) {
            for (DataPointRT dp : dataPoints) {
                updatePoint(dp, channel, time);

            }

        }

    }

}
