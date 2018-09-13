package org.scada_lts.workdomain.datasource.amqp;

import br.org.scadabr.api.constants.DataType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
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

        super.initialize();

    }

    @Override
    protected void doPoll(long time) {
        for(DataPointRT dp : dataPoints) {
            try {
                initDataPoint(dp,channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void terminate(){
        try {
            connection.close();
            channel.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        super.terminate();

    }

    private void initDataPoint(DataPointRT dp, Channel channel) throws IOException {

        AmqpReceiverPointLocatorRT locator = dp.getPointLocator();
        String queueName = locator.getVO().getQueueName();

        try {
            channel.queueDeclare(queueName, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GetResponse response = channel.basicGet(queueName, true);
        if(response != null) {
            byte[] body = response.getBody();
            String result = new String(body);

            if (dp.getDataTypeId() == DataTypes.ALPHANUMERIC) {
                dp.updatePointValue( new PointValueTime(
                        new AlphanumericValue(result), System.currentTimeMillis()
                ));
            }
            else if (dp.getDataTypeId() == DataTypes.NUMERIC) {
                dp.updatePointValue( new PointValueTime(
                        new NumericValue(Double.parseDouble(result)), System.currentTimeMillis()
                ));
            } else {
                System.out.println("AMQP DP: New value [other] " + result);
            }

        }

    }

}
