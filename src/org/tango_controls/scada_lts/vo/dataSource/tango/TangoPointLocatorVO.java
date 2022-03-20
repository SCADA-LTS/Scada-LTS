package org.tango_controls.scada_lts.vo.dataSource.tango;

import com.serotonin.json.*;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.tango_controls.scada_lts.rt.dataSource.tango.TangoPointLocatorRT;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author GP Orcullo
 */

@JsonRemoteEntity
public class TangoPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    private int dataTypeId;
    @JsonRemoteProperty
    private String attribute;
    @JsonRemoteProperty
    private boolean settable;

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", attribute);
    }

    public boolean isSettable() {
        return settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public PointLocatorRT createRuntime() {
        return new TangoPointLocatorRT(this);
    }

    public void validate(DwrResponseI18n response) {
        if (!DataTypes.CODES.isValidId(dataTypeId))
            response.addContextualMessage("dataTypeId", "validate.invalidValue");
        if (StringUtils.isEmpty(attribute))
            response.addContextualMessage("attribute", "validate.attribute");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", dataTypeId);
        AuditEventType.addPropertyMessage(list, "dsEdit.tango.attribute", attribute);
        AuditEventType.addPropertyMessage(list, "dsEdit.tango.writable", settable);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        TangoPointLocatorVO from = (TangoPointLocatorVO) o;
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType",
                from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.tango.attribute",
                from.attribute, attribute);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.tango.writable",
                from.settable, settable);
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
        out.writeInt(dataTypeId);
        SerializationHelper.writeSafeUTF(out, attribute);
        out.writeBoolean(settable);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        if (ver == 1) {
            dataTypeId = in.readInt();
            attribute = SerializationHelper.readSafeUTF(in);
            settable = in.readBoolean();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        Integer value = deserializeDataType(json, DataTypes.IMAGE);
        if (value != null)
            dataTypeId = value;
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        serializeDataType(map);
    }

}