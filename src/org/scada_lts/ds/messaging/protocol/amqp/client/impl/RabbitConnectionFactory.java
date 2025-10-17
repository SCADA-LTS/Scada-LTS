package org.scada_lts.ds.messaging.protocol.amqp.client.impl;

import com.rabbitmq.client.ConnectionFactory;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.protocol.amqp.client.AmqpConnection;

public final class RabbitConnectionFactory {

    private RabbitConnectionFactory() {}

    public static AmqpConnection openConnection(AmqpDataSourceVO vo) throws Exception {
        ConnectionFactory rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost(vo.getServerHost());
        rabbitFactory.setPort(vo.getServerPortNumber());

        rabbitFactory.setChannelRpcTimeout(vo.getChannelRpcTimeout());
        rabbitFactory.setAutomaticRecoveryEnabled(vo.isAutomaticRecoveryEnabled());
        rabbitFactory.setNetworkRecoveryInterval(vo.getNetworkRecoveryInterval());

        rabbitFactory.setConnectionTimeout(vo.getConnectionTimeout());
        rabbitFactory.setShutdownTimeout(vo.getConnectionTimeout());
        rabbitFactory.setHandshakeTimeout(vo.getConnectionTimeout());

        if (!vo.getServerUsername().isBlank()) {
            rabbitFactory.setUsername(vo.getServerUsername());
        }

        if (!vo.getServerPassword().isBlank()) {
            rabbitFactory.setPassword(vo.getServerPassword());
        }

        if (!vo.getServerVirtualHost().isBlank()) {
            rabbitFactory.setVirtualHost(vo.getServerVirtualHost());
        }
        return new RabbitConnection(rabbitFactory.newConnection());
    }
}
