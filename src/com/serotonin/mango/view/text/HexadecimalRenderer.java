package com.serotonin.mango.view.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.ImplDefinition;


@JsonRemoteEntity
public class HexadecimalRenderer extends BaseTextRenderer {
    public static final String TYPE_NAME = "textRendererHexadecimal";

    private static ImplDefinition definition = new ImplDefinition(
            TYPE_NAME,
            "HEX",
            "textRenderer.hex",
            new int[] { DataTypes.NUMERIC }
    );

    public static ImplDefinition getDefinition() {
        return definition;
    }

    public HexadecimalRenderer() {
        // no op
    }

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public ImplDefinition getDef() {
        return definition;
    }

    @Override
    public String getMetaText() {
        return null;
    }

    @Override
    protected String getTextImpl(MangoValue value, int hint) {
        if (value == null)
            return UNKNOWN_VALUE;

        long i = (long) value.getDoubleValue();
        String hex = Long.toHexString(i).toUpperCase();
        return "0x" + hex;
    }

    @Override
    protected String getColourImpl(MangoValue value) {
        return null;
    }

    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        in.readInt();
    }
}
