package com.uroria.kebab.protocol.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class KebabOutputStream extends DataOutputStream {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    private final ByteArrayOutputStream output;

    public KebabOutputStream(ByteArrayOutputStream output) {
        super(output);
        this.output = output;
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

    public byte[] toByteArray() {
        return output.toByteArray();
    }

    @Override
    public void close() throws IOException {
        super.close();
        output.close();
    }
}
