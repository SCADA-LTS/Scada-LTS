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
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.virtual.ChangeTypeRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ChangeComparableObject;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.IntMessagePair;
import com.serotonin.util.SerializationHelper;
import com.serotonin.web.i18n.LocalizableMessage;

abstract public class ChangeTypeVO implements Serializable, JsonSerializable, ChangeComparableObject {
    public interface Types {
        public static final int ALTERNATE_BOOLEAN = 1;
        public static final int BROWNIAN = 2;
        public static final int INCREMENT_ANALOG = 3;
        public static final int INCREMENT_MULTISTATE = 4;
        public static final int NO_CHANGE = 5;
        public static final int RANDOM_ANALOG = 6;
        public static final int RANDOM_BOOLEAN = 7;
        public static final int RANDOM_MULTISTATE = 8;
        public static final int ANALOG_ATTRACTOR = 9;
    }

    public static final ExportCodes CHANGE_TYPE_CODES = new ExportCodes();
    static {
        CHANGE_TYPE_CODES.addElement(Types.ALTERNATE_BOOLEAN, "ALTERNATE_BOOLEAN",
                "dsEdit.virtual.changeType.alternate");
        CHANGE_TYPE_CODES.addElement(Types.BROWNIAN, "BROWNIAN", "dsEdit.virtual.changeType.brownian");
        CHANGE_TYPE_CODES.addElement(Types.INCREMENT_ANALOG, "INCREMENT_ANALOG", "dsEdit.virtual.changeType.increment");
        CHANGE_TYPE_CODES.addElement(Types.INCREMENT_MULTISTATE, "INCREMENT_MULTISTATE",
                "dsEdit.virtual.changeType.increment");
        CHANGE_TYPE_CODES.addElement(Types.NO_CHANGE, "NO_CHANGE", "dsEdit.virtual.changeType.noChange");
        CHANGE_TYPE_CODES.addElement(Types.RANDOM_ANALOG, "RANDOM_ANALOG", "dsEdit.virtual.changeType.random");
        CHANGE_TYPE_CODES.addElement(Types.RANDOM_BOOLEAN, "RANDOM_BOOLEAN", "dsEdit.virtual.changeType.random");
        CHANGE_TYPE_CODES.addElement(Types.RANDOM_MULTISTATE, "RANDOM_MULTISTATE", "dsEdit.virtual.changeType.random");
        CHANGE_TYPE_CODES.addElement(Types.ANALOG_ATTRACTOR, "ANALOG_ATTRACTOR", "dsEdit.virtual.changeType.attractor");
    }

    public static IntMessagePair[] getChangeTypes(int dataTypeId) {
        switch (dataTypeId) {
        case DataTypes.BINARY:
            return new IntMessagePair[] { new IntMessagePair(Types.ALTERNATE_BOOLEAN, AlternateBooleanChangeVO.KEY),
                    new IntMessagePair(Types.NO_CHANGE, NoChangeVO.KEY),
                    new IntMessagePair(Types.RANDOM_BOOLEAN, RandomBooleanChangeVO.KEY), };
        case DataTypes.MULTISTATE:
            return new IntMessagePair[] {
                    new IntMessagePair(Types.INCREMENT_MULTISTATE, IncrementMultistateChangeVO.KEY),
                    new IntMessagePair(Types.NO_CHANGE, NoChangeVO.KEY),
                    new IntMessagePair(Types.RANDOM_MULTISTATE, RandomMultistateChangeVO.KEY), };
        case DataTypes.NUMERIC:
            return new IntMessagePair[] { new IntMessagePair(Types.BROWNIAN, BrownianChangeVO.KEY),
                    new IntMessagePair(Types.INCREMENT_ANALOG, IncrementAnalogChangeVO.KEY),
                    new IntMessagePair(Types.NO_CHANGE, NoChangeVO.KEY),
                    new IntMessagePair(Types.RANDOM_ANALOG, RandomAnalogChangeVO.KEY),
                    new IntMessagePair(Types.ANALOG_ATTRACTOR, AnalogAttractorChangeVO.KEY), };
        case DataTypes.ALPHANUMERIC:
            return new IntMessagePair[] { new IntMessagePair(Types.NO_CHANGE, NoChangeVO.KEY), };
        }
        return new IntMessagePair[] {};
    }

    abstract public int typeId();

    abstract public LocalizableMessage getDescription();

    abstract public ChangeTypeRT createRuntime();

    @JsonRemoteProperty
    private String startValue;

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.startValue", startValue);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        ChangeTypeVO from = (ChangeTypeVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.virtual.startValue", from.startValue, startValue);
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
        SerializationHelper.writeSafeUTF(out, startValue);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            startValue = SerializationHelper.readSafeUTF(in);
        }
    }

    /**
     * @throws JsonException
     */
    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        // no op
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("type", CHANGE_TYPE_CODES.getCode(typeId()));
    }
}
