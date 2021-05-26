package org.scada_lts.ds.amqp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
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

public class AmqpPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {

    public interface ExchangeType {
        String A_NONE   = "";
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

    private static String DEFAULT_NOT_SET = "";
    private static boolean DEFAULT_WRITABLE = true;

    @JsonRemoteProperty
    private boolean settable;
    @JsonRemoteProperty
    private boolean writable = DEFAULT_WRITABLE;
    @JsonRemoteProperty
    private String exchangeType;
    @JsonRemoteProperty
    private String exchangeName = ExchangeType.A_NONE;
    @JsonRemoteProperty
    private String  queueName = DEFAULT_NOT_SET;
    @JsonRemoteProperty
    private String  routingKey = DEFAULT_NOT_SET;
    @JsonRemoteProperty
    private String queueDurability = DurabilityType.DURABLE;
    @JsonRemoteProperty
    private String messageAck = MessageAckType.NO_ACK;

    private int     dataTypeId;

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
        if (exchangeType.equalsIgnoreCase(ExchangeType.A_NONE)) {
            routingKey = "";
            exchangeName = "";
            if (queueName.isBlank()) {
                response.addContextualMessage("queueName", "validate.invalidValue");
            }
        }
        if (exchangeType.equalsIgnoreCase(ExchangeType.A_DIRECT) || exchangeType.equalsIgnoreCase(ExchangeType.A_TOPIC)){
            queueName = "";
            if (exchangeName.isBlank()) {
                response.addContextualMessage("exchangeName", "validate.invalidValue");
            }
            if (routingKey.isBlank()) {
                response.addContextualMessage("routingKey", "validate.invalidValue");
            }
        }

        if (exchangeType.equalsIgnoreCase(ExchangeType.A_FANOUT)) {
            routingKey = "";
            if (exchangeName.isBlank()) {
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
        AmqpPointLocatorVO from = (AmqpPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, settable);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.exchangeType", from.exchangeType, exchangeType);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.exchangeName", from.exchangeName, exchangeName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.queueName", from.queueName, queueName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.queueDurability", from.queueDurability, queueDurability);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.routingKey", from.routingKey, routingKey);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.messageAck", from.messageAck, routingKey);
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
        out.writeBoolean(writable);
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
            writable        = in.readBoolean();
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
    public void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

    }

    @Override
    public void serializeWithType(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException {

    }

}
