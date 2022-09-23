package org.scada_lts.ds.messaging.protocol.amqp;

import com.serotonin.json.*;
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
import org.scada_lts.ds.messaging.protocol.ProtocolVersion;
import org.scada_lts.ds.messaging.service.MessagingServiceFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonRemoteEntity
public class AmqpDataSourceVO extends DataSourceVO<AmqpDataSourceVO> implements DataSourceUpdatable<AmqpDataSourceVO> {

    public static final Type TYPE = Type.AMQP;

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    public static final String VALIDATE_INVALID_VALUE = "validate.invalidValue";

    static {
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_POINT_INIT_EXCEPTION_EVENT, "DATA_POINT_INIT_EXCEPTION");
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_POINT_PUBLISH_EXCEPTION_EVENT, "DATA_POINT_PUBLISH_EXCEPTION");
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
    private int serverPortNumber = 5672;
    @JsonRemoteProperty
    private String serverVirtualHost = "";
    @JsonRemoteProperty
    private String serverUsername = "";
    @JsonRemoteProperty
    private String serverPassword = "";
    @JsonRemoteProperty
    private int connectionTimeout = 10000;
    @JsonRemoteProperty
    private int networkRecoveryInterval = 5000;
    @JsonRemoteProperty
    private int channelRpcTimeout = 10000;
    @JsonRemoteProperty
    private boolean automaticRecoveryEnabled = true;

    private ProtocolVersion protocolVersion = AmqpVersion.V0_9_1_EXT_AMQP;
    private BrokerMode brokerMode = BrokerMode.NATIVE;

    @Override
    public DataSourceRT createDataSourceRT() {
        return new MessagingDataSourceRT(this, MessagingServiceFactory.newService(this));
    }

    @Override
    public PointLocatorVO createPointLocator() {
        return new AmqpPointLocatorVO();
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        if (serverHost.length() == 0 || serverPortNumber == 0)
            return new LocalizableMessage("dsEdit.amqp");
        return new LocalizableMessage("common.default", "amqp://" + serverHost + ":" + serverPortNumber + "/" + serverVirtualHost);
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
            response.addContextualMessage("serverHost", VALIDATE_INVALID_VALUE);
        }
        if (serverPortNumber < 0) {
            response.addContextualMessage("serverPortNumber",VALIDATE_INVALID_VALUE);
        }
        if (updateAttempts < 0 || updateAttempts > 10) {
            response.addContextualMessage("updateAttempts", "validate.updateAttempts");
        }

        if (networkRecoveryInterval < 0) {
            response.addContextualMessage("networkRecoveryInterval",VALIDATE_INVALID_VALUE);
        }

        if (channelRpcTimeout < 0) {
            response.addContextualMessage("channelRpcTimeout",VALIDATE_INVALID_VALUE);
        }

        if (connectionTimeout < 0) {
            response.addContextualMessage("connectionTimeout",VALIDATE_INVALID_VALUE);
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
        AuditEventType.addPropertyMessage(list, "dsEdit.serverHost", serverHost);
        AuditEventType.addPropertyMessage(list, "dsEdit.serverPortNumber", serverPortNumber);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.serverVirtualhost", serverVirtualHost);
        AuditEventType.addPropertyMessage(list, "dsEdit.serverUsername", serverUsername);
        AuditEventType.addPropertyMessage(list, "dsEdit.serverPassword", serverPassword);
        AuditEventType.addPropertyMessage(list,"dsEdit.amqp.channelRpcTimeout", channelRpcTimeout);
        AuditEventType.addPropertyMessage(list,"dsEdit.amqp.automaticRecoveryEnabled", automaticRecoveryEnabled);
        AuditEventType.addPropertyMessage(list,"dsEdit.connectionTimeout", connectionTimeout);
        AuditEventType.addPropertyMessage(list,"dsEdit.amqp.networkRecoveryInterval", networkRecoveryInterval);
        AuditEventType.addPropertyMessage(list,"dsEdit.messaging.protocolVersion", protocolVersion);
        AuditEventType.addPropertyMessage(list,"dsEdit.messaging.brokerMode", brokerMode);
        AuditEventType.addPropertyMessage(list,"dsEdit.messaging.updateAttempts", updateAttempts);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, AmqpDataSourceVO from) {
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods ,updatePeriodType, updatePeriods);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.serverHost",from.serverHost,serverHost);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.serverPortNumber",from.serverPortNumber,serverPortNumber);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.serverVirtualhost",from.serverVirtualHost,serverVirtualHost);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.serverUsername",from.serverUsername,serverUsername);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.serverPassword",from.serverPassword,serverPassword);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.channelRpcTimeout",from.channelRpcTimeout,channelRpcTimeout);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.automaticRecoveryEnabled",from.automaticRecoveryEnabled,automaticRecoveryEnabled);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.connectionTimeout",from.connectionTimeout,connectionTimeout);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.networkRecoveryInterval",from.networkRecoveryInterval,networkRecoveryInterval);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.messaging.protocolVersion",from.protocolVersion, protocolVersion);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.messaging.brokerMode",from.brokerMode,brokerMode);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.messaging.updateAttempts",from.updateAttempts,updateAttempts);
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
        SerializationHelper.writeSafeUTF(out, serverVirtualHost);
        SerializationHelper.writeSafeUTF(out, serverUsername);
        SerializationHelper.writeSafeUTF(out, serverPassword);
        out.writeInt(connectionTimeout);
        out.writeInt(networkRecoveryInterval);
        out.writeInt(channelRpcTimeout);
        out.writeBoolean(automaticRecoveryEnabled);
        out.writeObject(protocolVersion);
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
            serverVirtualHost = SerializationHelper.readSafeUTF(in);
            serverUsername = SerializationHelper.readSafeUTF(in);
            serverPassword = SerializationHelper.readSafeUTF(in);
            connectionTimeout = in.readInt();
            networkRecoveryInterval  = in.readInt();
            channelRpcTimeout  = in.readInt();
            automaticRecoveryEnabled  = in.readBoolean();
            try {
                protocolVersion = (AmqpVersion) in.readObject();
            } catch (Exception e) {
                protocolVersion = AmqpVersion.V0_9_1_EXT_AMQP;
            }
            try {
                brokerMode = (BrokerMode) in.readObject();
            } catch (Exception e) {
                brokerMode = BrokerMode.NATIVE;
            }
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        serializeUpdatePeriodType(map, updatePeriodType);
        map.put("protocolVersion", protocolVersion.getName());
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
                protocolVersion = AmqpVersion.valueOf(protocolVersionJson);
            } catch (Exception ex) {
                protocolVersion = AmqpVersion.V0_9_1_EXT_AMQP;
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

    public String getServerVirtualHost() {
        return serverVirtualHost;
    }

    public void setServerVirtualHost(String serverVirtualHost) {
        this.serverVirtualHost = serverVirtualHost;
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

    public int getNetworkRecoveryInterval() {
        return networkRecoveryInterval;
    }

    public void setNetworkRecoveryInterval(int networkRecoveryInterval) {
        this.networkRecoveryInterval = networkRecoveryInterval;
    }

    public int getChannelRpcTimeout() {
        return channelRpcTimeout;
    }

    public void setChannelRpcTimeout(int channelRpcTimeout) {
        this.channelRpcTimeout = channelRpcTimeout;
    }

    public boolean isAutomaticRecoveryEnabled() {
        return automaticRecoveryEnabled;
    }

    public void setAutomaticRecoveryEnabled(boolean automaticRecoveryEnabled) {
        this.automaticRecoveryEnabled = automaticRecoveryEnabled;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(AmqpVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public BrokerMode getBrokerMode() {
        return brokerMode;
    }

    public void setBrokerMode(BrokerMode brokerMode) {
        this.brokerMode = brokerMode;
    }

    @Override
    public DataSourceVO<AmqpDataSourceVO> toDataSource() {
        return this;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AmqpDataSourceVO)) return false;
        AmqpDataSourceVO that = (AmqpDataSourceVO) o;
        return getUpdatePeriodType() == that.getUpdatePeriodType()
                && getUpdatePeriods() == that.getUpdatePeriods()
                && getUpdateAttempts() == that.getUpdateAttempts()
                && getServerPortNumber() == that.getServerPortNumber()
                && getConnectionTimeout() == that.getConnectionTimeout()
                && getNetworkRecoveryInterval() == that.getNetworkRecoveryInterval()
                && getChannelRpcTimeout() == that.getChannelRpcTimeout()
                && isAutomaticRecoveryEnabled() == that.isAutomaticRecoveryEnabled()
                && Objects.equals(getServerHost(), that.getServerHost())
                && Objects.equals(getServerVirtualHost(), that.getServerVirtualHost())
                && Objects.equals(getServerUsername(), that.getServerUsername())
                && Objects.equals(getServerPassword(), that.getServerPassword())
                && Objects.equals(getProtocolVersion(), that.getProtocolVersion())
                && getBrokerMode() == that.getBrokerMode()
                && getId() == that.getId()
                && isEnabled() == that.isEnabled()
                && Objects.equals(getXid(), that.getXid())
                && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUpdatePeriodType(), getUpdatePeriods(),
                getUpdateAttempts(), getServerHost(),
                getServerPortNumber(), getServerVirtualHost(),
                getServerUsername(), getServerPassword(), getConnectionTimeout(),
                getNetworkRecoveryInterval(), getChannelRpcTimeout(),
                isAutomaticRecoveryEnabled(), getProtocolVersion(), getBrokerMode(),
                getId(), getXid(), getName(), isEnabled());
    }

    @Override
    public String toString() {
        return "AmqpDataSourceVO{" +
                "updatePeriodType=" + updatePeriodType +
                ", updatePeriods=" + updatePeriods +
                ", updateAttempts=" + updateAttempts +
                ", serverHost='" + serverHost + '\'' +
                ", serverPortNumber=" + serverPortNumber +
                ", serverVirtualHost='" + serverVirtualHost + '\'' +
                ", serverUsername='" + serverUsername + '\'' +
                ", serverPassword='" + serverPassword + '\'' +
                ", connectionTimeout=" + connectionTimeout +
                ", networkRecoveryInterval=" + networkRecoveryInterval +
                ", channelRpcTimeout=" + channelRpcTimeout +
                ", automaticRecoveryEnabled=" + automaticRecoveryEnabled +
                ", protocolVersion=" + protocolVersion +
                ", brokerMode=" + brokerMode +
                ", id=" + getId() +
                ", xid=" + getXid() +
                ", name=" + getName() +
                ", enabled=" + isEnabled() +
                '}';
    }
}
