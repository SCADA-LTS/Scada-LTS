package com.serotonin.mango.vo.dataSource.persistent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.persistent.PersistentDataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class PersistentDataSourceVO extends DataSourceVO<PersistentDataSourceVO> {
    public static final Type TYPE = Type.PERSISTENT;

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(PersistentDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(PersistentDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION_EVENT");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("dsEdit.persistent.dsconn", port);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new PersistentDataSourceRT(this);
    }

    @Override
    public PersistentPointLocatorVO createPointLocator() {
        return new PersistentPointLocatorVO();
    }

    @JsonRemoteProperty
    private int port;
    @JsonRemoteProperty
    private String authorizationKey;
    @JsonRemoteProperty
    private boolean acceptPointUpdates;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAuthorizationKey() {
        return authorizationKey;
    }

    public void setAuthorizationKey(String authorizationKey) {
        this.authorizationKey = authorizationKey;
    }

    public boolean isAcceptPointUpdates() {
        return acceptPointUpdates;
    }

    public void setAcceptPointUpdates(boolean acceptPointUpdates) {
        this.acceptPointUpdates = acceptPointUpdates;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (port <= 0 || port > 65535)
            response.addContextualMessage("port", "validate.invalidValue");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.persistent.port", port);
        AuditEventType.addPropertyMessage(list, "dsEdit.persistent.authorizationKey", authorizationKey);
        AuditEventType.addPropertyMessage(list, "dsEdit.persistent.acceptPointUpdates", acceptPointUpdates);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, PersistentDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.persistent.port", from.port, port);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.persistent.acceptPointUpdates",
                from.acceptPointUpdates, acceptPointUpdates);
    }

    //
    //
    // Serialization
    //
    private static final long serialVersionUID = -1;
    private static final int version = 2;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeInt(port);
        SerializationHelper.writeSafeUTF(out, authorizationKey);
        out.writeBoolean(acceptPointUpdates);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            port = in.readInt();
            authorizationKey = SerializationHelper.readSafeUTF(in);
            acceptPointUpdates = false;
        }
        else if (ver == 2) {
            port = in.readInt();
            authorizationKey = SerializationHelper.readSafeUTF(in);
            acceptPointUpdates = in.readBoolean();
        }
    }
}
