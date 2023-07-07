package org.kebab.protocol.io;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.UUID;

public final class KebabOutputStream extends DataOutputStream {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    private final ByteArrayOutputStream output;

    public KebabOutputStream(ByteArrayOutputStream output) {
        super(output);
        this.output = output;
    }

    public KebabOutputStream(OutputStream outputStream) {
        super(outputStream);
        this.output = null;
    }

    public KebabOutputStream() {
        this(new ByteArrayOutputStream());
    }

    public void writeVarInt(int value) throws IOException {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                writeByte(value);
                return;
            }

            writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);

            value >>>= 7;
        }
    }

    public void writeVarLong(long value) throws IOException {
        while (true) {
            if ((value & ~((long) SEGMENT_BITS)) == 0) {
                writeByte((int) value);
                return;
            }

            writeByte((int) ((value & SEGMENT_BITS) | CONTINUE_BIT));

            value >>>= 7;
        }
    }

    public void writeComponent(Component component) throws IOException {
        writeString(GsonComponentSerializer.gson().serialize(component), StandardCharsets.UTF_8);
    }

    public void writeString(String text, Charset charset) throws IOException {
        byte[] bytes = text.getBytes(charset);
        writeVarInt(bytes.length);
        out.write(bytes);
    }

    public void writeString(String text) throws IOException {
        writeString(text, StandardCharsets.UTF_8);
    }

    public void writeUUID(UUID uuid) throws IOException {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public void writeFixedBitSet(BitSet bitset, int i) throws IOException {
        if (bitset.length() > i) {
            int j = bitset.length();
            throw new RuntimeException("BitSet is larger than expected size (" + j + ">" + i + ")");
        } else {
            byte[] aByte = bitset.toByteArray();
            write(Arrays.copyOf(aByte, -Math.floorDiv(-i, 8)));
        }
    }

    public <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumset, Class<E> oclass) throws IOException {
        E[] ae = oclass.getEnumConstants();
        BitSet bitset = new BitSet(ae.length);

        for (int i = 0; i < ae.length; ++i) {
            bitset.set(i, enumset.contains(ae[i]));
        }

        writeFixedBitSet(bitset, ae.length);
    }

    public byte[] toByteArray() {
        if (output == null) return new byte[0];
        return output.toByteArray();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (output == null) return;
        output.close();
    }
}
