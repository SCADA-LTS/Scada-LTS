package org.scada_lts.workdomain.event;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.serotonin.mango.rt.event.EventInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ Exporter
 * <p>
 * Export ScadaLTS Events to remote broker by AMQPv0.9.1 module.
 *
 * @author Radek Jajko
 * @version 1.0.0
 * @since 25-09-2018
 */
public class RabbitMqExporter implements EventExporter {

    private final Log log = LogFactory.getLog(RabbitMqExporter.class);
    private final String EXCHANGE_NAME = "ScadaLTS-Events";

    private Connection connection;
    private Channel channel;

    @Override
    public void initialize() {

        String host = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_HOST);
        String virtual = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_VIRTUAL);
        int port = SystemSettingsDAO.getIntValue(SystemSettingsDAO.ALARM_EXPORT_PORT);
        String username = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_USERNAME);
        String password = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_PASSWORD);

        log.info("Initializing RabbitMQ instance with host = " + host + ":" + port + virtual);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost(virtual);
        if (!username.isEmpty() && !password.isEmpty()) {
            connectionFactory.setUsername(username);
            connectionFactory.setPassword(password);
        }

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            String exchangeType = "topic";
            boolean durability = true;

            channel.exchangeDeclare(EXCHANGE_NAME, exchangeType, durability);
            log.info("Initialized RabbitMqExporter instance!");
        } catch (IOException | TimeoutException e) {
            log.error("Initializing RabbitMQ instance failed!");
            log.error(e);
        }
    }

    @Override
    public void terminate() {

        if (connection.isOpen()) {
            try {
                channel.close();
                connection.close();
                log.info("RabbitMQ connection terminated");
            } catch (IOException | TimeoutException e) {
                log.error("Terminating failed!");
                log.error(e);
            }
        }
    }

    @Override
    public void export(EventInstance eventInstance) {

        String location = String.valueOf(eventInstance.getEventType().getEventSourceId());
        String message = eventInstance.getMessage().getKey();

        try {
            channel.basicPublish(EXCHANGE_NAME, location, null, message.getBytes());
        } catch (IOException e) {
            log.error("Failed to publish a message : " + message);
            log.error(e);
        }
    }

    public boolean isConnected() {
        return channel.isOpen();
    }

}
