package org.scada_lts.ds.messaging.amqp;

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
import org.scada_lts.ds.messaging.MessagingDataSourceRT;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class AmqpDataSourceVO extends DataSourceVO<AmqpDataSourceVO> {

    public static final Type TYPE = Type.AMQP;

    private static final ExportCodes EVENT_CODES = new ExportCodes();

    static {
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_POINT_INIT_EXCEPTION_EVENT, "DATA_POINT_READ_EXCEPTION");
        EVENT_CODES.addElement(MessagingDataSourceRT.DATA_POINT_PUBLISH_EXCEPTION_EVENT, "DATA_POINT_WRITE_EXCEPTION");
    }

    private int updatePeriodType = Common.TimePeriods.MINUTES;

    //Dependent of updatePeriodType (5 MINUTES update period)
    @JsonRemoteProperty
    private int updatePeriods = 5;
    @JsonRemoteProperty
    private int updateAttempts = 5;
    @JsonRemoteProperty
    private String serverIpAddress = "localhost";
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

    @Override
    public DataSourceRT createDataSourceRT() {
        return new MessagingDataSourceRT(this);
    }

    @Override
    public PointLocatorVO createPointLocator() {
        return new AmqpPointLocatorVO();
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        if (serverIpAddress.length() == 0 || serverPortNumber == 0)
            return new LocalizableMessage("dsEdit.amqp");
        return null;
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
        if (serverIpAddress.isBlank()) {
            response.addContextualMessage("serverIpAddress","validate.required");
        }
        try {
            InetAddress.getByName(serverIpAddress);
        } catch (Exception e) {
            response.addContextualMessage("serverIpAddress","validate.invalidValue");
        }
        if (serverPortNumber < 0) {
            response.addContextualMessage("serverPortNumber","validate.invalidValue");
        }
        if (updateAttempts < 0 || updateAttempts > 10) {
            response.addContextualMessage("updateAttempts", "validate.updateAttempts");
        }

        if (networkRecoveryInterval < 0) {
            response.addContextualMessage("networkRecoveryInterval","validate.invalidValue");
        }

        if (channelRpcTimeout < 0) {
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
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.serverIpAddress", serverIpAddress);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.serverPortNumber", serverPortNumber);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.serverVirtualHost", serverVirtualHost);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.serverUsername", serverUsername);
        AuditEventType.addPropertyMessage(list, "dsEdit.amqp.serverPassword", serverPassword);
        AuditEventType.addPropertyMessage(list,"dsEdit.amqp.channelRpcTimeout", channelRpcTimeout);
        AuditEventType.addPropertyMessage(list,"dsEdit.amqp.automaticRecoveryEnabled", automaticRecoveryEnabled);
        AuditEventType.addPropertyMessage(list,"dsEdit.amqp.connectionTimeout", connectionTimeout);
        AuditEventType.addPropertyMessage(list,"dsEdit.amqp.networkRecoveryInterval", networkRecoveryInterval);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, AmqpDataSourceVO from) {
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType, from.updatePeriods ,updatePeriodType, updatePeriods);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.serverIpAddress",from.serverIpAddress,serverIpAddress);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.serverPortNumber",from.serverPortNumber,serverPortNumber);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.serverVirtualHost",from.serverVirtualHost,serverVirtualHost);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.serverUsername",from.serverUsername,serverUsername);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.serverPassword",from.serverPassword,serverPassword);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.channelRpcTimeout",from.channelRpcTimeout,channelRpcTimeout);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.automaticRecoveryEnabled",from.automaticRecoveryEnabled,automaticRecoveryEnabled);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.connectionTimeout",from.connectionTimeout,connectionTimeout);
        AuditEventType.maybeAddPropertyChangeMessage(list,"dsEdit.amqp.networkRecoveryInterval",from.networkRecoveryInterval,networkRecoveryInterval);
    }

    private static final long serialVersionUID = -1;
    private static final int VERSION = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {

        out.writeInt(VERSION);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
        out.writeInt(updateAttempts);
        SerializationHelper.writeSafeUTF(out, serverIpAddress);
        out.writeInt(serverPortNumber);
        SerializationHelper.writeSafeUTF(out, serverVirtualHost);
        SerializationHelper.writeSafeUTF(out, serverUsername);
        SerializationHelper.writeSafeUTF(out, serverPassword);
        out.writeInt(connectionTimeout);
        out.writeInt(networkRecoveryInterval);
        out.writeInt(channelRpcTimeout);
        out.writeBoolean(automaticRecoveryEnabled);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if (ver == 1) {
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            updateAttempts = in.readInt();
            serverIpAddress = SerializationHelper.readSafeUTF(in);
            serverPortNumber = in.readInt();
            serverVirtualHost = SerializationHelper.readSafeUTF(in);
            serverUsername = SerializationHelper.readSafeUTF(in);
            serverPassword = SerializationHelper.readSafeUTF(in);
            connectionTimeout = in.readInt();
            networkRecoveryInterval  = in.readInt();
            channelRpcTimeout  = in.readInt();
            automaticRecoveryEnabled  = in.readBoolean();
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map){
        super.jsonSerialize(map);
        serializeUpdatePeriodType(map, updatePeriodType);
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader,json);
        Integer value = deserializeUpdatePeriodType(json);
        if (value != null)
            updatePeriodType = value;
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

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
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


}
