package org.scada_lts.workdomain.datasource.amqp;

import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.serorepl.utils.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * AMQP Receiver DataSource Virtual Object
 *
 * @author Radek Jajko
 * @version 1.0
 * @since 2018-09-11
 */
@JsonRemoteEntity
public class AmqpReceiverDataSourceVO extends DataSourceVO<AmqpReceiverDataSourceVO> {

    public static final Type TYPE = Type.AMQP_RECEIVER;

    private int updatePeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int updatePeriods = 5;
    @JsonRemoteProperty
    private String serverIpAddress = new String("localhost");
    @JsonRemoteProperty
    private String serverPortNumber = new String("5672");
    //TODO: Create RabbitMQ virtual host parameter
    //@JsonRemoteProperty
    //private String serverVirtualHost = new String("/");
    @JsonRemoteProperty
    private String serverUsername = new String("");
    @JsonRemoteProperty
    private String serverPassword = new String("");

    @Override
    protected void addEventTypes(List<EventTypeVO> eventTypes) {

    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {

    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, AmqpReceiverDataSourceVO from) {

    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new AmqpReceiverDataSourceRT(this);
    }

    @Override
    public PointLocatorVO createPointLocator() {
        return new AmqpReceiverPointLocatorVO();
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        if (serverIpAddress.length() == 0 || serverPortNumber.length() == 0)
            return new LocalizableMessage("dsEdit.amqpReceiver");
        return null;
    }

    @Override
    public ExportCodes getEventCodes() {
        return null;
    }

    @Override
    public Type getType() {
        return TYPE;
    }


    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (StringUtils.isEmpty(serverIpAddress) || StringUtils.isEmpty(serverPortNumber))
            response.addContextualMessage("serverIpAddress","validate.invalidValue");
    }

    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
        SerializationHelper.writeSafeUTF(out, serverIpAddress);
        SerializationHelper.writeSafeUTF(out, serverPortNumber);
        SerializationHelper.writeSafeUTF(out, serverUsername);
        SerializationHelper.writeSafeUTF(out, serverPassword);

    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if (ver == 1) {
            updatePeriodType    = in.readInt();
            updatePeriods       = in.readInt();
            serverIpAddress     = SerializationHelper.readSafeUTF(in);
            serverPortNumber    = SerializationHelper.readSafeUTF(in);
            serverUsername      = SerializationHelper.readSafeUTF(in);
            serverPassword      = SerializationHelper.readSafeUTF(in);

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

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public String getServerPortNumber() {
        return serverPortNumber;
    }

    public void setServerPortNumber(String serverPortNumber) {
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

}
