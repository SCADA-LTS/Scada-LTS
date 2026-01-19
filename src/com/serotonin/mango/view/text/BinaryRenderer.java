package com.serotonin.mango.view.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.ImplDefinition;

@JsonRemoteEntity
public class BinaryRenderer extends BaseTextRenderer {
    public static final String TYPE_NAME = "textRendererBinaryNumber";

    private static ImplDefinition definition = new ImplDefinition(
            TYPE_NAME,
            "BINARY",
            "textRenderer.binary.number",
            new int[] { DataTypes.NUMERIC }
    );

    public static ImplDefinition getDefinition() {
        return definition;
    }

    public BinaryRenderer() {
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
        String bin = Long.toBinaryString(i);
        int groups = (bin.length() + 3) / 4;
        int totalBits = groups * 4;
        String padded = String.format("%" + totalBits + "s", bin).replace(' ', '0');
        StringBuilder sb = new StringBuilder();
        for (int g = 0; g < groups; g++) {
            if (g > 0) sb.append(' ');
            sb.append(padded, g * 4, g * 4 + 4);
        }
        return sb.toString();
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
        in.readInt(); // version
    }
}
