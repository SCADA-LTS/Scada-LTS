package org.scada_lts.workdomain.datasource.amqp;

import com.serotonin.json.*;
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
        String A_HEADERS  = "headers";
        String A_FANOUT   = "fanout";
    }

    public interface DurabilityType {
        String DURABLE    = "1";
        String TRANSIENT   = "0";
    }

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
        return false;
    }

    @Override
    public PointLocatorRT createRuntime() {
        return new AmqpReceiverPointLocatorRT(this);
    }

    @Override
    public void validate(DwrResponseI18n response) {
//        super.validate(response);

    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.exchangeType", exchangeType);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.exchangeName", exchangeName);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.queueName", queueName);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.queueDurability", queueDurability);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.routingKey", routingKey);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        AmqpReceiverPointLocatorVO from = (AmqpReceiverPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.exchangeType", from.exchangeType, exchangeType);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.exchangeName", from.exchangeName, exchangeName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.queueName", from.queueName, queueName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.queueDurability", from.queueDurability, queueDurability);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.amqp.routingKey", from.routingKey, routingKey);
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


    private static final int version = 1;
    // Serialization //
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeInt(dataTypeId);
        SerializationHelper.writeSafeUTF(out, exchangeType);
        SerializationHelper.writeSafeUTF(out, exchangeName);
        SerializationHelper.writeSafeUTF(out, queueName);
        SerializationHelper.writeSafeUTF(out, queueDurability);
        SerializationHelper.writeSafeUTF(out, routingKey);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if(ver == 1) {
            dataTypeId      = in.readInt();
            exchangeType    = SerializationHelper.readSafeUTF(in);
            exchangeName    = SerializationHelper.readSafeUTF(in);
            queueName       = SerializationHelper.readSafeUTF(in);
            queueDurability = SerializationHelper.readSafeUTF(in);
            routingKey      = SerializationHelper.readSafeUTF(in);
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
