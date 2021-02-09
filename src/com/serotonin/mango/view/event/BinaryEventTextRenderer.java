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

/**
 * This class is called "binary" so that we can refer to values as 0 and 1, which is the actual representation in most
 * BA systems. However, the render method actually expects a boolean value which (arbitrarily) maps 0 to false and 1 to
 * true.
 * 
 * @author Artur Wolak
 */
@JsonRemoteEntity
public class BinaryEventTextRenderer extends BaseEventTextRenderer {
    private static ImplDefinition definition = new ImplDefinition("eventTextRendererBinary", "EVENT_BINARY",
            "textRenderer.binary", new int[] { DataTypes.BINARY });

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
    private String zeroShortLabel;
    @JsonRemoteProperty
    private String zeroLongLabel;
    @JsonRemoteProperty
    private String zeroColour;
    @JsonRemoteProperty
    private String oneShortLabel;
    @JsonRemoteProperty
    private String oneLongLabel;
    @JsonRemoteProperty
    private String oneColour;

    public BinaryEventTextRenderer() {
        // no op
    }

    public BinaryEventTextRenderer(String zeroShortValue, String zeroLongValue, String zeroColour, String oneShortValue, String oneLongValue, String oneColour) {
        zeroShortLabel = zeroShortValue;
        zeroLongLabel = zeroLongValue;
        this.zeroColour = zeroColour;
        oneShortLabel = oneShortValue;
        oneLongLabel = oneLongValue;
        this.oneColour = oneColour;
    }

    @Override
    protected String getShortTextImpl(MangoValue value, int hint) {
        if (!(value instanceof BinaryValue))
            return null;
        return getShortText(value.getBooleanValue(), hint);
    }

    @Override
    protected String getLongTextImpl(MangoValue value, int hint) {
        if (!(value instanceof BinaryValue))
            return null;
        return getLongText(value.getBooleanValue(), hint);
    }

    @Override
    protected String getColourImpl(MangoValue value) {
        if (!(value instanceof BinaryValue))
            return null;
        return getColour(value.getBooleanValue());
    }

    @Override
    public String getColour(boolean value) {
        if (value)
            return oneColour;
        return zeroColour;
    }

    public String getZeroShortLabel() {
        return zeroShortLabel;
    }

    public void setZeroShortLabel(String zeroShortLabel) {
        this.zeroShortLabel = zeroShortLabel;
    }

    public String getZeroLongLabel() {
        return zeroLongLabel;
    }

    public void setZeroLongLabel(String zeroLongLabel) {
        this.zeroLongLabel = zeroLongLabel;
    }

    public String getOneShortLabel() {
        return oneShortLabel;
    }

    public void setOneShortLabel(String oneShortLabel) {
        this.oneShortLabel = oneShortLabel;
    }

    public String getOneLongLabel() {
        return oneLongLabel;
    }

    public void setOneLongLabel(String oneLongLabel) {
        this.oneLongLabel = oneLongLabel;
    }

    public String getOneColour() {
        return oneColour;
    }

    public void setOneColour(String oneColour) {
        this.oneColour = oneColour;
    }

    public String getZeroColour() {
        return zeroColour;
    }

    public void setZeroColour(String zeroColour) {
        this.zeroColour = zeroColour;
    }


    @Override
    public String getShortText(boolean value, int hint) {
        if (hint == EventTextRenderer.HINT_RAW)
            return value ? "1" : "0";
        if (value)
            return oneShortLabel;
        return zeroShortLabel;
    }

    @Override
    public String getLongText(boolean value, int hint) {
        if (hint == EventTextRenderer.HINT_RAW)
            return value ? "1" : "0";
        if (value)
            return oneLongLabel;
        return zeroLongLabel;
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
        SerializationHelper.writeSafeUTF(out, zeroShortLabel);
        SerializationHelper.writeSafeUTF(out, zeroLongLabel);
        SerializationHelper.writeSafeUTF(out, zeroColour);
        SerializationHelper.writeSafeUTF(out, oneShortLabel);
        SerializationHelper.writeSafeUTF(out, oneLongLabel);
        SerializationHelper.writeSafeUTF(out, oneColour);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            zeroShortLabel = SerializationHelper.readSafeUTF(in);
            zeroLongLabel = SerializationHelper.readSafeUTF(in);
            zeroColour = SerializationHelper.readSafeUTF(in);
            oneShortLabel = SerializationHelper.readSafeUTF(in);
            oneLongLabel = SerializationHelper.readSafeUTF(in);
            oneColour = SerializationHelper.readSafeUTF(in);
        }
    }
}
