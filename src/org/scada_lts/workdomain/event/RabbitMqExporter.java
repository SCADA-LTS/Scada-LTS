package org.scada_lts.workdomain.event;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ Exporter
 *
 * Export ScadaLTS Events to remote broker by AMQPv0.9.1 module.
 *
 * @author Radek Jajko
 * @version 1.0.0
 * @since 25-09-2018
 */
public class RabbitMqExporter implements EventExporter {

    private final Log log = LogFactory.getLog(RabbitMqExporter.class);

    private Connection connection;
    private Channel channel;
    private String exchangeName;

    @Override
    public void initialize() {

        String host = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_HOST);
        String virtual = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_VIRTUAL);
        int port = SystemSettingsDAO.getIntValue(SystemSettingsDAO.ALARM_EXPORT_PORT);
        String username = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_USERNAME);
        String password = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_PASSWORD);
        String queueName = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_Q_NAME);
        exchangeName = SystemSettingsDAO.getValue(SystemSettingsDAO.ALARM_EXPORT_EX_NAME);


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

            channel.queueDeclare(queueName, durability, false, false, null);
            channel.exchangeDeclare(exchangeName, exchangeType, durability);
            channel.queueBind(queueName, exchangeName, "#");
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

        String routingKey = eventInstance.getAlarmLevel()
                + "." + eventInstance.getEventType().getEventSourceId()
                + "." + eventInstance.getMessage().getKey();

        String message = parseMessageToJSON(eventInstance);

        try {
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
        } catch (IOException e) {
            log.error("Failed to publish a message : " + message);
            log.error(e);
        }

    }

    public boolean isConnected() {
        if (channel != null) {
            return channel.isOpen();
        } else {
            return false;
        }

    }

    private String parseMessageToJSON(EventInstance eventInstance) {

        StringBuilder messageJson = new StringBuilder("{");
        messageJson.append("\"eventTime\":").append(eventInstance.getActiveTimestamp());
        messageJson.append(",\"eventType\":").append(eventInstance.getEventType().getEventSourceId());
        messageJson.append(",\"alarmLevel\":").append(eventInstance.getAlarmLevel());
        messageJson.append(",\"messageKey\":\"").append(eventInstance.getMessage().getKey()).append("\"");

        if (eventInstance.getMessage().getArgs() != null) {
            messageJson.append(",\"messageArgs\":[");
            Object last = null;

            for (Object arg : eventInstance.getMessage().getArgs()) {
                if (arg instanceof LocalizableMessage) {
                    for ( Object arg2 : ((LocalizableMessage) arg).getArgs()) {
                        messageJson.append("\"").append(arg2).append("\",");
                    }
                } else if( arg != null ) {
                    messageJson.append("\"").append(arg).append("\",");
                }
                last = arg;
            }
            if (last != null) {
                messageJson.replace(messageJson.length() - 1, messageJson.length(), "]");
            } else {
                messageJson.replace(messageJson.length() - 16, messageJson.length(), "");
            }

        }

        if (eventInstance.getContext() != null) {
            messageJson.append(parseContextToJson(eventInstance.getContext()));
        }
        messageJson.append("}");

        return messageJson.toString();
    }

    private String parseContextToJson(Map<String, Object> context) {

        StringBuilder contextMessage = new StringBuilder(",");
        DataPointVO dp = (DataPointVO) context.get("point");
        if (dp != null) {
            contextMessage.append("\"handler\":\"pointEventDetector\"");
            contextMessage.append(",\"dataPoint\":{");
            contextMessage.append("\"xid\":\"").append(dp.getXid()).append("\"");
            contextMessage.append(",\"id\":").append(dp.getId());
            contextMessage.append(",\"name\":\"").append(dp.getName()).append("\"");
            contextMessage.append("}");
        }

        DataSourceVO ds = (DataSourceVO) context.get("dataSource");
        if (ds != null) {
            if (dp != null) {
                contextMessage.append(",");
            }
            contextMessage.append("\"handler\":\"dataSourceEventDetector\"");
            contextMessage.append(",\"dataSource\":{");
            contextMessage.append("\"xid\":\"").append(ds.getXid()).append("\"");
            contextMessage.append(",\"id\":").append(ds.getId());
            contextMessage.append(",\"name\":\"").append(ds.getName()).append("\"");
            contextMessage.append("}");
        }

        return contextMessage.toString();
    }

}
