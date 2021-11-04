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
package com.serotonin.mango.view.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonValue;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.util.SerializationHelper;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
abstract public class ImageSetComponent extends PointComponent {
    protected ImageSet imageSet;
    @JsonRemoteProperty
    private boolean displayText;

    public ImageSet tgetImageSet() {
        return imageSet;
    }

    public void tsetImageSet(ImageSet imageSet) {
        this.imageSet = imageSet;
    }

    public boolean isDisplayText() {
        return displayText;
    }

    public void setDisplayText(boolean displayText) {
        this.displayText = displayText;
    }

    @Override
    public String snippetName() {
        return "imageContent";
    }

    abstract public String getImage(PointValueTime pointValue);

    public String defaultImage() {
        if (imageSet != null)
            return getImage(null);
        return null;
    }

    public int getHeight() {
        if (imageSet == null)
            return 0;
        return imageSet.getHeight();
    }

    public int getWidth() {
        if (imageSet == null)
            return 0;
        return imageSet.getWidth();
    }

    public int getTextX() {
        if (imageSet == null)
            return 0;
        return imageSet.getTextX();
    }

    public int getTextY() {
        if (imageSet == null)
            return 0;
        return imageSet.getTextY();
    }

    public String getImageSetId() {
        if (imageSet == null)
            return null;
        return imageSet.getId();
    }

    @Override
    public void addDataToModel(Map<String, Object> model, PointValueTime pointValue) {
        if (imageSet != null)
            model.put("image", getImage(pointValue));
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
        if (imageSet == null)
            SerializationHelper.writeSafeUTF(out, null);
        else
            SerializationHelper.writeSafeUTF(out, imageSet.getId());
        out.writeBoolean(displayText);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            imageSet = Common.ctx.getImageSet(SerializationHelper.readSafeUTF(in));
            displayText = in.readBoolean();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);

        JsonValue jsonImageId = json.getValue("imageSet");
        if (jsonImageId != null) {
            if (jsonImageId.isNull())
                imageSet = null;
            else {
                String id = jsonImageId.toJsonString().getValue();
                imageSet = Common.ctx.getImageSet(id);
                if (imageSet == null)
                    throw new LocalizableJsonException("emport.error.component.unknownImageSet", id, Common.ctx
                            .getImageSetIds());
            }
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);

        if (imageSet == null)
            map.put("imageSet", null);
        else
            map.put("imageSet", imageSet.getId());
    }
}
