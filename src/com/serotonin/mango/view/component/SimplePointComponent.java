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
package com.serotonin.mango.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.util.SerializationHelper;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class SimplePointComponent extends PointComponent {
    public static ImplDefinition DEFINITION = new ImplDefinition("simple", "SIMPLE", "graphic.simple", new int[] {
            DataTypes.BINARY, DataTypes.MULTISTATE, DataTypes.NUMERIC, DataTypes.ALPHANUMERIC });

    @JsonRemoteProperty
    private boolean displayPointName;

    @JsonRemoteProperty
    private String styleAttribute;

    public boolean isDisplayPointName() {
        return displayPointName;
    }

    public void setDisplayPointName(boolean displayPointName) {
        this.displayPointName = displayPointName;
    }

    public String getStyleAttribute() {
        return styleAttribute;
    }

    public void setStyleAttribute(String styleAttribute) {
        this.styleAttribute = styleAttribute;
    }

    @Override
    public String snippetName() {
        return "basicContent";
    }

    @Override
    public void addDataToModel(Map<String, Object> model, PointValueTime pointValue) {
        model.put("displayPointName", displayPointName);
        model.put("styleAttribute", styleAttribute);
    }

    @Override
    public ImplDefinition definition() {
        return DEFINITION;
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 3;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);

        out.writeBoolean(displayPointName);
        SerializationHelper.writeSafeUTF(out, styleAttribute);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            displayPointName = false;
            styleAttribute = "";
        }
        else if (ver == 2) {
            displayPointName = in.readBoolean();
            styleAttribute = "";
        }
        else if (ver == 3) {
            displayPointName = in.readBoolean();
            styleAttribute = SerializationHelper.readSafeUTF(in);
        }
    }
}
