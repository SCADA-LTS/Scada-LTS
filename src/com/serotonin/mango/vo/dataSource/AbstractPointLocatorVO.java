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
package com.serotonin.mango.vo.dataSource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.ResourceBundle;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.web.i18n.LocalizableMessage;

abstract public class AbstractPointLocatorVO implements PointLocatorVO {
    public LocalizableMessage getDataTypeMessage() {
        return DataTypes.getDataTypeMessage(getDataTypeId());
    }

    protected String getMessage(ResourceBundle bundle, String key, Object... args) {
        return new LocalizableMessage(key, args).getLocalizedMessage(bundle);
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
    }

    private void readObject(ObjectInputStream in) throws IOException {
        in.readInt(); // Read the version. Value is currently not used.
    }

    protected void serializeDataType(Map<String, Object> map) {
        map.put("dataType", DataTypes.CODES.getCode(getDataTypeId()));
    }

    protected Integer deserializeDataType(JsonObject json, int... excludeIds) throws JsonException {
        String text = json.getString("dataType");
        if (text == null)
            return null;

        int dataType = DataTypes.CODES.getId(text);
        if (!DataTypes.CODES.isValidId(dataType, excludeIds))
            throw new LocalizableJsonException("emport.error.invalid", "dataType", text,
                    DataTypes.CODES.getCodeList(excludeIds));

        return dataType;
    }

    /**
     * Defaults to returning null. Override to return something else.
     */
    @Override
    public DataPointSaveHandler getDataPointSaveHandler() {
        return null;
    }

    /**
     * Defaults to returning false. Override to return something else.
     */
    @Override
    public boolean isRelinquishable() {
        return false;
    }
}
