/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.vo.dataSource.openv4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import net.sf.openv4j.AccessType;
import net.sf.openv4j.DataPoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.openv4j.OpenV4JPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

// Container to move data with json and ajax so ony basic datatypes
@JsonRemoteEntity
public class OpenV4JPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {

    private final static Log LOG = LogFactory.getLog(OpenV4JPointLocatorVO.class);
    /**
     * The address of the device.
     */
    private DataPoint dataPoint = DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID;

    @Override
    public int getDataTypeId() {
        if (dataPoint == null) {
            return DataTypes.UNKNOWN;
        }
        switch (dataPoint.getType()) {
        case BOOL:
            return DataTypes.BINARY;
        case SHORT_HEX:
        case CYCLE_TIMES:
        case ERROR_LIST_ENTRY:
        case TIME_STAMP_8:
            return DataTypes.ALPHANUMERIC;
        case BYTE:
        case SHORT:
        case INTEGER:
            return DataTypes.NUMERIC;
        default:
            return DataTypes.UNKNOWN;
        }
    }

    @Override
    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("dsEdit.openv4j", "Something", "I dont know");
    }

    @Override
    public boolean isSettable() {
        return AccessType.RW.equals(dataPoint.getAccess()) || AccessType.WO.equals(dataPoint.getAccess());
    }

    @Override
    public PointLocatorRT createRuntime() {
        return new OpenV4JPointLocatorRT(this);
    }

    @Override
    public void validate(DwrResponseI18n response) {
        // no op
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.openv4j.dataPoint", dataPoint);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        OpenV4JPointLocatorVO from = (OpenV4JPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.openv4j.dataPoint", from.dataPoint, dataPoint);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int serialVersion = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(serialVersion);
        SerializationHelper.writeSafeUTF(out, dataPoint.name());
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        switch (ver) {
        case 1:
            String s = SerializationHelper.readSafeUTF(in);
            try {
                dataPoint = DataPoint.valueOf(s);
            }
            catch (IllegalArgumentException ex) {
                LOG.fatal("UNKNOWN DataPoint: " + s);
                dataPoint = DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID;
            }
            break;
        default:
            LOG.fatal("Version fall trough DataPoint unknown");
            dataPoint = DataPoint.COMMON_CONFIG_DEVICE_TYPE_ID;

        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) {
        dataPoint = DataPoint.valueOf(json.getString("dataPointName"));
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("dataPointName", dataPoint.name());
    }

    public String getGroupName() {
        return dataPoint.getGroup().getName();
    }

    public String getGroupLabel() {
        return dataPoint.getGroup().getLabel();
    }

    public String getLabel() {
        return dataPoint.getLabel();
    }

    /**
     * @return the dataPoint
     */
    public String getDataPointName() {
        return dataPoint.getName();
    }

    /**
     * @param property
     *            the dataPoint to set
     */
    public void setDataPointName(String dataPointName) {
        dataPoint = DataPoint.valueOf(dataPointName);
    }
}
