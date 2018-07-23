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
package com.serotonin.mango.view.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.util.SerializationHelper;

@JsonRemoteEntity
public class AnalogRenderer extends BaseTextRenderer {
    private static ImplDefinition definition = new ImplDefinition("textRendererAnalog", "ANALOG",
            "textRenderer.analog", new int[] { DataTypes.NUMERIC });

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
    private String format;
    @JsonRemoteProperty
    private String suffix;

    private DecimalFormat formatInstance;

    public AnalogRenderer() {
        // no op
    }

    public AnalogRenderer(String format, String suffix) {
        setFormat(format);
        this.suffix = suffix;
    }

    @Override
    public String getMetaText() {
        return suffix;
    }

    @Override
    protected String getTextImpl(MangoValue value, int hint) {
        if (!(value instanceof NumericValue))
            return null;
        return getText(value.getDoubleValue(), hint);
    }

    @Override
    public String getText(double value, int hint) {
        if (hint == HINT_RAW || suffix == null)
            return formatInstance.format(value);
        return formatInstance.format(value) + suffix;
    }

    @Override
    protected String getColourImpl(MangoValue value) {
        return null;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
        formatInstance = new DecimalFormat(format);
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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
        SerializationHelper.writeSafeUTF(out, format);
        SerializationHelper.writeSafeUTF(out, suffix);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            setFormat(SerializationHelper.readSafeUTF(in));
            suffix = SerializationHelper.readSafeUTF(in);
        }
    }
}
