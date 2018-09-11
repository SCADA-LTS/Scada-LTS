package org.scada_lts.workdomain.datasource.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    }

    @Override
    public void initialize() {
//        String serverAddress = vo.getServerIpAddress();
//        String serverPort    = vo.getServerPortNumber();
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(serverAddress);
//        factory.setPort(Integer.parseInt(serverPort));
//
//        try {
//            connection = factory.newConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            channel = connection.createChannel();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        log.debug("DataSourceRT:AMQP - Initialized!");
        super.initialize();

    }

    @Override
    public void terminate(){
        super.terminate();
    }

    @Override
    protected void doPoll(long time) {
        for(DataPointRT dataPoint : dataPoints){
            AmqpReceiverPointLocatorRT locator = dataPoint.getPointLocator();

//            dataPoint.updatePointValue(new PointValueTime(locator.getCurrnetValue(), time));
        }

    }

}
