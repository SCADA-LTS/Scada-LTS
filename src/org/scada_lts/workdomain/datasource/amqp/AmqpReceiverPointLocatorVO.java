package org.scada_lts.workdomain.datasource.amqp;

import com.serotonin.json.*;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * AMQP Receiver data point Virtual Object definition
 *
 * @author Radek Jajko
 * @version 1.0
 * @since 2018-09-11
 *
 */
public class AmqpReceiverPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {

    public interface ExchangeType {
        String A_NONE     = "";
        String A_DIRECT   = "direct";
        String A_TOPIC    = "topic";
        String A_FANOUT   = "fanout";
    }

    public interface DurabilityType {
        String DURABLE    = "1";
        String TRANSIENT   = "0";
    }

    public interface MessageAckType {
        String ACK      = "0";
        String NO_ACK   = "1";
    }

    @JsonRemoteProperty
    private boolean settable;
    @JsonRemoteProperty
    private String exchangeType;
    @JsonRemoteProperty
    private String exchangeName = new String("");
    @JsonRemoteProperty
    private String  queueName = new String("");
    @JsonRemoteProperty
    private String  routingKey = new String("");
    @JsonRemoteProperty
    private String queueDurability = new String("");
    @JsonRemoteProperty
    private String messageAck = new String("1");

    private int     dataTypeId;


    @Override
    public int getDataTypeId() {
        return dataTypeId;
    }

    @Override
    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", queueName);
    }

    @Override
    public boolean isSettable() {
        return settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    @Override
    public PointLocatorRT createRuntime() {
        return new AmqpReceiverPointLocatorRT(this);
    }

    @Override
    public void validate(DwrResponseI18n response) {
        if (exchangeType.equalsIgnoreCase(ExchangeType.A_NONE)) {
            routingKey = "";
            exchangeName = "";
            if (StringUtils.isEmpty(queueName)) {
                response.addContextualMessage("queueName", "validate.invalidValue");
            }
        }
        if (exchangeType.equalsIgnoreCase(ExchangeType.A_DIRECT) || exchangeType.equalsIgnoreCase(ExchangeType.A_TOPIC)){
            queueName = "";
            if (StringUtils.isEmpty(exchangeName)) {
                response.addContextualMessage("exchangeName", "validate.invalidValue");
            }
            if (StringUtils.isEmpty(routingKey)) {
                response.addContextualMessage("routingKey", "validate.invalidValue");
            }
        }

        if (exchangeType.equalsIgnoreCase(ExchangeType.A_FANOUT)) {
            routingKey = "";
            if (StringUtils.isEmpty(exchangeName)) {
                response.addContextualMessage("exchangeName", "validate.invalidValue");
            }
        }

    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.exchangeType", exchangeType);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.exchangeName", exchangeName);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.queueName", queueName);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.queueDurability", queueDurability);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.routingKey", routingKey);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.messageAck", messageAck);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        AmqpReceiverPointLocatorVO from = (AmqpReceiverPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, settable);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.exchangeType", from.exchangeType, exchangeType);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.exchangeName", from.exchangeName, exchangeName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.queueName", from.queueName, queueName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.queueDurability", from.queueDurability, queueDurability);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.routingKey", from.routingKey, routingKey);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.messageAck", from.messageAck, routingKey);
    }

    // Getters and Setters //
    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueDurability() {
        return queueDurability;
    }

    public void setQueueDurability(String queueDurability) {
        this.queueDurability = queueDurability;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public String getMessageAck() {
        return messageAck;
    }

    public void setMessageAck(String messageAck) {
        this.messageAck = messageAck;
    }

    private static final int version = 1;
    // Serialization //
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeBoolean(settable);
        out.writeInt(dataTypeId);
        SerializationHelper.writeSafeUTF(out, exchangeType);
        SerializationHelper.writeSafeUTF(out, exchangeName);
        SerializationHelper.writeSafeUTF(out, queueName);
        SerializationHelper.writeSafeUTF(out, queueDurability);
        SerializationHelper.writeSafeUTF(out, routingKey);
        SerializationHelper.writeSafeUTF(out, messageAck);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if(ver == 1) {
            settable        = in.readBoolean();
            dataTypeId      = in.readInt();
            exchangeType    = SerializationHelper.readSafeUTF(in);
            exchangeName    = SerializationHelper.readSafeUTF(in);
            queueName       = SerializationHelper.readSafeUTF(in);
            queueDurability = SerializationHelper.readSafeUTF(in);
            routingKey      = SerializationHelper.readSafeUTF(in);
            messageAck      = SerializationHelper.readSafeUTF(in);
        }
    }
    @Override
    public void jsonSerialize(Map<String, Object> map) {
        serializeDataType(map);
    }

    @Override
    public void jsonDeserialize(JsonReader jsonReader, JsonObject jsonObject) throws JsonException {
        deserializeDataType(jsonObject);

    }

}
