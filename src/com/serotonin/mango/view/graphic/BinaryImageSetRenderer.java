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

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.mango.view.ImplDefinition;

/**
 * This class is called "binary" so that we can refer to values as 0 and 1, which is the actual representation in most
 * BA systems. However, the render method actually expects a boolean value which (arbitrarily) maps 0 to false and 1 to
 * true.
 * 
 * @author mlohbihler
 */
@Deprecated
// Use ViewComponent instead
public class BinaryImageSetRenderer extends ImageSetRenderer {
    private static ImplDefinition definition = new ImplDefinition("graphicRendererBinaryImage", "BINARY_IMAGE",
            "graphic.binaryImage", new int[] { DataTypes.BINARY });

    public static ImplDefinition getDefinition() {
        return definition;
    }

    public String getTypeName() {
        return definition.getName();
    }

    public ImplDefinition getDef() {
        return definition;
    }

    private int zeroImage;
    private int oneImage;

    public BinaryImageSetRenderer(ImageSet imageSet, int zeroImage, int oneImage, boolean displayText) {
        super(imageSet, displayText);
        this.zeroImage = zeroImage;
        this.oneImage = oneImage;
    }

    @Override
    public String getImage(PointValueTime pointValue) {
        boolean bvalue = false;
        if (pointValue != null && pointValue.getValue() != null)
            bvalue = pointValue.getBooleanValue();

        return imageSet.getImageFilename(bvalue ? oneImage : zeroImage);
    }

    public int getOneImage() {
        return oneImage;
    }

    public int getZeroImage() {
        return zeroImage;
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
        out.writeInt(zeroImage);
        out.writeInt(oneImage);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            zeroImage = in.readInt();
            oneImage = in.readInt();
        }
    }
}
