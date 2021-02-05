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
import java.util.ArrayList;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.ImplDefinition;

@JsonRemoteEntity
public class MultistateEventRenderer extends BaseEventTextRenderer {
    private static ImplDefinition definition = new ImplDefinition("eventTextRendererMultistate", "EVENT_MULTISTATE",
            "textRenderer.multistate", new int[] { DataTypes.MULTISTATE });

    public static ImplDefinition getDefinition() {
        return definition;
    }

    public String getTypeName() {
        return definition.getName();
    }

    public ImplDefinition getDef() {
        return definition;
    }

    @JsonRemoteProperty(innerType = MultistateEventValue.class)
    private List<MultistateEventValue> multistateValues = new ArrayList<MultistateEventValue>();

    public void addMultistateValue(int key, String shortText, String longText, String colour) {
        multistateValues.add(new MultistateEventValue(key, shortText, longText, colour));
    }

    public List<MultistateEventValue> getMultistateValues() {
        return multistateValues;
    }

    public void setMultistateValues(List<MultistateEventValue> multistateValues) {
        this.multistateValues = multistateValues;
    }

    @Override
    protected String getShortTextImpl(MangoValue value, int hint) {
        if (!(value instanceof com.serotonin.mango.rt.dataImage.types.MultistateValue))
            return null;
        return getShortText(value.getIntegerValue(), hint);
    }

    @Override
    protected String getLongTextImpl(MangoValue value, int hint) {
        if (!(value instanceof com.serotonin.mango.rt.dataImage.types.MultistateValue))
            return null;
        return getLongText(value.getIntegerValue(), hint);
    }

    @Override
    public String getShortText(int value, int hint) {
        if (hint == HINT_RAW)
            return Integer.toString(value);

        MultistateEventValue mv = getMultistateValue(value);
        if (mv == null)
            return Integer.toString(value);
        return mv.getShortText();
    }

    @Override
    public String getLongText(int value, int hint) {
        if (hint == HINT_RAW)
            return Integer.toString(value);

        MultistateEventValue mv = getMultistateValue(value);
        if (mv == null)
            return Integer.toString(value);
        return mv.getLongText();
    }

    @Override
    protected String getColourImpl(MangoValue value) {
        if (!(value instanceof com.serotonin.mango.rt.dataImage.types.MultistateValue))
            return null;
        return getColour(value.getIntegerValue());
    }

    @Override
    public String getColour(int value) {
        MultistateEventValue mv = getMultistateValue(value);
        if (mv == null)
            return null;
        return mv.getColour();
    }

    private MultistateEventValue getMultistateValue(int value) {
        for (MultistateEventValue mv : multistateValues) {
            if (mv.getKey() == value)
                return mv;
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
        out.writeObject(multistateValues);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            multistateValues = (List<MultistateEventValue>) in.readObject();
        }
    }
}
