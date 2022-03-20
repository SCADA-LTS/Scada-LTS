package org.tango_controls.scada_lts.vo.dataSource.tango;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.tango_controls.scada_lts.rt.dataSource.tango.TangoDataSourceRT;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author GP Orcullo
 */

@JsonRemoteEntity
public class TangoDataSourceVO extends DataSourceVO<TangoDataSourceVO> {
    public static final Type TYPE = Type.TANGO;
    @JsonRemoteProperty
    private String deviceID;
    @JsonRemoteProperty
    private String hostName;
    @JsonRemoteProperty
    private int port = 10000;
    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(TangoDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT,
                "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(TangoDataSourceRT.POINT_READ_EXCEPTION_EVENT,
                "POINT_READ_EXCEPTION_EVENT");
        EVENT_CODES.addElement(TangoDataSourceRT.POINT_WRITE_EXCEPTION_EVENT,
                "POINT_WRITE_EXCEPTION_EVENT");
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.default", deviceID);
    }

    @Override
    public PointLocatorVO createPointLocator() {
        return new TangoPointLocatorVO();
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new TangoDataSourceRT(this);
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    protected void addEventTypes(List<EventTypeVO> eventTypes) {
        eventTypes.add(createEventType(TangoDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT,
                new LocalizableMessage("event.ds.dataSource")));
        eventTypes.add(createEventType(TangoDataSourceRT.POINT_READ_EXCEPTION_EVENT,
                new LocalizableMessage("event.ds.pointRead")));
        eventTypes.add(createEventType(TangoDataSourceRT.POINT_WRITE_EXCEPTION_EVENT,
                new LocalizableMessage("event.ds.pointWrite")));
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (StringUtils.isEmpty(deviceID))
            response.addContextualMessage("deviceID", "validate.required");
        if (StringUtils.isEmpty(hostName))
            response.addContextualMessage("hostName", "validate.required");
        if (port <= 0)
            response.addContextualMessage("port", "validate.greaterThanZero");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.tango.deviceID", deviceID);
        AuditEventType.addPropertyMessage(list, "dsEdit.tango.hostName", hostName);
        AuditEventType.addPropertyMessage(list, "dsEdit.tango.port", port);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, TangoDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list,
                "dsEdit.tango.deviceID", from.deviceID, deviceID);
        AuditEventType.maybeAddPropertyChangeMessage(list,
                "dsEdit.tango.hostName", from.hostName, hostName);
        AuditEventType.maybeAddPropertyChangeMessage(list,
                "dsEdit.tango.port", from.port, port);
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, deviceID);
        SerializationHelper.writeSafeUTF(out, hostName);
        out.writeInt(port);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if (ver == 1) {
            deviceID = SerializationHelper.readSafeUTF(in);
            hostName = SerializationHelper.readSafeUTF(in);
            port = in.readInt();
        }
    }
}