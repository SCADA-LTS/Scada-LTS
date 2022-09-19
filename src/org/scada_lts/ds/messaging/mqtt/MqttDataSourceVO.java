package org.scada_lts.ds.messaging.mqtt;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.ds.DataSourceUpdatable;
import org.scada_lts.ds.messaging.BrokerMode;
import org.scada_lts.ds.messaging.MessagingDataSourceRT;
import org.scada_lts.ds.messaging.MessagingServiceFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class MqttDataSourceVO extends DataSourceVO<MqttDataSourceVO> implements DataSourceUpdatable<MqttDataSourceVO> {

    public static final Type TYPE = Type.MQTT;

    private static final ExportCodes EVENT_CODES = new ExportCodes();

    static {
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_POINT_INIT_EXCEPTION_EVENT, "DATA_POINT_READ_EXCEPTION");
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_POINT_PUBLISH_EXCEPTION_EVENT, "DATA_POINT_WRITE_EXCEPTION");
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_POINT_UPDATE_EXCEPTION_EVENT, "DATA_POINT_UPDATE_EXCEPTION");
    }

    private int updatePeriodType = Common.TimePeriods.MINUTES;

    //Dependent of updatePeriodType (5 MINUTES update period)
    @JsonRemoteProperty
    private int updatePeriods = 5;
    @JsonRemoteProperty
    private int updateAttempts = 5;
    @JsonRemoteProperty
    private String serverHost = "localhost";
    @JsonRemoteProperty
    private int serverPortNumber = 1883;
    @JsonRemoteProperty
    private String serverUsername = "";
    @JsonRemoteProperty
    private String serverPassword = "";
    @JsonRemoteProperty
    private int connectionTimeout = 10000;
    @JsonRemoteProperty
    private int maxReconnectDelay = 5000;
    @JsonRemoteProperty
    private int executorServiceTimeout = 10000;
    @JsonRemoteProperty
    private boolean automaticReconnect = true;
    @JsonRemoteProperty
    private int keepAliveInterval = 20;
    @JsonRemoteProperty
    private boolean cleanSession = true;

    private MqttVersion protocolVersion = MqttVersion.V3_1_1;
    private BrokerMode brokerMode = BrokerMode.NATIVE;

    @Override
    public DataSourceRT createDataSourceRT() {
        return new MessagingDataSourceRT(this, MessagingServiceFactory.newService(this));
    }

    @Override
    public PointLocatorVO createPointLocator() {
        return new MqttPointLocatorVO();
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        if (serverHost.length() == 0 || serverPortNumber == 0)
            return new LocalizableMessage("dsEdit.mqtt");
        return new LocalizableMessage("common.default", "tcp://" + serverHost + ":" + serverPortNumber);
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (serverHost.isBlank()) {
            response.addContextualMessage("serverHost","validate.required");
        }
        try {
            InetAddress.getByName(serverHost);
        } catch (Exception e) {
            response.addContextualMessage("serverHost","validate.invalidValue");
        }
        if (serverPortNumber < 0) {
            response.addContextualMessage("serverPortNumber","validate.invalidValue");
        }
        if (updateAttempts < 0 || updateAttempts > 10) {
            response.addContextualMessage("updateAttempts", "validate.updateAttempts");
        }

        if (maxReconnectDelay < 0) {
            response.addContextualMessage("networkRecoveryInterval","validate.invalidValue");
        }

        if (executorServiceTimeout < 0) {
            response.addContextualMessage("channelRpcTimeout","validate.invalidValue");
        }

        if (connectionTimeout < 0) {
            response.addContextualMessage("connectionTimeout","validate.invalidValue");
        }
    }

    @Override
    protected void addEventTypes(List<EventTypeVO> eventTypes) {
        eventTypes.add(createEventType(MessagingDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage("event.ds.dataSource") ));
        eventTypes.add(createEventType(MessagingDataSourceRT.DATA_POINT_INIT_EXCEPTION_EVENT, new LocalizableMessage("event.ds.initReceiver") ));
        eventTypes.add(createEventType(MessagingDataSourceRT.DATA_POINT_PUBLISH_EXCEPTION_EVENT, new LocalizableMessage("event.ds.pointPublish") ));
        eventTypes.add(createEventType(MessagingDataSourceRT.DATA_POINT_UPDATE_EXCEPTION_EVENT, new LocalizableMessage("event.ds.pointUpdate") ));
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.updateAttempts", updateAttempts);
        AuditEventType.addPropertyMessage(list, "dsEdit.serverHost", serverHost);
        AuditEventType.addPropertyMessage(list, "dsEdit.serverPortNumber", serverPortNumber);
        AuditEventType.addPropertyMessage(list, "dsEdit.serverUsername", serverUsername);
        AuditEventType.addPropertyMessage(list, "dsEdit.serverPassword", serverPassword);
        AuditEventType.addPropertyMessage(list,"dsEdit.mqtt.executorServiceTimeout", executorServiceTimeout);
        AuditEventType.addPropertyMessage(list,"dsEdit.mqtt.automaticReconnect", automaticReconnect);
        AuditEventType.addPropertyMessage(list,"dsEdit.connectionTimeout", connectionTimeout);
        AuditEventType.addPropertyMessage(list,"dsEdit.mqtt.maxReconnectDelay", maxReconnectDelay);
        AuditEventType.addPropertyMessage(list, "dsEdit.mqtt.version", protocolVersion);
        AuditEventType.addPropertyMessage(list, "dsEdit.mqtt.keepAliveInterval", keepAliveInterval);
        AuditEventType.addPropertyMessage(list, "dsEdit.mqtt.cleanSession", cleanSession);
        AuditEventType.addPropertyMessage(list, "dsEdit.messaging.brokerMode", brokerMode);
        AuditEventType.addPropertyMessage(list, "dsEdit.messaging.protocolVersion", protocolVersion);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, MqttDataSourceVO from) {
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods ,updatePeriodType, updatePeriods);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.updateAttempts",from.updateAttempts,updateAttempts);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.serverHost",from.serverHost, serverHost);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.serverPortNumber",from.serverPortNumber,serverPortNumber);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.serverUsername",from.serverUsername,serverUsername);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.serverPassword",from.serverPassword,serverPassword);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.mqtt.executorServiceTimeout",from.executorServiceTimeout, executorServiceTimeout);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.mqtt.automaticReconnect",from.automaticReconnect, automaticReconnect);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.connectionTimeout",from.connectionTimeout,connectionTimeout);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.mqtt.maxReconnectDelay",from.maxReconnectDelay, maxReconnectDelay);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.mqtt.keepAliveInterval",from.keepAliveInterval, keepAliveInterval);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.mqtt.cleanSession",from.cleanSession, cleanSession);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.messaging.brokerMode",from.brokerMode,brokerMode);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.messaging.protocolVersion",from.protocolVersion, protocolVersion);
    }

    private static final long serialVersionUID = -1;
    private static final int VERSION = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {

        out.writeInt(VERSION);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
        out.writeInt(updateAttempts);
        SerializationHelper.writeSafeUTF(out, serverHost);
        out.writeInt(serverPortNumber);
        SerializationHelper.writeSafeUTF(out, serverUsername);
        SerializationHelper.writeSafeUTF(out, serverPassword);
        out.writeInt(connectionTimeout);
        out.writeInt(maxReconnectDelay);
        out.writeInt(executorServiceTimeout);
        out.writeBoolean(automaticReconnect);
        out.writeObject(protocolVersion);
        out.writeInt(keepAliveInterval);
        out.writeBoolean(cleanSession);
        out.writeObject(brokerMode);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if (ver == 1) {
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            updateAttempts = in.readInt();
            serverHost = SerializationHelper.readSafeUTF(in);
            serverPortNumber = in.readInt();
            serverUsername = SerializationHelper.readSafeUTF(in);
            serverPassword = SerializationHelper.readSafeUTF(in);
            connectionTimeout = in.readInt();
            maxReconnectDelay = in.readInt();
            executorServiceTimeout = in.readInt();
            automaticReconnect = in.readBoolean();
            try {
                protocolVersion = (MqttVersion) in.readObject();
            } catch (Exception e) {
                protocolVersion = MqttVersion.V3_1_1;
            }
            keepAliveInterval = in.readInt();
            cleanSession = in.readBoolean();
            try {
                brokerMode = (BrokerMode) in.readObject();
            } catch (Exception e) {
                brokerMode = BrokerMode.NATIVE;
            }
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map){
        super.jsonSerialize(map);
        serializeUpdatePeriodType(map, updatePeriodType);
        map.put("protocolVersion", protocolVersion.name());
        map.put("brokerMode", brokerMode.name());
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader,json);
        Integer value = deserializeUpdatePeriodType(json);
        if (value != null)
            updatePeriodType = value;
        String protocolVersionJson = json.getString("protocolVersion");
        if(protocolVersionJson != null) {
            try {
                protocolVersion = MqttVersion.valueOf(protocolVersionJson);
            } catch (Exception ex) {
                protocolVersion = MqttVersion.V3_1_1;
            }
        }
        String brokerModeJson = json.getString("brokerMode");
        if(brokerModeJson != null) {
            try {
                brokerMode = BrokerMode.valueOf(brokerModeJson);
            } catch (Exception ex) {
                brokerMode = BrokerMode.NATIVE;
            }
        }
    }

    public int getUpdatePeriodType() {
        return updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getUpdatePeriods() {
        return updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    public int getUpdateAttempts() {
        return updateAttempts;
    }

    public void setUpdateAttempts(int updateAttempts) {
        this.updateAttempts = updateAttempts;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPortNumber() {
        return serverPortNumber;
    }

    public void setServerPortNumber(int serverPortNumber) {
        this.serverPortNumber = serverPortNumber;
    }

    public String getServerUsername() {
        return serverUsername;
    }

    public void setServerUsername(String serverUsername) {
        this.serverUsername = serverUsername;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getMaxReconnectDelay() {
        return maxReconnectDelay;
    }

    public void setMaxReconnectDelay(int maxReconnectDelay) {
        this.maxReconnectDelay = maxReconnectDelay;
    }

    public int getExecutorServiceTimeout() {
        return executorServiceTimeout;
    }

    public void setExecutorServiceTimeout(int executorServiceTimeout) {
        this.executorServiceTimeout = executorServiceTimeout;
    }

    public boolean isAutomaticReconnect() {
        return automaticReconnect;
    }

    public void setAutomaticReconnect(boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
    }

    public MqttVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(MqttVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public BrokerMode getBrokerMode() {
        return brokerMode;
    }

    public void setBrokerMode(BrokerMode brokerMode) {
        this.brokerMode = brokerMode;
    }

    @Override
    public DataSourceVO<MqttDataSourceVO> toDataSource() {
        return this;
    }
}
