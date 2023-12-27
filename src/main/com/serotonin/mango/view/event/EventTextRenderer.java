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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.ImplDefinition;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "typeName"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BinaryEventTextRenderer.class, name = BinaryEventTextRenderer.TYPE_NAME),
        @JsonSubTypes.Type(value = MultistateEventRenderer.class, name = MultistateEventRenderer.TYPE_NAME),
        @JsonSubTypes.Type(value = RangeEventRenderer.class, name = RangeEventRenderer.TYPE_NAME),
        @JsonSubTypes.Type(value = NoneEventRenderer.class, name = NoneEventRenderer.TYPE_NAME)
})
public interface EventTextRenderer extends Serializable {

    String UNKNOWN_VALUE = "";

    String getText();

    String getText(boolean value);

    String getText(int value);

    String getText(double value);

    String getText(MangoValue value);

    String getText(String value);

    String getMetaText();

    String getTypeName();

    ImplDefinition getDef();
}
