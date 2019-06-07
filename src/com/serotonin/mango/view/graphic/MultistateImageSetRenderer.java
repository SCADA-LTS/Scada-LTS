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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.mango.view.ImplDefinition;

/**
 * @author Matthew Lohbihler
 */
@Deprecated
// Use ViewComponent instead
public class MultistateImageSetRenderer extends ImageSetRenderer {
    private static ImplDefinition definition = new ImplDefinition("graphicRendererMultistateImage", "MULTISTATE_IMAGE",
            "graphic.multistateImage", new int[] { DataTypes.MULTISTATE });

    public static ImplDefinition getDefinition() {
        return definition;
    }

    public String getTypeName() {
        return definition.getName();
    }

    public ImplDefinition getDef() {
        return definition;
    }

    private Map<Integer, Integer> stateImageMap;
    private int defaultImage;

    public MultistateImageSetRenderer(ImageSet imageSet, List<IntValuePair> imageStateList, int defaultImage,
            boolean displayText) {
        super(imageSet, displayText);

        stateImageMap = new HashMap<Integer, Integer>();
        for (IntValuePair ivp : imageStateList) {
            String[] states = ivp.getValue().split(",");
            for (String stateStr : states) {
                int state = Integer.parseInt(stateStr.trim());
                stateImageMap.put(state, ivp.getKey());
            }
        }

        this.defaultImage = defaultImage;
    }

    @Override
    public String getImage(PointValueTime pointValue) {
        Integer state = null;
        if (pointValue != null && pointValue.getValue() != null)
            state = pointValue.getIntegerValue();

        Integer imageId = null;
        if (state != null)
            imageId = stateImageMap.get(state);

        if (imageId == null)
            imageId = defaultImage;

        if (imageId != null) {
            int id = imageId;

            if (id >= 0 && id < imageSet.getImageCount())
                return imageSet.getImageFilename(id);
        }

        return null;
    }

    public List<IntValuePair> getImageStateList() {
        List<IntValuePair> result = new ArrayList<IntValuePair>();
        for (Integer state : stateImageMap.keySet()) {
            Integer imageId = stateImageMap.get(state);

            IntValuePair stateList = null;
            for (IntValuePair ivp : result) {
                if (ivp.getKey() == imageId) {
                    stateList = ivp;
                    break;
                }
            }

            if (stateList == null) {
                stateList = new IntValuePair(imageId, state.toString());
                result.add(stateList);
            }
            else
                stateList.setValue(stateList.getValue() + ',' + state.toString());
        }
        return result;
    }

    public int getDefaultImage() {
        return defaultImage;
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
        out.writeObject(stateImageMap);
        out.writeInt(defaultImage);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            stateImageMap = (Map<Integer, Integer>) in.readObject();
            defaultImage = in.readInt();
        }
    }
}
