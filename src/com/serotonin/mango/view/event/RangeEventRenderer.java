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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.util.SerializationHelper;

@JsonRemoteEntity
public class RangeEventRenderer extends BaseEventTextRenderer {
    private static ImplDefinition definition = new ImplDefinition(RangeEventRenderer.TYPE_NAME, "EVENT_RANGE", "textRenderer.range",
            new int[] { DataTypes.NUMERIC });

    public static ImplDefinition getDefinition() {
        return definition;
    }

    public String getTypeName() {
        return definition.getName();
    }

    public ImplDefinition getDef() {
        return definition;
    }

    public static final String TYPE_NAME = "eventTextRendererRange";

    @JsonRemoteProperty(innerType = RangeEventValue.class)
    private List<RangeEventValue> rangeEventValues = new ArrayList<RangeEventValue>();

    public RangeEventRenderer() {
        // no op
    }

    public void addRangeEventValues(double from, double to, String text) {
        rangeEventValues.add(new RangeEventValue(from, to, text));
    }

    public List<RangeEventValue> getRangeEventValues() {
        return rangeEventValues;
    }

    public void setRangeEventValues(List<RangeEventValue> rangeEventValues) {
        this.rangeEventValues = rangeEventValues;
    }

    @Override
    protected String getTextImpl(MangoValue value) {
        if (!(value instanceof NumericValue))
            return null;
        return getText(value.getDoubleValue());
    }

    @Override
    public String getText(double value) {
        RangeEventValue range = getRangeEventValue(value);
        if (range == null)
            return null;
        return range.getText();
    }

    private RangeEventValue getRangeEventValue(double value) {
        for (RangeEventValue range : rangeEventValues) {
            if (range.contains(value)) {
                return range;
            }
        }
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
        out.writeObject(rangeEventValues);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            rangeEventValues = (List<RangeEventValue>) in.readObject();
        }
    }
}
