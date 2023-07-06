package com.uroria.kebab.protocol.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.UUID;

public final class KebabInputStream extends DataInputStream {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public KebabInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public KebabInputStream(byte[] bytes) {
        this(new ByteArrayInputStream(bytes));
    }

    public int readVarInt() throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    public long readVarLong() throws IOException {
        long value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (long) (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 64) throw new RuntimeException("VarLong is too big");
        }

        return value;
    }

    public BitSet readFixedBitSet(int i) throws IOException {
        byte[] abyte = new byte[-Math.floorDiv(-i, 8)];
        readFully(abyte);
        return BitSet.valueOf(abyte);
    }

    public UUID readUUID() throws IOException {
        return new UUID(readLong(), readLong());
    }

    public String readString(Charset charset) throws IOException {
        int length = readVarInt();

        if (length == -1) {
            throw new IOException("Premature end of stream.");
        }

        byte[] b = new byte[length];
        readFully(b);
        return new String(b, charset);
    }

    public String readString() throws IOException {
        return readString(StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
}
