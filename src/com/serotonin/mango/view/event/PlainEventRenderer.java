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
package com.serotonin.mango.view.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.util.SerializationHelper;

@JsonRemoteEntity
public class PlainEventRenderer extends BaseEventTextRenderer {
    private static ImplDefinition definition = new ImplDefinition("eventTextRendererPlain", "EVENT_PLAIN", "textRenderer.plain",
            new int[] { DataTypes.BINARY, DataTypes.ALPHANUMERIC, DataTypes.MULTISTATE, DataTypes.NUMERIC });

    public static ImplDefinition getDefinition() {
        return definition;
    }

    public String getTypeName() {
        return definition.getName();
    }

    public ImplDefinition getDef() {
        return definition;
    }

    @JsonRemoteProperty
    private String shortSuffix;
    @JsonRemoteProperty
    private String longSuffix;

    public PlainEventRenderer() {
        // no op
    }

    public PlainEventRenderer(String shortSuffix, String longSuffix) {
        this.shortSuffix = shortSuffix;
        this.longSuffix = longSuffix;
    }

    @Override
    public String getMetaShortText() {
        return shortSuffix;
    }

    @Override
    public String getMetaLongText() {
        return longSuffix;
    }

    @Override
    protected String getShortTextImpl(MangoValue value, int hint) {
        if (hint == HINT_RAW || shortSuffix == null)
            return getStringValue(value);
        return getStringValue(value) + shortSuffix;
    }

    @Override
    protected String getLongTextImpl(MangoValue value, int hint) {
        if (hint == HINT_RAW || longSuffix == null)
            return getStringValue(value);
        return getStringValue(value) + longSuffix;
    }

    private String getStringValue(MangoValue value) {
        if (value instanceof BinaryValue) {
            if (value.getBooleanValue())
                return "1";
            return "0";
        }
        return value.toString();
    }

    public String getShortSuffix() {
        return shortSuffix;
    }

    public void setShortSuffix(String shortSuffix) {
        this.shortSuffix = shortSuffix;
    }

    public String getLongSuffix() {
        return longSuffix;
    }

    public void setLongSuffix(String longSuffix) {
        this.longSuffix = longSuffix;
    }

    @Override
    protected String getColourImpl(MangoValue value) {
        return null;
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
        SerializationHelper.writeSafeUTF(out, shortSuffix);
        SerializationHelper.writeSafeUTF(out, longSuffix);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            shortSuffix = SerializationHelper.readSafeUTF(in);
            longSuffix = SerializationHelper.readSafeUTF(in);
        }
    }
}
