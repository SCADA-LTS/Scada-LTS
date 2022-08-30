package org.scada_lts.ds.messaging.amqp;

import com.serotonin.json.*;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class AmqpPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {

    @JsonRemoteProperty
    private boolean settable;
    @JsonRemoteProperty
    private boolean writable = true;
    @JsonRemoteProperty
    private ExchangeType exchangeType = ExchangeType.NONE;
    @JsonRemoteProperty
    private String exchangeName = "";
    @JsonRemoteProperty
    private String queueName = "";
    @JsonRemoteProperty
    private String routingKey = "";
    @JsonRemoteProperty
    private DurabilityType durability = DurabilityType.DURABLE;
    @JsonRemoteProperty
    private MessageAckType messageAck = MessageAckType.NO_ACK;
    @JsonRemoteProperty
    private int dataTypeId;
    @JsonRemoteProperty
    private int qos = 0;
    @JsonRemoteProperty
    private boolean autoDelete;
    @JsonRemoteProperty
    private boolean internal;

    @Override
    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", queueName);
    }

    @Override
    public PointLocatorRT createRuntime() {
        return new AmqpPointLocatorRT(this);
    }

    @Override
    public void validate(DwrResponseI18n response) {
        if (queueName.isBlank()) {
            response.addContextualMessage("queueName", "validate.invalidValue");
        }
        if (exchangeType == ExchangeType.NONE) {
            if (queueName.isBlank()) {
                response.addContextualMessage("queueName", "validate.invalidValue");
            }
        }
        if (exchangeType == ExchangeType.DIRECT || exchangeType == ExchangeType.TOPIC) {
            if (exchangeName.isBlank()) {
                response.addContextualMessage("exchangeName", "validate.invalidValue");
            }
            if (routingKey.isBlank()) {
                response.addContextualMessage("routingKey", "validate.invalidValue");
            }
        }

        if (exchangeType == ExchangeType.FANOUT) {
            if (exchangeName.isBlank()) {
                response.addContextualMessage("exchangeName", "validate.invalidValue");
            }
        }

        if(qos < 0) {
            response.addContextualMessage("qos", "validate.invalidValue");
        }

    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
        AuditEventType.addPropertyMessage(list, "dsEdit.writable", writable);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.exchangeType", exchangeType);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.exchangeName", exchangeName);
        AuditEventType.addPropertyMessage(list, "dsEdit.messaging.queueName", queueName);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.durability", durability);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.routingKey", routingKey);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.messageAck", messageAck);
        AuditEventType.addPropertyMessage(list, "dsEdit.qos", qos);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.autoDelete", autoDelete);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.internal", internal);
        AuditEventType.addPropertyMessage(list, "dsEdit.dataTypeId", dataTypeId);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        AmqpPointLocatorVO from = (AmqpPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, settable);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.writable", from.writable, writable);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.exchangeType", from.exchangeType, exchangeType);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.exchangeName", from.exchangeName, exchangeName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.messaging.queueName", from.queueName, queueName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.durability", from.durability, durability);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.routingKey", from.routingKey, routingKey);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.messageAck", from.messageAck, routingKey);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.qos", from.qos, qos);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.autoDelete", from.autoDelete, autoDelete);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.internal", from.internal, internal);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.dataTypeId", from.dataTypeId, dataTypeId);
    }

    // Getters and Setters //
    @Override
    public int getDataTypeId() {
        return dataTypeId;
    }

    @Override
    public boolean isSettable() {
        return settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
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

    public DurabilityType getDurability() {
        return durability;
    }

    public void setDurability(DurabilityType durability) {
        this.durability = durability;
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

    public MessageAckType getMessageAck() {
        return messageAck;
    }

    public void setMessageAck(MessageAckType messageAck) {
        this.messageAck = messageAck;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    private static final long serialVersionUID = -1;
    private static final int VERSION = 1;
    // Serialization //
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(VERSION);
        out.writeBoolean(settable);
        out.writeBoolean(writable);
        out.writeInt(dataTypeId);
        SerializationHelper.writeSafeUTF(out, exchangeType.name());
        SerializationHelper.writeSafeUTF(out, exchangeName);
        SerializationHelper.writeSafeUTF(out, queueName);
        SerializationHelper.writeSafeUTF(out, durability.name());
        SerializationHelper.writeSafeUTF(out, routingKey);
        SerializationHelper.writeSafeUTF(out, messageAck.name());
        out.writeInt(qos);
        out.writeBoolean(autoDelete);
        out.writeBoolean(internal);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if(ver == 1) {
            settable = in.readBoolean();
            writable = in.readBoolean();
            dataTypeId = in.readInt();
            exchangeType = ExchangeType.valueOf(SerializationHelper.readSafeUTF(in));
            exchangeName = SerializationHelper.readSafeUTF(in);
            queueName = SerializationHelper.readSafeUTF(in);
            durability = DurabilityType.valueOf(SerializationHelper.readSafeUTF(in));
            routingKey = SerializationHelper.readSafeUTF(in);
            messageAck = MessageAckType.valueOf(SerializationHelper.readSafeUTF(in));
            qos = in.readInt();
            autoDelete = in.readBoolean();
            internal = in.readBoolean();
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
