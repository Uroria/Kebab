package org.kebab.protocol.nbt;

import org.kebab.protocol.io.Deserializer;
import org.kebab.protocol.io.LittleEndianNBTInputStream;
import org.kebab.protocol.io.NBTInputStream;
import org.kebab.protocol.tag.NamedTag;
import org.kebab.protocol.tag.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class NBTDeserializer implements Deserializer<NamedTag> {

    private boolean compressed, littleEndian;

    public NBTDeserializer() {
        this(true);
    }

    public NBTDeserializer(boolean compressed) {
        this.compressed = compressed;
    }

    public NBTDeserializer(boolean compressed, boolean littleEndian) {
        this.compressed = compressed;
        this.littleEndian = littleEndian;
    }

    @Override
    public NamedTag fromStream(InputStream stream) throws IOException {
        NBTInput nbtIn;
        InputStream input;
        if (compressed) {
            input = new GZIPInputStream(stream);
        } else {
            input = stream;
        }

        if (littleEndian) {
            nbtIn = new LittleEndianNBTInputStream(input);
        } else {
            nbtIn = new NBTInputStream(input);
        }
        return nbtIn.readTag(Tag.DEFAULT_MAX_DEPTH);
    }
}
