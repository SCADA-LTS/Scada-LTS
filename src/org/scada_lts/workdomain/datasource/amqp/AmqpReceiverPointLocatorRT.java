package org.scada_lts.workdomain.datasource.amqp;


import com.rabbitmq.client.AMQP;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class AmqpReceiverPointLocatorRT extends PointLocatorRT {

    private final AmqpReceiverPointLocatorVO vo;
    private final AMQP.Queue queue;

    public AmqpReceiverPointLocatorRT(AmqpReceiverPointLocatorVO vo){
        this.vo = vo;
        this.queue = new AMQP.Queue();
    }

    public MangoValue variableToValue(String variable) {
//        vo.get
        return new AlphanumericValue(variable.toString());
    }

    public AmqpReceiverPointLocatorVO getVO() {return vo;}

    @Override
    public boolean isSettable() {
        return vo.isSettable();
    }
}
