package com.serotonin.mango.view.event;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.util.SerializationHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@JsonRemoteEntity
public class EventTextRenderer extends BaseEventTextRenderer{

    private static ImplDefinition definition = new ImplDefinition("eventTextRenderer", "TEXT", "eventTextRenderer.text",
            new int[] { DataTypes.BINARY, DataTypes.ALPHANUMERIC, DataTypes.MULTISTATE, DataTypes.NUMERIC });

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
    private String text;

    public EventTextRenderer() {
        // no op
    }

    public EventTextRenderer(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        SerializationHelper.writeSafeUTF(out, text);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            text = SerializationHelper.readSafeUTF(in);
        }
    }
}
