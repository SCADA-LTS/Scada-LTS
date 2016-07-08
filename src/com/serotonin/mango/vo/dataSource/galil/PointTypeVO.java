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
package com.serotonin.mango.vo.dataSource.galil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.rt.dataSource.galil.PointTypeRT;
import com.serotonin.mango.util.ChangeComparableObject;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
abstract public class PointTypeVO implements Serializable, JsonSerializable, ChangeComparableObject {
    public interface Types {
        public static final int COMMAND = 1;
        public static final int INPUT = 2;
        public static final int OUTPUT = 3;
        public static final int TELL_POSITION = 4;
        public static final int VARIABLE = 5;
    }

    public static final ExportCodes POINT_TYPE_CODES = new ExportCodes();
    static {
        POINT_TYPE_CODES.addElement(Types.COMMAND, "COMMAND", "dsEdit.galil.pointType.command");
        POINT_TYPE_CODES.addElement(Types.INPUT, "INPUT", "dsEdit.galil.pointType.input");
        POINT_TYPE_CODES.addElement(Types.OUTPUT, "OUTPUT", "dsEdit.galil.pointType.output");
        POINT_TYPE_CODES.addElement(Types.TELL_POSITION, "TELL_POSITION", "dsEdit.galil.pointType.tellPosition");
        POINT_TYPE_CODES.addElement(Types.VARIABLE, "VARIABLE", "dsEdit.galil.pointType.variable");
    }

    abstract public int typeId();

    abstract public LocalizableMessage getDescription();

    abstract public PointTypeRT createRuntime();

    abstract public int getDataTypeId();

    abstract public boolean isSettable();

    abstract public void validate(DwrResponseI18n response);

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            // no op
        }
    }

    /**
     * @throws JsonException
     *             subclasses can throw this exception
     */
    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        // no op
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("type", POINT_TYPE_CODES.getCode(typeId()));
    }
}
