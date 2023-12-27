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
    private static ImplDefinition definition = new ImplDefinition(MultistateEventRenderer.TYPE_NAME, "EVENT_MULTISTATE",
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

    public static final String TYPE_NAME = "eventTextRendererMultistate";

    @JsonRemoteProperty(innerType = MultistateEventValue.class)
    private List<MultistateEventValue> multistateEventValues = new ArrayList<MultistateEventValue>();

    public void addMultistateEventValue(int key, String text) {
        multistateEventValues.add(new MultistateEventValue(key, text));
    }

    public List<MultistateEventValue> getMultistateEventValues() {
        return multistateEventValues;
    }

    public void setMultistateEventValues(List<MultistateEventValue> multistateEventValues) {
        this.multistateEventValues = multistateEventValues;
    }

    @Override
    protected String getTextImpl(MangoValue value) {
        if (!(value instanceof com.serotonin.mango.rt.dataImage.types.MultistateValue))
            return null;
        return getText(value.getIntegerValue());
    }

    @Override
    public String getText(int value) {
        MultistateEventValue mv = getMultistateEventValue(value);
        if (mv == null)
            return null;
        return mv.getText();
    }

    private MultistateEventValue getMultistateEventValue(int value) {
        for (MultistateEventValue mv : multistateEventValues) {
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
        out.writeObject(multistateEventValues);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            multistateEventValues = (List<MultistateEventValue>) in.readObject();
        }
    }
}
