/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.vo.dataSource.virtual;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.dataSource.virtual.AnalogAttractorChangeRT;
import com.serotonin.mango.rt.dataSource.virtual.ChangeTypeRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class AnalogAttractorChangeVO extends ChangeTypeVO {
    public static final LocalizableMessage KEY = new LocalizableMessage("dsEdit.virtual.changeType.attractor");

    @JsonRemoteProperty
    private double maxChange;
    @JsonRemoteProperty
    private double volatility;
    private int attractionPointId;

    @Override
    public int typeId() {
        return Types.ANALOG_ATTRACTOR;
    }

    @Override
    public LocalizableMessage getDescription() {
        return KEY;
    }

    @Override
    public ChangeTypeRT createRuntime() {
        return new AnalogAttractorChangeRT(this);
    }

    public int getAttractionPointId() {
        return attractionPointId;
    }

    public void setAttractionPointId(int attractionPointId) {
        this.attractionPointId = attractionPointId;
    }

    public double getMaxChange() {
        return maxChange;
    }

    public void setMaxChange(double maxChange) {
        this.maxChange = maxChange;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        super.addProperties(list);
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.maxChange", maxChange);
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.volatility", volatility);
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.attractionPoint", getAttractionPointName());
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        super.addPropertyChanges(list, o);
        AnalogAttractorChangeVO from = (AnalogAttractorChangeVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.virtual.maxChange", from.maxChange, maxChange);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.virtual.volatility", from.volatility, volatility);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.virtual.attractionPoint", from
                .getAttractionPointName(), getAttractionPointName());
    }

    private String getAttractionPointName() {
        DataPointVO dp = new DataPointDao().getDataPoint(attractionPointId);
        if (dp == null)
            return "";
        return dp.getName();
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
        out.writeDouble(maxChange);
        out.writeDouble(volatility);
        out.writeInt(attractionPointId);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            maxChange = in.readDouble();
            volatility = in.readDouble();
            attractionPointId = in.readInt();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);
        String text = json.getString("attractionPointId");
        if (text != null) {
            DataPointVO dp = new DataPointDao().getDataPoint(text);
            if (dp == null)
                throw new LocalizableJsonException("emport.error.attractor.missingPoint", "attractionPointId", text);
            attractionPointId = dp.getId();
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        DataPointVO dp = new DataPointDao().getDataPoint(attractionPointId);
        if (dp == null)
            map.put("attractionPointId", null);
        else
            map.put("attractionPointId", dp.getXid());
    }
}
