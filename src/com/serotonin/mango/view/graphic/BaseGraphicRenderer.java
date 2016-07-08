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
package com.serotonin.mango.view.graphic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.view.ImplDefinition;

@Deprecated
// Use ViewComponent instead
abstract public class BaseGraphicRenderer implements GraphicRenderer {
    private static List<ImplDefinition> definitions;

    public static List<ImplDefinition> getImplementations(int dataType) {
        if (definitions == null) {
            List<ImplDefinition> d = new ArrayList<ImplDefinition>();
            d.add(AnalogImageSetRenderer.getDefinition());
            d.add(BasicRenderer.getDefinition());
            d.add(BinaryImageSetRenderer.getDefinition());
            d.add(MultistateImageSetRenderer.getDefinition());
            d.add(BasicImageRenderer.getDefinition());
            d.add(ThumbnailRenderer.getDefinition());
            d.add(DynamicImageRenderer.getDefinition());
            d.add(ScriptRenderer.getDefinition());
            definitions = d;
        }

        List<ImplDefinition> impls = new ArrayList<ImplDefinition>();
        for (ImplDefinition def : definitions) {
            if (def.supports(dataType))
                impls.add(def);
        }
        return impls;
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
        in.readInt();
    }
}
