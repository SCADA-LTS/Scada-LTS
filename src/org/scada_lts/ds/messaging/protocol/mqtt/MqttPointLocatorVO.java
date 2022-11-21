package org.scada_lts.ds.messaging.protocol.mqtt;

import com.serotonin.json.*;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.MqttUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

@JsonRemoteEntity
public class MqttPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {

    @JsonRemoteProperty
    private boolean settable;
    @JsonRemoteProperty
    private boolean writable = true;
    @JsonRemoteProperty
    private String topicFilter = "";
    @JsonRemoteProperty
    private boolean retained;
    @JsonRemoteProperty
    private int dataTypeId;
    @JsonRemoteProperty
    private int qos = 0;
    @JsonRemoteProperty
    private String clientId = MqttUtils.generateUniqueClientId();
    @JsonRemoteProperty
    private String dataPointXid;

    @Override
    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", clientId);
    }

    @Override
    public PointLocatorRT createRuntime() {
        return new MqttPointLocatorRT(this);
    }

    @Override
    public void validate(DwrResponseI18n response) {

        if (StringUtils.isEmpty(clientId)) {
            response.addContextualMessage("clientId", "validate.invalidValue");
        } else {
            DataSourceService dataSourceService = new DataSourceService();
            DataPointService dataPointService = new DataPointService();
            boolean existsClientId = MqttUtils.isExistsClientId(clientId, dataPointService, dataPointXid, dataSourceService);
            if(existsClientId) {
                response.addContextualMessage("clientId", "validate.clientIdUsed");
            }
        }

        if(StringUtils.isEmpty(topicFilter)) {
            response.addContextualMessage("topicFilter", "validate.invalidValue");
        }

        if(settable && (topicFilter.contains("#") || topicFilter.contains("+") || topicFilter.contains("$"))) {
            response.addContextualMessage("topicFilter", "validate.wildcardNotAllowedForSettable");
        }

        if(qos < 0 || qos > 2) {
            response.addContextualMessage("qos", "validate.invalidValue");
        }

    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
        AuditEventType.addPropertyMessage(list, "dsEdit.writable", writable);
        AuditEventType.addPropertyMessage(list, "dsEdit.mqtt.topicFilter", topicFilter);
        AuditEventType.addPropertyMessage(list, "dsEdit.mqtt.retained", retained);
        AuditEventType.addPropertyMessage(list, "dsEdit.qos", qos);
        AuditEventType.addPropertyMessage(list, "dsEdit.mqtt.clientId", clientId);
        AuditEventType.addPropertyMessage(list, "dsEdit.mqtt.dataPointXid", dataPointXid);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        MqttPointLocatorVO from = (MqttPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, settable);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.writable", from.writable, writable);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mqtt.topicFilter", from.topicFilter, topicFilter);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mqtt.retained", from.retained, retained);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.qos", from.qos, qos);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.mqtt.clientId",from.clientId, clientId);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.mqtt.dataPointXid",from.dataPointXid, dataPointXid);
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

    public String getTopicFilter() {
        return topicFilter;
    }

    public void setTopicFilter(String topicFilter) {
        this.topicFilter = topicFilter;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDataPointXid() {
        return dataPointXid;
    }

    public void setDataPointXid(String dataPointXid) {
        this.dataPointXid = dataPointXid;
    }

    private static final long serialVersionUID = -1;
    private static final int VERSION = 2;
    // Serialization //
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(VERSION);
        out.writeBoolean(settable);
        out.writeBoolean(writable);
        out.writeInt(dataTypeId);
        SerializationHelper.writeSafeUTF(out, topicFilter);
        out.writeInt(qos);
        out.writeBoolean(retained);
        SerializationHelper.writeSafeUTF(out, clientId);
        SerializationHelper.writeSafeUTF(out, dataPointXid);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if(ver == 1) {
            settable = in.readBoolean();
            writable = in.readBoolean();
            dataTypeId = in.readInt();
            topicFilter = SerializationHelper.readSafeUTF(in);
            qos = in.readInt();
            retained = in.readBoolean();
            clientId = SerializationHelper.readSafeUTF(in);
        } else if(ver == 2) {
            settable = in.readBoolean();
            writable = in.readBoolean();
            dataTypeId = in.readInt();
            topicFilter = SerializationHelper.readSafeUTF(in);
            qos = in.readInt();
            retained = in.readBoolean();
            clientId = SerializationHelper.readSafeUTF(in);
            dataPointXid = SerializationHelper.readSafeUTF(in);
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
