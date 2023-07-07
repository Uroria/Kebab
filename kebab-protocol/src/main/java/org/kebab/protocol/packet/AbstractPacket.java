package org.kebab.protocol.packet;

import org.kebab.protocol.io.KebabOutputStream;

import java.io.IOException;
import java.nio.charset.Charset;

public abstract class AbstractPacket {

    public static int getVarIntLength(int value) throws IOException {
        KebabOutputStream out = new KebabOutputStream();
        do {
            byte temp = (byte)(value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            out.writeByte(temp);
        } while (value != 0);
        out.close();
        return out.toByteArray().length;
    }

    public static int getStringLength(String string, Charset charset) throws IOException {
        byte[] bytes = string.getBytes(charset);
        return getVarIntLength(bytes.length) + bytes.length;
    }
}
